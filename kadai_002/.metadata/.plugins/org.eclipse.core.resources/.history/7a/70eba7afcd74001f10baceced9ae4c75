package com.example.nagoyameshi.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethod.Card;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.PaymentMethodListParams;
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
		if (baseUrl == null) {
			// ベースURLが取得できない場合のエラーハンドリング
			throw new IllegalStateException("Base URL cannot be determined.");
		}

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

	// クレジットカード情報から PaymentMethod を作成するメソッド
	public String createPaymentMethod(String cardNumber, String expMonth, String expYear, String cvc)
			throws StripeException {
		PaymentMethodCreateParams.CardDetails cardDetails = PaymentMethodCreateParams.CardDetails.builder()
				.setNumber(cardNumber)
				.setExpMonth(Long.parseLong(expMonth))
				.setExpYear(Long.parseLong(expYear))
				.setCvc(cvc)
				.build();

		PaymentMethodCreateParams params = PaymentMethodCreateParams.builder()
				.setType(PaymentMethodCreateParams.Type.CARD)
				.setCard(cardDetails)
				.build();

		PaymentMethod paymentMethod = PaymentMethod.create(params);
		return paymentMethod.getId(); // PaymentMethod の ID を返す
	}

	// クレジットカード情報を更新するメソッド
	public void updateCustomerCreditCard(String customerId, String paymentMethodId) throws StripeException {
		// 顧客の情報を取得
		Customer customer = Customer.retrieve(customerId);

		// 支払い方法を顧客に紐付ける
		PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
		PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
				.setCustomer(customerId)
				.build();
		paymentMethod.attach(attachParams);

		// 顧客のデフォルト支払い方法を更新
		CustomerUpdateParams params = CustomerUpdateParams.builder()
				.setInvoiceSettings(
						CustomerUpdateParams.InvoiceSettings.builder()
								.setDefaultPaymentMethod(paymentMethodId)
								.build())
				.build();
		customer.update(params);
	}

	// リクエストからベースURLを取得するヘルパーメソッド
	private String getBaseUrl(HttpServletRequest request) {
		if (request == null) {
			return null; // Requestがnullの場合はnullを返す
		}
		String requestUrl = request.getRequestURL().toString();
		String servletPath = request.getServletPath();
		return requestUrl.replace(servletPath, "");
	}

	// 顧客を検索または作成するメソッド
	public String findOrCreateCustomerByEmail(String email) throws StripeException {
		// 1. 既存の顧客をメールアドレスで検索
		CustomerListParams listParams = CustomerListParams.builder()
				.setEmail(email)
				.setLimit(1L) // 同じメールアドレスで一意の顧客が返ると仮定している
				.build();

		CustomerCollection customers = Customer.list(listParams);

		if (!customers.getData().isEmpty()) {
			// 既存の顧客が見つかった場合、その顧客IDを返す
			return customers.getData().get(0).getId();
		}

		// 2. 顧客が存在しない場合は、新規作成
		CustomerCreateParams createParams = CustomerCreateParams.builder()
				.setEmail(email)
				.build();

		Customer customer = Customer.create(createParams);
		return customer.getId(); // 新しく作成された顧客のIDを返す
	}

	public void attachPaymentMethodToCustomer(String customerEmail, String paymentMethodId) throws StripeException {
		// Stripe APIを使って、PaymentMethodを既存の顧客に追加
		PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
		// 顧客ID（この例ではメールアドレスに基づいて検索）
		String customerId = findOrCreateCustomerByEmail(customerEmail); // ここは顧客の作成/検索を行うメソッド
		paymentMethod.attach(PaymentMethodAttachParams.builder().setCustomer(customerId).build());
	}

	public Map<String, String> getCreditCardInfo(String customerId) throws StripeException {
		// 顧客IDがnullまたは空の場合のエラーハンドリング
		if (customerId == null || customerId.isEmpty()) {
			throw new IllegalArgumentException("顧客IDが無効です。");
		}

		// Stripe APIを使って顧客のPaymentMethodを取得
		PaymentMethodCollection paymentMethods = PaymentMethod.list(
				PaymentMethodListParams.builder()
						.setCustomer(customerId)
						.setType(PaymentMethodListParams.Type.CARD)
						.build());

		if (paymentMethods.getData().isEmpty()) {
			throw new IllegalStateException("クレジットカード情報が存在しません。");
		}

		// 最新のクレジットカード情報を取得
		PaymentMethod paymentMethod = paymentMethods.getData().get(0);
		Card card = paymentMethod.getCard();

		// 必要な情報をMapにして返す
		Map<String, String> cardInfo = new HashMap<>();
		cardInfo.put("number", "**** **** **** " + card.getLast4()); // カード番号の下4桁のみ表示
		cardInfo.put("expiry", card.getExpMonth() + "/" + card.getExpYear()); // 有効期限

		// ログで取得した情報を確認（デバッグ用）
		System.out.println("Retrieved card info: " + cardInfo);

		return cardInfo;
	}
}