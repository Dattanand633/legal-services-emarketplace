package com.legalsahyog.legalsahyoghub.controller;

import com.legalsahyog.legalsahyoghub.entity.*;
import com.legalsahyog.legalsahyoghub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AdminRepository adminRepository;

    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        // Remove password from response for security
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    /**
     * Get all providers
     */
    @GetMapping("/providers")
    public ResponseEntity<List<Provider>> getAllProviders() {
        List<Provider> providers = providerRepository.findAll();
        // Remove password from response for security
        providers.forEach(provider -> provider.setPassword(null));
        return ResponseEntity.ok(providers);
    }

    /**
     * Get all bookings
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get all payments
     */
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return ResponseEntity.ok(payments);
    }

    /**
     * Get all reviews
     */
    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total users
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findByIsActiveTrue().size();

        // Total providers
        long totalProviders = providerRepository.count();
        long activeProviders = providerRepository.findByIsActiveTrue().size();
        long verifiedProviders = providerRepository.findByVerificationStatus(Provider.VerificationStatus.VERIFIED).size();
        long pendingProviders = providerRepository.findByVerificationStatus(Provider.VerificationStatus.PENDING).size();

        // Total bookings
        long totalBookings = bookingRepository.count();
        List<Booking> allBookings = bookingRepository.findAll();
        long completedBookings = allBookings.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == Booking.BookingStatus.COMPLETED)
                .count();
        long pendingBookings = allBookings.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == Booking.BookingStatus.PENDING)
                .count();
        long confirmedBookings = allBookings.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .count();

        // Payments and revenue
        long totalPayments = paymentRepository.count();
        List<Payment> allPayments = paymentRepository.findAll();
        BigDecimal platformRevenue = allPayments.stream()
                .filter(p -> p.getPaymentStatus() != null && p.getPaymentStatus() == Payment.PaymentStatus.SUCCESS)
                .map(p -> p.getPlatformFee() != null ? p.getPlatformFee() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalRevenue = allPayments.stream()
                .filter(p -> p.getPaymentStatus() != null && p.getPaymentStatus() == Payment.PaymentStatus.SUCCESS)
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Reviews
        long totalReviews = reviewRepository.count();
        List<Review> allReviews = reviewRepository.findAll();
        double averageRating = allReviews.stream()
                .filter(r -> r.getRating() != null)
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("totalProviders", totalProviders);
        stats.put("activeProviders", activeProviders);
        stats.put("verifiedProviders", verifiedProviders);
        stats.put("pendingProviders", pendingProviders);
        stats.put("totalBookings", totalBookings);
        stats.put("completedBookings", completedBookings);
        stats.put("pendingBookings", pendingBookings);
        stats.put("confirmedBookings", confirmedBookings);
        stats.put("totalPayments", totalPayments);
        stats.put("platformRevenue", platformRevenue);
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalReviews", totalReviews);
        stats.put("averageRating", averageRating);

        return ResponseEntity.ok(stats);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setPassword(null); // Remove password
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get provider by ID
     */
    @GetMapping("/providers/{id}")
    public ResponseEntity<Provider> getProviderById(@PathVariable Long id) {
        return providerRepository.findById(id)
                .map(provider -> {
                    provider.setPassword(null); // Remove password
                    return ResponseEntity.ok(provider);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update user status
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setIsActive(request.get("isActive"));
                    User updated = userRepository.save(user);
                    updated.setPassword(null);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update provider verification status
     */
    @PutMapping("/providers/{id}/verification")
    public ResponseEntity<Provider> updateProviderVerification(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        return providerRepository.findById(id)
                .map(provider -> {
                    String status = request.get("status");
                    if (status != null) {
                        try {
                            provider.setVerificationStatus(Provider.VerificationStatus.valueOf(status));
                            if (status.equals("VERIFIED")) {
                                provider.setIsActive(true);
                            }
                        } catch (IllegalArgumentException e) {
                            // Invalid status
                        }
                    }
                    Provider updated = providerRepository.save(provider);
                    updated.setPassword(null);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update provider active status
     */
    @PutMapping("/providers/{id}/status")
    public ResponseEntity<Provider> updateProviderStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        return providerRepository.findById(id)
                .map(provider -> {
                    provider.setIsActive(request.get("isActive"));
                    Provider updated = providerRepository.save(provider);
                    updated.setPassword(null);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get pending approvals (providers waiting for verification)
     */
    @GetMapping("/pending-approvals")
    public ResponseEntity<List<Provider>> getPendingApprovals() {
        List<Provider> pending = providerRepository.findByVerificationStatus(Provider.VerificationStatus.PENDING);
        pending.forEach(p -> p.setPassword(null));
        return ResponseEntity.ok(pending);
    }

    /**
     * Get recent bookings
     */
    @GetMapping("/recent-bookings")
    public ResponseEntity<List<Booking>> getRecentBookings(@RequestParam(defaultValue = "10") int limit) {
        List<Booking> bookings = bookingRepository.findAll().stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(limit)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookings);
    }
}

