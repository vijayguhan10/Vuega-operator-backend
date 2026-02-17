error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/operatorConfig/OperatorConfigController.java:_empty_/`<any>`#body#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/operatorConfig/OperatorConfigController.java
empty definition using pc, found symbol in pc: _empty_/`<any>`#body#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 825
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/operatorConfig/OperatorConfigController.java
text:
```scala
package net.vuega.vuega_backend.Controller.OperatorConfig;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.operator_config.OperatorConfigDTO;
import net.vuega.vuega_backend.Service.operator_config.OperatorConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operator-config")
@RequiredArgsConstructor
public class OperatorConfigController {

    private final OperatorConfigService service;

    // CREATE
    @PostMapping
    public ResponseEntity<OperatorConfigDTO> create(@RequestBody OperatorConfigDTO dto) {
        OperatorConfigDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body@@(created);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<OperatorConfigDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<OperatorConfigDTO> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // READ BY LICENSE KEY
    @GetMapping("/license/{licenseKey}")
    public ResponseEntity<OperatorConfigDTO> findByLicenseKey(@PathVariable String licenseKey) {
        return service.findByLicenseKey(licenseKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE (full)
    @PutMapping("/{id}")
    public ResponseEntity<OperatorConfigDTO> update(@PathVariable Long id, @RequestBody OperatorConfigDTO dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PATCH (partial)
    @PatchMapping("/{id}")
    public ResponseEntity<OperatorConfigDTO> patch(@PathVariable Long id, @RequestBody OperatorConfigDTO dto) {
        return service.patch(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE BY ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE ALL
    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        service.deleteAll();
        return ResponseEntity.noContent().build();
    }

    // CHECK EXISTS BY ID
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(service.existsById(id));
    }

    // CHECK EXISTS BY LICENSE KEY
    @GetMapping("/exists/license/{licenseKey}")
    public ResponseEntity<Boolean> existsByLicenseKey(@PathVariable String licenseKey) {
        return ResponseEntity.ok(service.existsByLicenseKey(licenseKey));
    }

    // COUNT
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.count());
    }

    // UPDATE LAST CHECKED (validate license)
    @PostMapping("/{id}/validate")
    public ResponseEntity<OperatorConfigDTO> updateLastChecked(@PathVariable Long id) {
        return service.updateLastChecked(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/`<any>`#body#