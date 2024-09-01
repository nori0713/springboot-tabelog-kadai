package com.example.nagoyameshi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.service.UserService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

@RestController
public class StripeWebhookController {

	private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

	private final String endpointSecret = "whsec_23f27dc8377279f8735576acf26de8fede9ee7ce09109486b7dc5f5be4b8d04b";

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/stripe/webhook")
	public String handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
		Event event;

		try {
			event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
			logger.info("Webhook event constructed successfully: {}", event.getType());
		} catch (SignatureVerificationException e) {
			logger.error("Webhook signature verification failed: {}", e.getMessage());
			return "Webhook Error: " + e.getMessage();
		}

		// イベントの処理
		switch (event.getType()) {
		case "checkout.session.completed":
			logger.info("Processing checkout.session.completed event.");
			handleCheckoutSessionCompleted(event);
			break;
		case "invoice.payment_failed":
			logger.warn("Payment failed for session: {}", event.getId());
			break;
		case "customer.subscription.deleted":
			logger.warn("Subscription deleted for session: {}", event.getId());
			break;
		default:
			logger.info("Unhandled event type: {}", event.getType());
			return "Unhandled event type: " + event.getType();
		}

		return "Success";
	}

	private void handleCheckoutSessionCompleted(Event event) {
		try {
			// イベントデータからCheckoutSessionオブジェクトを取得
			Session session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();

			// メールアドレスからユーザーを取得
			String userEmail = session.getCustomerEmail();
			logger.info("Checkout completed for email: {}", userEmail);

			User user = userRepository.findByEmail(userEmail);

			if (user != null) {
				logger.info("User found: {}", user.getName());

				// ユーザーのロールをROLE_PREMIUMに変更
				userService.assignRole(user, "ROLE_PREMIUM");
				logger.info("Role changed to ROLE_PREMIUM for user: {}", user.getName());

				// サブスクリプションステータスをACTIVEに設定
				user.setSubscriptionStatus("ACTIVE");

				// ユーザー情報を更新
				userRepository.save(user);
				logger.info("User updated with new role and subscription status.");

				// 成功URLがある場合はログ出力
				String returnUrl = session.getSuccessUrl();
				if (returnUrl != null && !returnUrl.isEmpty()) {
					logger.info("Success URL: {}", returnUrl);
				}
			} else {
				logger.error("User not found for email: {}", userEmail);
			}
		} catch (Exception e) {
			logger.error("Error handling checkout.session.completed event: {}", e.getMessage(), e);
		}
	}
}