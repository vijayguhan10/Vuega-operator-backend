error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/operator_config/OperatorController.java:_empty_/OperatorService#login#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/operator_config/OperatorController.java
empty definition using pc, found symbol in pc: _empty_/OperatorService#login#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1072
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Controller/operator_config/OperatorController.java
text:
```scala
package net.vuega.vuega_backend.Controller.operator_config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.DTO.ResponseDto;
import net.vuega.vuega_backend.DTO.operator_config.LoginRequest;
import net.vuega.vuega_backend.DTO.operator_config.OperatorDTO;
import net.vuega.vuega_backend.Service.operator_config.OperatorService;

@RestController
@RequestMapping("/api/operators")
@RequiredArgsConstructor
public class OperatorController {

    private final OperatorService service;

    // LOGIN — forwards to control panel and returns operator data
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<OperatorDTO>> login(@RequestBody LoginRequest request) {
        OperatorDTO result = service.log@@in(request);
        if (result != null) {
            return ResponseEntity.ok(ResponseDto.success(result));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDto.error(401, "Invalid email or password"));
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/OperatorService#login#