package net.vuega.vuega_backend.Control_pannel.dto.operators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.OperatorStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatorsDto {

    private Long operatorId;
    private String operatorName;
    private String companyName;
    private OperatorStatus status;
}
