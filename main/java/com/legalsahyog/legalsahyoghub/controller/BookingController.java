package com.legalsahyog.legalsahyoghub.controller;

import com.legalsahyog.legalsahyoghub.entity.Booking;
import com.legalsahyog.legalsahyoghub.entity.User;
import com.legalsahyog.legalsahyoghub.repository.BookingRepository;
import com.legalsahyog.legalsahyoghub.repository.UserRepository;
import com.legalsahyog.legalsahyoghub.security.JwtUtil;
import com.legalsahyog.legalsahyoghub.service.BookingService;
import com.legalsahyog.legalsahyoghub.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ProviderService providerService;

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
    
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable @NonNull Long id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user")
    public ResponseEntity<List<Booking>> getUserBookings(HttpServletRequest request) {
        Optional<User> userOpt = getCurrentUser(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        User user = userOpt.get();
        List<Booking> bookings = bookingRepository.findByUser(user);
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/provider")
    public ResponseEntity<List<Booking>> getProviderBookings(HttpServletRequest request) {
        try {
            // Get provider from JWT token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }

            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            
            // Get provider by email
            Optional<com.legalsahyog.legalsahyoghub.entity.Provider> providerOpt = 
                providerService.getProviderByEmail(email);
            
            if (providerOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }

            com.legalsahyog.legalsahyoghub.entity.Provider provider = providerOpt.get();
            List<Booking> bookings = bookingRepository.findByProvider(provider);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    @GetMapping("/available-slots/{providerId}")
    public ResponseEntity<List<LocalTime>> getAvailableTimeSlots(
            @PathVariable Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<LocalTime> slots = bookingService.getAvailableTimeSlots(providerId, date);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    


    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> bookingData, HttpServletRequest request) {
        // get current user from JWT
        Optional<User> currentUserOpt = getCurrentUser(request);
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized: user not found in token"));
        }
        Long userId = currentUserOpt.get().getId();

        try {
            Long providerId = bookingData.get("providerId") != null ? Long.valueOf(bookingData.get("providerId").toString()) : null;
            Long serviceId = bookingData.get("serviceId") != null ? Long.valueOf(bookingData.get("serviceId").toString()) : null;
            LocalDate bookingDate = bookingData.get("bookingDate") != null ? LocalDate.parse(bookingData.get("bookingDate").toString()) : null;
            LocalTime startTime = bookingData.get("startTime") != null ? LocalTime.parse(bookingData.get("startTime").toString()) : null;
            LocalTime endTime = bookingData.get("endTime") != null ? LocalTime.parse(bookingData.get("endTime").toString()) : null;
            String notes = bookingData.get("notes") != null ? bookingData.get("notes").toString() : "";

            if (providerId == null) return ResponseEntity.badRequest().body(Map.of("message", "Missing or invalid 'providerId'"));
            if (serviceId == null) return ResponseEntity.badRequest().body(Map.of("message", "Missing or invalid 'serviceId'. Please select a service."));
            if (bookingDate == null) return ResponseEntity.badRequest().body(Map.of("message", "Missing or invalid 'bookingDate'"));
            if (startTime == null) return ResponseEntity.badRequest().body(Map.of("message", "Missing or invalid 'startTime'"));

            Booking booking = bookingService.createBooking(userId, providerId, serviceId, bookingDate, startTime, endTime, notes);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException ie) {
            return ResponseEntity.badRequest().body(Map.of("message", ie.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to create booking: " + e.getMessage()));
        }
    }


    @PutMapping("/{id}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable @NonNull Long id) {
        try {
            Booking booking = bookingService.confirmBooking(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/complete")
    public ResponseEntity<Booking> completeBooking(@PathVariable @NonNull Long id) {
        try {
            Booking booking = bookingService.completeBooking(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable @NonNull Long id) {
        try {
            Booking booking = bookingService.cancelBooking(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(
            @PathVariable @NonNull Long id, 
            @RequestBody Map<String, String> statusData) {
        try {
            Booking.BookingStatus status = Booking.BookingStatus.valueOf(statusData.get("status"));
            Booking booking = bookingService.updateBookingStatus(id, status);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable @NonNull Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

