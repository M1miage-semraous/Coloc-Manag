package com.colocmanager;

import com.colocmanager.model.User;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage primaryStage;
    private static User currentUser;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void showLogin() {
        new LoginView(primaryStage);
    }

    public static void showDashboard(User user) {
        currentUser = user;
        new DashboardView(primaryStage, user);
    }

    public static Stage getStage() {
        return primaryStage;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}