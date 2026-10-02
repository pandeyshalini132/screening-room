package com.cinema.screeningroom.showing;

import com.cinema.screeningroom.booking.BookingSeatRepository;
import com.cinema.screeningroom.booking.SeatResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/showings")
public class ShowingController {

	private final ShowingRepository showings;
	private final BookingSeatRepository bookingSeats;

	public ShowingController(ShowingRepository showings, BookingSeatRepository bookingSeats) {
		this.showings = showings;
		this.bookingSeats = bookingSeats;
	}

	@GetMapping
	public List<ShowingResponse> list(
			@RequestParam(required = false) LocalDate date,
			@RequestParam(required = false) Long movieId) {
		LocalDateTime from = date == null ? LocalDateTime.now() : date.atStartOfDay();
		LocalDateTime until = date == null ? null : date.plusDays(1).atStartOfDay();
		return showings.findScheduled(from, until, movieId).stream().map(ShowingResponse::from).toList();
	}

	@GetMapping("/{id}/seats")
	public SeatResponse seats(@PathVariable Long id) {
		Showing showing = showings.findWithMovieById(id)
				.filter(item -> item.getStartsAt().isAfter(LocalDateTime.now()))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Upcoming showing not found"));
		return SeatResponse.from(showing, bookingSeats.findSeatLabelsByShowingId(id));
	}
}
