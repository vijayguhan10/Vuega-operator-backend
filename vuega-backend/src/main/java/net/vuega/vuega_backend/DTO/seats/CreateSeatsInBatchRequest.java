package net.vuega.vuega_backend.DTO.seats;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
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
