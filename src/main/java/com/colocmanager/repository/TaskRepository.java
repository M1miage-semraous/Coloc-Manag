package com.colocmanager.repository;

import com.colocmanager.DatabaseManager;
import com.colocmanager.enums.ImportanceLevel;
import com.colocmanager.enums.TaskStatus;
import com.colocmanager.model.Task;
import com.colocmanager.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskRepository {

    private final Connection connection;

    public TaskRepository() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public void save(Task task) {
        String sql = """
            INSERT OR REPLACE INTO tasks (id, title, description, status, priority,
            importance, estimated_hours, deadline, created_at, creator_id, assigned_to_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, task.getId().toString());
            stmt.setString(2, task.getTitle());
            stmt.setString(3, task.getDescription());
            stmt.setString(4, task.getStatus().name());
            stmt.setString(5, task.getCalculatedPriority().name());
            stmt.setString(6, task.getImportance().name());
            stmt.setInt(7, task.getEstimatedTimeHours());
            stmt.setString(8, task.getDeadline() != null ? task.getDeadline().toString() : null);
            stmt.setString(9, task.getCreatedAt().toString());
            stmt.setString(10, task.getCreatedBy() != null ? task.getCreatedBy().getId().toString() : null);
            stmt.setString(11, task.getAssignedUser() != null ? task.getAssignedUser().getId().toString() : null);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur save task : " + e.getMessage());
        }
    }

    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tasks.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findAll tasks : " + e.getMessage());
        }
        return tasks;
    }

    public Optional<Task> findById(UUID id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findById task : " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Task> findByAssignedUser(User user) {
        List<Task> tasks = new ArrayList<>();
        if (user == null || user.getId() == null) return tasks;
        String sql = "SELECT * FROM tasks WHERE assigned_to_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getId().toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByAssignedUser : " + e.getMessage());
        }
        return tasks;
    }

    public List<Task> findByCreatedBy(User user) {
        List<Task> tasks = new ArrayList<>();
        if (user == null || user.getId() == null) return tasks;
        String sql = "SELECT * FROM tasks WHERE creator_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getId().toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByCreatedBy : " + e.getMessage());
        }
        return tasks;
    }

    public List<Task> findByMonth(int month, int year) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE strftime('%m', created_at) = ? AND strftime('%Y', created_at) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, String.format("%02d", month));
            stmt.setString(2, String.valueOf(year));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByMonth : " + e.getMessage());
        }
        return tasks;
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur delete task : " + e.getMessage());
        }
    }

    private Task mapResultSet(ResultSet rs) throws SQLException {
        User assignedUser = null;
        String assignedId = rs.getString("assigned_to_id");
        if (assignedId != null) {
            assignedUser = loadUserById(assignedId);
        }

        User createdBy = null;
        String creatorId = rs.getString("creator_id");
        if (creatorId != null) {
            createdBy = loadUserById(creatorId);
        }

        Task task = new Task(
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("deadline") != null ? LocalDate.parse(rs.getString("deadline")) : null,
                ImportanceLevel.valueOf(rs.getString("importance")),
                rs.getInt("estimated_hours"),
                assignedUser,
                createdBy
        );
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setId(UUID.fromString(rs.getString("id")));
        return task;
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
            System.err.println("Erreur loadUserById : " + e.getMessage());
        }
        return null;
    }
}