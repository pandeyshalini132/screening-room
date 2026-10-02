package com.cinema.screeningroom.movie;

public record MovieResponse(
		Long id,
		String title,
		String synopsis,
		String genre,
		int runtimeMinutes,
		String rating,
		String posterUrl) {

	static MovieResponse from(Movie movie) {
		return new MovieResponse(movie.getId(), movie.getTitle(), movie.getSynopsis(), movie.getGenre(),
				movie.getRuntimeMinutes(), movie.getRating(), movie.getPosterUrl());
	}
}
