package com.example.nagoyameshi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Service
public class RestaurantService {

	private final RestaurantRepository restaurantRepository;
	private final CategoryRepository categoryRepository;

	// ローカルで画像を保存するディレクトリを設定（固定のパス）
	private final String imageStoragePath = "src/main/resources/static/storage/images";

	public RestaurantService(RestaurantRepository restaurantRepository, CategoryRepository categoryRepository) {
		this.restaurantRepository = restaurantRepository;
		this.categoryRepository = categoryRepository;
	}

	// レストランの作成
	@Transactional
	public void create(RestaurantRegisterForm restaurantRegisterForm) throws IOException {
		Restaurant restaurant = new Restaurant();
		MultipartFile imageFile = restaurantRegisterForm.getImageFile();

		// カテゴリの設定
		Category category = categoryRepository.findById(restaurantRegisterForm.getCategoryId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid category Id"));

		// 画像の処理（ローカルパスを使用）
		if (!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get(imageStoragePath, hashedImageName); // ローカルパスを使用
			copyImageFile(imageFile, filePath);
			restaurant.setImageName(hashedImageName);
		}

		// レストランの基本情報を設定
		restaurant.setName(restaurantRegisterForm.getName());
		restaurant.setDescription(restaurantRegisterForm.getDescription());
		restaurant.setPrice(restaurantRegisterForm.getPrice());
		restaurant.setCategory(category);
		restaurant.setPostalCode(restaurantRegisterForm.getPostalCode());
		restaurant.setAddress(restaurantRegisterForm.getAddress());
		restaurant.setPhoneNumber(restaurantRegisterForm.getPhoneNumber());
		restaurant.setCapacity(restaurantRegisterForm.getCapacity());
		restaurant.setOpeningTime(restaurantRegisterForm.getOpeningTime());
		restaurant.setClosingTime(restaurantRegisterForm.getClosingTime());

		restaurantRepository.save(restaurant);
	}

	// レストランの更新
	@Transactional
	public void update(Integer id, RestaurantEditForm restaurantEditForm) throws IOException {
		Restaurant restaurant = restaurantRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id:" + id));
		MultipartFile imageFile = restaurantEditForm.getImageFile();

		// カテゴリの設定
		Category category = categoryRepository.findById(restaurantEditForm.getCategoryId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid category Id"));

		// 画像の処理（ローカルパスを使用）
		if (!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get(imageStoragePath, hashedImageName); // ローカルパスを使用
			copyImageFile(imageFile, filePath);
			restaurant.setImageName(hashedImageName);
		}

		// レストランの基本情報を設定
		restaurant.setName(restaurantEditForm.getName());
		restaurant.setDescription(restaurantEditForm.getDescription());
		restaurant.setPrice(restaurantEditForm.getPrice());
		restaurant.setCategory(category);
		restaurant.setPostalCode(restaurantEditForm.getPostalCode());
		restaurant.setAddress(restaurantEditForm.getAddress());
		restaurant.setPhoneNumber(restaurantEditForm.getPhoneNumber());
		restaurant.setCapacity(restaurantEditForm.getCapacity());
		restaurant.setOpeningTime(restaurantEditForm.getOpeningTime());
		restaurant.setClosingTime(restaurantEditForm.getClosingTime());

		restaurantRepository.save(restaurant);
	}

	// 新しいファイル名の生成
	public String generateNewFileName(String fileName) {
		String extension = fileName.substring(fileName.lastIndexOf("."));
		return UUID.randomUUID().toString() + extension;
	}

	// 画像ファイルのコピー
	public void copyImageFile(MultipartFile imageFile, Path filePath) throws IOException {
		// 既存のファイルがある場合は上書きする
		Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
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
	public Page<Restaurant> searchByCategory(Integer categoryId, Pageable pageable) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid category Id"));
		return restaurantRepository.findByCategoryOrderByCreatedAtDesc(category, pageable);
	}

	// レストランの削除
	@Transactional
	public void delete(Integer id) {
		restaurantRepository.deleteById(id);
	}
}