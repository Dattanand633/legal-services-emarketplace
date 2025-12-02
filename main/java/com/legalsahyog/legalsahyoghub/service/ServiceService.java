package com.legalsahyog.legalsahyoghub.service;

import com.legalsahyog.legalsahyoghub.entity.ServiceCategory;
import com.legalsahyog.legalsahyoghub.repository.ServiceCategoryRepository;
import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;


    public List<com.legalsahyog.legalsahyoghub.entity.Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public List<com.legalsahyog.legalsahyoghub.entity.Service> getAvailableServices() {
        return serviceRepository.findAvailableServicesFromVerifiedProviders();
    }

    public Optional<com.legalsahyog.legalsahyoghub.entity.Service> getServiceById(@NonNull Long id) {
        return serviceRepository.findById(id);
    }

    public List<com.legalsahyog.legalsahyoghub.entity.Service> getServicesByProvider(Provider provider) {
        return serviceRepository.findByProviderAndIsAvailableTrue(provider);
    }

    public List<com.legalsahyog.legalsahyoghub.entity.Service> getServicesByCategory(ServiceCategory category) {
        return serviceRepository.findAvailableServicesByCategory(category);
    }

    public List<com.legalsahyog.legalsahyoghub.entity.Service> getServicesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return serviceRepository.findByPriceRange(minPrice, maxPrice);
    }

    public List<com.legalsahyog.legalsahyoghub.entity.Service> getServicesByProviderCity(String city) {
        return serviceRepository.findByProviderCity(city);
    }

    public List<com.legalsahyog.legalsahyoghub.entity.Service> searchServicesByKeyword(String keyword) {
        return serviceRepository.findByKeyword(keyword);
    }

    /**
     * Create a Service with defensive handling for category:
     * - If category is null -> try to find or create a default "General" category and assign it.
     * - If category has id -> ensure it exists.
     * - If category has no id but has name -> resolve by name.
     * - Otherwise throw IllegalArgumentException.
     */
    public com.legalsahyog.legalsahyoghub.entity.Service createService(
            @org.springframework.lang.NonNull com.legalsahyog.legalsahyoghub.entity.Service service) {

        ServiceCategory category = service.getCategory();

        if (category == null) {
            // Try to get a default category named "General". Create if missing.
            String defaultName = "General";
            ServiceCategory defaultCat = serviceCategoryRepository.findByNameIgnoreCase(defaultName)
                    .orElseGet(() -> {
                        ServiceCategory newCat = new ServiceCategory();
                        newCat.setName(defaultName);
                        newCat.setDescription("Default category assigned automatically");
                        return serviceCategoryRepository.save(newCat);
                    });
            service.setCategory(defaultCat);
        } else {
            // If category object provided but id present, validate it
            if (category.getId() != null) {
                Long catId = category.getId();
                if (!serviceCategoryRepository.existsById(catId)) {
                    throw new IllegalArgumentException("Invalid categoryId: " + catId);
                }
                // Re-attach managed entity
                Optional<ServiceCategory> managed = serviceCategoryRepository.findById(catId);
                managed.ifPresent(service::setCategory);
            } else {
                // Try to resolve by name if present
                if (category.getName() != null && !category.getName().isBlank()) {
                    ServiceCategory resolved = serviceCategoryRepository.findByNameIgnoreCase(category.getName())
                            .orElseThrow(() -> new IllegalArgumentException("Category not found with name: " + category.getName()));
                    service.setCategory(resolved);
                } else {
                    throw new IllegalArgumentException("Service category must include a valid id or name.");
                }
            }
        }

        return serviceRepository.save(service);
    }


    public com.legalsahyog.legalsahyoghub.entity.Service updateService(@NonNull Long id, com.legalsahyog.legalsahyoghub.entity.Service serviceDetails) {
        Optional<com.legalsahyog.legalsahyoghub.entity.Service> serviceOptional = serviceRepository.findById(id);
        if (serviceOptional.isPresent()) {
            com.legalsahyog.legalsahyoghub.entity.Service service = serviceOptional.get();
            service.setTitle(serviceDetails.getTitle());
            service.setDescription(serviceDetails.getDescription());
            service.setPrice(serviceDetails.getPrice());
            service.setDurationMinutes(serviceDetails.getDurationMinutes());
            service.setIsAvailable(serviceDetails.getIsAvailable());
            service.setLanguages(serviceDetails.getLanguages());
            return serviceRepository.save(service);
        }
        throw new RuntimeException("Service not found with id: " + id);
    }

    public void deleteService(@NonNull Long id) {
        serviceRepository.deleteById(id);
    }

    public com.legalsahyog.legalsahyoghub.entity.Service toggleServiceAvailability(@NonNull Long id) {
        Optional<com.legalsahyog.legalsahyoghub.entity.Service> serviceOptional = serviceRepository.findById(id);
        if (serviceOptional.isPresent()) {
            com.legalsahyog.legalsahyoghub.entity.Service service = serviceOptional.get();
            service.setIsAvailable(!service.getIsAvailable());
            return serviceRepository.save(service);
        }
        throw new RuntimeException("Service not found with id: " + id);
    }
}
