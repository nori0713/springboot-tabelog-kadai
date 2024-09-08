package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.service.ReviewService;

@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {
	private final ReviewService reviewService;

	public AdminReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@GetMapping
	public String index(Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.DESC) Pageable pageable,
			@RequestParam(name = "keyword", required = false) String keyword) {

		Page<Review> reviewPage;

		if (keyword != null && !keyword.isEmpty()) {
			// キーワードがある場合は検索結果を表示
			reviewPage = reviewService.searchReviews(keyword, pageable);
		} else {
			// キーワードがない場合はすべてのレビューを表示
			reviewPage = reviewService.getAllReviews(pageable);
		}

		model.addAttribute("reviewPage", reviewPage);
		model.addAttribute("keyword", keyword);

		return "admin/reviews/index";
	}
}