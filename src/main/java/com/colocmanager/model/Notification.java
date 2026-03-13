package com.colocmanager.model;

import com.colocmanager.enums.NotificationType;
import java.util.UUID;
import  java.time.LocalDateTime;

public class Notification {

    private UUID id;
    private String title;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;


}












