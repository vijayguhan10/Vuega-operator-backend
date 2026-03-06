package net.vuega.control_plane.dto.operators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.control_plane.util.OperatorStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatorsDto {

    private Long operatorId;
    private String operatorName;
    private String companyName;
    private OperatorStatus status;
}
