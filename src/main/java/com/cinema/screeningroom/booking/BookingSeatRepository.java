package com.cinema.screeningroom.booking;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

	@Query("select seat.seatLabel from BookingSeat seat where seat.showing.id = :showingId")
	List<String> findSeatLabelsByShowingId(@Param("showingId") Long showingId);

	boolean existsByShowingIdAndSeatLabelIn(Long showingId, List<String> seatLabels);
}
