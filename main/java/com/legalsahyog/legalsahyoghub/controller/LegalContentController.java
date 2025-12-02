package com.legalsahyog.legalsahyoghub.controller;

import com.legalsahyog.legalsahyoghub.entity.LegalContent;
import com.legalsahyog.legalsahyoghub.service.LegalContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/legal-content")
@CrossOrigin(origins = "*")
public class LegalContentController {
    
    @Autowired
    private LegalContentService legalContentService;
    
    @GetMapping
    public ResponseEntity<List<LegalContent>> getAllContent() {
        List<LegalContent> content = legalContentService.getPublishedContent();
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LegalContent> getContentById(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        return legalContentService.getContentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<LegalContent>> getContentByType(@PathVariable String type) {
        try {
            LegalContent.ContentType contentType = LegalContent.ContentType.valueOf(type.toUpperCase());
            List<LegalContent> content = legalContentService.getContentByType(contentType);
            return ResponseEntity.ok(content);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<LegalContent>> getContentByCategory(@PathVariable String category) {
        List<LegalContent> content = legalContentService.getContentByCategory(category);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<LegalContent>> searchContent(@RequestParam String keyword) {
        List<LegalContent> content = legalContentService.searchContent(keyword);
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/featured")
    public ResponseEntity<List<LegalContent>> getFeaturedContent() {
        List<LegalContent> content = legalContentService.getFeaturedContent();
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<LegalContent>> getPopularContent() {
        List<LegalContent> content = legalContentService.getPopularContent();
        return ResponseEntity.ok(content);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = legalContentService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/tags")
    public ResponseEntity<List<String>> getAllTags() {
        List<String> tags = legalContentService.getAllTags();
        return ResponseEntity.ok(tags);
    }
    
    @PostMapping
    public ResponseEntity<LegalContent> createContent(@RequestBody LegalContent content) {
        try {
            // Ensure viewCount is initialized
            if (content.getViewCount() == null) {
                content.setViewCount(0);
            }
            LegalContent createdContent = legalContentService.createContent(content);
            return ResponseEntity.ok(createdContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LegalContent> updateContent(@PathVariable Long id, @RequestBody LegalContent content) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            LegalContent updatedContent = legalContentService.updateContent(id, content);
            return ResponseEntity.ok(updatedContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/publish")
    public ResponseEntity<LegalContent> publishContent(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            LegalContent content = legalContentService.publishContent(id);
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/unpublish")
    public ResponseEntity<LegalContent> unpublishContent(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            LegalContent content = legalContentService.unpublishContent(id);
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            legalContentService.deleteContent(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

