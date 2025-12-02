package com.legalsahyog.legalsahyoghub.controller;

import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.entity.ProviderAvailability;
import com.legalsahyog.legalsahyoghub.entity.Service;
import com.legalsahyog.legalsahyoghub.repository.ProviderRepository;
import com.legalsahyog.legalsahyoghub.security.JwtUtil;
import com.legalsahyog.legalsahyoghub.service.ProviderService;
import com.legalsahyog.legalsahyoghub.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/providers")
@CrossOrigin(origins = "*")
public class ProviderController {
    
    @Autowired
    private ProviderService providerService;
    
    @Autowired
    private ServiceService serviceService;
    
    @Autowired
    private ProviderRepository providerRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private Optional<Provider> getCurrentProvider(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            return providerService.getProviderByEmail(email);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<Provider> getCurrentProviderProfile(HttpServletRequest request) {
        Optional<Provider> providerOpt = getCurrentProvider(request);
        if (providerOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(providerOpt.get());
    }
    
    @PutMapping("/me")
    public ResponseEntity<Provider> updateProviderProfile(
            HttpServletRequest request,
            @RequestBody Map<String, Object> providerData) {
        Optional<Provider> providerOpt = getCurrentProvider(request);
        if (providerOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        Provider provider = providerOpt.get();
        
        // Update fields
        if (providerData.containsKey("firstName")) {
            provider.setFirstName(providerData.get("firstName").toString());
        }
        if (providerData.containsKey("lastName")) {
            provider.setLastName(providerData.get("lastName").toString());
        }
        if (providerData.containsKey("phone")) {
            provider.setPhone(providerData.get("phone").toString());
        }
        if (providerData.containsKey("practiceArea")) {
            provider.setPracticeArea(providerData.get("practiceArea").toString());
        }
        if (providerData.containsKey("experienceYears")) {
            provider.setExperienceYears(Integer.parseInt(providerData.get("experienceYears").toString()));
        }
        if (providerData.containsKey("qualification")) {
            provider.setQualification(providerData.get("qualification").toString());
        }
        if (providerData.containsKey("bio")) {
            provider.setBio(providerData.get("bio").toString());
        }
        if (providerData.containsKey("address")) {
            provider.setAddress(providerData.get("address").toString());
        }
        if (providerData.containsKey("city")) {
            provider.setCity(providerData.get("city").toString());
        }
        if (providerData.containsKey("state")) {
            provider.setState(providerData.get("state").toString());
        }
        if (providerData.containsKey("pincode")) {
            provider.setPincode(providerData.get("pincode").toString());
        }
        if (providerData.containsKey("isActive")) {
            provider.setIsActive(Boolean.parseBoolean(providerData.get("isActive").toString()));
        }
        if (providerData.containsKey("consultationFee")) {
            provider.setConsultationFee(java.math.BigDecimal.valueOf(Double.parseDouble(providerData.get("consultationFee").toString())));
        }
        
        Provider updated = providerService.updateProvider(provider.getId(), provider);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/me/services")
    public ResponseEntity<List<Service>> getMyServices(HttpServletRequest request) {
        Optional<Provider> providerOpt = getCurrentProvider(request);
        if (providerOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        List<Service> services = serviceService.getServicesByProvider(providerOpt.get());
        return ResponseEntity.ok(services);
    }
    
    @PostMapping("/me/services")
    public ResponseEntity<Service> createService(
            HttpServletRequest request,
            @RequestBody Map<String, Object> serviceData) {
        Optional<Provider> providerOpt = getCurrentProvider(request);
        if (providerOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        Provider provider = providerOpt.get();
        Service service = new Service();
        service.setProvider(provider);
        service.setTitle(serviceData.get("title").toString());
        service.setDescription(serviceData.get("description") != null ? serviceData.get("description").toString() : "");
        service.setPrice(java.math.BigDecimal.valueOf(Double.parseDouble(serviceData.get("price").toString())));
        service.setDurationMinutes(Integer.parseInt(serviceData.get("durationMinutes").toString()));
        service.setIsAvailable(serviceData.get("isAvailable") != null ? 
            Boolean.parseBoolean(serviceData.get("isAvailable").toString()) : true);
        
        Service created = serviceService.createService(service);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/me/services/{serviceId}")
    public ResponseEntity<Service> updateService(
            HttpServletRequest request,
            @PathVariable Long serviceId,
            @RequestBody Map<String, Object> serviceData) {
        Optional<Provider> providerOpt = getCurrentProvider(request);
        if (providerOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        // Verify service belongs to provider
        Optional<Service> serviceOpt = serviceService.getServiceById(serviceId);
        if (serviceOpt.isEmpty() || !serviceOpt.get().getProvider().getId().equals(providerOpt.get().getId())) {
            return ResponseEntity.status(403).build();
        }
        
        Service service = serviceOpt.get();
        if (serviceData.containsKey("title")) {
            service.setTitle(serviceData.get("title").toString());
        }
        if (serviceData.containsKey("description")) {
            service.setDescription(serviceData.get("description").toString());
        }
        if (serviceData.containsKey("price")) {
            service.setPrice(java.math.BigDecimal.valueOf(Double.parseDouble(serviceData.get("price").toString())));
        }
        if (serviceData.containsKey("durationMinutes")) {
            service.setDurationMinutes(Integer.parseInt(serviceData.get("durationMinutes").toString()));
        }
        if (serviceData.containsKey("isAvailable")) {
            service.setIsAvailable(Boolean.parseBoolean(serviceData.get("isAvailable").toString()));
        }
        
        Service updated = serviceService.updateService(serviceId, service);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/me/services/{serviceId}")
    public ResponseEntity<Void> deleteService(
            HttpServletRequest request,
            @PathVariable Long serviceId) {
        Optional<Provider> providerOpt = getCurrentProvider(request);
        if (providerOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        
        // Verify service belongs to provider
        Optional<Service> serviceOpt = serviceService.getServiceById(serviceId);
        if (serviceOpt.isEmpty() || !serviceOpt.get().getProvider().getId().equals(providerOpt.get().getId())) {
            return ResponseEntity.status(403).build();
        }
        
        serviceService.deleteService(serviceId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/me/availability")
    public ResponseEntity<List<ProviderAvailability>> getMyAvailability(HttpServletRequest request) {
        Optional<Provider> providerOpt = getCurrentProvider(request);
        if (providerOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        // This would require a ProviderAvailabilityService - for now return empty
        return ResponseEntity.ok(List.of());
    }
    
    @PostMapping("/me/availability")
    public ResponseEntity<ProviderAvailability> setAvailability(
            HttpServletRequest request,
            @RequestBody Map<String, Object> availabilityData) {
        Optional<Provider> providerOpt = getCurrentProvider(request);
        if (providerOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        // This would require a ProviderAvailabilityService - for now return placeholder
        return ResponseEntity.status(501).build();
    }
}

