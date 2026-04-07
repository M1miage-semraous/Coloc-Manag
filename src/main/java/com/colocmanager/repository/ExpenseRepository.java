package com.colocmanager.repository;

import com.colocmanager.DatabaseManager;
import com.colocmanager.model.Expense;
import com.colocmanager.model.ExpenseShare;
import com.colocmanager.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ExpenseRepository {

    private final Connection connection;

    public ExpenseRepository() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public ExpenseRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Expense expense) {
        String sql = """
            INSERT OR REPLACE INTO expenses (id, description, amount, date, paid_by_id)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, expense.getId().toString());
            stmt.setString(2, expense.getLabel());
            stmt.setDouble(3, expense.getAmount());
            stmt.setString(4, expense.getExpenseDate().toString());
            stmt.setString(5, expense.getPaidBy().getId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur save expense : " + e.getMessage());
        }
    }

    public List<Expense> findAll() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                expenses.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findAll expenses : " + e.getMessage());
        }
        return expenses;
    }

    public Optional<Expense> findById(UUID id) {
        String sql = "SELECT * FROM expenses WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findById expense : " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Expense> findByPaidBy(UUID userId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE paid_by_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByPaidBy : " + e.getMessage());
        }
        return expenses;
    }

    public List<Expense> findByMonth(int month, int year) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE strftime('%m', date) = ? AND strftime('%Y', date) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, String.format("%02d", month));
            stmt.setString(2, String.valueOf(year));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByMonth expenses : " + e.getMessage());
        }
        return expenses;
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur delete expense : " + e.getMessage());
        }
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM expenses";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Erreur count expenses : " + e.getMessage());
        }
        return 0;
    }

    private Expense mapResultSet(ResultSet rs) throws SQLException {
        String expenseId = rs.getString("id");
        String paidById  = rs.getString("paid_by_id");
        User paidBy = loadUserById(paidById);

        Expense expense = new Expense(
                rs.getString("description"),
                rs.getDouble("amount"),
                "",
                LocalDate.parse(rs.getString("date")),
                paidBy
        );

        expense.setId(UUID.fromString(expenseId)); // ← ID chargé depuis la base

        List<ExpenseShare> shares = loadSharesForExpense(expenseId);
        expense.setShares(shares);
        return expense;
    }

    private List<ExpenseShare> loadSharesForExpense(String expenseId) {
        List<ExpenseShare> shares = new ArrayList<>();
        String sql = "SELECT * FROM expense_shares WHERE expense_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, expenseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String userId  = rs.getString("user_id");
                double amount  = rs.getDouble("amount");
                boolean isPaid = rs.getInt("is_paid") == 1;
                String shareId = rs.getString("id");
                User user = loadUserById(userId);
                if (user != null) {
                    ExpenseShare share = new ExpenseShare(user, amount);
                    share.setId(UUID.fromString(shareId));
                    share.setPaid(isPaid);
                    shares.add(share);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur loadSharesForExpense : " + e.getMessage());
        }
        return shares;
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
            System.err.println("Erreur loadUserById expense : " + e.getMessage());
        }
        return null;
    }
}