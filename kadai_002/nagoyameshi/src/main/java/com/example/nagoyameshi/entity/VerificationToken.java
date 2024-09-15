package com.example.nagoyameshi.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "verification_tokens")
@Data
public class VerificationToken {
	private static final int EXPIRATION_TIME_IN_MINUTES = 60 * 24; // 24時間

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "token")
	private String token;

	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;

	@Column(name = "expiration_date")
	private Timestamp expirationDate;

	// トークンが期限切れかを判定するメソッド
	public boolean isExpired() {
		return Timestamp.valueOf(LocalDateTime.now()).after(expirationDate);
	}

	// トークンの有効期限を設定するメソッド
	public void setExpirationDate() {
		LocalDateTime expirationTime = LocalDateTime.now().plus(EXPIRATION_TIME_IN_MINUTES, ChronoUnit.MINUTES);
		this.expirationDate = Timestamp.valueOf(expirationTime);
	}
}