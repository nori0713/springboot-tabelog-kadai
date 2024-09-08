package com.example.nagoyameshi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Service
public class RestaurantService {
	private final RestaurantRepository restaurantRepository;

	public RestaurantService(RestaurantRepository restaurantRepository) {
		this.restaurantRepository = restaurantRepository;
	}

	// レストランの作成
	@Transactional
	public void create(RestaurantRegisterForm restaurantRegisterForm) {
		Restaurant restaurant = new Restaurant();
		MultipartFile imageFile = restaurantRegisterForm.getImageFile();

		if (!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get("src/main/resources/static/storage/images/" + hashedImageName);
			copyImageFile(imageFile, filePath);
			restaurant.setImageName(hashedImageName);
		}

		restaurant.setName(restaurantRegisterForm.getName());
		restaurant.setDescription(restaurantRegisterForm.getDescription());
		restaurant.setPrice(restaurantRegisterForm.getPrice());
		restaurant.setCategory(restaurantRegisterForm.getCategory()); // カテゴリを設定
		restaurant.setPostalCode(restaurantRegisterForm.getPostalCode());
		restaurant.setAddress(restaurantRegisterForm.getAddress());
		restaurant.setPhoneNumber(restaurantRegisterForm.getPhoneNumber());
		restaurant.setCapacity(restaurantRegisterForm.getCapacity()); // 予約最大人数を設定
		restaurant.setOpeningTime(restaurantRegisterForm.getOpeningTime()); // 開店時間を設定
		restaurant.setClosingTime(restaurantRegisterForm.getClosingTime()); // 閉店時間を設定

		restaurantRepository.save(restaurant);
	}

	// レストランの更新
	@Transactional
	public void update(Integer id, RestaurantEditForm restaurantEditForm) {
		Restaurant restaurant = restaurantRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id:" + id));
		MultipartFile imageFile = restaurantEditForm.getImageFile();

		if (!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get("src/main/resources/static/storage/images/" + hashedImageName);
			copyImageFile(imageFile, filePath);
			restaurant.setImageName(hashedImageName);
		}

		restaurant.setName(restaurantEditForm.getName());
		restaurant.setDescription(restaurantEditForm.getDescription());
		restaurant.setPrice(restaurantEditForm.getPrice());
		restaurant.setCategory(restaurantEditForm.getCategory()); // カテゴリを設定
		restaurant.setPostalCode(restaurantEditForm.getPostalCode());
		restaurant.setAddress(restaurantEditForm.getAddress());
		restaurant.setPhoneNumber(restaurantEditForm.getPhoneNumber());
		restaurant.setCapacity(restaurantEditForm.getCapacity()); // 予約最大人数を設定
		restaurant.setOpeningTime(restaurantEditForm.getOpeningTime()); // 開店時間を設定
		restaurant.setClosingTime(restaurantEditForm.getClosingTime()); // 閉店時間を設定

		restaurantRepository.save(restaurant);
	}

	// 新しいファイル名の生成
	public String generateNewFileName(String fileName) {
		String[] fileNames = fileName.split("\\.");
		for (int i = 0; i < fileNames.length - 1; i++) {
			fileNames[i] = UUID.randomUUID().toString();
		}
		String hashedFileName = String.join(".", fileNames);
		return hashedFileName;
	}

	// 画像ファイルのコピー
	public void copyImageFile(MultipartFile imageFile, Path filePath) {
		try {
			Files.copy(imageFile.getInputStream(), filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// レストランのIDに基づいてレストランを取得し、開店・閉店時間をフォーマット
	@Transactional(readOnly = true)
	public Restaurant getRestaurantWithFormattedTimes(Integer id, Model model) {
		Restaurant restaurant = restaurantRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id:" + id));

		// LocalTimeをフォーマット
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		String formattedOpeningTime = restaurant.getOpeningTime().format(timeFormatter);
		String formattedClosingTime = restaurant.getClosingTime().format(timeFormatter);

		// フォーマット済みの時間をモデルに追加
		model.addAttribute("formattedOpeningTime", formattedOpeningTime);
		model.addAttribute("formattedClosingTime", formattedClosingTime);

		return restaurant;
	}

	// カテゴリでレストランを検索する
	@Transactional(readOnly = true)
	public Page<Restaurant> searchByCategory(String category, Pageable pageable) {
		return restaurantRepository.findByCategoryOrderByCreatedAtDesc(category, pageable);
	}

	// レストランの削除
	@Transactional
	public void delete(Integer id) {
		restaurantRepository.deleteById(id);
	}
}