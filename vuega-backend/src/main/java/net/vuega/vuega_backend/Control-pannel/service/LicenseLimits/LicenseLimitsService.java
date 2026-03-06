package net.vuega.control_plane.service.licenselimits;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.vuega.control_plane.dto.licenselimits.LicenseLimitsDto;
import net.vuega.control_plane.model.licenselimits.LicenseLimits;
import net.vuega.control_plane.repository.licenselimits.LicenseLimitsRepository;
import net.vuega.control_plane.repository.licenses.LicenseRepository;

@Service
public class LicenseLimitsService {

    private final LicenseLimitsRepository licenseLimitsRepository;
    private final LicenseRepository licenseRepository;

    public LicenseLimitsService(
            LicenseLimitsRepository licenseLimitsRepository,
            LicenseRepository licenseRepository) {
        this.licenseLimitsRepository = licenseLimitsRepository;
        this.licenseRepository = licenseRepository;
    }

    @Transactional
    public LicenseLimitsDto createLicenseLimits(LicenseLimitsDto dto) {
        validateForCreate(dto);

        if (!licenseRepository.existsById(dto.getLicenseId())) {
            throw new IllegalArgumentException("License not found with ID: " + dto.getLicenseId());
        }

        LicenseLimits entity = convertToEntity(dto);
        LicenseLimits saved = licenseLimitsRepository.save(entity);
        return convertToDto(saved);
    }

    @Transactional
    public Optional<LicenseLimitsDto> getLicenseLimitsByLicenseId(Long licenseId) {
        if (licenseId == null) {
            throw new IllegalArgumentException("Invalid license ID");
        }

        return Optional.ofNullable(licenseLimitsRepository.findByLicenseId(licenseId))
                .map(this::convertToDto);
    }

    @Transactional
    public LicenseLimitsDto updateLicenseLimitsByLicenseId(Long licenseId, LicenseLimitsDto dto) {
        if (licenseId == null) {
            throw new IllegalArgumentException("Invalid license ID");
        }

        validateForUpdate(dto);

        LicenseLimits existing = licenseLimitsRepository.findByLicenseId(licenseId);
        if (existing == null) {
            throw new IllegalArgumentException("License limits not found for license ID: " + licenseId);
        }

        existing.setBusLimit(dto.getBusLimit());
        existing.setRouteLimit(dto.getRouteLimit());
        existing.setPatnerLimit(dto.getPatnerLimit());
        existing.setTripLimit(dto.getTripLimit());

        LicenseLimits updated = licenseLimitsRepository.save(existing);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteLicenseLimitsByLicenseId(Long licenseId) {
        if (licenseId == null) {
            throw new IllegalArgumentException("Invalid license ID");
        }

        LicenseLimits existing = licenseLimitsRepository.findByLicenseId(licenseId);
        if (existing == null) {
            throw new IllegalArgumentException("License limits not found for license ID: " + licenseId);
        }

        licenseLimitsRepository.delete(existing);
    }

    private LicenseLimitsDto convertToDto(LicenseLimits entity) {
        return new LicenseLimitsDto(
                entity.getLimitId(),
                entity.getLicenseId(),
                entity.getBusLimit(),
                entity.getRouteLimit(),
                entity.getPatnerLimit(),
                entity.getTripLimit());
    }

    private LicenseLimits convertToEntity(LicenseLimitsDto dto) {
        LicenseLimits entity = new LicenseLimits();
        entity.setLimitId(dto.getLimitId());
        entity.setLicenseId(dto.getLicenseId());
        entity.setBusLimit(dto.getBusLimit());
        entity.setRouteLimit(dto.getRouteLimit());
        entity.setPatnerLimit(dto.getPatnerLimit());
        entity.setTripLimit(dto.getTripLimit());
        return entity;
    }

    private void validateForCreate(LicenseLimitsDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("License limits cannot be null");
        }
        if (dto.getLicenseId() == null || dto.getLicenseId() <= 0) {
            throw new IllegalArgumentException("License ID is required");
        }
        validateLimits(dto);
    }

    private void validateForUpdate(LicenseLimitsDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("License limits cannot be null");
        }
        validateLimits(dto);
    }

    private void validateLimits(LicenseLimitsDto dto) {
        if (dto.getBusLimit() == null || dto.getBusLimit() < 0) {
            throw new IllegalArgumentException("Bus limit cannot be negative");
        }
        if (dto.getPatnerLimit() == null || dto.getPatnerLimit() < 0) {
            throw new IllegalArgumentException("Device limit cannot be negative");
        }
        if (dto.getRouteLimit() == null || dto.getRouteLimit() < 0) {
            throw new IllegalArgumentException("Route limit cannot be negative");
        }
        if (dto.getTripLimit() == null || dto.getTripLimit() < 0) {
            throw new IllegalArgumentException("Trip limit cannot be negative");
        }
    }
}
