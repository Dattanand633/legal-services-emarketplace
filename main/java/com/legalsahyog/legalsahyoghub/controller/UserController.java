package com.legalsahyog.legalsahyoghub.controller;

import com.legalsahyog.legalsahyoghub.entity.Booking;
import com.legalsahyog.legalsahyoghub.entity.Payment;
import com.legalsahyog.legalsahyoghub.entity.User;
import com.legalsahyog.legalsahyoghub.entity.UserSubscription;
import com.legalsahyog.legalsahyoghub.repository.BookingRepository;
import com.legalsahyog.legalsahyoghub.repository.PaymentRepository;
import com.legalsahyog.legalsahyoghub.repository.UserRepository;
import com.legalsahyog.legalsahyoghub.repository.UserSubscriptionRepository;
import com.legalsahyog.legalsahyoghub.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserSubscriptionRepository subscriptionRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Helper method to get current user from JWT token
     */
    private Optional<User> getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Get user dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(HttpServletRequest request) {
        Optional<User> userOpt = getCurrentUser(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        User user = userOpt.get();
        Map<String, Object> stats = new HashMap<>();

        // Get all bookings for this user
        List<Booking> bookings = bookingRepository.findByUser(user);
        long totalBookings = bookings.size();
        long completedSessions = bookings.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == Booking.BookingStatus.COMPLETED)
                .count();

        // Get active subscriptions
        List<UserSubscription> subscriptions = subscriptionRepository.findByUser(user).stream()
                .filter(s -> s.getIsActive() != null && s.getIsActive())
                .filter(s -> s.getEndDate() != null && !s.getEndDate().isBefore(java.time.LocalDate.now()))
                .collect(Collectors.toList());
        long activeSubscriptions = subscriptions.size();

        // Get total spent from successful payments
        List<Payment> payments = paymentRepository.findByUser(user);
        BigDecimal totalSpent = payments.stream()
                .filter(p -> p.getPaymentStatus() != null && p.getPaymentStatus() == Payment.PaymentStatus.SUCCESS)
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("totalBookings", totalBookings);
        stats.put("completedSessions", completedSessions);
        stats.put("activeSubscriptions", activeSubscriptions);
        stats.put("totalSpent", totalSpent);

        return ResponseEntity.ok(stats);
    }

    /**
     * Get user bookings
     */
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getUserBookings(HttpServletRequest request) {
        Optional<User> userOpt = getCurrentUser(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        User user = userOpt.get();
        List<Booking> bookings = bookingRepository.findByUser(user);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get user payments
     */
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getUserPayments(HttpServletRequest request) {
        Optional<User> userOpt = getCurrentUser(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        User user = userOpt.get();
        List<Payment> payments = paymentRepository.findByUser(user);
        return ResponseEntity.ok(payments);
    }

    /**
     * Get user subscriptions
     */
    @GetMapping("/subscriptions")
    public ResponseEntity<List<UserSubscription>> getUserSubscriptions(HttpServletRequest request) {
        Optional<User> userOpt = getCurrentUser(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        User user = userOpt.get();
        List<UserSubscription> subscriptions = subscriptionRepository.findByUser(user);
        return ResponseEntity.ok(subscriptions);
    }

    /**
     * Get user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(HttpServletRequest request) {
        Optional<User> userOpt = getCurrentUser(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        User user = userOpt.get();
        user.setPassword(null); // Remove password for security
        return ResponseEntity.ok(user);
    }
}

