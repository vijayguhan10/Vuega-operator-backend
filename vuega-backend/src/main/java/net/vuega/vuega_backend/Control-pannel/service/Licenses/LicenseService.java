package net.vuega.control_plane.service.licenses;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.vuega.control_plane.dto.licenses.LicensesDto;
import net.vuega.control_plane.model.licenses.Licenses;
import net.vuega.control_plane.repository.licenses.LicenseRepository;
import net.vuega.control_plane.repository.operators.OperatorRepository;
import net.vuega.control_plane.util.LicenseStatus;

@Service
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final OperatorRepository operatorRepository;

    public LicenseService(
            LicenseRepository licenseRepository,
            OperatorRepository operatorRepository) {
        this.licenseRepository = licenseRepository;
        this.operatorRepository = operatorRepository;
    }

    @Transactional
    public List<LicensesDto> getAllLicenses() {
        return licenseRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<LicensesDto> getLicenseById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid license ID");
        }

        return licenseRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional
    public LicensesDto createLicense(LicensesDto dto) {
        validate(dto);

        if (!operatorRepository.existsById(dto.getOperatorId())) {
            throw new IllegalArgumentException("Operator not found with ID: " + dto.getOperatorId());
        }
        if (dto.getLicenseId() != null && licenseRepository.existsByLicenseId(
                dto.getLicenseId(),
                dto.getLicenseKey())) {
            throw new IllegalArgumentException("License already exists");
        }

        Licenses entity = convertToEntity(dto);
        Licenses saved = licenseRepository.save(entity);
        return convertToDto(saved);
    }

    @Transactional
    public LicensesDto updateLicense(Long id, LicensesDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid license ID");
        }

        validate(dto);

        Licenses existing = licenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("License not found with ID: " + id));

        // existing.setOperatorId(dto.getOperatorId());
        existing.setLicenseKey(dto.getLicenseKey());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setStatus(dto.getStatus());

        Licenses updated = licenseRepository.save(existing);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteLicense(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Invalid license ID");
        }

        Licenses existing = licenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("License not found with ID: " + id));

        if (existing.getStatus() == LicenseStatus.INACTIVE) {
            throw new IllegalStateException("License already inactive");
        }

        existing.setStatus(LicenseStatus.INACTIVE);
        licenseRepository.save(existing);
    }

    private LicensesDto convertToDto(Licenses entity) {
        return new LicensesDto(
                entity.getLicenseId(),
                entity.getOperatorId(),
                entity.getLicenseKey(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus());
    }

    private Licenses convertToEntity(LicensesDto dto) {
        Licenses entity = new Licenses();
        if (dto.getLicenseId() != null) {
            entity.setLicenseId(dto.getLicenseId());
        }
        entity.setOperatorId(dto.getOperatorId());
        entity.setLicenseKey(dto.getLicenseKey());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    private void validate(LicensesDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("License cannot be null");
        }
        if (dto.getLicenseKey() == null || dto.getLicenseKey().isBlank()) {
            throw new IllegalArgumentException("License key is required");
        }

        if (dto.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }
        if (dto.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (dto.getStatus() == null) {
            throw new IllegalArgumentException("License status is required");
        }
    }

}
