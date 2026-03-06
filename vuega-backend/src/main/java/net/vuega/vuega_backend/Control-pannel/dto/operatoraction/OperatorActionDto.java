package net.vuega.control_plane.dto.operatoraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.control_plane.util.OperatorActionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatorActionDto {

    private Long actionId;
    private Long operatorId;
    private OperatorActionType action;
    private String reason;
}