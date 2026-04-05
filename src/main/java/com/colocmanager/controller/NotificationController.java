package com.colocmanager.controller;

import com.colocmanager.MainApp;
import com.colocmanager.model.Notification;
import com.colocmanager.model.User;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class NotificationController {

    private final User currentUser;

    public NotificationController(User currentUser) {
        this.currentUser = currentUser;
    }

    public List<Notification> getNotifications() {
        return MainApp.notificationService.getNotificationsByUser(currentUser);
    }

    public int getUnreadCount() {
        return MainApp.notificationService.getUnreadNotifications(currentUser).size();
    }

    public void handleMarkAllAsRead(Label lblResult) {
        MainApp.notificationService.markAllAsRead(currentUser);
        lblResult.setStyle("-fx-text-fill: #0D9488; -fx-font-size: 12;");
        lblResult.setText("✓ Toutes les notifications marquées comme lues.");
    }
}