package net.vuega.vuega_backend.Control_pannel.dto.licenselimits;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenseLimitsDto {

    private Long limitId;

    @NotNull(message = "License ID is required")
    private Long licenseId;

    @NotNull(message = "Bus limit is required")
    @Min(value = 0, message = "Bus limit cannot be negative")
    private Long busLimit;

    @NotNull(message = "Route limit is required")
    @Min(value = 0, message = "Route limit cannot be negative")
    private Long routeLimit;

    @NotNull(message = "Partner limit is required")
    @Min(value = 0, message = "Partner limit cannot be negative")
    private Long patnerLimit;

    @NotNull(message = "Trip limit is required")
    @Min(value = 0, message = "Trip limit cannot be negative")
    private Long tripLimit;
}
