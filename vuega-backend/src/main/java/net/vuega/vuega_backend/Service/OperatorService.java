package net.vuega.vuega_backend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.OperatorDTO;
import net.vuega.vuega_backend.Model.Operator;
import net.vuega.vuega_backend.Repository.OperatorRepository;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final OperatorRepository repository;

    // CREATE
    @Transactional
    public OperatorDTO create(OperatorDTO dto) {
        Operator entity = toEntity(dto);
        return toDTO(repository.save(entity));
    }

    // READ ALL
    @Transactional(readOnly = true)
    public List<OperatorDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    // READ BY ID
    @Transactional(readOnly = true)
    public Optional<OperatorDTO> findById(Long id) {
        return repository.findById(id).map(this::toDTO);
    }

    // READ BY EMAIL
    @Transactional(readOnly = true)
    public Optional<OperatorDTO> findByEmail(String email) {
        return repository.findByServiceEmail(email).map(this::toDTO);
    }

    // UPDATE (full replace)
    @Transactional
    public Optional<OperatorDTO> update(Long id, OperatorDTO dto) {
        return repository.findById(id).map(existing -> {
            existing.setServiceEmail(dto.getServiceEmail());
            existing.setAccessToken(dto.getAccessToken());
            existing.setTokenExpiresAt(dto.getTokenExpiresAt());
            existing.setOperatorName(dto.getOperatorName());
            existing.setOrganizationName(dto.getOrganizationName());
            existing.setLicenseStatus(dto.getLicenseStatus());
            existing.setAccountStatus(dto.getAccountStatus());
            existing.setBusLimit(dto.getBusLimit());
            existing.setRouteLimit(dto.getRouteLimit());
            existing.setLastChecked(dto.getLastChecked());
            return toDTO(repository.save(existing));
        });
    }

    // PATCH (partial update — only non-null fields)
    @Transactional
    public Optional<OperatorDTO> patch(Long id, OperatorDTO dto) {
        return repository.findById(id).map(existing -> {
            if (dto.getServiceEmail() != null)
                existing.setServiceEmail(dto.getServiceEmail());
            if (dto.getAccessToken() != null)
                existing.setAccessToken(dto.getAccessToken());
            if (dto.getTokenExpiresAt() != null)
                existing.setTokenExpiresAt(dto.getTokenExpiresAt());
            if (dto.getOperatorName() != null)
                existing.setOperatorName(dto.getOperatorName());
            if (dto.getOrganizationName() != null)
                existing.setOrganizationName(dto.getOrganizationName());
            if (dto.getLicenseStatus() != null)
                existing.setLicenseStatus(dto.getLicenseStatus());
            if (dto.getAccountStatus() != null)
                existing.setAccountStatus(dto.getAccountStatus());
            if (dto.getBusLimit() != null)
                existing.setBusLimit(dto.getBusLimit());
            if (dto.getRouteLimit() != null)
                existing.setRouteLimit(dto.getRouteLimit());
            if (dto.getLastChecked() != null)
                existing.setLastChecked(dto.getLastChecked());
            return toDTO(repository.save(existing));
        });
    }

    // DELETE
    @Transactional
    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    // DELETE ALL
    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    // EXISTS
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return repository.existsByServiceEmail(email);
    }

    // COUNT
    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

    // VALIDATE — updates lastChecked timestamp
    @Transactional
    public Optional<OperatorDTO> validate(Long id) {
        return repository.findById(id).map(existing -> {
            existing.setLastChecked(LocalDateTime.now());
            return toDTO(repository.save(existing));
        });
    }

    // ── Mapping helpers ──

    private OperatorDTO toDTO(Operator e) {
        return OperatorDTO.builder()
                .operatorId(e.getOperatorId())
                .serviceEmail(e.getServiceEmail())
                .accessToken(e.getAccessToken())
                .tokenExpiresAt(e.getTokenExpiresAt())
                .operatorName(e.getOperatorName())
                .organizationName(e.getOrganizationName())
                .licenseStatus(e.getLicenseStatus())
                .accountStatus(e.getAccountStatus())
                .busLimit(e.getBusLimit())
                .routeLimit(e.getRouteLimit())
                .lastChecked(e.getLastChecked())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private Operator toEntity(OperatorDTO dto) {
        return Operator.builder()
                .serviceEmail(dto.getServiceEmail())
                .accessToken(dto.getAccessToken())
                .tokenExpiresAt(dto.getTokenExpiresAt())
                .operatorName(dto.getOperatorName())
                .organizationName(dto.getOrganizationName())
                .licenseStatus(dto.getLicenseStatus())
                .accountStatus(dto.getAccountStatus())
                .busLimit(dto.getBusLimit())
                .routeLimit(dto.getRouteLimit())
                .lastChecked(dto.getLastChecked())
                .build();
    }
}
