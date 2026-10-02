package com.cinema.screeningroom.booking;

import com.cinema.screeningroom.showing.Showing;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 16)
	private String reference;

	@Column(name = "customer_name", nullable = false, length = 120)
	private String customerName;

	@Column(nullable = false, length = 254)
	private String email;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "showing_id", nullable = false)
	private Showing showing;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private BookingStatus status;

	@Column(nullable = false, precision = 9, scale = 2)
	private BigDecimal total;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BookingSeat> seats = new ArrayList<>();

	protected Booking() {
	}

	public Booking(String reference, String customerName, String email, Showing showing, BigDecimal total) {
		this.reference = reference;
		this.customerName = customerName;
		this.email = email;
		this.showing = showing;
		this.total = total;
		this.status = BookingStatus.CONFIRMED;
		this.createdAt = LocalDateTime.now();
	}

	public void addSeat(BookingSeat seat) {
		seats.add(seat);
	}

	public void cancel() {
		this.status = BookingStatus.CANCELLED;
		seats.clear();
	}

	public Long getId() {
		return id;
	}

	public String getReference() {
		return reference;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getEmail() {
		return email;
	}

	public Showing getShowing() {
		return showing;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public List<BookingSeat> getSeats() {
		return seats;
	}
}
