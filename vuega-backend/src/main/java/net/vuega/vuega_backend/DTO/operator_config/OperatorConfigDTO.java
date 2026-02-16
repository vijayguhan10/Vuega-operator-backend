package net.vuega.vuega_backend.DTO.operator_config;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorConfigDTO {

    private Long operatorId;
    private String licenseKey;
    private LocalDateTime lastChecked;
}
