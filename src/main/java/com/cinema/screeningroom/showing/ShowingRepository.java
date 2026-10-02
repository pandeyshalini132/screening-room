package com.cinema.screeningroom.showing;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShowingRepository extends JpaRepository<Showing, Long> {

	@Query("""
			select s from Showing s
			join fetch s.movie m
			where s.startsAt >= :from
			  and (:until is null or s.startsAt < :until)
			  and (:movieId is null or m.id = :movieId)
			order by s.startsAt, m.title
			""")
	List<Showing> findScheduled(
			@Param("from") LocalDateTime from,
			@Param("until") LocalDateTime until,
			@Param("movieId") Long movieId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select s from Showing s join fetch s.movie where s.id = :id")
	Optional<Showing> findByIdForUpdate(@Param("id") Long id);

	@Query("select s from Showing s join fetch s.movie where s.id = :id")
	Optional<Showing> findWithMovieById(@Param("id") Long id);

	boolean existsByStartsAtAfter(LocalDateTime startsAt);
}
