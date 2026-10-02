package com.cinema.screeningroom.booking;

public class SeatUnavailableException extends RuntimeException {

	public SeatUnavailableException(String message) {
		super(message);
	}
}
