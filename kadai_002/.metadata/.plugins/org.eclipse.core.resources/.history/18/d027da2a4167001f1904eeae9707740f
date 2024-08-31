package com.example.nagoyameshi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.Event;
import com.stripe.net.Webhook;

@RestController
public class StripeWebhookController {

	private final String endpointSecret = "your_webhook_secret";

	@PostMapping("/stripe/webhook")
	public String handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
		Event event;

		try {
			event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
		} catch (Exception e) {
			return "Webhook Error: " + e.getMessage();
		}

		// イベントの処理
		switch (event.getType()) {
		case "checkout.session.completed":
			// サブスクリプション成功時の処理
			break;
		case "invoice.payment_failed":
			// 支払い失敗時の処理
			break;
		case "customer.subscription.deleted":
			// サブスクリプションキャンセル時の処理
			break;
		default:
			return "Unhandled event type: " + event.getType();
		}

		return "";
	}
}