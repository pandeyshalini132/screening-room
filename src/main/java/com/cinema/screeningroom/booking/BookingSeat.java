package com.cinema.screeningroom.booking;

import com.cinema.screeningroom.showing.Showing;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "booking_seats", uniqueConstraints = {
		@UniqueConstraint(name = "uk_showing_seat", columnNames = { "showing_id", "seat_label" })
})
public class BookingSeat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "booking_id", nullable = false)
	private Booking booking;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "showing_id", nullable = false)
	private Showing showing;

	@Column(name = "seat_label", nullable = false, length = 4)
	private String seatLabel;

	@Column(nullable = false, precision = 8, scale = 2)
	private BigDecimal price;

	protected BookingSeat() {
	}

	public BookingSeat(Booking booking, Showing showing, String seatLabel, BigDecimal price) {
		this.booking = booking;
		this.showing = showing;
		this.seatLabel = seatLabel;
		this.price = price;
	}

	public String getSeatLabel() {
		return seatLabel;
	}

	public BigDecimal getPrice() {
		return price;
	}
}
