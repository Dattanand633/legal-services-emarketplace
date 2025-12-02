package com.legalsahyog.legalsahyoghub.service;

import com.legalsahyog.legalsahyoghub.entity.ServiceCategory;
import com.legalsahyog.legalsahyoghub.repository.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceCategoryService {
    
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;
    
    public List<ServiceCategory> getAllCategories() {
        return serviceCategoryRepository.findAll();
    }
    
    public List<ServiceCategory> getActiveCategories() {
        return serviceCategoryRepository.findByIsActiveTrue();
    }
    
    public Optional<ServiceCategory> getCategoryById(@NonNull Long id) {
        return serviceCategoryRepository.findById(id);
    }
    
    public ServiceCategory getCategoryByName(String name) {
        return serviceCategoryRepository.findByName(name);
    }
    
    public ServiceCategory createCategory(@NonNull ServiceCategory category) {
        return serviceCategoryRepository.save(category);
    }
    
    public ServiceCategory updateCategory(@NonNull Long id, ServiceCategory categoryDetails) {
        Optional<ServiceCategory> categoryOptional = serviceCategoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            ServiceCategory category = categoryOptional.get();
            category.setName(categoryDetails.getName());
            category.setDescription(categoryDetails.getDescription());
            category.setIcon(categoryDetails.getIcon());
            category.setIsActive(categoryDetails.getIsActive());
            return serviceCategoryRepository.save(category);
        }
        throw new RuntimeException("Service category not found with id: " + id);
    }
    
    public void deleteCategory(@NonNull Long id) {
        serviceCategoryRepository.deleteById(id);
    }
    
    public ServiceCategory activateCategory(@NonNull Long id) {
        Optional<ServiceCategory> categoryOptional = serviceCategoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            ServiceCategory category = categoryOptional.get();
            category.setIsActive(true);
            return serviceCategoryRepository.save(category);
        }
        throw new RuntimeException("Service category not found with id: " + id);
    }
    
    public ServiceCategory deactivateCategory(@NonNull Long id) {
        Optional<ServiceCategory> categoryOptional = serviceCategoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            ServiceCategory category = categoryOptional.get();
            category.setIsActive(false);
            return serviceCategoryRepository.save(category);
        }
        throw new RuntimeException("Service category not found with id: " + id);
    }
}
