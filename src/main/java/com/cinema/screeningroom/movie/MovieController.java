package com.cinema.screeningroom.movie;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

	private final MovieRepository movies;

	public MovieController(MovieRepository movies) {
		this.movies = movies;
	}

	@GetMapping
	public List<MovieResponse> list(
			@RequestParam(required = false) String q,
			@RequestParam(required = false) String genre) {
		String query = q == null ? "" : q.trim().toLowerCase();
		String selectedGenre = genre == null ? "" : genre.trim();
		return movies.findByActiveTrueOrderByTitleAsc().stream()
				.filter(movie -> selectedGenre.isEmpty() || movie.getGenre().equalsIgnoreCase(selectedGenre))
				.filter(movie -> query.isEmpty()
						|| movie.getTitle().toLowerCase().contains(query)
						|| movie.getSynopsis().toLowerCase().contains(query))
				.map(MovieResponse::from)
				.toList();
	}
}
