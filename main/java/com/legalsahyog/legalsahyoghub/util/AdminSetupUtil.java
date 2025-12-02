package com.legalsahyog.legalsahyoghub.util;

import com.legalsahyog.legalsahyoghub.entity.Admin;
import com.legalsahyog.legalsahyoghub.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Utility class to setup admin account with correct password hash
 * Run this once to create/update the admin account
 */
@Component
public class AdminSetupUtil {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Creates or updates the admin account with password "admin123"
     * Call this method once during application startup or manually
     */
    public void setupAdmin() {
        String email = "admin@legalsahyog.com";
        String password = "admin123";
        
        // Generate BCrypt hash for the password
        String hashedPassword = passwordEncoder.encode(password);
        
        System.out.println("Generated password hash for 'admin123': " + hashedPassword);
        
        // Check if admin exists
        Admin admin = adminRepository.findByEmail(email).orElse(new Admin());
        
        // Set admin properties
        admin.setEmail(email);
        admin.setPassword(hashedPassword);
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(Admin.Role.SUPER_ADMIN);
        admin.setIsActive(true);
        
        // Save admin
        adminRepository.save(admin);
        
        System.out.println("Admin account created/updated successfully!");
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hashedPassword);
    }
}

