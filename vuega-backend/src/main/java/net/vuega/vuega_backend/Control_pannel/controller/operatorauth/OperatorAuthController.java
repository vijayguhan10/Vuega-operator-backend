package net.vuega.vuega_backend.Control_pannel.controller.operatorauth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.dto.common.ApiResponse;
import net.vuega.vuega_backend.Control_pannel.dto.operatorauth.AuthResponse;
import net.vuega.vuega_backend.Control_pannel.dto.operatorauth.Login;
import net.vuega.vuega_backend.Control_pannel.dto.operatorauth.Register;
import net.vuega.vuega_backend.Control_pannel.service.operatorauth.OperatorAuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OperatorAuthController {

    private final OperatorAuthService operatorAuthService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody Register request) {
        operatorAuthService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "User registered successfully",
                        null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody Login request) {
        AuthResponse response = operatorAuthService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Login successful",
                response));
    }
}