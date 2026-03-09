package net.vuega.vuega_backend.Control_pannel.dto.licenses;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.LicenseStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicensesDto {

    private Long licenseId;

    @NotNull(message = "Operator ID is required")
    private Long operatorId;

    @NotBlank(message = "License key is required")
    private String licenseKey;

    @NotNull(message = "Start date is required")
    private Date startDate;

    @NotNull(message = "End date is required")
    private Date endDate;

    @NotNull(message = "License status is required")
    private LicenseStatus status;
}
