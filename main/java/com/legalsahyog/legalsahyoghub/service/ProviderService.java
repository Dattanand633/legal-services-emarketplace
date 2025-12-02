package com.legalsahyog.legalsahyoghub.service;

import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProviderService {
    
    @Autowired
    private ProviderRepository providerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }
    
    public Optional<Provider> getProviderById(@NonNull Long id) {
        return providerRepository.findById(id);
    }
    
    public Optional<Provider> getProviderByEmail(String email) {
        return providerRepository.findByEmail(email);
    }
    
    public Provider createProvider(Provider provider) {
        provider.setPassword(passwordEncoder.encode(provider.getPassword()));
        return providerRepository.save(provider);
    }
    
    public Provider updateProvider(@NonNull Long id, Provider providerDetails) {
        Optional<Provider> providerOptional = providerRepository.findById(id);
        if (providerOptional.isPresent()) {
            Provider provider = providerOptional.get();
            provider.setFirstName(providerDetails.getFirstName());
            provider.setLastName(providerDetails.getLastName());
            provider.setPhone(providerDetails.getPhone());
            provider.setPracticeArea(providerDetails.getPracticeArea());
            provider.setExperienceYears(providerDetails.getExperienceYears());
            provider.setQualification(providerDetails.getQualification());
            provider.setBio(providerDetails.getBio());
            provider.setAddress(providerDetails.getAddress());
            provider.setCity(providerDetails.getCity());
            provider.setState(providerDetails.getState());
            provider.setPincode(providerDetails.getPincode());
            provider.setProfileImage(providerDetails.getProfileImage());
            return providerRepository.save(provider);
        }
        throw new RuntimeException("Provider not found with id: " + id);
    }
    
    public void deleteProvider(@NonNull Long id) {
        providerRepository.deleteById(id);
    }
    
    public List<Provider> getVerifiedProviders() {
        // Return only verified AND active providers
        return providerRepository.findAll().stream()
                .filter(p -> p.getVerificationStatus() == Provider.VerificationStatus.VERIFIED 
                        && Boolean.TRUE.equals(p.getIsActive()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    public List<Provider> getPendingProviders() {
        return providerRepository.findByVerificationStatus(Provider.VerificationStatus.PENDING);
    }
    
    public List<Provider> getProvidersByCity(String city) {
        return providerRepository.findByCityAndActiveAndVerified(city);
    }
    
    public List<Provider> getProvidersByState(String state) {
        return providerRepository.findByStateAndActiveAndVerified(state);
    }
    
    public List<Provider> getProvidersByPracticeArea(String practiceArea) {
        return providerRepository.findByPracticeAreaContainingAndActiveAndVerified(practiceArea);
    }
    
    public List<Provider> getTopRatedProviders() {
        return providerRepository.findTopRatedProviders();
    }
    
    public List<Provider> getMostExperiencedProviders() {
        return providerRepository.findMostExperiencedProviders();
    }
    
    public Provider verifyProvider(@NonNull Long id) {
        Optional<Provider> providerOptional = providerRepository.findById(id);
        if (providerOptional.isPresent()) {
            Provider provider = providerOptional.get();
            provider.setVerificationStatus(Provider.VerificationStatus.VERIFIED);
            provider.setIsActive(true);
            return providerRepository.save(provider);
        }
        throw new RuntimeException("Provider not found with id: " + id);
    }
    
    public Provider rejectProvider(@NonNull Long id) {
        Optional<Provider> providerOptional = providerRepository.findById(id);
        if (providerOptional.isPresent()) {
            Provider provider = providerOptional.get();
            provider.setVerificationStatus(Provider.VerificationStatus.REJECTED);
            provider.setIsActive(false);
            return providerRepository.save(provider);
        }
        throw new RuntimeException("Provider not found with id: " + id);
    }
    
    public Provider updateProviderRating(@NonNull Long id, BigDecimal newRating) {
        Optional<Provider> providerOptional = providerRepository.findById(id);
        if (providerOptional.isPresent()) {
            Provider provider = providerOptional.get();
            provider.setRating(newRating);
            return providerRepository.save(provider);
        }
        throw new RuntimeException("Provider not found with id: " + id);
    }
    
    public Provider incrementSessionCount(@NonNull Long id) {
        Optional<Provider> providerOptional = providerRepository.findById(id);
        if (providerOptional.isPresent()) {
            Provider provider = providerOptional.get();
            provider.setTotalSessions(provider.getTotalSessions() + 1);
            return providerRepository.save(provider);
        }
        throw new RuntimeException("Provider not found with id: " + id);
    }
    
    public Provider updateEarnings(@NonNull Long id, BigDecimal earnings) {
        Optional<Provider> providerOptional = providerRepository.findById(id);
        if (providerOptional.isPresent()) {
            Provider provider = providerOptional.get();
            provider.setTotalEarnings(provider.getTotalEarnings().add(earnings));
            return providerRepository.save(provider);
        }
        throw new RuntimeException("Provider not found with id: " + id);
    }
}
