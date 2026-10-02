package com.cinema.screeningroom.booking;

import com.cinema.screeningroom.showing.Showing;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SeatResponse(
		Long showingId,
		String movieTitle,
		String theatre,
		String screenName,
		LocalDateTime startsAt,
		BigDecimal ticketPrice,
		List<Seat> seats) {

	public static SeatResponse from(Showing showing, List<String> reservedLabels) {
		List<Seat> seats = java.util.stream.IntStream.rangeClosed('A', 'H')
				.boxed()
				.flatMap(row -> java.util.stream.IntStream.rangeClosed(1, 12)
						.mapToObj(number -> new Seat((char) row.intValue() + String.valueOf(number),
								reservedLabels.contains((char) row.intValue() + String.valueOf(number)))))
				.toList();
		return new SeatResponse(showing.getId(), showing.getMovie().getTitle(), showing.getTheatre(),
				showing.getScreenName(), showing.getStartsAt(), showing.getTicketPrice(), seats);
	}

	public record Seat(String label, boolean reserved) {
	}
}
