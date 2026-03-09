package net.vuega.vuega_backend.Control_pannel.dto.operatoraction;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.OperatorActionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatorActionDto {

    private Long actionId;

    @NotNull(message = "Operator ID is required")
    private Long operatorId;

    @NotNull(message = "Action is required")
    private OperatorActionType action;

    private String reason;
}