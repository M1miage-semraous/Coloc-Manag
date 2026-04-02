package com.colocmanager.repository;

import com.colocmanager.DatabaseManager;
import com.colocmanager.enums.ValidationDecision;
import com.colocmanager.model.TaskValidation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskValidationRepository {

    private final Connection connection;

    public TaskValidationRepository() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public void save(TaskValidation validation) {
        if (validation == null || validation.getTask() == null || validation.getTask().getId() == null) {
            return;
        }

        String sql = """
            INSERT OR REPLACE INTO task_validations (id, task_id, validator_id, decision, comment, validated_at)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, validation.getId().toString());
            stmt.setString(2, validation.getTask().getId().toString());
            stmt.setString(3, validation.getValidator() != null ? validation.getValidator().getId().toString() : null);
            stmt.setString(4, validation.getDecision().name());
            stmt.setString(5, validation.getComment());
            stmt.setString(6, validation.getValidatedAt().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur save taskValidation : " + e.getMessage());
        }
    }

    public List<TaskValidation> findAll() {
        List<TaskValidation> validations = new ArrayList<>();
        String sql = "SELECT * FROM task_validations";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                validations.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findAll taskValidations : " + e.getMessage());
        }
        return validations;
    }

    public Optional<TaskValidation> findById(UUID id) {
        String sql = "SELECT * FROM task_validations WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findById taskValidation : " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<TaskValidation> findByTaskId(UUID taskId) {
        String sql = "SELECT * FROM task_validations WHERE task_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, taskId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByTaskId : " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<TaskValidation> findByValidatorId(UUID validatorId) {
        List<TaskValidation> validations = new ArrayList<>();
        String sql = "SELECT * FROM task_validations WHERE validator_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, validatorId.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                validations.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByValidatorId : " + e.getMessage());
        }
        return validations;
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM task_validations WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur delete taskValidation : " + e.getMessage());
        }
    }

    private TaskValidation mapResultSet(ResultSet rs) throws SQLException {
        TaskValidation validation = new TaskValidation();
        validation.setDecision(ValidationDecision.valueOf(rs.getString("decision")));
        validation.setComment(rs.getString("comment"));
        // task et validator chargés à null — à résoudre via leurs repositories si besoin
        return validation;
    }
}