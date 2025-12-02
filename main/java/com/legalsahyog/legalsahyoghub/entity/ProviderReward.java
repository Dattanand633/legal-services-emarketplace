package com.legalsahyog.legalsahyoghub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "provider_rewards")
public class ProviderReward {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type")
    private RewardType rewardType;
    
    @NotNull
    private Integer points;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum RewardType {
        SESSION_COMPLETION, HIGH_RATING, PUNCTUALITY, REFERRAL, BONUS
    }
    
    // Constructors
    public ProviderReward() {}
    
    public ProviderReward(Provider provider, RewardType rewardType, Integer points, String description) {
        this.provider = provider;
        this.rewardType = rewardType;
        this.points = points;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Provider getProvider() {
        return provider;
    }
    
    public void setProvider(Provider provider) {
        this.provider = provider;
    }
    
    public RewardType getRewardType() {
        return rewardType;
    }
    
    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
    }
    
    public Integer getPoints() {
        return points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Booking getBooking() {
        return booking;
    }
    
    public void setBooking(Booking booking) {
        this.booking = booking;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
