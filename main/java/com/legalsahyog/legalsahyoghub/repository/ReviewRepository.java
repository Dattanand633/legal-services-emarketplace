package com.legalsahyog.legalsahyoghub.repository;

import com.legalsahyog.legalsahyoghub.entity.Booking;
import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.entity.Review;
import com.legalsahyog.legalsahyoghub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByProvider(Provider provider);
    
    List<Review> findByUser(User user);
    
    Optional<Review> findByBooking(Booking booking);
    
    List<Review> findByProviderAndIsPublicTrue(Provider provider);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.provider = :provider AND r.isPublic = true")
    BigDecimal getAverageRatingByProvider(@Param("provider") Provider provider);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.provider = :provider AND r.isPublic = true")
    Long getReviewCountByProvider(@Param("provider") Provider provider);
    
    @Query("SELECT r FROM Review r WHERE r.provider = :provider AND r.isPublic = true ORDER BY r.createdAt DESC")
    List<Review> findPublicReviewsByProviderOrderByDate(@Param("provider") Provider provider);
}
