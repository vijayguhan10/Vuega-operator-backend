package net.vuega.vuega_backend.DTO.bookings;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.vuega.vuega_backend.Model.passengers.Gender;

@Data
public class PassengerRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "age is required")
    @Min(value = 0, message = "age must be >= 0")
    private Integer age;

    @NotNull(message = "gender is required")
    private Gender gender;
}
