package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.FavoriteRepository;

@Service
public class FavoriteService {
	private final FavoriteRepository favoriteRepository;

	public FavoriteService(FavoriteRepository favoriteRepository) {
		this.favoriteRepository = favoriteRepository;
	}

	@Transactional
	public void create(Restaurant restaurant, User user) {
		if (!isFavorite(restaurant, user)) {
			Favorite favorite = new Favorite();
			favorite.setRestaurant(restaurant);
			favorite.setUser(user);
			favoriteRepository.save(favorite);
		}
	}

	@Transactional
	public void delete(Restaurant restaurant, User user) {
		Favorite favorite = favoriteRepository.findByRestaurantAndUser(restaurant, user);
		if (favorite != null) {
			favoriteRepository.delete(favorite);
		}
	}

	public boolean isFavorite(Restaurant restaurant, User user) {
		return favoriteRepository.findByRestaurantAndUser(restaurant, user) != null;
	}
}