package com.legalsahyog.legalsahyoghub.config;

import com.legalsahyog.legalsahyoghub.entity.Admin;
import com.legalsahyog.legalsahyoghub.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Automatically creates admin account on application startup if it doesn't exist
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            String adminEmail = "admin@legalsahyog.com";
            String adminPassword = "admin123";
            
            logger.info("=== AdminInitializer: Starting admin account setup ===");
            
            // Check if admin already exists
            Optional<Admin> existingAdmin = adminRepository.findByEmail(adminEmail);
            
            if (existingAdmin.isEmpty()) {
                logger.info("Admin account not found. Creating new admin account...");
                
                Admin admin = new Admin();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setRole(Admin.Role.SUPER_ADMIN);
                admin.setIsActive(true);
                
                Admin savedAdmin = adminRepository.save(admin);
                
                logger.info("✓ Default admin account created successfully!");
                logger.info("  Email: {}", adminEmail);
                logger.info("  Password: {}", adminPassword);
                logger.info("  ID: {}", savedAdmin.getId());
                logger.info("  Role: {}", savedAdmin.getRole());
            } else {
                Admin admin = existingAdmin.get();
                logger.info("Admin account found. Verifying and updating if needed...");
                logger.info("  Current ID: {}", admin.getId());
                logger.info("  Current Role: {}", admin.getRole());
                logger.info("  Current Active Status: {}", admin.getIsActive());
                
                boolean needsUpdate = false;
                
                // Check and update password
                if (!passwordEncoder.matches(adminPassword, admin.getPassword())) {
                    logger.info("  Password doesn't match. Updating password...");
                    admin.setPassword(passwordEncoder.encode(adminPassword));
                    needsUpdate = true;
                } else {
                    logger.info("  Password is correct.");
                }
                
                // Ensure admin is active
                if (admin.getIsActive() == null || !admin.getIsActive()) {
                    logger.info("  Admin is inactive. Activating...");
                    admin.setIsActive(true);
                    needsUpdate = true;
                } else {
                    logger.info("  Admin is active.");
                }
                
                // Ensure role is SUPER_ADMIN
                if (admin.getRole() != Admin.Role.SUPER_ADMIN) {
                    logger.info("  Role is not SUPER_ADMIN. Updating...");
                    admin.setRole(Admin.Role.SUPER_ADMIN);
                    needsUpdate = true;
                }
                
                if (needsUpdate) {
                    adminRepository.save(admin);
                    logger.info("✓ Admin account updated successfully!");
                } else {
                    logger.info("✓ Admin account is already correctly configured!");
                }
                
                logger.info("  Email: {}", adminEmail);
                logger.info("  Password: {}", adminPassword);
            }
            
            logger.info("=== AdminInitializer: Completed ===");
            
        } catch (Exception e) {
            logger.error("ERROR in AdminInitializer: Failed to setup admin account", e);
            e.printStackTrace();
        }
    }
}

