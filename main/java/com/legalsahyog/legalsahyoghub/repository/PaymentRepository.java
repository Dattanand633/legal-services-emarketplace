package com.legalsahyog.legalsahyoghub.repository;

import com.legalsahyog.legalsahyoghub.entity.Payment;
import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByUser(User user);
    
    List<Payment> findByProvider(Provider provider);
    
    List<Payment> findByPaymentStatus(Payment.PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.provider = :provider AND p.paymentStatus = 'SUCCESS'")
    BigDecimal getTotalEarningsByProvider(@Param("provider") Provider provider);
    
    @Query("SELECT SUM(p.platformFee) FROM Payment p WHERE p.paymentStatus = 'SUCCESS' AND p.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getPlatformRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'SUCCESS' AND p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findSuccessfulPaymentsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
