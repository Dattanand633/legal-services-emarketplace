package com.legalsahyog.legalsahyoghub.repository;

import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.entity.ProviderReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProviderRewardRepository extends JpaRepository<ProviderReward, Long> {
    
    List<ProviderReward> findByProvider(Provider provider);
    
    List<ProviderReward> findByProviderAndRewardType(Provider provider, ProviderReward.RewardType rewardType);
    
    @Query("SELECT SUM(pr.points) FROM ProviderReward pr WHERE pr.provider = :provider")
    Integer getTotalPointsByProvider(@Param("provider") Provider provider);
    
    @Query("SELECT SUM(pr.amount) FROM ProviderReward pr WHERE pr.provider = :provider")
    BigDecimal getTotalRewardAmountByProvider(@Param("provider") Provider provider);
}
