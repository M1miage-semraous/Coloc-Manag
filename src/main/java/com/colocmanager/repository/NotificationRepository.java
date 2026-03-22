package com.colocmanager.repository;

import com.colocmanager.model.Notification;
import com.colocmanager.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotificationRepository {

    private final List<Notification> notifications = new ArrayList<>();

    public void save(Notification notification) {
        notifications.add(notification);
    }

    public List<Notification> findAll() {
        return new ArrayList<>(notifications);
    }

    public Optional<Notification> findById(UUID id) {
        return notifications.stream()
                .filter(notification -> notification.getId() != null && notification.getId().equals(id))
                .findFirst();
    }

    public List<Notification> findByRecipient(User user) {
        return notifications.stream()
                .filter(notification -> notification.getRecipient() != null
                        && notification.getRecipient().getId() != null
                        && user != null
                        && user.getId() != null
                        && notification.getRecipient().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    public List<Notification> findUnreadByRecipient(User user) {
        return notifications.stream()
                .filter(notification -> notification.getRecipient() != null
                        && notification.getRecipient().getId() != null
                        && user != null
                        && user.getId() != null
                        && notification.getRecipient().getId().equals(user.getId())
                        && !notification.isRead())
                .collect(Collectors.toList());
    }

    public void delete(UUID id) {
        notifications.removeIf(notification -> notification.getId() != null && notification.getId().equals(id));
    }
}