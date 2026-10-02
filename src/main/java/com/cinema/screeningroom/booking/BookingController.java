package com.cinema.screeningroom.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

	private final BookingService bookings;

	public BookingController(BookingService bookings) {
		this.bookings = bookings;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookingResponse book(@Valid @RequestBody CreateBookingRequest request) {
		return bookings.book(request);
	}

	@GetMapping("/{reference}")
	public BookingResponse get(
			@PathVariable String reference,
			@RequestParam @NotBlank @Email String email) {
		return bookings.find(reference, email);
	}

	@DeleteMapping("/{reference}")
	public BookingResponse cancel(
			@PathVariable String reference,
			@RequestParam @NotBlank @Email String email) {
		return bookings.cancel(reference, email);
	}
}
