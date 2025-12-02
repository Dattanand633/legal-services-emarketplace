package com.legalsahyog.legalsahyoghub.controller;

import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.entity.Service;
import com.legalsahyog.legalsahyoghub.entity.ServiceCategory;
import com.legalsahyog.legalsahyoghub.service.ProviderService;
import com.legalsahyog.legalsahyoghub.service.ServiceCategoryService;
import com.legalsahyog.legalsahyoghub.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicController {
    
    @Autowired
    private ServiceCategoryService serviceCategoryService;
    
    @Autowired
    private ProviderService providerService;
    
    @Autowired
    private ServiceService serviceService;
    
    @GetMapping("/categories")
    public ResponseEntity<List<ServiceCategory>> getPublicCategories() {
        List<ServiceCategory> categories = serviceCategoryService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/providers")
    public ResponseEntity<List<Provider>> getPublicProviders() {
        List<Provider> providers = providerService.getVerifiedProviders();
        return ResponseEntity.ok(providers);
    }
    
    @GetMapping("/providers/{id}")
    public ResponseEntity<Provider> getProviderById(@PathVariable Long id) {
        return providerService.getProviderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/providers/top-rated")
    public ResponseEntity<List<Provider>> getTopRatedProviders() {
        List<Provider> providers = providerService.getTopRatedProviders();
        return ResponseEntity.ok(providers);
    }
    
    @GetMapping("/providers/experienced")
    public ResponseEntity<List<Provider>> getMostExperiencedProviders() {
        List<Provider> providers = providerService.getMostExperiencedProviders();
        return ResponseEntity.ok(providers);
    }
    
    @GetMapping("/providers/city/{city}")
    public ResponseEntity<List<Provider>> getProvidersByCity(@PathVariable String city) {
        List<Provider> providers = providerService.getProvidersByCity(city);
        return ResponseEntity.ok(providers);
    }
    
    @GetMapping("/providers/state/{state}")
    public ResponseEntity<List<Provider>> getProvidersByState(@PathVariable String state) {
        List<Provider> providers = providerService.getProvidersByState(state);
        return ResponseEntity.ok(providers);
    }
    
    @GetMapping("/providers/practice-area/{practiceArea}")
    public ResponseEntity<List<Provider>> getProvidersByPracticeArea(@PathVariable String practiceArea) {
        List<Provider> providers = providerService.getProvidersByPracticeArea(practiceArea);
        return ResponseEntity.ok(providers);
    }
    
    @GetMapping("/providers/category/{categoryId}")
    public ResponseEntity<List<Provider>> getProvidersByCategory(@PathVariable Long categoryId) {
        // Get category by ID and match providers by practice area
        ServiceCategory category = serviceCategoryService.getCategoryById(categoryId).orElse(null);
        if (category != null && category.getName() != null) {
            // Match category name with provider practice area (case-insensitive)
            String categoryName = category.getName();
            List<Provider> providers = providerService.getProvidersByPracticeArea(categoryName);
            
            // If no exact match, try case-insensitive search
            if (providers.isEmpty()) {
                List<Provider> allVerified = providerService.getVerifiedProviders();
                providers = allVerified.stream()
                    .filter(p -> p.getPracticeArea() != null && 
                            p.getPracticeArea().equalsIgnoreCase(categoryName))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            return ResponseEntity.ok(providers);
        }
        
        // If category not found, return all verified providers as fallback
        // This ensures providers are shown even if categories aren't set up
        return ResponseEntity.ok(providerService.getVerifiedProviders());
    }
    
    @GetMapping("/services")
    public ResponseEntity<List<Service>> getPublicServices() {
        List<Service> services = serviceService.getAvailableServices();
        return ResponseEntity.ok(services);
    }
}
