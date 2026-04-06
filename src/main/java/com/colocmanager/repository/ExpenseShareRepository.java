package com.colocmanager.repository;

import com.colocmanager.DatabaseManager;
import com.colocmanager.model.ExpenseShare;
import com.colocmanager.model.User;

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

    public ExpenseShareRepository(Connection connection) {
        this.connection = connection;
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
            stmt.setInt(5, expenseShare.isPaid() ? 1 : 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur save expenseShare : " + e.getMessage());
        }
    }

    public void markAsPaid(UUID shareId) {
        String sql = "UPDATE expense_shares SET is_paid = 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, shareId.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur markAsPaid : " + e.getMessage());
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
        String sql = "SELECT SUM(amount) FROM expense_shares WHERE user_id = ? AND is_paid = 0";
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
        String userId  = rs.getString("user_id");
        double amount  = rs.getDouble("amount");
        boolean isPaid = rs.getInt("is_paid") == 1;
        String shareId = rs.getString("id");
        User user = loadUserById(userId);
        ExpenseShare share = new ExpenseShare(user, amount);
        share.setId(UUID.fromString(shareId));
        share.setPaid(isPaid);
        return share;
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
            System.err.println("Erreur loadUserById share : " + e.getMessage());
        }
        return null;
    }
}