package com.example.nagoyameshi.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.User;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

	// ユーザーごとに予約を取得し、予約日付で降順に並べる
	Page<Reservation> findByUserOrderByReservationDateDesc(User user, Pageable pageable);

	// 検索条件に基づいて予約を取得（レストラン名またはユーザー名で検索）
	Page<Reservation> findByRestaurantNameContainingOrUserNameContaining(String restaurantName, String userName,
			Pageable pageable);

	// 予約日と予約時間を指定して予約を検索
	Page<Reservation> findByReservationDateAndReservationTime(LocalDate reservationDate, LocalTime reservationTime,
			Pageable pageable);
}