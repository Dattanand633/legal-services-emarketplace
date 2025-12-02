package com.legalsahyog.legalsahyoghub.controller;

import com.legalsahyog.legalsahyoghub.entity.Notification;
import com.legalsahyog.legalsahyoghub.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Notification>> getProviderNotifications(@PathVariable Long providerId) {
        List<Notification> notifications = notificationService.getProviderNotifications(providerId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<Notification>> getAdminNotifications(@PathVariable Long adminId) {
        List<Notification> notifications = notificationService.getAdminNotifications(adminId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/provider/{providerId}/unread")
    public ResponseEntity<List<Notification>> getUnreadProviderNotifications(@PathVariable Long providerId) {
        List<Notification> notifications = notificationService.getUnreadProviderNotifications(providerId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/admin/{adminId}/unread")
    public ResponseEntity<List<Notification>> getUnreadAdminNotifications(@PathVariable Long adminId) {
        List<Notification> notifications = notificationService.getUnreadAdminNotifications(adminId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCountForUser(@PathVariable Long userId) {
        Long count = notificationService.getUnreadCountForUser(userId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/provider/{providerId}/unread-count")
    public ResponseEntity<Long> getUnreadCountForProvider(@PathVariable Long providerId) {
        Long count = notificationService.getUnreadCountForProvider(providerId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/admin/{adminId}/unread-count")
    public ResponseEntity<Long> getUnreadCountForAdmin(@PathVariable Long adminId) {
        Long count = notificationService.getUnreadCountForAdmin(adminId);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Map<String, Object> notificationData) {
        try {
            String title = notificationData.get("title").toString();
            String message = notificationData.get("message").toString();
            Notification.NotificationType type = Notification.NotificationType.valueOf(
                notificationData.get("type").toString().toUpperCase()
            );
            
            Notification notification = notificationService.createNotification(title, message, type);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/user")
    public ResponseEntity<Notification> createUserNotification(@RequestBody Map<String, Object> notificationData) {
        try {
            Long userId = Long.valueOf(notificationData.get("userId").toString());
            String title = notificationData.get("title").toString();
            String message = notificationData.get("message").toString();
            Notification.NotificationType type = Notification.NotificationType.valueOf(
                notificationData.get("type").toString().toUpperCase()
            );
            
            Notification notification = notificationService.createUserNotification(userId, title, message, type);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/provider")
    public ResponseEntity<Notification> createProviderNotification(@RequestBody Map<String, Object> notificationData) {
        try {
            Long providerId = Long.valueOf(notificationData.get("providerId").toString());
            String title = notificationData.get("title").toString();
            String message = notificationData.get("message").toString();
            Notification.NotificationType type = Notification.NotificationType.valueOf(
                notificationData.get("type").toString().toUpperCase()
            );
            
            Notification notification = notificationService.createProviderNotification(providerId, title, message, type);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable @NonNull Long id) {
        try {
            Notification notification = notificationService.markAsRead(id);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsReadForUser(@PathVariable Long userId) {
        try {
            notificationService.markAllAsReadForUser(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/provider/{providerId}/read-all")
    public ResponseEntity<Void> markAllAsReadForProvider(@PathVariable Long providerId) {
        try {
            notificationService.markAllAsReadForProvider(providerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/admin/{adminId}/read-all")
    public ResponseEntity<Void> markAllAsReadForAdmin(@PathVariable Long adminId) {
        try {
            notificationService.markAllAsReadForAdmin(adminId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/archive")
    public ResponseEntity<Notification> archiveNotification(@PathVariable @NonNull Long id) {
        try {
            Notification notification = notificationService.archiveNotification(id);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable @NonNull Long id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

