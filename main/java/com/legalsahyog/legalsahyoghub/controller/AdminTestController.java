package com.legalsahyog.legalsahyoghub.controller;

import com.legalsahyog.legalsahyoghub.entity.Admin;
import com.legalsahyog.legalsahyoghub.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Test Controller for Admin Setup and Verification
 * WARNING: Remove or secure this controller in production!
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class AdminTestController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Test endpoint to check if admin exists and verify password
     * DELETE THIS IN PRODUCTION!
     * Access: GET http://localhost:8080/api/test/admin-check
     */
    @GetMapping("/admin-check")
    public ResponseEntity<Map<String, Object>> checkAdmin() {
        System.out.println("=== Admin Check Endpoint Called ===");
        Map<String, Object> response = new HashMap<>();
        
        String email = "admin@legalsahyog.com";
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            response.put("exists", true);
            response.put("email", admin.getEmail());
            response.put("firstName", admin.getFirstName());
            response.put("lastName", admin.getLastName());
            response.put("role", admin.getRole());
            response.put("isActive", admin.getIsActive());
            response.put("hasPassword", admin.getPassword() != null && !admin.getPassword().isEmpty());
            
            // Test password match
            boolean passwordMatches = passwordEncoder.matches("admin123", admin.getPassword());
            response.put("passwordMatches", passwordMatches);
            response.put("passwordHash", admin.getPassword());
        } else {
            response.put("exists", false);
            response.put("message", "Admin account not found");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to create/update admin account
     * DELETE THIS IN PRODUCTION!
     * Access: POST http://localhost:8080/api/test/create-admin
     * Or visit in browser: http://localhost:8080/api/test/create-admin
     */
    @GetMapping("/create-admin")
    @PostMapping("/create-admin")
    public ResponseEntity<Map<String, Object>> createAdmin() {
        System.out.println("=== Create Admin Endpoint Called ===");
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = "admin@legalsahyog.com";
            String password = "admin123";
            
            Optional<Admin> adminOpt = adminRepository.findByEmail(email);
            Admin admin;
            
            if (adminOpt.isPresent()) {
                admin = adminOpt.get();
                response.put("action", "updated");
            } else {
                admin = new Admin();
                response.put("action", "created");
            }
            
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(Admin.Role.SUPER_ADMIN);
            admin.setIsActive(true);
            
            adminRepository.save(admin);
            
            response.put("success", true);
            response.put("email", email);
            response.put("password", password);
            response.put("message", "Admin account " + response.get("action") + " successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(response);
    }
}

