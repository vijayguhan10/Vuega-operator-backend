package net.vuega.vuega_backend.Control_pannel.dto.operators;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.OperatorStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatorsDto {

    private Long operatorId;

    @NotBlank(message = "Operator name is required")
    private String operatorName;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotNull(message = "Status is required")
    private OperatorStatus status;
}
