package com.example.nagoyameshi.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.service.VerificationTokenService;

@Component
public class SignupEventListener {
	private final VerificationTokenService verificationTokenService;
	private final JavaMailSender javaMailSender;

	public SignupEventListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
		this.verificationTokenService = verificationTokenService;
		this.javaMailSender = mailSender;
	}

	@EventListener
	private void onSignupEvent(SignupEvent signupEvent) {
		User user = signupEvent.getUser();
		String token = UUID.randomUUID().toString();
		verificationTokenService.create(user, token);

		String recipientAddress = user.getEmail();
		String verificationUrl = signupEvent.getRequestUrl() + "/verify?token=" + token;

		// メール内容を生成
		String subject = "【NAGOYAMESHI】メールアドレス確認のお願い";
		String message = String.format("こんにちは %s さん,\n\n"
				+ "以下のリンクをクリックして、メールアドレスを確認してください。\n"
				+ "リンク: %s\n\n"
				+ "有料会員の方は、認証後に決済ページにリダイレクトされます。\n"
				+ "無料会員の方は、ログインページにリダイレクトされます。\n\n"
				+ "よろしくお願いします。\n\n"
				+ "NAGOYAMESHIチーム", user.getName(), verificationUrl);

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom("yasuminorihiro0713@gmail.com"); // 送信者アドレスを明示的に設定
		mailMessage.setTo(recipientAddress);
		mailMessage.setSubject(subject);
		mailMessage.setText(message);
		javaMailSender.send(mailMessage);
	}
}