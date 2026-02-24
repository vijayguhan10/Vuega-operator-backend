package net.vuega.vuega_backend.DTO.passengers;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.vuega.vuega_backend.Model.passengers.Gender;

@Data
public class CreatePassengerRequest {

    @NotNull
    private Long bookingId;

    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    private Integer age;

    @NotNull
    private Gender gender;
}
