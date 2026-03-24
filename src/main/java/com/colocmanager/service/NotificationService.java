package com.colocmanager.service;

import com.colocmanager.enums.NotificationType;
import com.colocmanager.model.Notification;
import com.colocmanager.model.User;
import com.colocmanager.repository.NotificationRepository;

import java.util.List;
import java.util.UUID;

public class NotificationService {

    private NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification sendNotification(String title, String message, NotificationType type, User recipient) {
        Notification notification = new Notification(title, message, type, recipient);
        notificationRepository.save(notification);
        return notification;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByUser(UUID userId) {
        return notificationRepository.findByRecipient(userId);
    }

    public List<Notification> getUnreadNotifications(UUID userId) {
        return notificationRepository.findUnreadByRecipient(userId);
    }

    public int countUnread(UUID userId) {
        return notificationRepository.findUnreadByRecipient(userId).size();
    }

    public void markAsRead(UUID notificationId) {
        notificationRepository.findAll().stream()
                .filter(n -> n.getId().equals(notificationId))
                .findFirst()
                .ifPresent(Notification::markAsRead);
    }

    public void markAllAsRead(UUID userId) {
        notificationRepository.findUnreadByRecipient(userId)
                .forEach(Notification::markAsRead);
    }

    public void deleteNotification(UUID id) {
        notificationRepository.delete(id);
    }
}
