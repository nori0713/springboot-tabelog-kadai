package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

	// ユーザーの全お気に入りを取得
	List<Favorite> findByUser(User user);

	// ページネーション付きでユーザーのお気に入りを取得
	Page<Favorite> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	// 特定のレストランとユーザーのお気に入りを取得
	Favorite findByRestaurantAndUser(Restaurant restaurant, User user);
}