package net.vuega.control_plane.controller.licenselimits;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.vuega.control_plane.dto.common.ApiResponse;
import net.vuega.control_plane.dto.licenselimits.LicenseLimitsDto;
import net.vuega.control_plane.service.licenselimits.LicenseLimitsService;

@RestController
@RequestMapping("/api/license-limits")
public class LicenseLimitsController {

    private final LicenseLimitsService licenseLimitsService;

    public LicenseLimitsController(LicenseLimitsService licenseLimitsService) {
        this.licenseLimitsService = licenseLimitsService;
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<LicenseLimitsDto>> createLicenseLimits(@RequestBody LicenseLimitsDto dto) {
        LicenseLimitsDto created = licenseLimitsService.createLicenseLimits(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "License limits created successfully",
                        created));
    }

    @GetMapping("/{licenseId}")
    public ResponseEntity<ApiResponse<LicenseLimitsDto>> getLicenseLimitsByLicenseId(@PathVariable Long licenseId) {
        LicenseLimitsDto dto = licenseLimitsService.getLicenseLimitsByLicenseId(licenseId)
                .orElseThrow(() -> new RuntimeException("License limits not found"));
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "License limits fetched successfully",
                dto));
    }

    @PutMapping("/{licenseId}")
    public ResponseEntity<ApiResponse<LicenseLimitsDto>> updateLicenseLimitsByLicenseId(
            @PathVariable Long licenseId,
            @RequestBody LicenseLimitsDto dto) {
        LicenseLimitsDto updated = licenseLimitsService.updateLicenseLimitsByLicenseId(licenseId, dto);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "License limits updated successfully",
                updated));
    }

    @DeleteMapping("/{licenseId}")
    public ResponseEntity<ApiResponse<Void>> deleteLicenseLimitsByLicenseId(@PathVariable Long licenseId) {
        licenseLimitsService.deleteLicenseLimitsByLicenseId(licenseId);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "License limits deleted successfully",
                null));
    }

}
