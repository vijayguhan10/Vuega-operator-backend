package net.vuega.vuega_backend.Operator_pannel.Controller.operatorconfig;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.Operator_pannel.DTO.ResponseDto;
import net.vuega.vuega_backend.Operator_pannel.DTO.operatorconfig.LoginRequest;
import net.vuega.vuega_backend.Operator_pannel.DTO.operatorconfig.OperatorDTO;
import net.vuega.vuega_backend.Operator_pannel.Service.operatorconfig.OperatorService;

@RestController
@RequestMapping("/api/operators")
@RequiredArgsConstructor
public class OperatorController {

    private final OperatorService service;

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<OperatorDTO>> login(@RequestBody LoginRequest request) {
        OperatorDTO result = service.login(request);
        if (result != null) {
            return ResponseEntity.ok(ResponseDto.success(result));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDto.error(401, "Invalid email or password"));
    }
}
