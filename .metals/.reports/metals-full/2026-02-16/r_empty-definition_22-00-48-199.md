error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/operator_config/OperatorConfigService.java:_empty_/`<any>`#lastChecked#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/operator_config/OperatorConfigService.java
empty definition using pc, found symbol in pc: _empty_/`<any>`#lastChecked#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 3730
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/operator_config/OperatorConfigService.java
text:
```scala
package net.vuega.vuega_backend.Service.operator_config;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.operator_config.OperatorConfigDTO;
import net.vuega.vuega_backend.Model.operator_config.OperatorConfig;
import net.vuega.vuega_backend.Repository.OperatorConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperatorConfigService {

    private final OperatorConfigRepository repository;

    // CREATE
    @Transactional
    public OperatorConfigDTO create(OperatorConfigDTO dto) {
        OperatorConfig entity = OperatorConfig.builder()
                .licenseKey(dto.getLicenseKey())
                .lastChecked(dto.getLastChecked() != null ? dto.getLastChecked() : LocalDateTime.now())
                .build();
        OperatorConfig saved = repository.save(entity);
        return toDTO(saved);
    }

    // READ ALL
    public List<OperatorConfigDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // READ BY ID
    public Optional<OperatorConfigDTO> findById(Long id) {
        return repository.findById(id).map(this::toDTO);
    }

    // READ BY LICENSE KEY
    public Optional<OperatorConfigDTO> findByLicenseKey(String licenseKey) {
        return repository.findByLicenseKey(licenseKey).map(this::toDTO);
    }

    // UPDATE
    @Transactional
    public Optional<OperatorConfigDTO> update(Long id, OperatorConfigDTO dto) {
        return repository.findById(id).map(existing -> {
            existing.setLicenseKey(dto.getLicenseKey());
            existing.setLastChecked(dto.getLastChecked() != null ? dto.getLastChecked() : LocalDateTime.now());
            return toDTO(repository.save(existing));
        });
    }

    // PATCH (partial update)
    @Transactional
    public Optional<OperatorConfigDTO> patch(Long id, OperatorConfigDTO dto) {
        return repository.findById(id).map(existing -> {
            if (dto.getLicenseKey() != null) {
                existing.setLicenseKey(dto.getLicenseKey());
            }
            if (dto.getLastChecked() != null) {
                existing.setLastChecked(dto.getLastChecked());
            }
            return toDTO(repository.save(existing));
        });
    }

    // DELETE BY ID
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

    // CHECK EXISTS BY ID
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    // CHECK EXISTS BY LICENSE KEY
    public boolean existsByLicenseKey(String licenseKey) {
        return repository.existsByLicenseKey(licenseKey);
    }

    // COUNT
    public long count() {
        return repository.count();
    }

    // UPDATE LAST CHECKED
    @Transactional
    public Optional<OperatorConfigDTO> updateLastChecked(Long id) {
        return repository.findById(id).map(existing -> {
            existing.setLastChecked(LocalDateTime.now());
            return toDTO(repository.save(existing));
        });
    }

    // HELPER: Entity to DTO
    private OperatorConfigDTO toDTO(OperatorConfig entity) {
        return OperatorConfigDTO.builder()
               
                .licenseKey(entity.getLicenseKey())
                .lastChe@@cked(entity.getLastChecked())
                .build();
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/`<any>`#lastChecked#