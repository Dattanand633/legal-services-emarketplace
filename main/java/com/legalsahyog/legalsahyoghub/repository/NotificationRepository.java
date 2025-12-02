package com.legalsahyog.legalsahyoghub.repository;

import com.legalsahyog.legalsahyoghub.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Notification.NotificationStatus status);
    
    List<Notification> findByProviderIdAndStatusOrderByCreatedAtDesc(Long providerId, Notification.NotificationStatus status);
    
    List<Notification> findByAdminIdAndStatusOrderByCreatedAtDesc(Long adminId, Notification.NotificationStatus status);
    
    List<Notification> findByUserIdAndIsReadFalseAndStatusOrderByCreatedAtDesc(Long userId, Notification.NotificationStatus status);
    
    List<Notification> findByProviderIdAndIsReadFalseAndStatusOrderByCreatedAtDesc(Long providerId, Notification.NotificationStatus status);
    
    List<Notification> findByAdminIdAndIsReadFalseAndStatusOrderByCreatedAtDesc(Long adminId, Notification.NotificationStatus status);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.isRead = false AND n.status = :status")
    Long countUnreadByUserId(@Param("userId") Long userId, @Param("status") Notification.NotificationStatus status);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.providerId = :providerId AND n.isRead = false AND n.status = :status")
    Long countUnreadByProviderId(@Param("providerId") Long providerId, @Param("status") Notification.NotificationStatus status);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.adminId = :adminId AND n.isRead = false AND n.status = :status")
    Long countUnreadByAdminId(@Param("adminId") Long adminId, @Param("status") Notification.NotificationStatus status);
    
    @Query("SELECT n FROM Notification n WHERE n.expiresAt < :now AND n.status = :status")
    List<Notification> findExpiredNotifications(@Param("now") LocalDateTime now, @Param("status") Notification.NotificationStatus status);
    
    @Query("SELECT n FROM Notification n WHERE n.type = :type AND n.status = :status ORDER BY n.createdAt DESC")
    List<Notification> findByTypeAndStatus(@Param("type") Notification.NotificationType type, @Param("status") Notification.NotificationStatus status);
    
    @Query("SELECT n FROM Notification n WHERE n.relatedEntityType = :entityType AND n.relatedEntityId = :entityId AND n.status = :status")
    List<Notification> findByRelatedEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId, @Param("status") Notification.NotificationStatus status);
}

