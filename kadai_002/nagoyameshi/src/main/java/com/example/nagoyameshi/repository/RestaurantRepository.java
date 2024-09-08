package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nagoyameshi.entity.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

	// 名前のみを使ってキーワード検索を行うメソッド
	Page<Restaurant> findByNameLike(String keyword, Pageable pageable);

	// キーワード検索に基づくメソッド
	Page<Restaurant> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword,
			Pageable pageable);

	Page<Restaurant> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword,
			Pageable pageable);

	// 価格範囲検索に基づくメソッド
	Page<Restaurant> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);

	Page<Restaurant> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);

	// 価格が指定値以上のレストランを検索するメソッド
	Page<Restaurant> findByPriceGreaterThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);

	Page<Restaurant> findByPriceGreaterThanEqualOrderByPriceAsc(Integer price, Pageable pageable);

	// 価格が指定した範囲内のレストランを検索するメソッド
	Page<Restaurant> findByPriceBetweenOrderByCreatedAtDesc(Integer minPrice, Integer maxPrice, Pageable pageable);

	Page<Restaurant> findByPriceBetweenOrderByPriceAsc(Integer minPrice, Integer maxPrice, Pageable pageable);

	// 全件を並び替えで取得するメソッド
	Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);

	Page<Restaurant> findAllByOrderByPriceAsc(Pageable pageable);

	// 新しいレストランを上位10件取得するメソッド
	List<Restaurant> findTop10ByOrderByCreatedAtDesc();

	// 予約最大人数に基づく検索・ソートメソッド
	Page<Restaurant> findByCapacityGreaterThanEqualOrderByCreatedAtDesc(Integer capacity, Pageable pageable);

	Page<Restaurant> findByCapacityLessThanEqualOrderByCreatedAtDesc(Integer capacity, Pageable pageable);

	Page<Restaurant> findByCapacityBetweenOrderByCreatedAtDesc(Integer minCapacity, Integer maxCapacity,
			Pageable pageable);

	Page<Restaurant> findByCapacityGreaterThanEqualOrderByPriceAsc(Integer capacity, Pageable pageable);

	Page<Restaurant> findByCapacityLessThanEqualOrderByPriceAsc(Integer capacity, Pageable pageable);

	Page<Restaurant> findByCapacityBetweenOrderByPriceAsc(Integer minCapacity, Integer maxCapacity, Pageable pageable);

	// カテゴリ検索メソッドの追加
	Page<Restaurant> findByCategoryOrderByCreatedAtDesc(String category, Pageable pageable);

	Page<Restaurant> findByCategoryOrderByPriceAsc(String category, Pageable pageable);

	// カテゴリと価格の範囲で検索
	Page<Restaurant> findByCategoryAndPriceBetweenOrderByCreatedAtDesc(String category, Integer minPrice,
			Integer maxPrice, Pageable pageable);

	Page<Restaurant> findByCategoryAndPriceBetweenOrderByPriceAsc(String category, Integer minPrice, Integer maxPrice,
			Pageable pageable);

	// カテゴリと最大人数で検索
	Page<Restaurant> findByCategoryAndCapacityBetweenOrderByCreatedAtDesc(String category, Integer minCapacity,
			Integer maxCapacity, Pageable pageable);

	Page<Restaurant> findByCategoryAndCapacityBetweenOrderByPriceAsc(String category, Integer minCapacity,
			Integer maxCapacity, Pageable pageable);
}