package com.cinema.screeningroom.booking;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateBookingRequest(
		@NotBlank @Size(max = 120) String customerName,
		@NotBlank @Email @Size(max = 254) String email,
		@NotNull @Positive Long showingId,
		@NotEmpty @Size(max = 8) List<@NotBlank @Size(max = 4) String> seatLabels) {
}
