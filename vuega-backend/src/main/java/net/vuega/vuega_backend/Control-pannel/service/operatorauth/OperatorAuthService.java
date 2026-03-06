package net.vuega.control_plane.service.operatorauth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.vuega.control_plane.dto.operatorauth.AuthResponse;
import net.vuega.control_plane.dto.operatorauth.Login;
import net.vuega.control_plane.dto.operatorauth.Register;
import net.vuega.control_plane.model.operatorauth.OperatorAuth;
import net.vuega.control_plane.repository.licenses.LicenseRepository;
import net.vuega.control_plane.repository.operatorauth.OperatorAuthRepository;
import net.vuega.control_plane.repository.operators.OperatorRepository;

@Service
@RequiredArgsConstructor
public class OperatorAuthService {

    private final OperatorAuthRepository repository;
    private final OperatorRepository operatorRepository;
    private final LicenseRepository licenseRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ================= REGISTER =================
    public void register(Register request) {

        // Validate operator exists
        if (!operatorRepository.existsById(request.getOperatorId())) {
            throw new IllegalArgumentException("Operator ID does not exist");
        }

        // Validate license exists (try as ID first, then as key)
        boolean licenseExists = false;
        try {
            long licenseId = Long.parseLong(request.getLicenceId());
            licenseExists = licenseRepository.existsById(licenseId);
        } catch (NumberFormatException e) {
            // If not a number, check as license key
            licenseExists = licenseRepository.existsByLicenseKey(request.getLicenceId());
        }

        if (!licenseExists) {
            throw new IllegalArgumentException("License ID does not exist");
        }

        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (repository.existsByLicenceId(request.getLicenceId())) {
            throw new IllegalArgumentException("Licence ID already exists");
        }

        OperatorAuth user = new OperatorAuth();
        user.setOperatorId(request.getOperatorId());
        user.setLicenceId(request.getLicenceId());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        repository.save(user);
    }

    // ================= LOGIN =================
    public AuthResponse login(Login request) {

        OperatorAuth user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return new AuthResponse(
                String.valueOf(user.getOperatorId()),
                user.getEmail(),
                user.getRole().name());
    }
}