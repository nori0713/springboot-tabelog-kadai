package com.example.nagoyameshi.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.repository.ReviewRepository;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;

	public ReviewService(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}

	@Transactional
	public void create(Restaurant restaurant, User user, ReviewRegisterForm reviewRegisterForm) {
		Review review = new Review();
		review.setRestaurant(restaurant);
		review.setUser(user);
		review.setScore(reviewRegisterForm.getScore());
		review.setContent(reviewRegisterForm.getContent());
		reviewRepository.save(review);
	}

	@Transactional
	public void update(ReviewEditForm reviewEditForm) {
		Optional<Review> reviewOpt = reviewRepository.findById(reviewEditForm.getId());
		if (reviewOpt.isEmpty()) {
			throw new IllegalArgumentException("指定されたレビューが見つかりません。");
		}

		Review review = reviewOpt.get();
		review.setScore(reviewEditForm.getScore());
		review.setContent(reviewEditForm.getContent());
		reviewRepository.save(review);
	}

	public boolean hasUserAlreadyReviewed(Restaurant restaurant, User user) {
		return reviewRepository.findByRestaurantAndUser(restaurant, user) != null;
	}

	// すべてのレビューを取得
	public Page<Review> getAllReviews(Pageable pageable) {
		return reviewRepository.findAll(pageable);
	}

	// 検索キーワードによるレビューの取得（レストラン名・ユーザー名）
	public Page<Review> searchReviews(String keyword, Pageable pageable) {
		return reviewRepository.findByRestaurant_NameContainingOrUser_NameContaining(keyword, keyword, pageable);
	}
}