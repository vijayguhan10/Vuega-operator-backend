error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/OperatorController.java:_empty_/ResponseEntity#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/OperatorController.java
empty definition using pc, found symbol in pc: _empty_/ResponseEntity#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 2660
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/OperatorController.java
text:
```scala
package net.vuega.vuega_backend.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.LoginRequest;
import net.vuega.vuega_backend.DTO.OperatorDTO;
import net.vuega.vuega_backend.DTO.ResponseDto;
import net.vuega.vuega_backend.Service.OperatorService;

@RestController
@RequestMapping("/api/operators")
@RequiredArgsConstructor
public class OperatorController {

    private final OperatorService service;

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<OperatorDTO>> login(@RequestBody LoginRequest request) {
        return service.login(request)
                .map(dto -> ResponseEntity.ok(ResponseDto.success(dto)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseDto.error(401, "Invalid email or password")));
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ResponseDto<OperatorDTO>> create(@RequestBody OperatorDTO dto) {
        OperatorDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.created(created));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<ResponseDto<List<OperatorDTO>>> findAll() {
        return ResponseEntity.ok(ResponseDto.success(service.findAll()));
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<OperatorDTO>> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(dto -> ResponseEntity.ok(ResponseDto.success(dto)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.notFound("Operator not found")));
    }

    // READ BY EMAIL
    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseDto<OperatorDTO>> findByEmail(@PathVariable String email) {
        return service.findByEmail(email)
                .map(dto -> ResponseEntity.ok(ResponseDto.success(dto)))
                .orElse(ResponseEntit@@y.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.notFound("Operator not found")));
    }

    // UPDATE (full)
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<OperatorDTO>> update(@PathVariable Long id, @RequestBody OperatorDTO dto) {
        return service.update(id, dto)
                .map(d -> ResponseEntity.ok(ResponseDto.success(d)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.notFound("Operator not found")));
    }

    // PATCH (partial)
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto<OperatorDTO>> patch(@PathVariable Long id, @RequestBody OperatorDTO dto) {
        return service.patch(id, dto)
                .map(d -> ResponseEntity.ok(ResponseDto.success(d)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.notFound("Operator not found")));
    }

    // DELETE BY ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable Long id) {
        if (service.delete(id)) {
            return ResponseEntity.ok(ResponseDto.success(null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.notFound("Operator not found"));
    }

    // DELETE ALL
    @DeleteMapping
    public ResponseEntity<ResponseDto<Void>> deleteAll() {
        service.deleteAll();
        return ResponseEntity.ok(ResponseDto.success(null));
    }

    // EXISTS BY ID
    @GetMapping("/exists/{id}")
    public ResponseEntity<ResponseDto<Boolean>> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseDto.success(service.existsById(id)));
    }

    // EXISTS BY EMAIL
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<ResponseDto<Boolean>> existsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(ResponseDto.success(service.existsByEmail(email)));
    }

    // COUNT
    @GetMapping("/count")
    public ResponseEntity<ResponseDto<Long>> count() {
        return ResponseEntity.ok(ResponseDto.success(service.count()));
    }

    // VALIDATE — refresh lastChecked
    @PostMapping("/{id}/validate")
    public ResponseEntity<ResponseDto<OperatorDTO>> validate(@PathVariable Long id) {
        return service.validate(id)
                .map(dto -> ResponseEntity.ok(ResponseDto.success(dto)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.notFound("Operator not found")));
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/ResponseEntity#