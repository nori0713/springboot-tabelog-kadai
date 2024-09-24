package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

	// 名前による検索
	Page<Restaurant> findByNameLike(String keyword, Pageable pageable);

	// 名前または住所によるキーワード検索
	Page<Restaurant> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword,
			Pageable pageable);

	Page<Restaurant> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeyword, String addressKeyword,
			Pageable pageable);

	// 最新の10件を取得
	List<Restaurant> findTop10ByOrderByCreatedAtDesc();

	// 価格帯検索
	Page<Restaurant> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);

	Page<Restaurant> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);

	// 価格が指定値以上のレストランを検索
	Page<Restaurant> findByPriceGreaterThanEqual(Integer price, Pageable pageable);

	// 価格が指定した範囲内のレストランを検索
	Page<Restaurant> findByPriceBetweenOrderByCreatedAtDesc(Integer minPrice, Integer maxPrice, Pageable pageable);

	Page<Restaurant> findByPriceBetweenOrderByPriceAsc(Integer minPrice, Integer maxPrice, Pageable pageable);

	// 新しく追加するメソッド: 価格範囲 + 予約人数検索
	Page<Restaurant> findByPriceBetweenAndCapacityGreaterThanEqual(Integer minPrice, Integer maxPrice,
			Integer capacity, Pageable pageable);

	// 新しく追加するメソッド: カテゴリ + 価格範囲 + 予約人数検索
	Page<Restaurant> findByCategoryIdAndPriceBetweenAndCapacityGreaterThanEqual(Integer categoryId, Integer minPrice,
			Integer maxPrice, Integer capacity,
			Pageable pageable);

	// カテゴリによる検索（価格で並び替え、作成日時で並び替え）
	Page<Restaurant> findByCategory_IdOrderByPriceAsc(Integer categoryId, Pageable pageable);

	Page<Restaurant> findByCategory_IdOrderByCreatedAtDesc(Integer categoryId, Pageable pageable);

	// カテゴリと価格範囲で検索
	Page<Restaurant> findByCategoryIdAndPriceBetween(Integer categoryId, Integer minPrice, Integer maxPrice,
			Pageable pageable);

	// カテゴリと最大人数による検索
	Page<Restaurant> findByCategoryAndCapacityBetweenOrderByCreatedAtDesc(Category category, Integer minCapacity,
			Integer maxCapacity, Pageable pageable);

	Page<Restaurant> findByCategoryAndCapacityBetweenOrderByPriceAsc(Category category, Integer minCapacity,
			Integer maxCapacity, Pageable pageable);

	// カテゴリ、価格帯、予約人数の複合検索
	Page<Restaurant> findByCategoryIdAndPriceLessThanEqualAndCapacityGreaterThanEqual(Integer categoryId, Integer price,
			Integer capacity, Pageable pageable);

	Page<Restaurant> findByCategoryIdAndCapacityGreaterThanEqual(Integer categoryId, Integer capacity,
			Pageable pageable);

	// 価格帯 + 予約人数検索
	Page<Restaurant> findByPriceLessThanEqualAndCapacityGreaterThanEqual(Integer price, Integer capacity,
			Pageable pageable);

	// 価格帯のみの検索
	Page<Restaurant> findByPriceBetween(Integer minPrice, Integer maxPrice, Pageable pageable);

	// 予約人数のみの検索
	Page<Restaurant> findByCapacityGreaterThanEqual(Integer capacity, Pageable pageable);

	// 全てのレストランを取得
	Page<Restaurant> findAll(Pageable pageable);

	// カテゴリIDでレストランを検索するメソッドを定義
	Page<Restaurant> findByCategoryId(Integer categoryId, Pageable pageable);

	// カテゴリでレストランを作成日時の降順で検索するメソッド
	Page<Restaurant> findByCategoryOrderByCreatedAtDesc(Category category, Pageable pageable);

	// 名前順で並び替え
	Page<Restaurant> findAllByOrderByNameAsc(Pageable pageable);

	// 予約人数の多い順で並び替え
	Page<Restaurant> findAllByOrderByCapacityDesc(Pageable pageable);

	// 価格の安い順で並び替え
	Page<Restaurant> findAllByOrderByPriceAsc(Pageable pageable);

	// 作成日時の降順で全てのレストランを取得
	Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);
}