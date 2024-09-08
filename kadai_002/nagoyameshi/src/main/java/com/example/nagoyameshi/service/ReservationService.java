package com.example.nagoyameshi.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReservationRegisterForm;
import com.example.nagoyameshi.repository.ReservationRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;

	public ReservationService(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository,
			UserRepository userRepository) {
		this.reservationRepository = reservationRepository;
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public void create(ReservationRegisterForm reservationRegisterForm) {
		Reservation reservation = new Reservation();
		Restaurant restaurant = restaurantRepository.getReferenceById(reservationRegisterForm.getRestaurantId());
		User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
		LocalDate reservationDate = LocalDate.parse(reservationRegisterForm.getReservationDate());
		LocalTime reservationTime = LocalTime.parse(reservationRegisterForm.getReservationTime());

		// 営業時間内での予約かを確認
		if (!isWithinBusinessHours(reservationTime, restaurant.getOpeningTime(), restaurant.getClosingTime())) {
			throw new IllegalArgumentException("予約時間は営業時間内に設定してください。");
		}

		reservation.setRestaurant(restaurant);
		reservation.setUser(user);
		reservation.setReservationDate(reservationDate);
		reservation.setReservationTime(reservationTime); // 予約時間を設定
		reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());

		reservationRepository.save(reservation);
	}

	// 営業時間内に予約時間が収まっているかチェック
	public boolean isWithinBusinessHours(LocalTime reservationTime, LocalTime openingTime, LocalTime closingTime) {
		return !reservationTime.isBefore(openingTime) && !reservationTime.isAfter(closingTime);
	}

	public boolean isWithinCapacity(Integer numberOfPeople, Integer capacity) {
		return numberOfPeople <= capacity;
	}

	// 予約一覧を取得
	public Page<Reservation> getAllReservations(Pageable pageable) {
		return reservationRepository.findAll(pageable);
	}

	// キーワードによる予約検索（レストラン名またはユーザー名）
	public Page<Reservation> searchReservations(String keyword, Pageable pageable) {
		return reservationRepository.findByRestaurantNameContainingOrUserNameContaining(keyword, keyword, pageable);
	}

	// ユーザーごとの予約リストを取得
	public Page<Reservation> getReservationsForUser(User user, Pageable pageable) {
		return reservationRepository.findByUserOrderByReservationDateDesc(user, pageable);
	}
}