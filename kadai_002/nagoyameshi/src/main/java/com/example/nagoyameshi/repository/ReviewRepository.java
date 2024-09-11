package com.example.nagoyameshi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	// 最新の6件のレビューを取得（リスト）
	List<Review> findTop6ByRestaurantOrderByCreatedAtDesc(Restaurant restaurant);

	// 特定のレストランとユーザーによるレビューを1件取得
	Optional<Review> findByRestaurantAndUser(Restaurant restaurant, User user);

	// レストランのレビュー数をカウント
	long countByRestaurant(Restaurant restaurant);

	// ページング対応でレビューを取得
	Page<Review> findByRestaurantOrderByCreatedAtDesc(Restaurant restaurant, Pageable pageable);

	// 特定のレストランのすべてのレビューを取得（リスト）
	List<Review> findAllByRestaurantOrderByCreatedAtDesc(Restaurant restaurant);

	// レストラン名またはユーザー名でレビューを検索（ページング対応）
	Page<Review> findByRestaurant_NameContainingOrUser_NameContaining(String restaurantName, String userName,
			Pageable pageable);
}