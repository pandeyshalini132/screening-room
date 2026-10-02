package com.cinema.screeningroom.showing;

import com.cinema.screeningroom.movie.Movie;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "showings")
public class Showing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "movie_id", nullable = false)
	private Movie movie;

	@Column(nullable = false, length = 120)
	private String theatre;

	@Column(name = "screen_name", nullable = false, length = 40)
	private String screenName;

	@Column(name = "starts_at", nullable = false)
	private LocalDateTime startsAt;

	@Column(name = "ticket_price", nullable = false, precision = 8, scale = 2)
	private BigDecimal ticketPrice;

	@Version
	private long version;

	protected Showing() {
	}

	public Showing(Movie movie, String theatre, String screenName, LocalDateTime startsAt, BigDecimal ticketPrice) {
		this.movie = movie;
		this.theatre = theatre;
		this.screenName = screenName;
		this.startsAt = startsAt;
		this.ticketPrice = ticketPrice;
	}

	public Long getId() {
		return id;
	}

	public Movie getMovie() {
		return movie;
	}

	public String getTheatre() {
		return theatre;
	}

	public String getScreenName() {
		return screenName;
	}

	public LocalDateTime getStartsAt() {
		return startsAt;
	}

	public BigDecimal getTicketPrice() {
		return ticketPrice;
	}
}
