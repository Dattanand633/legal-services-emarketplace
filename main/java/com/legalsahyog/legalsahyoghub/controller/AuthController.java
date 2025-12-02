package com.legalsahyog.legalsahyoghub.controller;

import com.legalsahyog.legalsahyoghub.dto.LoginRequest;
import com.legalsahyog.legalsahyoghub.dto.LoginResponse;
import com.legalsahyog.legalsahyoghub.dto.ProviderRegistrationRequest;
import com.legalsahyog.legalsahyoghub.dto.UserRegistrationRequest;
import com.legalsahyog.legalsahyoghub.entity.Admin;
import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.entity.User;
import com.legalsahyog.legalsahyoghub.repository.AdminRepository;
import com.legalsahyog.legalsahyoghub.repository.ProviderRepository;
import com.legalsahyog.legalsahyoghub.repository.UserRepository;
import com.legalsahyog.legalsahyoghub.service.AuthService;
import com.legalsahyog.legalsahyoghub.security.JwtUtil;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private ProviderRepository providerRepository;
    @Autowired private AdminRepository adminRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(401).body(
                    new java.util.HashMap<String, Object>() {{
                        put("message", ex.getMessage());
                    }}
            );
        }
    }

    // 🔥 REQUIRED by frontend
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String header) {

        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing token");
        }

        String token = header.substring(7);
        String email = jwtUtil.extractUsername(token);

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) return ResponseEntity.ok(user.get());

        Optional<Provider> provider = providerRepository.findByEmail(email);
        if (provider.isPresent()) return ResponseEntity.ok(provider.get());

        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) return ResponseEntity.ok(admin.get());

        return ResponseEntity.status(404).body("User not found");
    }

    @PostMapping("/register/user")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        User user = authService.registerUser(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register/provider")
    public ResponseEntity<Provider> registerProvider(@Valid @RequestBody ProviderRegistrationRequest request) {
        Provider provider = authService.registerProvider(request);
        return ResponseEntity.ok(provider);
    }
}
