package com.colocmanager.repository;

import com.colocmanager.DatabaseManager;
import com.colocmanager.model.ExpenseShare;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ExpenseShareRepository {

    private final Connection connection;

    public ExpenseShareRepository() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public void save(ExpenseShare expenseShare, UUID expenseId) {
        String sql = """
            INSERT OR REPLACE INTO expense_shares (id, expense_id, user_id, amount, is_paid)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, expenseShare.getId().toString());
            stmt.setString(2, expenseId.toString());
            stmt.setString(3, expenseShare.getUser().getId().toString());
            stmt.setDouble(4, expenseShare.getAmountDue());
            stmt.setInt(5, 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur save expenseShare : " + e.getMessage());
        }
    }

    public List<ExpenseShare> findAll() {
        List<ExpenseShare> shares = new ArrayList<>();
        String sql = "SELECT * FROM expense_shares";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                shares.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findAll expenseShares : " + e.getMessage());
        }
        return shares;
    }

    public Optional<ExpenseShare> findById(UUID id) {
        String sql = "SELECT * FROM expense_shares WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findById expenseShare : " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<ExpenseShare> findByUserId(UUID userId) {
        List<ExpenseShare> shares = new ArrayList<>();
        String sql = "SELECT * FROM expense_shares WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                shares.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByUserId : " + e.getMessage());
        }
        return shares;
    }

    public List<ExpenseShare> findByExpenseId(UUID expenseId) {
        List<ExpenseShare> shares = new ArrayList<>();
        String sql = "SELECT * FROM expense_shares WHERE expense_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, expenseId.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                shares.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByExpenseId : " + e.getMessage());
        }
        return shares;
    }

    public double getTotalDueByUser(UUID userId) {
        String sql = "SELECT SUM(amount) FROM expense_shares WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Erreur getTotalDueByUser : " + e.getMessage());
        }
        return 0.0;
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM expense_shares WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur delete expenseShare : " + e.getMessage());
        }
    }

    public void deleteByExpenseId(UUID expenseId) {
        String sql = "DELETE FROM expense_shares WHERE expense_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, expenseId.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur deleteByExpenseId : " + e.getMessage());
        }
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM expense_shares";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Erreur count expenseShares : " + e.getMessage());
        }
        return 0;
    }

    private ExpenseShare mapResultSet(ResultSet rs) throws SQLException {
        // user chargé à null — à résoudre via UserRepository si besoin
        return new ExpenseShare(null, rs.getDouble("amount"));
    }
}
