package com.colocmanager;

import com.colocmanager.enums.Role;
import com.colocmanager.repository.*;
import com.colocmanager.service.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static UserService userService;
    public static TaskService taskService;
    public static ExpenseService expenseService;
    public static NotificationService notificationService;

    @Override
    public void start(Stage primaryStage) {

        DatabaseManager.getInstance();
        SceneManager.init(primaryStage);

        UserRepository userRepo = new UserRepository();
        TaskRepository taskRepo = new TaskRepository();
        TaskValidationRepository valRepo = new TaskValidationRepository();
        ExpenseRepository expenseRepo = new ExpenseRepository();
        ExpenseShareRepository shareRepo = new ExpenseShareRepository();
        NotificationRepository notifRepo = new NotificationRepository();

        userService = new UserService(userRepo);
        taskService = new TaskService(taskRepo, valRepo, notifRepo, userRepo);
        expenseService = new ExpenseService(expenseRepo, shareRepo);
        notificationService = new NotificationService(notifRepo);

        if (userService.findByEmail("adnane@gmail.com").isEmpty()) {
            userService.createUser("Adnan", "adnane@gmail.com", "1234", Role.ADMIN);
        }
        if (userService.findByEmail("alice@gmail.com").isEmpty()) {
            userService.createUser("Alice", "alice@gmail.com", "1234", Role.USER);
        }
        if (userService.findByEmail("bob@gmail.com").isEmpty()) {
            userService.createUser("Bob", "bob@gmail.com", "1234", Role.USER);
        }

        SceneManager.showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}