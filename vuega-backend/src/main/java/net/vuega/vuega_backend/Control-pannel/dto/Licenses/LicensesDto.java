package net.vuega.control_plane.dto.licenses;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.control_plane.util.LicenseStatus;

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
