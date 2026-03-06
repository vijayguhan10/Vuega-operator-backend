package net.vuega.control_plane.dto.expansionrequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.control_plane.util.ExpansionRequestFor;
import net.vuega.control_plane.util.ExpansionRequestStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpansionRequestDto {

    private Long requestId;

    @NotNull(message = "Operator ID is required")
    private Long operatorId;

    @NotNull(message = "Request type is required")
    private ExpansionRequestFor requestFor;

    @NotNull(message = "Count is required")
    @Min(value = 1, message = "Count must be greater than 0")
    private Integer count;

    private ExpansionRequestStatus status;
}