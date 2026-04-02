package com.colocmanager.controller;

import com.colocmanager.MainApp;
import com.colocmanager.model.Task;
import com.colocmanager.model.User;
import com.colocmanager.enums.ImportanceLevel;
import com.colocmanager.enums.Role;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.LocalDate;
import java.util.List;

public class DashboardController {

    private final User currentUser;

    public DashboardController(User currentUser) {
        this.currentUser = currentUser;
    }

    // --- Accueil ---

    public int getTotalTasks() {
        return MainApp.taskService.getAllTasks().size();
    }

    public int getMyTasksCount() {
        return MainApp.taskService.getTasksByAssignedUser(currentUser).size();
    }

    public int getNotifCount() {
        return MainApp.notificationService.getUnreadNotifications(currentUser).size();
    }

    public double getMyDue() {
        return MainApp.expenseService.getTotalDueByUser(currentUser.getId());
    }

    // --- Tâches ---

    public void handleCreateTask(String titre, String description, String joursStr,
                                 String importanceVal, String selectedUserName, Label lblResult) {
        if (titre.isBlank()) {
            lblResult.setText("✗ Le titre est obligatoire.");
            return;
        }
        try {
            int jours = Integer.parseInt(joursStr);
            User assignee = MainApp.userService.getAllUsers().stream()
                    .filter(u -> u.getFullName().equals(selectedUserName))
                    .findFirst()
                    .orElse(currentUser);

            MainApp.taskService.createTask(
                    titre,
                    description,
                    LocalDate.now().plusDays(jours),
                    ImportanceLevel.valueOf(importanceVal),
                    1,
                    assignee,
                    currentUser
            );
            lblResult.setText("✓ Tâche créée avec succès.");
        } catch (NumberFormatException e) {
            lblResult.setText("✗ Deadline invalide.");
        } catch (Exception e) {
            lblResult.setText("✗ " + e.getMessage());
        }
    }

    public void handleStartTask(int index, ListView<String> taskList, Label lblAction) {
        if (index < 0) { lblAction.setText("✗ Sélectionnez une tâche."); return; }
        Task t = MainApp.taskService.getAllTasks().get(index);
        MainApp.taskService.startTask(t.getId());
        refreshTachesList(taskList);
        lblAction.setText("✓ Tâche démarrée.");
    }

    public void handleCompleteTask(int index, ListView<String> taskList, Label lblAction) {
        if (index < 0) { lblAction.setText("✗ Sélectionnez une tâche."); return; }
        Task t = MainApp.taskService.getAllTasks().get(index);
        MainApp.taskService.markTaskCompleted(t.getId());
        refreshTachesList(taskList);
        lblAction.setText("✓ Tâche terminée.");
    }

    public void handleValidateTask(int index, ListView<String> taskList, Label lblAction) {
        if (currentUser.getRole() != Role.ADMIN) {
            lblAction.setText("✗ Seul l'admin peut valider.");
            return;
        }
        if (index < 0) { lblAction.setText("✗ Sélectionnez une tâche."); return; }
        Task t = MainApp.taskService.getAllTasks().get(index);
        MainApp.taskService.validateTask(t.getId(), currentUser, "Validation OK");
        refreshTachesList(taskList);
        lblAction.setText("✓ Tâche validée.");
    }

    public void handleRejectTask(int index, ListView<String> taskList, Label lblAction) {
        if (currentUser.getRole() != Role.ADMIN) {
            lblAction.setText("✗ Seul l'admin peut rejeter.");
            return;
        }
        if (index < 0) { lblAction.setText("✗ Sélectionnez une tâche."); return; }
        Task t = MainApp.taskService.getAllTasks().get(index);
        MainApp.taskService.rejectTask(t.getId(), currentUser, "À refaire");
        refreshTachesList(taskList);
        lblAction.setText("✓ Tâche rejetée.");
    }

    public void refreshTachesList(ListView<String> listView) {
        listView.getItems().clear();
        for (Task t : MainApp.taskService.getAllTasks()) {
            listView.getItems().add(
                    t.getTitle()
                            + " | " + t.getStatus()
                            + " | priorité: " + t.getCalculatedPriority()
                            + " | assignée à: " + (t.getAssignedUser() != null ? t.getAssignedUser().getFullName() : "-")
                            + " | deadline: " + t.getDeadline()
            );
        }
    }

    // --- Dépenses ---

    public void handleCreateExpense(String label, String montantStr, Label lblResult) {
        if (label.isBlank()) {
            lblResult.setText("✗ Le libellé est obligatoire.");
            return;
        }
        try {
            double montant = Double.parseDouble(montantStr.replace(",", "."));
            List<User> users = MainApp.userService.getAllUsers();
            MainApp.expenseService.createExpense(label, montant, "", LocalDate.now(), currentUser, users);
            lblResult.setText("✓ Dépense ajoutée : " + label);
        } catch (NumberFormatException e) {
            lblResult.setText("✗ Montant invalide.");
        } catch (Exception e) {
            lblResult.setText("✗ " + e.getMessage());
        }
    }

    public List<String> getUserNames() {
        return MainApp.userService.getAllUsers().stream()
                .map(User::getFullName)
                .toList();
    }

    public User getCurrentUser() {
        return currentUser;
    }
}