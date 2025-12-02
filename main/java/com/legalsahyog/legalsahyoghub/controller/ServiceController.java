package com.legalsahyog.legalsahyoghub.controller;

import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.entity.Service;
import com.legalsahyog.legalsahyoghub.entity.ServiceCategory;
import com.legalsahyog.legalsahyoghub.service.ProviderService;
import com.legalsahyog.legalsahyoghub.service.ServiceCategoryService;
import com.legalsahyog.legalsahyoghub.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
public class ServiceController {
    
    @Autowired
    private ServiceService serviceService;
    
    @Autowired
    private ServiceCategoryService serviceCategoryService;
    
    @Autowired
    private ProviderService providerService;
    
    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        List<Service> services = serviceService.getAvailableServices();
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Service>> searchServices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String practiceArea) {
        
        List<Service> services = serviceService.getAvailableServices();
        
        // Apply filters sequentially
        if (categoryId != null) {
            ServiceCategory category = serviceCategoryService.getCategoryById(categoryId).orElse(null);
            if (category != null) {
                services = serviceService.getServicesByCategory(category);
            }
        }
        
        if (keyword != null && !keyword.trim().isEmpty() && services != null && !services.isEmpty()) {
            // Filter by keyword from already filtered services
            String keywordLower = keyword.toLowerCase();
            services = services.stream()
                .filter(s -> (s.getTitle() != null && s.getTitle().toLowerCase().contains(keywordLower)) ||
                            (s.getDescription() != null && s.getDescription().toLowerCase().contains(keywordLower)))
                .collect(java.util.stream.Collectors.toList());
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            services = serviceService.searchServicesByKeyword(keyword);
        }
        
        if (city != null && !city.trim().isEmpty() && services != null && !services.isEmpty()) {
            String cityLower = city.toLowerCase();
            services = services.stream()
                .filter(s -> s.getProvider() != null && s.getProvider().getCity() != null &&
                            s.getProvider().getCity().toLowerCase().contains(cityLower))
                .collect(java.util.stream.Collectors.toList());
        } else if (city != null && !city.trim().isEmpty()) {
            services = serviceService.getServicesByProviderCity(city);
        }
        
        if (practiceArea != null && !practiceArea.trim().isEmpty() && services != null && !services.isEmpty()) {
            String practiceAreaLower = practiceArea.toLowerCase();
            services = services.stream()
                .filter(s -> (s.getProvider() != null && s.getProvider().getPracticeArea() != null &&
                             s.getProvider().getPracticeArea().toLowerCase().contains(practiceAreaLower)) ||
                            (s.getCategory() != null && s.getCategory().getName() != null &&
                             s.getCategory().getName().toLowerCase().contains(practiceAreaLower)))
                .collect(java.util.stream.Collectors.toList());
        }
        
        if (minPrice != null && services != null && !services.isEmpty()) {
            services = services.stream()
                .filter(s -> s.getPrice() != null && s.getPrice().compareTo(minPrice) >= 0)
                .collect(java.util.stream.Collectors.toList());
        }
        
        if (maxPrice != null && services != null && !services.isEmpty()) {
            services = services.stream()
                .filter(s -> s.getPrice() != null && s.getPrice().compareTo(maxPrice) <= 0)
                .collect(java.util.stream.Collectors.toList());
        }
        
        return ResponseEntity.ok(services != null ? services : java.util.Collections.emptyList());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable @NonNull Long id) {
        return serviceService.getServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        try {
            // Get current provider from authentication
            // In a real implementation, you would get the provider by email
            // For now, we'll assume the service has the provider set
            if (service == null) {
                return ResponseEntity.badRequest().build();
            }
            Service createdService = serviceService.createService(service);
            return ResponseEntity.ok(createdService);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable @NonNull Long id, @RequestBody Service service) {
        try {
            Service updatedService = serviceService.updateService(id, service);
            return ResponseEntity.ok(updatedService);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable @NonNull Long id) {
        try {
            serviceService.deleteService(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/toggle-availability")
    public ResponseEntity<Service> toggleServiceAvailability(@PathVariable @NonNull Long id) {
        try {
            Service service = serviceService.toggleServiceAvailability(id);
            return ResponseEntity.ok(service);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Service>> getServicesByProvider(@PathVariable @NonNull Long providerId) {
        try {
            // Get provider by ID
            Optional<Provider> providerOptional = providerService.getProviderById(providerId);
            if (providerOptional.isPresent()) {
                Provider provider = providerOptional.get();
                List<Service> services = serviceService.getServicesByProvider(provider);
                return ResponseEntity.ok(services);
            }
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
}

