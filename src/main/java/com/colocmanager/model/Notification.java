package com.colocmanager.model;

import com.colocmanager.enums.NotificationType;
import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {


    private UUID id;
    private String title;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private User recipient;

    // --- CONSTRUCTEUR ---
    public Notification(String title, String message, NotificationType type, User recipient) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
        this.recipient = recipient;
    }



    public void markAsRead() {
        this.isRead = true;
    }


    public UUID getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    // pas de setType() : le type d'une notification ne change pas après création

    public boolean isRead() { return isRead; }


    public LocalDateTime getCreatedAt() { return createdAt; }

    public User getRecipient() { return recipient; }

    @Override
    public String toString() {
        return "Notification{id=" + id + ", title='" + title + "', type=" + type +
                ", isRead=" + isRead + ", recipient=" + recipient.getFullName() + "}";
    }
}












