package com.example.nagoyameshi.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationRegisterForm {
	private Integer restaurantId;
	private Integer userId;
	private String reservationDate;
	private String reservationTime; // 予約時間を追加
	private Integer numberOfPeople;
}