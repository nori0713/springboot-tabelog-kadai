package com.example.nagoyameshi.form;

import java.time.LocalTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantEditForm {

	@NotNull
	private Integer id;

	@NotBlank(message = "飲食店名を入力してください。")
	private String name;

	private MultipartFile imageFile;

	@NotBlank(message = "説明を入力してください。")
	private String description;

	@NotNull(message = "料金を入力してください。")
	@Min(value = 1, message = "料金は1円以上に設定してください。")
	private Integer price;

	@NotNull(message = "カテゴリを選択してください。")
	private Integer categoryId; // String型からCategory型に変更

	@NotBlank(message = "郵便番号を入力してください。")
	private String postalCode;

	@NotBlank(message = "住所を入力してください。")
	private String address;

	@NotBlank(message = "電話番号を入力してください。")
	private String phoneNumber;

	@NotNull(message = "予約最大人数を入力してください。")
	@Min(value = 1, message = "予約最大人数は1人以上に設定してください。")
	private Integer capacity;

	// 営業時間の追加
	@NotNull(message = "開店時間を入力してください。")
	private LocalTime openingTime;

	@NotNull(message = "閉店時間を入力してください。")
	private LocalTime closingTime;
}