package com.colocmanager.repository;

import com.colocmanager.DatabaseManager;
import com.colocmanager.enums.NotificationType;
import com.colocmanager.model.Notification;
import com.colocmanager.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NotificationRepository {

    private final Connection connection;

    public NotificationRepository() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public NotificationRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Notification notification) {
        String sql = """
            INSERT OR REPLACE INTO notifications (id, title, message, type, is_read, created_at, user_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, notification.getId().toString());
            stmt.setString(2, notification.getTitle());
            stmt.setString(3, notification.getMessage());
            stmt.setString(4, notification.getType().name());
            stmt.setInt(5, notification.isRead() ? 1 : 0);
            stmt.setString(6, notification.getCreatedAt().toString());
            stmt.setString(7, notification.getRecipient().getId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur save notification : " + e.getMessage());
        }
    }

    public void markAsRead(UUID id) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur markAsRead notification : " + e.getMessage());
        }
    }

    public List<Notification> findAll() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String title   = rs.getString("title");
                String message = rs.getString("message");
                String type    = rs.getString("type");
                int isRead     = rs.getInt("is_read");
                String userId  = rs.getString("user_id");
                User recipient = loadUserById(userId);
                if (recipient != null) {
                    Notification n = new Notification(title, message, NotificationType.valueOf(type), recipient);
                    if (isRead == 1) n.markAsRead();
                    notifications.add(n);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findAll notifications : " + e.getMessage());
        }
        return notifications;
    }

    public Optional<Notification> findById(UUID id) {
        String sql = "SELECT * FROM notifications WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String title   = rs.getString("title");
                String message = rs.getString("message");
                String type    = rs.getString("type");
                int isRead     = rs.getInt("is_read");
                String userId  = rs.getString("user_id");
                User recipient = loadUserById(userId);
                if (recipient != null) {
                    Notification n = new Notification(title, message, NotificationType.valueOf(type), recipient);
                    if (isRead == 1) n.markAsRead();
                    return Optional.of(n);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findById notification : " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Notification> findByRecipient(User user) {
        List<Notification> notifications = new ArrayList<>();
        if (user == null || user.getId() == null) return notifications;
        String sql = "SELECT * FROM notifications WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getId().toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String title   = rs.getString("title");
                String message = rs.getString("message");
                String type    = rs.getString("type");
                int isRead     = rs.getInt("is_read");
                String userId  = rs.getString("user_id");
                User recipient = loadUserById(userId);
                if (recipient != null) {
                    Notification n = new Notification(title, message, NotificationType.valueOf(type), recipient);
                    if (isRead == 1) n.markAsRead();
                    notifications.add(n);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByRecipient : " + e.getMessage());
        }
        return notifications;
    }

    public List<Notification> findUnreadByRecipient(User user) {
        List<Notification> notifications = new ArrayList<>();
        if (user == null || user.getId() == null) return notifications;
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getId().toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String title   = rs.getString("title");
                String message = rs.getString("message");
                String type    = rs.getString("type");
                String userId  = rs.getString("user_id");
                User recipient = loadUserById(userId);
                if (recipient != null) {
                    Notification n = new Notification(title, message, NotificationType.valueOf(type), recipient);
                    notifications.add(n);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findUnreadByRecipient : " + e.getMessage());
        }
        return notifications;
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur delete notification : " + e.getMessage());
        }
    }

    private User loadUserById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        com.colocmanager.enums.Role.valueOf(rs.getString("role"))
                );
                user.setId(UUID.fromString(rs.getString("id")));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Erreur loadUserById notification : " + e.getMessage());
        }
        return null;
    }
}