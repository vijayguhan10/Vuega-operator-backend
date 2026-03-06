package net.vuega.vuega_backend.Operator_pannel.DTO.passengers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Operator_pannel.Model.passengers.Gender;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerDTO {

    private Long passengerId;
    private String name;
    private Integer age;
    private Gender gender;
}
