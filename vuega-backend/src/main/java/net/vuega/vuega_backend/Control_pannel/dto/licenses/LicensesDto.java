package net.vuega.vuega_backend.Control_pannel.dto.licenses;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.LicenseStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicensesDto {

    private Long licenseId;
    private Long operatorId;
    private String licenseKey;
    private Date startDate;
    private Date endDate;
    private LicenseStatus status;
}
