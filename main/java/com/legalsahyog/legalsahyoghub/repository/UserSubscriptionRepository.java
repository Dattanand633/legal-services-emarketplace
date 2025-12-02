package com.legalsahyog.legalsahyoghub.repository;

import com.legalsahyog.legalsahyoghub.entity.User;
import com.legalsahyog.legalsahyoghub.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    
    List<UserSubscription> findByUser(User user);
    
    List<UserSubscription> findByUserAndIsActiveTrue(User user);
    
    Optional<UserSubscription> findByUserAndIsActiveTrueAndEndDateAfter(User user, LocalDate date);
    
    List<UserSubscription> findByIsActiveTrueAndEndDateBefore(LocalDate date);
}
