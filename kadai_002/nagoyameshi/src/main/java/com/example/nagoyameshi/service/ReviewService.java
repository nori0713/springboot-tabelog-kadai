package com.example.nagoyameshi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.exception.ResourceNotFoundException;
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
		// ユーザーがすでにレビューを投稿しているか確認
		if (hasUserAlreadyReviewed(restaurant, user)) {
			throw new IllegalStateException("このユーザーはすでにこのレストランにレビューを投稿しています。");
		}

		Review review = new Review();
		review.setRestaurant(restaurant);
		review.setUser(user);
		review.setScore(reviewRegisterForm.getScore());
		review.setContent(reviewRegisterForm.getContent());
		reviewRepository.save(review);
	}

	@Transactional
	public void update(ReviewEditForm reviewEditForm) {
		// カスタム例外を使用して、レビューが存在しない場合の処理
		Review review = reviewRepository.findById(reviewEditForm.getId())
				.orElseThrow(() -> new ResourceNotFoundException("指定されたレビューが見つかりません。"));

		review.setScore(reviewEditForm.getScore());
		review.setContent(reviewEditForm.getContent());
		reviewRepository.save(review);
	}

	public boolean hasUserAlreadyReviewed(Restaurant restaurant, User user) {
		// Optionalを使用して存在チェック
		return reviewRepository.findByRestaurantAndUser(restaurant, user).isPresent();
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