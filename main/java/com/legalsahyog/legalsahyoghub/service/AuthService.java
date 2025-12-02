package com.legalsahyog.legalsahyoghub.service;

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
import com.legalsahyog.legalsahyoghub.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    public User registerUser(UserRegistrationRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setPincode(request.getPincode());

        return userRepository.save(user);
    }


    public Provider registerProvider(ProviderRegistrationRequest request) {

        if (providerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (providerRepository.existsByBarCouncilNumber(request.getBarCouncilNumber())) {
            throw new RuntimeException("Bar Council Number already exists");
        }

        Provider provider = new Provider();
        provider.setEmail(request.getEmail());
        provider.setPassword(passwordEncoder.encode(request.getPassword()));
        provider.setFirstName(request.getFirstName());
        provider.setLastName(request.getLastName());
        provider.setPhone(request.getPhone());
        provider.setBarCouncilNumber(request.getBarCouncilNumber());
        provider.setPracticeArea(request.getPracticeArea());
        provider.setExperienceYears(request.getExperienceYears());
        provider.setQualification(request.getQualification());
        provider.setBio(request.getBio());
        provider.setAddress(request.getAddress());
        provider.setCity(request.getCity());
        provider.setState(request.getState());
        provider.setPincode(request.getPincode());

        return providerRepository.save(provider);
    }



    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail();
        String password = request.getPassword();

        logger.info("Login attempt for email: {}", email);

        // 1️⃣ Admin Login - Check FIRST
        Optional<Admin> admin = adminRepository.findByEmail(email);
        logger.debug("Admin lookup for email '{}': {}", email, admin.isPresent() ? "Found" : "Not found");
        
        if (admin.isPresent()) {
            Admin a = admin.get();
            logger.debug("Admin found - ID: {}, Email: {}, Active: {}, Role: {}", 
                        a.getId(), a.getEmail(), a.getIsActive(), a.getRole());

            // Check if admin is active
            if (a.getIsActive() == null || !a.getIsActive()) {
                logger.warn("Attempted login with inactive admin account: {}", email);
                throw new RuntimeException("Admin account is inactive. Please contact system administrator.");
            }

            // Verify password
            boolean passwordMatches = passwordEncoder.matches(password, a.getPassword());
            logger.debug("Password match result for admin '{}': {}", email, passwordMatches);
            
            if (!passwordMatches) {
                logger.warn("Invalid password attempt for admin: {} (Password hash length: {})", 
                           email, a.getPassword() != null ? a.getPassword().length() : 0);
                throw new RuntimeException("Invalid email or password");
            }

            logger.info("✓ Admin login successful: {} (ID: {}, Role: {})", email, a.getId(), a.getRole());
            String token = jwtUtil.generateToken(a.getEmail(), "ROLE_ADMIN", a.getId());

            return new LoginResponse(
                    token,
                    "ROLE_ADMIN",
                    a.getId(),
                    a.getEmail(),
                    a.getFirstName(),
                    a.getLastName()
            );
        }

        // 2️⃣ Provider Login
        Optional<Provider> provider = providerRepository.findByEmail(email);
        if (provider.isPresent()) {
            Provider p = provider.get();

            if (!passwordEncoder.matches(password, p.getPassword())) {
                throw new RuntimeException("Invalid email or password");
            }

            String token = jwtUtil.generateToken(p.getEmail(), "ROLE_PROVIDER", p.getId());

            return new LoginResponse(
                    token,
                    "ROLE_PROVIDER",
                    p.getId(),
                    p.getEmail(),
                    p.getFirstName(),
                    p.getLastName()
            );
        }

        // 3️⃣ User Login
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User u = user.get();

            if (!passwordEncoder.matches(password, u.getPassword())) {
                throw new RuntimeException("Invalid email or password");
            }

            String token = jwtUtil.generateToken(u.getEmail(), "ROLE_USER", u.getId());

            return new LoginResponse(
                    token,
                    "ROLE_USER",
                    u.getId(),
                    u.getEmail(),
                    u.getFirstName(),
                    u.getLastName()
            );
        }

        logger.warn("Login attempt failed - user not found: {}", email);
        throw new RuntimeException("Invalid email or password");
    }


}
