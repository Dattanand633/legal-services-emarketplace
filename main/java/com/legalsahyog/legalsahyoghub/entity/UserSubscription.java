package com.legalsahyog.legalsahyoghub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscriptions")
public class UserSubscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type")
    private SubscriptionType subscriptionType;
    
    @NotNull
    private BigDecimal price;
    
    @NotNull
    @Column(name = "sessions_included")
    private Integer sessionsIncluded;
    
    @Column(name = "sessions_used")
    private Integer sessionsUsed = 0;
    
    @NotNull
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @NotNull
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum SubscriptionType {
        MONTHLY, QUARTERLY, YEARLY
    }
    
    // Constructors
    public UserSubscription() {}
    
    public UserSubscription(User user, SubscriptionType subscriptionType, BigDecimal price, 
                           Integer sessionsIncluded, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.subscriptionType = subscriptionType;
        this.price = price;
        this.sessionsIncluded = sessionsIncluded;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }
    
    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getSessionsIncluded() {
        return sessionsIncluded;
    }
    
    public void setSessionsIncluded(Integer sessionsIncluded) {
        this.sessionsIncluded = sessionsIncluded;
    }
    
    public Integer getSessionsUsed() {
        return sessionsUsed;
    }
    
    public void setSessionsUsed(Integer sessionsUsed) {
        this.sessionsUsed = sessionsUsed;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
