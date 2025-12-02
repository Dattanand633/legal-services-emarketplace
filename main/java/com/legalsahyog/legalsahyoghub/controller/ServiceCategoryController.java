package com.legalsahyog.legalsahyoghub.controller;

import com.legalsahyog.legalsahyoghub.entity.ServiceCategory;
import com.legalsahyog.legalsahyoghub.service.ServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class ServiceCategoryController {
    
    @Autowired
    private ServiceCategoryService serviceCategoryService;
    
    @GetMapping
    public ResponseEntity<List<ServiceCategory>> getAllCategories() {
        List<ServiceCategory> categories = serviceCategoryService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ServiceCategory> getCategoryById(@PathVariable @NonNull Long id) {
        return serviceCategoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<ServiceCategory> createCategory(@RequestBody ServiceCategory category) {
        try {
            if (category == null) {
                return ResponseEntity.badRequest().build();
            }
            ServiceCategory createdCategory = serviceCategoryService.createCategory(category);
            return ResponseEntity.ok(createdCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ServiceCategory> updateCategory(@PathVariable @NonNull Long id, @RequestBody ServiceCategory category) {
        try {
            ServiceCategory updatedCategory = serviceCategoryService.updateCategory(id, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @NonNull Long id) {
        try {
            serviceCategoryService.deleteCategory(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
