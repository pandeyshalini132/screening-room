package com.cinema.screeningroom.booking;

import com.cinema.screeningroom.showing.Showing;
import com.cinema.screeningroom.showing.ShowingRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingService {

	private static final Pattern SEAT_LABEL = Pattern.compile("[A-H](?:[1-9]|1[0-2])");

	private final ShowingRepository showings;
	private final BookingSeatRepository bookingSeats;
	private final BookingRepository bookings;

	public BookingService(ShowingRepository showings, BookingSeatRepository bookingSeats, BookingRepository bookings) {
		this.showings = showings;
		this.bookingSeats = bookingSeats;
		this.bookings = bookings;
	}

	@Transactional
	public BookingResponse book(CreateBookingRequest request) {
		Showing showing = showings.findByIdForUpdate(request.showingId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showing not found"));
		if (!showing.getStartsAt().isAfter(LocalDateTime.now())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This showing has already started");
		}

		Set<String> seatLabels = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (String requestedLabel : request.seatLabels()) {
			String label = requestedLabel.trim().toUpperCase(Locale.ROOT);
			if (!SEAT_LABEL.matcher(label).matches()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid seat label: " + requestedLabel);
			}
			if (!seatLabels.add(label)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat labels must not contain duplicates");
			}
		}

		if (bookingSeats.existsByShowingIdAndSeatLabelIn(showing.getId(), List.copyOf(seatLabels))) {
			throw new SeatUnavailableException("One or more selected seats are no longer available");
		}

		BigDecimal total = showing.getTicketPrice().multiply(BigDecimal.valueOf(seatLabels.size()));
		Booking booking = new Booking(newReference(), request.customerName().trim(), request.email().trim(),
				showing, total);
		for (String label : seatLabels) {
			booking.addSeat(new BookingSeat(booking, showing, label, showing.getTicketPrice()));
		}
		return BookingResponse.from(bookings.save(booking));
	}

	@Transactional
	public BookingResponse find(String reference, String email) {
		return BookingResponse.from(findOwned(reference, email));
	}

	@Transactional
	public BookingResponse cancel(String reference, String email) {
		Booking booking = findOwned(reference, email);
		if (booking.getStatus() == BookingStatus.CANCELLED) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is already cancelled");
		}
		if (!booking.getShowing().getStartsAt().isAfter(LocalDateTime.now())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "A started showing cannot be cancelled");
		}
		booking.cancel();
		return BookingResponse.from(bookings.save(booking));
	}

	private Booking findOwned(String reference, String email) {
		return bookings.findByReferenceAndEmailIgnoreCase(reference.trim(), email.trim())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
	}

	private String newReference() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT);
	}
}
