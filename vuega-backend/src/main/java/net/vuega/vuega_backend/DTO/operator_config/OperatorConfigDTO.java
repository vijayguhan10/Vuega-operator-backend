package net.vuega.vuega_backend.DTO.operator_config;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorConfigDTO {

    private Long operatorId;
    private String licenseKey;
    private LocalDateTime lastChecked;
}
