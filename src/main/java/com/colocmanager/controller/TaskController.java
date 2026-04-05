package com.colocmanager.controller;

import com.colocmanager.MainApp;
import com.colocmanager.enums.ImportanceLevel;
import com.colocmanager.enums.Role;
import com.colocmanager.model.Task;
import com.colocmanager.model.User;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TaskController {

    private final User currentUser;

    public TaskController(User currentUser) {
        this.currentUser = currentUser;
    }

    public void handleCreateTask(String titre, String description, String joursStr,
                                 String importanceVal, String selectedUserName,
                                 Label lblResult, ListView<String> taskList) {
        if (titre.isBlank()) {
            lblResult.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
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
                    titre, description,
                    LocalDate.now().plusDays(jours),
                    ImportanceLevel.valueOf(importanceVal),
                    1, assignee, currentUser
            );
            lblResult.setStyle("-fx-text-fill: #0D9488; -fx-font-size: 12;");
            lblResult.setText("✓ Tâche créée avec succès.");
            refreshTaskList(taskList);
        } catch (NumberFormatException e) {
            lblResult.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
            lblResult.setText("✗ Deadline invalide.");
        } catch (Exception e) {
            lblResult.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
            lblResult.setText("✗ " + e.getMessage());
        }
    }

    public void handleStartTask(int index, ListView<String> taskList, Label lblAction) {
        Task t = getTaskAt(index);
        if (t == null) { lblAction.setText("✗ Sélectionnez une tâche."); return; }
        MainApp.taskService.startTask(t.getId());
        refreshTaskList(taskList);
        lblAction.setStyle("-fx-text-fill: #0D9488; -fx-font-size: 12;");
        lblAction.setText("✓ Tâche démarrée.");
    }

    public void handleCompleteTask(int index, ListView<String> taskList, Label lblAction) {
        Task t = getTaskAt(index);
        if (t == null) { lblAction.setText("✗ Sélectionnez une tâche."); return; }
        MainApp.taskService.markTaskCompleted(t.getId());
        refreshTaskList(taskList);
        lblAction.setStyle("-fx-text-fill: #0D9488; -fx-font-size: 12;");
        lblAction.setText("✓ Tâche terminée.");
    }

    public void handleValidateTask(int index, ListView<String> taskList, Label lblAction) {
        if (currentUser.getRole() != Role.ADMIN) {
            lblAction.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
            lblAction.setText("✗ Seul l'admin peut valider.");
            return;
        }
        Task t = getTaskAt(index);
        if (t == null) { lblAction.setText("✗ Sélectionnez une tâche."); return; }
        MainApp.taskService.validateTask(t.getId(), currentUser, "Validation OK");
        refreshTaskList(taskList);
        lblAction.setStyle("-fx-text-fill: #0D9488; -fx-font-size: 12;");
        lblAction.setText("✓ Tâche validée.");
    }

    public void handleRejectTask(int index, String comment, ListView<String> taskList, Label lblAction) {
        if (currentUser.getRole() != Role.ADMIN) {
            lblAction.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
            lblAction.setText("✗ Seul l'admin peut rejeter.");
            return;
        }
        Task t = getTaskAt(index);
        if (t == null) { lblAction.setText("✗ Sélectionnez une tâche."); return; }
        String commentFinal = comment.isBlank() ? "À refaire" : comment;
        MainApp.taskService.rejectTask(t.getId(), currentUser, commentFinal);
        refreshTaskList(taskList);
        lblAction.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
        lblAction.setText("✓ Tâche rejetée.");
    }

    public void handleDeleteTask(int index, ListView<String> taskList, Label lblAction) {
        if (currentUser.getRole() != Role.ADMIN) {
            lblAction.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
            lblAction.setText("✗ Seul l'admin peut supprimer.");
            return;
        }
        Task t = getTaskAt(index);
        if (t == null) { lblAction.setText("✗ Sélectionnez une tâche."); return; }
        MainApp.taskService.deleteTask(t.getId());
        refreshTaskList(taskList);
        lblAction.setStyle("-fx-text-fill: #0D9488; -fx-font-size: 12;");
        lblAction.setText("✓ Tâche supprimée.");
    }

    public void refreshTaskList(ListView<String> listView) {
        listView.getItems().clear();
        List<Task> tasks = currentUser.getRole() == Role.ADMIN
                ? MainApp.taskService.getAllTasks()
                : MainApp.taskService.getTasksByAssignedUser(currentUser);
        for (Task t : tasks) {
            listView.getItems().add(
                    "📌 " + t.getTitle()
                            + "  |  " + t.getStatus()
                            + "  |  Priorité: " + t.getCalculatedPriority()
                            + "  |  Assignée à: " + (t.getAssignedUser() != null ? t.getAssignedUser().getFullName() : "-")
                            + "  |  Deadline: " + t.getDeadline()
            );
        }
    }

    private Task getTaskAt(int index) {
        if (index < 0) return null;
        List<Task> tasks = currentUser.getRole() == Role.ADMIN
                ? MainApp.taskService.getAllTasks()
                : MainApp.taskService.getTasksByAssignedUser(currentUser);
        if (index >= tasks.size()) return null;
        return tasks.get(index);
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