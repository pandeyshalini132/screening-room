package com.cinema.screeningroom.booking;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(SeatUnavailableException.class)
	ResponseEntity<Map<String, Object>> seatUnavailable(SeatUnavailableException exception) {
		return error(HttpStatus.CONFLICT, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<Map<String, Object>> invalidBody(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
				.orElse("Request validation failed");
		return error(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	ResponseEntity<Map<String, Object>> invalidParameter(ConstraintViolationException exception) {
		return error(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(ResponseStatusException.class)
	ResponseEntity<Map<String, Object>> responseStatus(ResponseStatusException exception) {
		return error(HttpStatus.valueOf(exception.getStatusCode().value()), exception.getReason());
	}

	private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(Map.of(
				"timestamp", Instant.now().toString(),
				"status", status.value(),
				"error", status.getReasonPhrase(),
				"message", message == null ? status.getReasonPhrase() : message));
	}
}
