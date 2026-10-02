package com.cinema.screeningroom;

import com.cinema.screeningroom.booking.BookingResponse;
import com.cinema.screeningroom.booking.BookingService;
import com.cinema.screeningroom.booking.CreateBookingRequest;
import com.cinema.screeningroom.booking.SeatUnavailableException;
import com.cinema.screeningroom.movie.MovieRepository;
import com.cinema.screeningroom.showing.ShowingRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ScreeningRoomApplicationTests {

	@Autowired
	private MovieRepository movies;

	@Autowired
	private ShowingRepository showings;

	@Autowired
	private BookingService bookings;

	@Test
	void seedsMoviesAndFutureShowtimes() {
		Assertions.assertEquals(6, movies.findByActiveTrueOrderByTitleAsc().size());
		var scheduled = showings.findScheduled(java.time.LocalDateTime.now(), null, null);
		Assertions.assertFalse(scheduled.isEmpty());
		Assertions.assertTrue(scheduled.stream().allMatch(showing ->
				showing.getTicketPrice().equals(new java.math.BigDecimal("250.00"))
						|| showing.getTicketPrice().equals(new java.math.BigDecimal("300.00"))));
	}

	@Test
	void bookingReservesSeatsAndCancellationReleasesThem() {
		var showing = showings.findScheduled(java.time.LocalDateTime.now(), null, null).get(0);
		var request = new CreateBookingRequest("Taylor Example", "taylor@example.com", showing.getId(),
				List.of("C5", "C6"));

		BookingResponse confirmed = bookings.book(request);

		Assertions.assertEquals("CONFIRMED", confirmed.status());
		Assertions.assertEquals(List.of("C5", "C6"), confirmed.seatLabels());
		Assertions.assertEquals(showing.getTicketPrice().multiply(java.math.BigDecimal.valueOf(2)), confirmed.total());
		Assertions.assertThrows(SeatUnavailableException.class, () -> bookings.book(request));

		BookingResponse cancelled = bookings.cancel(confirmed.reference(), "TAYLOR@example.com");

		Assertions.assertEquals("CANCELLED", cancelled.status());
		Assertions.assertTrue(cancelled.seatLabels().isEmpty());
		Assertions.assertEquals(List.of("C5", "C6"), bookings.book(request).seatLabels());
	}

	@Test
	void duplicateSeatLabelsAreRejected() {
		var showing = showings.findScheduled(java.time.LocalDateTime.now(), null, null).get(0);
		var request = new CreateBookingRequest("Taylor Example", "taylor@example.com", showing.getId(),
				List.of("C5", "c5"));

		Assertions.assertThrows(org.springframework.web.server.ResponseStatusException.class,
				() -> bookings.book(request));
	}
}
