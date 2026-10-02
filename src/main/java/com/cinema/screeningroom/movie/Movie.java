package com.cinema.screeningroom.movie;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "movies")
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 120)
	private String title;

	@Column(nullable = false, length = 1000)
	private String synopsis;

	@Column(nullable = false, length = 60)
	private String genre;

	@Column(name = "runtime_minutes", nullable = false)
	private int runtimeMinutes;

	@Column(nullable = false, length = 16)
	private String rating;

	@Column(name = "poster_url", nullable = false, length = 500)
	private String posterUrl;

	@Column(nullable = false)
	private boolean active;

	protected Movie() {
	}

	public Movie(String title, String synopsis, String genre, int runtimeMinutes, String rating, String posterUrl) {
		this.title = title;
		this.synopsis = synopsis;
		this.genre = genre;
		this.runtimeMinutes = runtimeMinutes;
		this.rating = rating;
		this.posterUrl = posterUrl;
		this.active = true;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public String getGenre() {
		return genre;
	}

	public int getRuntimeMinutes() {
		return runtimeMinutes;
	}

	public String getRating() {
		return rating;
	}

	public String getPosterUrl() {
		return posterUrl;
	}

	public boolean isActive() {
		return active;
	}
}
