package net.vuega.vuega_backend.Control_pannel.dto.operatoraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.OperatorActionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatorActionDto {

    private Long actionId;
    private Long operatorId;
    private OperatorActionType action;
    private String reason;
}