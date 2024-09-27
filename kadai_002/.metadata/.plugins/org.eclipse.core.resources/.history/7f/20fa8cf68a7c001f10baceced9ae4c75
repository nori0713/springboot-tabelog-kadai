package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;

import jakarta.persistence.EntityNotFoundException;

@Controller
public class FavoriteController {

	private final FavoriteRepository favoriteRepository;
	private final RestaurantRepository restaurantRepository;
	private final FavoriteService favoriteService;

	public FavoriteController(FavoriteRepository favoriteRepository, RestaurantRepository restaurantRepository,
			FavoriteService favoriteService) {
		this.favoriteRepository = favoriteRepository;
		this.restaurantRepository = restaurantRepository;
		this.favoriteService = favoriteService;
	}

	@GetMapping("/favorites")
	public String showFavorites(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
			Model model) {
		if (userDetailsImpl != null) {
			User user = userDetailsImpl.getUser();
			// お気に入り一覧を取得（ページネーション付き）
			Page<Favorite> favoritePage = favoriteService.getUserFavorites(user, pageable);
			model.addAttribute("favoritePage", favoritePage);
		} else {
			model.addAttribute("errorMessage", "ユーザー情報が見つかりません。");
		}
		return "favorites/index";
	}

	// お気に入りトグル機能
	@PostMapping("/restaurants/{restaurantId}/favorites/toggle")
	public String toggleFavorite(@PathVariable(name = "restaurantId") Integer restaurantId,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			RedirectAttributes redirectAttributes) {
		try {
			Restaurant restaurant = restaurantRepository.findById(restaurantId)
					.orElseThrow(() -> new EntityNotFoundException("指定された飲食店が見つかりませんでした。"));
			User user = userDetailsImpl.getUser();

			// お気に入りの状態を確認し、トグルする
			boolean isFavorite = favoriteService.isFavorite(restaurant, user);
			if (isFavorite) {
				favoriteService.delete(restaurant, user);
				redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました。");
			} else {
				favoriteService.create(restaurant, user);
				redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加しました。");
			}
		} catch (EntityNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}

		return "redirect:/restaurants/" + restaurantId;
	}
}