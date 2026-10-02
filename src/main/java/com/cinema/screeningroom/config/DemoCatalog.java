package com.cinema.screeningroom.config;

import com.cinema.screeningroom.movie.Movie;
import com.cinema.screeningroom.movie.MovieRepository;
import com.cinema.screeningroom.showing.Showing;
import com.cinema.screeningroom.showing.ShowingRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DemoCatalog implements CommandLineRunner {

	private final MovieRepository movies;
	private final ShowingRepository showings;

	public DemoCatalog(MovieRepository movies, ShowingRepository showings) {
		this.movies = movies;
		this.showings = showings;
	}

	@Override
	@Transactional
	public void run(String... args) {
		if (showings.existsByStartsAtAfter(LocalDateTime.now())) {
			return;
		}

		List<Movie> catalogue = List.of(
				new Movie("The Last Postcard",
						"A photographer returns to her coastal hometown and finds a stack of letters that change everything.",
						"Drama", 112, "PG-13",
						"https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1000&q=85"),
				new Movie("Orbit of Us",
						"Two astronauts on opposite sides of a mission discover the one thing the universe cannot measure.",
						"Sci-Fi", 128, "PG-13",
						"https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=1000&q=85"),
				new Movie("A Very Good Sunday",
						"An over-planner and a charmingly chaotic chef get one day to prove opposites can make a perfect recipe.",
						"Romance", 104, "PG",
						"https://images.unsplash.com/photo-1478720568477-152d9b164e26?auto=format&fit=crop&w=1000&q=85"),
				new Movie("The Quiet Floor",
						"On the night shift of a nearly empty hotel, a new receptionist begins receiving calls from a room that does not exist.",
						"Thriller", 116, "PG-13",
						"https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=1000&q=85"),
				new Movie("Paper Moon Parade",
						"A young inventor and a fearless paper crane set off on a hand-drawn adventure across a world of forgotten stories.",
						"Animation", 96, "G",
						"https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1000&q=85"),
				new Movie("Miles Between",
						"After years apart, three siblings take the long road home and finally talk about what sent them away.",
						"Adventure", 121, "PG-13",
						"https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1000&q=85"));

		List<Movie> savedMovies = movies.findAll().isEmpty() ? movies.saveAll(catalogue) : movies.findAll();
		LocalDate firstDay = LocalDate.now().plusDays(1);
		List<Showing> schedule = new java.util.ArrayList<>();
		for (int day = 0; day < 7; day++) {
			LocalDate date = firstDay.plusDays(day);
			for (int index = 0; index < savedMovies.size(); index++) {
				int group = index / 3;
				int screen = index % 3 + 1;
				LocalDateTime startsAt = date.atTime(group == 0 ? 14 : 19, 0);
				BigDecimal price = index == 1 || index == 5
						? new BigDecimal("300.00")
						: new BigDecimal("250.00");
				schedule.add(new Showing(savedMovies.get(index), "The Screening Room",
						"Screen " + screen, startsAt, price));
			}
		}
		showings.saveAll(schedule);
	}
}
