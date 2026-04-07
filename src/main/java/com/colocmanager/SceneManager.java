package com.colocmanager;

import com.colocmanager.model.User;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class SceneManager {

    private static Stage primaryStage;
    private static User currentUser;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void applyCSS(Scene scene) {
        String css = Objects.requireNonNull(SceneManager.class.getResource("/styles.css")).toExternalForm();
        scene.getStylesheets().add(css);
    }

    public static void showLogin() {
        new LoginView(primaryStage);
    }

    public static void showDashboard(User user) {
        currentUser = user;
        new DashboardView(primaryStage, user);
    }

    public static void showTasks(User user) {
        new TaskView(primaryStage, user);
    }

    public static void showExpenses(User user) {
        new ExpenseView(primaryStage, user);
    }

    public static void showNotifications(User user) {
        new NotificationView(primaryStage, user);
    }

    public static void showAdmin(User user) {
        new AdminView(primaryStage, user);
    }

    public static void showMonthlyReport(User user) {
        new MonthlyReportView(primaryStage, user);
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}