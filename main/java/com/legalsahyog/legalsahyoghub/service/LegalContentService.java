package com.legalsahyog.legalsahyoghub.service;

import com.legalsahyog.legalsahyoghub.entity.LegalContent;
import com.legalsahyog.legalsahyoghub.repository.LegalContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LegalContentService {
    
    @Autowired
    private LegalContentRepository legalContentRepository;
    
    public List<LegalContent> getAllContent() {
        return legalContentRepository.findAll();
    }
    
    public List<LegalContent> getPublishedContent() {
        List<LegalContent> contentList = legalContentRepository.findByStatusOrderByCreatedAtDesc(LegalContent.ContentStatus.PUBLISHED);
        // Ensure all content has a view count initialized and persist to database
        boolean needsSave = false;
        for (LegalContent content : contentList) {
            if (content.getViewCount() == null) {
                content.setViewCount(0);
                needsSave = true;
            }
        }
        // Save all at once if needed
        if (needsSave) {
            legalContentRepository.saveAll(contentList);
            legalContentRepository.flush(); // Force immediate write to database
        }
        return contentList;
    }
    
    public List<LegalContent> getContentByType(LegalContent.ContentType contentType) {
        return legalContentRepository.findByContentTypeAndStatusOrderByCreatedAtDesc(contentType, LegalContent.ContentStatus.PUBLISHED);
    }
    
    public List<LegalContent> getContentByCategory(String category) {
        return legalContentRepository.findByCategoryAndStatusOrderByCreatedAtDesc(category, LegalContent.ContentStatus.PUBLISHED);
    }
    
    public List<LegalContent> searchContent(String keyword) {
        return legalContentRepository.findByKeywordAndStatus(keyword, LegalContent.ContentStatus.PUBLISHED);
    }
    
    public List<LegalContent> getFeaturedContent() {
        return legalContentRepository.findByIsFeaturedTrueAndStatusOrderByCreatedAtDesc(LegalContent.ContentStatus.PUBLISHED);
    }
    
    public List<LegalContent> getPopularContent() {
        return legalContentRepository.findByStatusOrderByViewCountDesc(LegalContent.ContentStatus.PUBLISHED);
    }
    
    public Optional<LegalContent> getContentById(@NonNull Long id) {
        Optional<LegalContent> contentOptional = legalContentRepository.findById(id);
        if (contentOptional.isPresent()) {
            // Increment view count and persist to database
            LegalContent legalContent = contentOptional.get();
            if (legalContent.getViewCount() == null) {
                legalContent.setViewCount(0);
            }
            legalContent.setViewCount(legalContent.getViewCount() + 1);
            // Save to database to persist the view count
            LegalContent savedContent = legalContentRepository.saveAndFlush(legalContent);
            return Optional.of(savedContent);
        }
        return Optional.empty();
    }
    
    public LegalContent createContent(LegalContent content) {
        // Initialize view count if null
        if (content.getViewCount() == null) {
            content.setViewCount(0);
        }
        
        // Set status based on isPublished flag
        if (content.getIsPublished() != null && content.getIsPublished()) {
            content.setStatus(LegalContent.ContentStatus.PUBLISHED);
        } else {
            content.setStatus(LegalContent.ContentStatus.DRAFT);
        }
        
        return legalContentRepository.save(content);
    }
    
    public LegalContent updateContent(@NonNull Long id, LegalContent contentDetails) {
        Optional<LegalContent> contentOptional = legalContentRepository.findById(id);
        if (contentOptional.isPresent()) {
            LegalContent content = contentOptional.get();
            // Preserve view count - don't overwrite it
            Integer existingViewCount = content.getViewCount();
            if (existingViewCount == null) {
                existingViewCount = 0;
            }
            
            content.setTitle(contentDetails.getTitle());
            content.setContent(contentDetails.getContent());
            content.setSummary(contentDetails.getSummary());
            content.setCategory(contentDetails.getCategory());
            content.setTags(contentDetails.getTags());
            content.setContentType(contentDetails.getContentType());
            content.setIsFeatured(contentDetails.getIsFeatured());
            // Preserve the view count
            content.setViewCount(existingViewCount);
            
            return legalContentRepository.saveAndFlush(content);
        }
        throw new RuntimeException("Content not found with id: " + id);
    }
    
    public LegalContent publishContent(@NonNull Long id) {
        Optional<LegalContent> contentOptional = legalContentRepository.findById(id);
        if (contentOptional.isPresent()) {
            LegalContent content = contentOptional.get();
            content.setStatus(LegalContent.ContentStatus.PUBLISHED);
            return legalContentRepository.save(content);
        }
        throw new RuntimeException("Content not found with id: " + id);
    }
    
    public LegalContent unpublishContent(@NonNull Long id) {
        Optional<LegalContent> contentOptional = legalContentRepository.findById(id);
        if (contentOptional.isPresent()) {
            LegalContent content = contentOptional.get();
            content.setStatus(LegalContent.ContentStatus.DRAFT);
            return legalContentRepository.save(content);
        }
        throw new RuntimeException("Content not found with id: " + id);
    }
    
    public void deleteContent(@NonNull Long id) {
        legalContentRepository.deleteById(id);
    }
    
    public List<String> getAllCategories() {
        return legalContentRepository.findDistinctCategories();
    }
    
    public List<String> getAllTags() {
        return legalContentRepository.findDistinctTags();
    }
}

