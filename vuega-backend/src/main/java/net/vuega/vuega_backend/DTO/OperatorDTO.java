package net.vuega.vuega_backend.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorDTO {

    private Long operatorId;
    private String serviceEmail;
    private String accessToken;
    private LocalDateTime tokenExpiresAt;
    private String operatorName;
    private String organizationName;
    private String licenseStatus;
    private String accountStatus;
    private Integer busLimit;
    private Integer routeLimit;
    private LocalDateTime lastChecked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
