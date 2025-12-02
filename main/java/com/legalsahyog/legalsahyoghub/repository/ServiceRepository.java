package com.legalsahyog.legalsahyoghub.repository;

import com.legalsahyog.legalsahyoghub.entity.Service;
import com.legalsahyog.legalsahyoghub.entity.ServiceCategory;
import com.legalsahyog.legalsahyoghub.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    List<Service> findByProviderAndIsAvailableTrue(Provider provider);
    
    List<Service> findByCategoryAndIsAvailableTrue(ServiceCategory category);
    
    List<Service> findByIsAvailableTrue();
    
    @Query("SELECT s FROM Service s WHERE s.provider.isActive = true AND s.provider.verificationStatus = 'VERIFIED' AND s.isAvailable = true")
    List<Service> findAvailableServicesFromVerifiedProviders();
    
    @Query("SELECT s FROM Service s WHERE s.category = :category AND s.provider.isActive = true AND s.provider.verificationStatus = 'VERIFIED' AND s.isAvailable = true")
    List<Service> findAvailableServicesByCategory(@Param("category") ServiceCategory category);
    
    @Query("SELECT s FROM Service s WHERE s.price BETWEEN :minPrice AND :maxPrice AND s.provider.isActive = true AND s.provider.verificationStatus = 'VERIFIED' AND s.isAvailable = true")
    List<Service> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT s FROM Service s WHERE s.provider.city = :city AND s.provider.isActive = true AND s.provider.verificationStatus = 'VERIFIED' AND s.isAvailable = true")
    List<Service> findByProviderCity(@Param("city") String city);
    
    @Query("SELECT s FROM Service s WHERE (s.title LIKE %:keyword% OR s.description LIKE %:keyword%) AND s.provider.isActive = true AND s.provider.verificationStatus = 'VERIFIED' AND s.isAvailable = true")
    List<Service> findByKeyword(@Param("keyword") String keyword);
}
