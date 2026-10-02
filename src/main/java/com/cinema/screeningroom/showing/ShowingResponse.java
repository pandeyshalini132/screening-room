package com.cinema.screeningroom.showing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShowingResponse(
		Long id,
		Long movieId,
		String movieTitle,
		String theatre,
		String screenName,
		LocalDateTime startsAt,
		BigDecimal ticketPrice) {

	static ShowingResponse from(Showing showing) {
		return new ShowingResponse(showing.getId(), showing.getMovie().getId(), showing.getMovie().getTitle(),
				showing.getTheatre(), showing.getScreenName(), showing.getStartsAt(), showing.getTicketPrice());
	}
}
