package com.cinema.screeningroom.booking;

import com.cinema.screeningroom.showing.Showing;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
		String reference,
		String status,
		String customerName,
		String email,
		Long showingId,
		String movieTitle,
		String theatre,
		String screenName,
		LocalDateTime startsAt,
		List<String> seatLabels,
		BigDecimal total,
		LocalDateTime createdAt) {

	static BookingResponse from(Booking booking) {
		Showing showing = booking.getShowing();
		return new BookingResponse(
				booking.getReference(),
				booking.getStatus().name(),
				booking.getCustomerName(),
				booking.getEmail(),
				showing.getId(),
				showing.getMovie().getTitle(),
				showing.getTheatre(),
				showing.getScreenName(),
				showing.getStartsAt(),
				booking.getSeats().stream().map(BookingSeat::getSeatLabel).sorted().toList(),
				booking.getTotal(),
				booking.getCreatedAt());
	}
}
