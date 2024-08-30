package com.example.nagoyameshi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {

	@Value("${stripe.api.key}")
	private String stripeApiKey;

	@Value("${stripe.price.id}")
	private String stripePriceId;

	@PostConstruct
	public void init() {
		// StripeのAPIキーを初期化
		Stripe.apiKey = stripeApiKey;
	}

	// サブスクリプション用のセッションを作成し、Stripeに必要な情報を返す
	public String createSubscriptionSession(HttpServletRequest request, String userEmail) {
		String baseUrl = getBaseUrl(request);

		// Stripeのセッション作成
		SessionCreateParams params = SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.addLineItem(
						SessionCreateParams.LineItem.builder()
								.setPrice(stripePriceId) // 事前にStripeで作成した価格IDを使用
								.setQuantity(1L)
								.build())
				.setMode(SessionCreateParams.Mode.SUBSCRIPTION) // サブスクリプションモードに設定
				.setSuccessUrl(baseUrl + "/subscription/success")
				.setCancelUrl(baseUrl + "/subscription/cancel")
				.setCustomerEmail(userEmail) // ユーザーのメールアドレスを設定
				.build();

		try {
			Session session = Session.create(params);
			return session.getUrl(); // セッションのURLを返す
		} catch (StripeException e) {
			e.printStackTrace();
			return null; // エラーハンドリングを強化
		}
	}

	// リクエストからベースURLを取得するヘルパーメソッド
	private String getBaseUrl(HttpServletRequest request) {
		String requestUrl = request.getRequestURL().toString();
		String servletPath = request.getServletPath();
		return requestUrl.replace(servletPath, "");
	}
}