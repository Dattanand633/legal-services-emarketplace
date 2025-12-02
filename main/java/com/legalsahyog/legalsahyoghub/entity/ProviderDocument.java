package com.legalsahyog.legalsahyoghub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "provider_documents")
public class ProviderDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    private DocumentType documentType;
    
    @NotBlank
    @Column(name = "document_url")
    private String documentUrl;
    
    @Enumerated(EnumType.STRING)
    private DocumentStatus status = DocumentStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Admin reviewedBy;
    
    public enum DocumentType {
        BAR_COUNCIL_CERTIFICATE, EDUCATION_CERTIFICATE, ID_PROOF, ADDRESS_PROOF, PROFILE_PHOTO
    }
    
    public enum DocumentStatus {
        PENDING, APPROVED, REJECTED
    }
    
    // Constructors
    public ProviderDocument() {}
    
    public ProviderDocument(Provider provider, DocumentType documentType, String documentUrl) {
        this.provider = provider;
        this.documentType = documentType;
        this.documentUrl = documentUrl;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Provider getProvider() {
        return provider;
    }
    
    public void setProvider(Provider provider) {
        this.provider = provider;
    }
    
    public DocumentType getDocumentType() {
        return documentType;
    }
    
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
    
    public String getDocumentUrl() {
        return documentUrl;
    }
    
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
    
    public DocumentStatus getStatus() {
        return status;
    }
    
    public void setStatus(DocumentStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    
    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
    
    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    
    public Admin getReviewedBy() {
        return reviewedBy;
    }
    
    public void setReviewedBy(Admin reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
}
