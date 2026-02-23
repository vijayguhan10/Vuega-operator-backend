package net.vuega.vuega_backend.DTO.seats.lock;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

// Request DTO for confirming a seat booking.
@Data
public class BookSeatRequest {
	@NotNull(message="partnerId is required")
	private Long partnerId;
}
