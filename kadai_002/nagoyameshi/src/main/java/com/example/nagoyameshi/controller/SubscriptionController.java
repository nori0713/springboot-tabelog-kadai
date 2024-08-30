package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SubscriptionController {

	private final StripeService stripeService;
	private final UserService userService;

	public SubscriptionController(StripeService stripeService, UserService userService) {
		this.stripeService = stripeService;
		this.userService = userService;
	}

	@GetMapping("/subscription/success")
	public String subscriptionSuccess(RedirectAttributes redirectAttributes) {
		// サブスクリプションが成功したときの処理
		// 例えば、ユーザーのサブスクリプションステータスを更新する
		redirectAttributes.addFlashAttribute("successMessage", "サブスクリプションが成功しました。");
		return "redirect:/login"; // ログインページにリダイレクト
	}

	@GetMapping("/subscription/cancel")
	public String subscriptionCancel(RedirectAttributes redirectAttributes) {
		// サブスクリプションがキャンセルされたときの処理
		redirectAttributes.addFlashAttribute("errorMessage", "サブスクリプションがキャンセルされました。");
		return "redirect:/signup"; // サインアップページにリダイレクト
	}

	@GetMapping("/subscription/start")
	public String startSubscription(HttpServletRequest request, @RequestParam String email) {
		// サブスクリプション開始のためのStripeセッションを作成
		String sessionUrl = stripeService.createSubscriptionSession(request, email);
		if (sessionUrl != null) {
			return "redirect:" + sessionUrl; // StripeのセッションURLにリダイレクト
		} else {
			// エラーが発生した場合の処理
			return "redirect:/signup?error=stripe"; // エラーが発生した場合、サインアップページにリダイレクト
		}
	}
}