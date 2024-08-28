package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Controller
public class HomeController {
	private final RestaurantRepository restaurantRepository;

	public HomeController(RestaurantRepository restaurantRepository) {
		this.restaurantRepository = restaurantRepository;
	}

	@GetMapping("/")
	public String index(Model model) {
		// 新着のレストランを10件取得してビューに渡す
		List<Restaurant> newRestaurants = restaurantRepository.findTop10ByOrderByCreatedAtDesc();
		model.addAttribute("newRestaurants", newRestaurants);

		return "index";
	}
}