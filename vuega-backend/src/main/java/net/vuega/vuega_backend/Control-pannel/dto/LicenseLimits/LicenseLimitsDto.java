package net.vuega.control_plane.dto.licenselimits;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenseLimitsDto {

    private Long limitId;
    private Long licenseId;
    private Long busLimit;
    private Long routeLimit;
    private Long patnerLimit;
    private Long tripLimit;
}
