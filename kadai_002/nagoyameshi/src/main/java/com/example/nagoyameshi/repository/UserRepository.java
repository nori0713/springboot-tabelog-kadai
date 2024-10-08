package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	// メールアドレスでユーザーを検索するメソッド
	public User findByEmail(String email);

	// 名前またはフリガナでユーザーを検索するメソッド（部分一致）
	public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);

	// パスワードリセットトークンでユーザーを検索するメソッド
	public User findByResetToken(String resetToken);
}