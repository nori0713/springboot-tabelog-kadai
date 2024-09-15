package com.example.nagoyameshi.service;

import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.repository.VerificationTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class VerificationTokenService {
	private final VerificationTokenRepository verificationTokenRepository;

	public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
		this.verificationTokenRepository = verificationTokenRepository;
	}

	@Transactional
	public void create(User user, String token) {
		VerificationToken verificationToken = new VerificationToken();

		verificationToken.setUser(user);
		verificationToken.setToken(token);

		// 有効期限を現在から24時間後に設定
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, 24); // 24時間後に設定
		Timestamp expirationDate = new Timestamp(calendar.getTimeInMillis());
		verificationToken.setExpirationDate(expirationDate);

		verificationTokenRepository.save(verificationToken);
	}

	// トークンの文字列で検索した結果を返す
	public VerificationToken getVerificationToken(String token) {
		return verificationTokenRepository.findByToken(token);
	}
}