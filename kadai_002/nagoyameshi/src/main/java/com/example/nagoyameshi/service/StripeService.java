package com.example.nagoyameshi.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodDetachParams;
import com.stripe.param.PaymentMethodListParams;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {

	private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

	@Value("${stripe.api.key}")
	private String stripeApiKey;

	@Value("${stripe.price.id}")
	private String stripePriceId;

	@PostConstruct
	public void init() {
		Stripe.apiKey = stripeApiKey;
		logger.info("Stripe API initialized with key: {}", stripeApiKey);
	}

	// サブスクリプション用のセッションを作成し、Stripeに必要な情報を返す
	public String createSubscriptionSession(HttpServletRequest request, String userEmail) {
		String baseUrl = getBaseUrl(request);
		if (baseUrl == null) {
			logger.error("Base URL cannot be determined.");
			throw new IllegalStateException("Base URL cannot be determined.");
		}

		// セッション作成の処理
		SessionCreateParams params = SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.addLineItem(SessionCreateParams.LineItem.builder()
						.setPrice(stripePriceId)
						.setQuantity(1L)
						.build())
				.setMode(SessionCreateParams.Mode.SUBSCRIPTION)
				.setSuccessUrl(baseUrl + "/subscription/success")
				.setCancelUrl(baseUrl + "/subscription/cancel")
				.setCustomerEmail(userEmail)
				.build();

		try {
			Session session = Session.create(params);
			logger.info("Created subscription session: {}", session.getId());
			return session.getUrl();
		} catch (StripeException e) {
			logger.error("Failed to create subscription session: ", e);
			return null;
		}
	}

	// 顧客を検索または作成するメソッド
	public String findOrCreateCustomerByEmail(String email) throws StripeException {
		logger.info("Finding or creating customer with email: {}", email);
		CustomerListParams listParams = CustomerListParams.builder()
				.setEmail(email)
				.setLimit(1L)
				.build();

		CustomerCollection customers = Customer.list(listParams);

		if (!customers.getData().isEmpty()) {
			logger.info("Customer found with email: {}", email);
			return customers.getData().get(0).getId();
		}

		CustomerCreateParams createParams = CustomerCreateParams.builder()
				.setEmail(email)
				.build();

		Customer customer = Customer.create(createParams);
		logger.info("Created new customer: {}", customer.getId());
		return customer.getId();
	}

	// クレジットカード情報を更新するメソッド
	public void updateCustomerCreditCard(String customerId, String paymentMethodId) throws StripeException {
		logger.info("Updating credit card for customer: {}", customerId);

		// 既存の支払い方法をデタッチ
		detachOldPaymentMethod(customerId);

		// 新しい支払い方法を顧客に紐付ける
		PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
		PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
				.setCustomer(customerId)
				.build();
		paymentMethod.attach(attachParams);

		// 顧客のデフォルト支払い方法を更新
		CustomerUpdateParams customerUpdateParams = CustomerUpdateParams.builder()
				.setInvoiceSettings(
						CustomerUpdateParams.InvoiceSettings.builder()
								.setDefaultPaymentMethod(paymentMethodId)
								.build())
				.build();

		Customer customer = Customer.retrieve(customerId);
		customer.update(customerUpdateParams);

		logger.info("Updated default payment method for customer: {}", customerId);
	}

	// 既存の支払い方法を削除（デタッチ）するメソッド
	public void detachOldPaymentMethod(String customerId) throws StripeException {
		logger.info("Detaching old payment methods for customer: {}", customerId);

		// 既存の支払い方法を取得
		PaymentMethodListParams listParams = PaymentMethodListParams.builder()
				.setCustomer(customerId)
				.setType(PaymentMethodListParams.Type.CARD)
				.build();

		PaymentMethodCollection paymentMethods = PaymentMethod.list(listParams);

		// すべての支払い方法をデタッチ
		for (PaymentMethod paymentMethod : paymentMethods.getData()) {
			logger.info("Detaching payment method: {}", paymentMethod.getId());
			paymentMethod.detach(PaymentMethodDetachParams.builder().build());
		}
	}

	// クレジットカード情報を取得するメソッド
	public Map<String, String> getCreditCardInfo(String customerId) throws StripeException {
		if (customerId == null || customerId.isEmpty()) {
			logger.error("顧客IDが無効です: {}", customerId);
			throw new IllegalArgumentException("顧客IDが無効です。");
		}

		logger.info("Retrieving credit card info for customer: {}", customerId);

		PaymentMethodCollection paymentMethods = PaymentMethod.list(
				PaymentMethodListParams.builder()
						.setCustomer(customerId)
						.setType(PaymentMethodListParams.Type.CARD)
						.build());

		// PaymentMethodリストの内容をログ出力
		logger.info("取得したPaymentMethodのリスト: {}", paymentMethods.getData());

		if (paymentMethods.getData().isEmpty()) {
			logger.warn("クレジットカード情報が見つかりません: {}", customerId);
			throw new IllegalStateException("クレジットカード情報が存在しません。");
		}

		// 支払い方法が正しいか確認
		PaymentMethod paymentMethod = paymentMethods.getData().get(0);
		logger.info("PaymentMethod details: {}", paymentMethod);

		PaymentMethod.Card card = paymentMethod.getCard();
		if (card == null) {
			logger.error("Card details are null for payment method: {}", paymentMethod.getId());
			throw new IllegalStateException("クレジットカード情報が取得できませんでした。");
		}

		// 必要な情報をMapにして返す
		Map<String, String> cardInfo = new HashMap<>();
		cardInfo.put("number", "**** **** **** " + card.getLast4());
		cardInfo.put("expiry", card.getExpMonth() + "/" + card.getExpYear());

		logger.info("Retrieved card info: {}", cardInfo);

		return cardInfo;
	}

	// 支払い方法を顧客に紐付けるメソッド
	public void attachPaymentMethodToCustomer(String customerEmail, String paymentMethodId) throws StripeException {
		logger.info("Attaching payment method {} to customer with email {}", paymentMethodId, customerEmail);
		PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
		String customerId = findOrCreateCustomerByEmail(customerEmail);
		PaymentMethodAttachParams attachParams = PaymentMethodAttachParams.builder()
				.setCustomer(customerId)
				.build();
		paymentMethod.attach(attachParams);
		logger.info("Attached payment method {} to customer {}", paymentMethodId, customerId);
	}

	// リクエストからベースURLを取得するヘルパーメソッド
	private String getBaseUrl(HttpServletRequest request) {
		if (request == null) {
			logger.error("Request is null");
			return null;
		}
		String requestUrl = request.getRequestURL().toString();
		String servletPath = request.getServletPath();
		return requestUrl.replace(servletPath, "");
	}
}