package com.legalsahyog.legalsahyoghub.service;

import com.legalsahyog.legalsahyoghub.entity.Booking;
import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.entity.Review;
import com.legalsahyog.legalsahyoghub.entity.User;
import com.legalsahyog.legalsahyoghub.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private ProviderService providerService;
    
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
    
    public Optional<Review> getReviewById(@NonNull Long id) {
        return reviewRepository.findById(id);
    }
    
    public List<Review> getReviewsByProvider(Provider provider) {
        return reviewRepository.findByProvider(provider);
    }
    
    public List<Review> getPublicReviewsByProvider(Provider provider) {
        return reviewRepository.findPublicReviewsByProviderOrderByDate(provider);
    }
    
    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user);
    }
    
    public Review createReview(@NonNull Long bookingId, Integer rating, String reviewText, Boolean isPublic) {
        Booking booking = bookingService.getBookingById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Check if booking is completed
        if (booking.getStatus() != Booking.BookingStatus.COMPLETED) {
            throw new RuntimeException("Can only review completed bookings");
        }
        
        // Check if review already exists for this booking
        Optional<Review> existingReview = reviewRepository.findByBooking(booking);
        if (existingReview.isPresent()) {
            throw new RuntimeException("Review already exists for this booking");
        }
        
        Review review = new Review();
        review.setBooking(booking);
        review.setUser(booking.getUser());
        review.setProvider(booking.getProvider());
        review.setRating(rating);
        review.setReviewText(reviewText);
        review.setIsPublic(isPublic != null ? isPublic : true);
        
        Review savedReview = reviewRepository.save(review);
        
        // Update provider rating
        updateProviderRating(booking.getProvider());
        
        return savedReview;
    }
    
    public Review updateReview(@NonNull Long id, Integer rating, String reviewText, Boolean isPublic) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setRating(rating);
            review.setReviewText(reviewText);
            review.setIsPublic(isPublic != null ? isPublic : review.getIsPublic());
            
            Review updatedReview = reviewRepository.save(review);
            
            // Update provider rating
            updateProviderRating(review.getProvider());
            
            return updatedReview;
        }
        throw new RuntimeException("Review not found with id: " + id);
    }
    
    public void deleteReview(@NonNull Long id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            Provider provider = review.getProvider();
            
            reviewRepository.deleteById(id);
            
            // Update provider rating after deletion
            updateProviderRating(provider);
        } else {
            throw new RuntimeException("Review not found with id: " + id);
        }
    }
    
    public BigDecimal getAverageRatingByProvider(Provider provider) {
        BigDecimal averageRating = reviewRepository.getAverageRatingByProvider(provider);
        return averageRating != null ? averageRating.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }
    
    public Long getReviewCountByProvider(Provider provider) {
        return reviewRepository.getReviewCountByProvider(provider);
    }
    
    public List<Review> getRecentReviews(int limit) {
        return reviewRepository.findAll().stream()
                .filter(review -> review.getIsPublic())
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .limit(limit)
                .toList();
    }
    
    public List<Review> getTopRatedReviews(int limit) {
        return reviewRepository.findAll().stream()
                .filter(review -> review.getIsPublic() && review.getRating() >= 4)
                .sorted((r1, r2) -> r2.getRating().compareTo(r1.getRating()))
                .limit(limit)
                .toList();
    }
    
    private void updateProviderRating(Provider provider) {
        BigDecimal averageRating = getAverageRatingByProvider(provider);
        Long providerId = provider.getId();
        if (providerId != null) {
            providerService.updateProviderRating(providerId, averageRating);
        }
    }
}

