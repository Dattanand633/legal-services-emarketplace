package com.legalsahyog.legalsahyoghub.controller;

import com.legalsahyog.legalsahyoghub.entity.Review;
import com.legalsahyog.legalsahyoghub.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable @NonNull Long id) {
        return reviewService.getReviewById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Review>> getReviewsByProvider(@PathVariable Long providerId) {
        // In a real implementation, you would get the provider by ID
        // For now, return empty list
        return ResponseEntity.ok(List.of());
    }
    
    @GetMapping("/user")
    public ResponseEntity<List<Review>> getUserReviews() {
        // In a real implementation, you would get the user from authentication
        // For now, return empty list
        return ResponseEntity.ok(List.of());
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<Review>> getRecentReviews(@RequestParam(defaultValue = "10") int limit) {
        List<Review> reviews = reviewService.getRecentReviews(limit);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/top-rated")
    public ResponseEntity<List<Review>> getTopRatedReviews(@RequestParam(defaultValue = "10") int limit) {
        List<Review> reviews = reviewService.getTopRatedReviews(limit);
        return ResponseEntity.ok(reviews);
    }
    
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Map<String, Object> reviewData) {
        try {
            Long bookingId = Long.valueOf(reviewData.get("bookingId").toString());
            Integer rating = Integer.valueOf(reviewData.get("rating").toString());
            String reviewText = reviewData.get("reviewText") != null ? 
                reviewData.get("reviewText").toString() : "";
            Boolean isPublic = reviewData.get("isPublic") != null ? 
                Boolean.valueOf(reviewData.get("isPublic").toString()) : true;
            
            // Validate required parameters
            if (bookingId == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Review review = reviewService.createReview(bookingId, rating, reviewText, isPublic);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(
            @PathVariable @NonNull Long id,
            @RequestBody Map<String, Object> reviewData) {
        try {
            Integer rating = Integer.valueOf(reviewData.get("rating").toString());
            String reviewText = reviewData.get("reviewText") != null ? 
                reviewData.get("reviewText").toString() : "";
            Boolean isPublic = reviewData.get("isPublic") != null ? 
                Boolean.valueOf(reviewData.get("isPublic").toString()) : true;
            
            Review review = reviewService.updateReview(id, rating, reviewText, isPublic);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable @NonNull Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/provider/{providerId}/rating")
    public ResponseEntity<BigDecimal> getProviderRating(@PathVariable Long providerId) {
        try {
            // In a real implementation, you would get the provider by ID
            // For now, return a default rating
            return ResponseEntity.ok(new BigDecimal("4.5"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/provider/{providerId}/count")
    public ResponseEntity<Long> getProviderReviewCount(@PathVariable Long providerId) {
        try {
            // In a real implementation, you would get the provider by ID
            // For now, return a default count
            return ResponseEntity.ok(25L);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

