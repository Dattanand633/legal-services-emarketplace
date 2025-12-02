package com.legalsahyog.legalsahyoghub.repository;

import com.legalsahyog.legalsahyoghub.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    
    Optional<Provider> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByBarCouncilNumber(String barCouncilNumber);
    
    List<Provider> findByVerificationStatus(Provider.VerificationStatus status);
    
    List<Provider> findByIsActiveTrue();
    
    @Query("SELECT p FROM Provider p WHERE p.city = :city AND p.isActive = true AND p.verificationStatus = 'VERIFIED'")
    List<Provider> findByCityAndActiveAndVerified(@Param("city") String city);
    
    @Query("SELECT p FROM Provider p WHERE p.state = :state AND p.isActive = true AND p.verificationStatus = 'VERIFIED'")
    List<Provider> findByStateAndActiveAndVerified(@Param("state") String state);
    
    @Query("SELECT p FROM Provider p WHERE UPPER(p.practiceArea) LIKE UPPER(CONCAT('%', :practiceArea, '%')) AND p.isActive = true AND p.verificationStatus = 'VERIFIED'")
    List<Provider> findByPracticeAreaContainingAndActiveAndVerified(@Param("practiceArea") String practiceArea);
    
    @Query("SELECT p FROM Provider p WHERE p.isActive = true AND p.verificationStatus = 'VERIFIED' ORDER BY p.rating DESC")
    List<Provider> findTopRatedProviders();
    
    @Query("SELECT p FROM Provider p WHERE p.isActive = true AND p.verificationStatus = 'VERIFIED' ORDER BY p.totalSessions DESC")
    List<Provider> findMostExperiencedProviders();
}
