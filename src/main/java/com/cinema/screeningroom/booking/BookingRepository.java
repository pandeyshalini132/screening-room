package com.cinema.screeningroom.booking;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	@EntityGraph(attributePaths = { "showing", "showing.movie", "seats" })
	Optional<Booking> findByReferenceAndEmailIgnoreCase(String reference, String email);
}
