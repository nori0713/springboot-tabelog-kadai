package com.example.nagoyameshi.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;
import com.stripe.exception.StripeException;

import jakarta.servlet.http.HttpServletRequest;

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
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		logger.info("index method called"); // ログを追加
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		model.addAttribute("user", user);

		// クレジットカード情報取得ロジックを追加（プレミアム会員の場合）
		if ("ROLE_PREMIUM".equals(user.getRole().getName())) {
			logger.info("User is a premium member.");
			try {
				logger.info("Retrieving card details for customer: {}", user.getStripeCustomerId());
				Map<String, String> cardDetails = stripeService.getCreditCardInfo(user.getStripeCustomerId());
				if (cardDetails == null || cardDetails.isEmpty()) {
					logger.warn("No card details found for customer: {}", user.getStripeCustomerId());
					model.addAttribute("errorMessage", "クレジットカード情報が見つかりませんでした。");
				} else {
					logger.info("Card Details: {}", cardDetails);
					model.addAttribute("cardDetails", cardDetails);
				}
			} catch (Exception e) {
				logger.error("Error retrieving card details for customer: {}", user.getStripeCustomerId(), e);
				model.addAttribute("errorMessage", "クレジットカード情報の取得に失敗しました。");
			}
		} else {
			logger.info("User is not a premium member.");
		}

		return "user/index";
	}

	// ユーザー情報編集ページの表示
	@GetMapping("/edit")
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		logger.info("edit method called"); // ログを追加
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		UserEditForm userEditForm = new UserEditForm(user.getId(), user.getName(), user.getFurigana(),
				user.getPostalCode(), user.getAddress(), user.getPhoneNumber(), user.getEmail());
		model.addAttribute("userEditForm", userEditForm);
		return "user/edit";
	}

	// ユーザー情報の更新
	@PostMapping("/update")
	public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		logger.info("update method called"); // ログを追加

		// メールアドレスが変更されているか確認
		if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
			bindingResult.addError(fieldError);
		}

		if (bindingResult.hasErrors()) {
			return "user/edit";
		}

		userService.update(userEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
		return "redirect:/user";
	}

	// 有料会員への登録処理
	@PostMapping("/subscribe")
	public String subscribeToPremium(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			HttpServletRequest request) {
		logger.info("subscribeToPremium method called"); // ログを追加
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		String checkoutUrl = stripeService.createSubscriptionSession(request, user.getEmail());
		return "redirect:" + checkoutUrl;
	}

	// クレジットカード情報更新フォームの表示
	@GetMapping("/update-card")
	public String showUpdateCardForm(Model model) {
		logger.info("showUpdateCardForm method called"); // ログを追加
		return "user/update-card"; // クレジットカード情報の更新フォームページ
	}

	// クレジットカード情報の更新
	@PostMapping("/update-card")
	@ResponseBody
	public Map<String, Object> updateCard(@RequestBody Map<String, Object> payload,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		Map<String, Object> response = new HashMap<>();
		String paymentMethodId = (String) payload.get("paymentMethodId");
		String email = userDetailsImpl.getUser().getEmail(); // ユーザーのメールアドレスを取得

		try {
			String customerId = stripeService.findOrCreateCustomerByEmail(email);

			// 既存のクレジットカード情報を削除（必要に応じて）
			stripeService.detachOldPaymentMethod(customerId); // 必要に応じてこの処理をコメントアウト

			// 新しい支払い方法を設定
			stripeService.updateCustomerCreditCard(customerId, paymentMethodId);

			response.put("status", "success");
			response.put("message", "Customer's card updated successfully");
		} catch (StripeException e) {
			logger.error("Failed to update card for customer {}: {}", email, e);
			response.put("status", "error");
			response.put("message", "An error occurred while updating your card information. Please try again.");
		}
		return response;
	}

	// ユーザーの詳細情報とクレジットカード情報の取得
	@GetMapping("/details")
	public String getUserDetails(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		logger.info("getUserDetails method called"); // メソッドが呼び出されたことを確認
		User user = userDetailsImpl.getUser();
		logger.info("User retrieved: {}", user.getEmail()); // ユーザー情報が取得されたか確認
		model.addAttribute("user", user);

		// プレミアム会員の場合、クレジットカード情報を取得
		if (user.isPremiumMember()) { // もしくは `user.getRole().getName().equals("ROLE_PREMIUM")` を使用してもOK
			logger.info("User is a premium member.");
			try {
				logger.info("Retrieving card details for customer: {}", user.getStripeCustomerId());
				Map<String, String> cardDetails = stripeService.getCreditCardInfo(user.getStripeCustomerId());
				if (cardDetails == null || cardDetails.isEmpty()) {
					logger.warn("No card details found for customer: {}", user.getStripeCustomerId());
					model.addAttribute("errorMessage", "クレジットカード情報が見つかりませんでした。");
				} else {
					logger.info("Card Details: {}", cardDetails);
					model.addAttribute("cardDetails", cardDetails);
				}
			} catch (Exception e) {
				logger.error("Error retrieving card details for customer: {}", user.getStripeCustomerId(), e);
				model.addAttribute("errorMessage", "クレジットカード情報の取得に失敗しました。");
			}
		} else {
			logger.info("User is not a premium member.");
		}

		return "user/index";
	}
}