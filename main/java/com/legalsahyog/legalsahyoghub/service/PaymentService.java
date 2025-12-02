package com.legalsahyog.legalsahyoghub.service;

import com.legalsahyog.legalsahyoghub.entity.Booking;
import com.legalsahyog.legalsahyoghub.entity.Payment;
import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.entity.ProviderReward;
import com.legalsahyog.legalsahyoghub.entity.User;
import com.legalsahyog.legalsahyoghub.repository.PaymentRepository;
import com.legalsahyog.legalsahyoghub.repository.ProviderRewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private ProviderRewardRepository providerRewardRepository;
    
    @Autowired
    private BookingService bookingService;
    

    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public Optional<Payment> getPaymentById(@NonNull Long id) {
        return paymentRepository.findById(id);
    }
    
    public List<Payment> getPaymentsByUser(User user) {
        return paymentRepository.findByUser(user);
    }
    
    public List<Payment> getPaymentsByProvider(Provider provider) {
        return paymentRepository.findByProvider(provider);
    }
    
    public List<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status);
    }
    
    public Payment createPayment(@NonNull Long bookingId, Payment.PaymentMethod paymentMethod) {
        Booking booking = bookingService.getBookingById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUser(booking.getUser());
        payment.setProvider(booking.getProvider());
        payment.setAmount(booking.getTotalAmount());
        payment.setPlatformFee(booking.getPlatformFee());
        payment.setProviderAmount(booking.getProviderEarnings());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
        payment.setTransactionId(generateTransactionId());
        
        return paymentRepository.save(payment);
    }
    
    public Payment processPayment(@NonNull Long paymentId, String paymentGatewayResponse) {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            
            // Simulate payment processing
            boolean paymentSuccess = simulatePaymentGateway(paymentGatewayResponse);
            
            if (paymentSuccess) {
                payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
                payment.setPaidAt(LocalDateTime.now());
                payment.setPaymentGatewayResponse(paymentGatewayResponse);
                
                // Distribute rewards
                distributeRewards(payment);
                
                return paymentRepository.save(payment);
            } else {
                payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
                payment.setPaymentGatewayResponse(paymentGatewayResponse);
                return paymentRepository.save(payment);
            }
        }
        throw new RuntimeException("Payment not found with id: " + paymentId);
    }
    
    public Payment refundPayment(@NonNull Long paymentId, String reason) {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            
            if (payment.getPaymentStatus() != Payment.PaymentStatus.SUCCESS) {
                throw new RuntimeException("Cannot refund payment that is not successful");
            }
            
            payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
            payment.setPaymentGatewayResponse(payment.getPaymentGatewayResponse() + " | REFUNDED: " + reason);
            
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Payment not found with id: " + paymentId);
    }
    
    public BigDecimal getTotalEarningsByProvider(Provider provider) {
        return paymentRepository.getTotalEarningsByProvider(provider);
    }
    
    public BigDecimal getPlatformRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.getPlatformRevenueByDateRange(startDate, endDate);
    }
    
    public List<Payment> getSuccessfulPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findSuccessfulPaymentsByDateRange(startDate, endDate);
    }
    
    private void distributeRewards(Payment payment) {
        Provider provider = payment.getProvider();
        
        // Session completion reward
        ProviderReward sessionReward = new ProviderReward();
        sessionReward.setProvider(provider);
        sessionReward.setRewardType(ProviderReward.RewardType.SESSION_COMPLETION);
        sessionReward.setPoints(10);
        sessionReward.setAmount(BigDecimal.ZERO);
        sessionReward.setDescription("Completed consultation session");
        sessionReward.setBooking(payment.getBooking());
        providerRewardRepository.save(sessionReward);
        
        // Check for high rating bonus
        if (provider.getRating().compareTo(new BigDecimal("4.5")) >= 0) {
            ProviderReward ratingReward = new ProviderReward();
            ratingReward.setProvider(provider);
            ratingReward.setRewardType(ProviderReward.RewardType.HIGH_RATING);
            ratingReward.setPoints(5);
            ratingReward.setAmount(payment.getProviderAmount().multiply(new BigDecimal("0.05")));
            ratingReward.setDescription("High rating bonus (4.5+ stars)");
            providerRewardRepository.save(ratingReward);
        }
        
        // Check for punctuality reward (if booking was on time)
        if (isPunctual(payment.getBooking())) {
            ProviderReward punctualityReward = new ProviderReward();
            punctualityReward.setProvider(provider);
            punctualityReward.setRewardType(ProviderReward.RewardType.PUNCTUALITY);
            punctualityReward.setPoints(3);
            punctualityReward.setAmount(payment.getProviderAmount().multiply(new BigDecimal("0.02")));
            punctualityReward.setDescription("Punctuality reward");
            providerRewardRepository.save(punctualityReward);
        }
    }
    
    private boolean isPunctual(Booking booking) {
        // In a real implementation, you would check if the provider joined the meeting on time
        // For now, we'll assume 90% punctuality rate
        return Math.random() > 0.1;
    }
    
    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    private boolean simulatePaymentGateway(String response) {
        // Simulate payment gateway response
        // In a real implementation, you would integrate with actual payment gateways like Razorpay, PayU, etc.
        return response != null && !response.contains("FAILED");
    }
}

