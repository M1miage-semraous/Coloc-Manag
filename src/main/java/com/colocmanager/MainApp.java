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

        UserRepository userRepo = new UserRepository();
        TaskRepository taskRepo = new TaskRepository();
        TaskValidationRepository valRepo = new TaskValidationRepository();
        ExpenseRepository expenseRepo = new ExpenseRepository();
        ExpenseShareRepository shareRepo = new ExpenseShareRepository();
        NotificationRepository notifRepo = new NotificationRepository();

        userService = new UserService(userRepo);
        taskService = new TaskService(taskRepo, valRepo);
        expenseService = new ExpenseService(expenseRepo, shareRepo);
        notificationService = new NotificationService(notifRepo);

        userService.createUser("Adnan", "adnane@gmail.com", "1234", Role.ADMIN);
        userService.createUser("Alice", "alice@gmail.com", "1234", Role.USER);
        userService.createUser("Bob", "bob@gmail.com", "1234", Role.USER);

        new LoginView(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}