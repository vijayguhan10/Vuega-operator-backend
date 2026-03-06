package net.vuega.vuega_backend.DTO.seats.seat;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request DTO for bulk seat creation (max 100 seats per batch).
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSeatsInBatchRequest {

    @NotEmpty(message = "seats list must not be empty")
    @Size(max = 100, message = "Cannot create more than 100 seats in a single batch")
    @Valid
    private List<CreateSeatRequest> seats;
}
