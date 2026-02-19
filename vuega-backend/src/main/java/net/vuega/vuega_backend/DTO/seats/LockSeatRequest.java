package net.vuega.vuega_backend.DTO.seats;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LockSeatRequest {

    /**
     * Caller-supplied identity (e.g. session ID, user ID).
     * Used to verify ownership on unlock/book.
     */
    @NotBlank(message = "lockedBy (session or user ID) is required")
    @Size(max = 100, message = "lockedBy must be at most 100 characters")
    private String lockedBy;

    /** Boarding stop order (inclusive). Must be < toStopOrder. */
    @NotNull(message = "fromStopOrder is required")
    @Min(value = 0, message = "fromStopOrder must be >= 0")
    private Integer fromStopOrder;

    /** Drop stop order (inclusive). Must be > fromStopOrder. */
    @NotNull(message = "toStopOrder is required")
    @Min(value = 1, message = "toStopOrder must be >= 1")
    private Integer toStopOrder;
}
