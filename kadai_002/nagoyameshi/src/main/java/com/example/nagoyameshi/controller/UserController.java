package com.example.nagoyameshi.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;
import com.stripe.exception.StripeException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/user")
public class UserController {

	private final UserRepository userRepository;
	private final UserService userService;
	private final StripeService stripeService;
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	public UserController(UserRepository userRepository, UserService userService, StripeService stripeService) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.stripeService = stripeService;
	}

	// ユーザー情報ページの表示
	@GetMapping
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model,
			HttpServletResponse response) {
		logger.info("index method called");

		// キャッシュ無効化のためのヘッダーを設定
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);

		// ログイン中のユーザーを取得し、モデルに追加
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		model.addAttribute("user", user);

		// プレミアム会員であればクレジットカード情報を再取得して最新の情報を表示
		try {
			if ("ROLE_PREMIUM".equals(user.getRole().getName())) {
				Map<String, String> cardDetails = stripeService.getCreditCardInfo(user.getStripeCustomerId());
				if (cardDetails == null) {
					logger.warn("No card details found for customer: {}", user.getStripeCustomerId());
					model.addAttribute("errorMessage", "クレジットカード情報が見つかりませんでした。");
				} else {
					logger.info("Retrieved card details: {}", cardDetails);
					model.addAttribute("cardDetails", cardDetails);
				}
			}
		} catch (Exception e) {
			logger.error("Error retrieving card details: ", e);
			model.addAttribute("errorMessage", "クレジットカード情報の取得に失敗しました。");
		}

		return "user/index";
	}

	// 有料会員への登録処理
	@PostMapping("/subscribe")
	public String subscribeToPremium(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			HttpServletRequest request, RedirectAttributes redirectAttributes) {
		logger.info("subscribeToPremium method called");

		try {
			User user = userDetailsImpl.getUser();

			// Stripeで新しい顧客IDを取得
			String customerId = stripeService.findOrCreateCustomerByEmail(user.getEmail());

			// 顧客IDが異なる場合はSQLのIDを更新する
			if (!customerId.equals(user.getStripeCustomerId())) {
				user.setStripeCustomerId(customerId); // 新しい顧客IDをセット
				userRepository.save(user); // SQLに保存
				logger.info("Updated user StripeCustomerId in SQL: {}", customerId);
			}

			// サブスクリプションセッションを作成
			String checkoutUrl = stripeService.createSubscriptionSession(request, user.getEmail());

			if (checkoutUrl == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "サブスクリプションの作成に失敗しました。再度お試しください。");
				return "redirect:/user";
			}

			return "redirect:" + checkoutUrl;
		} catch (Exception e) {
			logger.error("Subscription failed: ", e);
			redirectAttributes.addFlashAttribute("errorMessage", "有料会員へのアップグレードに失敗しました。再度お試しください。");
			return "redirect:/user";
		}
	}

	// クレジットカード情報の更新
	@PostMapping("/update-card")
	@ResponseBody
	public Map<String, Object> updateCard(@RequestBody Map<String, Object> payload,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		Map<String, Object> response = new HashMap<>();
		String paymentMethodId = (String) payload.get("paymentMethodId");
		String email = userDetailsImpl.getUser().getEmail();

		try {
			// Stripeで新しい顧客IDを取得
			String customerId = stripeService.findOrCreateCustomerByEmail(email);

			// usersテーブルのstripe_customer_idと一致しているか確認
			User user = userDetailsImpl.getUser();
			if (!customerId.equals(user.getStripeCustomerId())) {
				// usersテーブルのstripe_customer_idを新しいcustomerIdで更新
				user.setStripeCustomerId(customerId);
				userRepository.save(user);
			}

			// 古い支払い方法を削除
			stripeService.detachOldPaymentMethod(customerId);

			// 新しい支払い方法を設定
			stripeService.updateCustomerCreditCard(customerId, paymentMethodId);

			// 更新後のカード情報を確認
			Map<String, String> updatedCardDetails = stripeService.getCreditCardInfo(customerId);
			logger.info("Updated card details after update: {}", updatedCardDetails);

			response.put("status", "success");
			response.put("message", "Customer's card updated successfully");

		} catch (StripeException e) {
			logger.error("Failed to update card for customer {}: {}", email, e);
			response.put("status", "error");
			response.put("message", "An error occurred while updating your card information. Please try again.");
		}

		return response;
	}

	// 解約機能のエンドポイント
	@PostMapping("/cancel-subscription")
	@Transactional
	public String cancelSubscription(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			RedirectAttributes redirectAttributes) {
		User user = userDetailsImpl.getUser();

		try {
			// クレジットカード情報を削除
			stripeService.deleteCustomerPaymentMethods(user.getStripeCustomerId());

			// 有料会員から無料会員に変更
			userService.downgradeToFree(user);

			// 新しい顧客IDを作成し、SQLに保存する
			String newCustomerId = stripeService.findOrCreateCustomerByEmail(user.getEmail());
			if (!newCustomerId.equals(user.getStripeCustomerId())) {
				user.setStripeCustomerId(newCustomerId); // 新しい顧客IDをセット
				userRepository.save(user); // SQLに保存
				logger.info("Updated user StripeCustomerId after cancellation: {}", newCustomerId);
			}

			redirectAttributes.addFlashAttribute("successMessage", "サブスクリプションを解約しました。");
		} catch (StripeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "クレジットカード情報の削除中にエラーが発生しました。");
			logger.error("Error during subscription cancellation", e);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "解約処理に失敗しました。再度お試しください。");
			logger.error("Error during subscription cancellation", e);
		}

		return "redirect:/user";
	}
}