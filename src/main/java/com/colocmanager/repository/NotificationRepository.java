package com.colocmanager.repository;

import com.colocmanager.DatabaseManager;
import com.colocmanager.enums.NotificationType;
import com.colocmanager.model.Notification;
import com.colocmanager.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NotificationRepository {

    private final Connection connection;

    public NotificationRepository() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public void save(Notification notification) {
        String sql = """
            INSERT OR REPLACE INTO notifications (id, message, type, is_read, created_at, user_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, notification.getId().toString());
            stmt.setString(2, notification.getMessage());
            stmt.setString(3, notification.getType().name());
            stmt.setInt(4, notification.isRead() ? 1 : 0);
            stmt.setString(5, notification.getCreatedAt().toString());
            stmt.setString(6, notification.getRecipient().getId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur save notification : " + e.getMessage());
        }
    }

    public List<Notification> findAll() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                notifications.add(mapResultSet(rs));
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
                return Optional.of(mapResultSet(rs));
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
                notifications.add(mapResultSet(rs));
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
                notifications.add(mapResultSet(rs));
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

    private Notification mapResultSet(ResultSet rs) throws SQLException {
        // recipient chargé à null — à résoudre via UserRepository si besoin
        Notification notification = new Notification(
                "",
                rs.getString("message"),
                NotificationType.valueOf(rs.getString("type")),
                null
        );
        return notification;
    }
}