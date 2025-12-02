package com.legalsahyog.legalsahyoghub.repository;

import com.legalsahyog.legalsahyoghub.entity.Provider;
import com.legalsahyog.legalsahyoghub.entity.ProviderDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderDocumentRepository extends JpaRepository<ProviderDocument, Long> {
    
    List<ProviderDocument> findByProvider(Provider provider);
    
    List<ProviderDocument> findByStatus(ProviderDocument.DocumentStatus status);
    
    List<ProviderDocument> findByProviderAndDocumentType(Provider provider, ProviderDocument.DocumentType documentType);
}
