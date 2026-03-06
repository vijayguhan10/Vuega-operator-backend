package net.vuega.control_plane.controller.licenses;

import java.util.List;

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
import net.vuega.control_plane.dto.licenses.LicensesDto;
import net.vuega.control_plane.service.licenses.LicenseService;

@RestController
@RequestMapping("/api/licenses")
public class LicenseController {

    private final LicenseService licenseService;

    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<LicensesDto>>> getAllLicenses() {
        List<LicensesDto> licenses = licenseService.getAllLicenses();
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Licenses fetched successfully",
                licenses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LicensesDto>> getLicenseById(@PathVariable Long id) {
        LicensesDto dto = licenseService.getLicenseById(id)
                .orElseThrow(() -> new RuntimeException("License not found"));
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "License fetched successfully",
                dto));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<LicensesDto>> createLicense(@RequestBody LicensesDto dto) {
        LicensesDto created = licenseService.createLicense(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "License created successfully",
                        created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LicensesDto>> updateLicense(@PathVariable Long id, @RequestBody LicensesDto dto) {
        LicensesDto updated = licenseService.updateLicense(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "License updated successfully",
                updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLicense(@PathVariable Long id) {
        licenseService.deleteLicense(id);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "License deleted successfully",
                null));
    }

}
