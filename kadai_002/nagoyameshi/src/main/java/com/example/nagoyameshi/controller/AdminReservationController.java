package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Reservation;
import com.example.nagoyameshi.service.ReservationService;

@Controller
@RequestMapping("/admin/reservations")
public class AdminReservationController {

	private final ReservationService reservationService;

	public AdminReservationController(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@GetMapping
	public String index(Model model,
			@PageableDefault(page = 0, size = 10, sort = "reservationDate", direction = Direction.DESC) Pageable pageable,
			@RequestParam(name = "keyword", required = false) String keyword) {

		Page<Reservation> reservationPage;

		if (keyword != null && !keyword.isEmpty()) {
			// キーワードがある場合は検索結果を表示
			reservationPage = reservationService.searchReservations(keyword, pageable);
		} else {
			// キーワードがない場合はすべての予約を表示
			reservationPage = reservationService.getAllReservations(pageable);
		}

		model.addAttribute("reservationPage", reservationPage);
		model.addAttribute("keyword", keyword);

		return "admin/reservations/index";
	}
}