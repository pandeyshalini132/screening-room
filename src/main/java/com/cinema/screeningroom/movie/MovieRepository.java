package com.cinema.screeningroom.movie;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

	List<Movie> findByActiveTrueOrderByTitleAsc();

	Optional<Movie> findByTitle(String title);
}
