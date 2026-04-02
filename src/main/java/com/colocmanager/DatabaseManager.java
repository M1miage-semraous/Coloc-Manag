
package com.colocmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:colocmanager.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            initTables();
            System.out.println("Base de données connectée.");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur connexion base de données : " + e.getMessage());
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initTables() throws SQLException {
        Statement stmt = connection.createStatement();

        // Table users
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id TEXT PRIMARY KEY,
                full_name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                created_at TEXT NOT NULL
            )
        """);

        // Table tasks
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS tasks (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                description TEXT,
                status TEXT NOT NULL,
                priority TEXT NOT NULL,
                importance TEXT NOT NULL,
                estimated_hours REAL,
                deadline TEXT,
                created_at TEXT NOT NULL,
                creator_id TEXT,
                assigned_to_id TEXT
            )
        """);

        // Table notifications
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS notifications (
                id TEXT PRIMARY KEY,
                message TEXT NOT NULL,
                type TEXT NOT NULL,
                is_read INTEGER NOT NULL DEFAULT 0,
                created_at TEXT NOT NULL,
                user_id TEXT NOT NULL
            )
        """);

        // Table expenses
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS expenses (
                id TEXT PRIMARY KEY,
                description TEXT NOT NULL,
                amount REAL NOT NULL,
                date TEXT NOT NULL,
                paid_by_id TEXT NOT NULL
            )
        """);

        // Table expense_shares
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS expense_shares (
                id TEXT PRIMARY KEY,
                expense_id TEXT NOT NULL,
                user_id TEXT NOT NULL,
                amount REAL NOT NULL,
                is_paid INTEGER NOT NULL DEFAULT 0
            )
        """);

        // Table task_validations
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS task_validations (
                id TEXT PRIMARY KEY,
                task_id TEXT NOT NULL,
                validator_id TEXT NOT NULL,
                decision TEXT NOT NULL,
                comment TEXT,
                validated_at TEXT NOT NULL
            )
        """);

        stmt.close();
    }
}