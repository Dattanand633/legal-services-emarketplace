package com.legalsahyog.legalsahyoghub.repository;

import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.entity.ProviderAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderAvailabilityRepository extends JpaRepository<ProviderAvailability, Long> {
    
    List<ProviderAvailability> findByProvider(Provider provider);
    
    List<ProviderAvailability> findByProviderAndIsAvailableTrue(Provider provider);
    
    List<ProviderAvailability> findByProviderAndDayOfWeek(Provider provider, ProviderAvailability.DayOfWeek dayOfWeek);
}
