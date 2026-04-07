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

public class TaskController {

    private final User currentUser;

    public TaskController(User currentUser) {
        this.currentUser = currentUser;
    }

    public void handleCreateTask(String titre, String description, String joursStr,
                                 String importanceVal, String selectedUserName,
                                 Label lblResult, ListView<String> taskList) {
        if (titre.isBlank()) {
            setError(lblResult, "✗ Le titre est obligatoire.");
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
            setSuccess(lblResult, "✓ Tâche créée avec succès.");
            if (taskList != null) refreshTaskList(taskList);
        } catch (NumberFormatException e) {
            setError(lblResult, "✗ Deadline invalide.");
        } catch (Exception e) {
            setError(lblResult, "✗ " + e.getMessage());
        }
    }

    public void handleStartTask(int index, ListView<String> taskList, Label lblAction) {
        Task t = getTaskAt(index);
        if (t == null) { setError(lblAction, "✗ Sélectionnez une tâche."); return; }
        MainApp.taskService.startTask(t.getId());
        if (taskList != null) refreshTaskList(taskList);
        setSuccess(lblAction, "✓ Tâche démarrée.");
    }

    public void handleCompleteTask(int index, ListView<String> taskList, Label lblAction) {
        Task t = getTaskAt(index);
        if (t == null) { setError(lblAction, "✗ Sélectionnez une tâche."); return; }
        MainApp.taskService.markTaskCompleted(t.getId());
        if (taskList != null) refreshTaskList(taskList);
        setSuccess(lblAction, "✓ Tâche terminée.");
    }

    public void handleValidateTask(int index, ListView<String> taskList, Label lblAction) {
        if (currentUser.getRole() != Role.ADMIN) {
            setError(lblAction, "✗ Seul l'admin peut valider.");
            return;
        }
        Task t = getTaskAt(index);
        if (t == null) { setError(lblAction, "✗ Sélectionnez une tâche."); return; }
        MainApp.taskService.validateTask(t.getId(), currentUser, "Validation OK");
        if (taskList != null) refreshTaskList(taskList);
        setSuccess(lblAction, "✓ Tâche validée.");
    }

    public void handleRejectTask(int index, String comment, ListView<String> taskList, Label lblAction) {
        if (currentUser.getRole() != Role.ADMIN) {
            setError(lblAction, "✗ Seul l'admin peut rejeter.");
            return;
        }
        Task t = getTaskAt(index);
        if (t == null) { setError(lblAction, "✗ Sélectionnez une tâche."); return; }
        String commentFinal = comment.isBlank() ? "À refaire" : comment;
        MainApp.taskService.rejectTask(t.getId(), currentUser, commentFinal);
        if (taskList != null) refreshTaskList(taskList);
        setError(lblAction, "✓ Tâche rejetée.");
    }

    public void handleDeleteTask(int index, ListView<String> taskList, Label lblAction) {
        if (currentUser.getRole() != Role.ADMIN) {
            setError(lblAction, "✗ Seul l'admin peut supprimer.");
            return;
        }
        Task t = getTaskAt(index);
        if (t == null) { setError(lblAction, "✗ Sélectionnez une tâche."); return; }
        MainApp.taskService.deleteTask(t.getId());
        if (taskList != null) refreshTaskList(taskList);
        setSuccess(lblAction, "✓ Tâche supprimée.");
    }

    public void refreshTaskList(ListView<String> listView) {
        if (listView == null) return;
        listView.getItems().clear();
        List<Task> tasks = currentUser.getRole() == Role.ADMIN
                ? MainApp.taskService.getAllTasks()
                : MainApp.taskService.getTasksByAssignedUser(currentUser);
        for (Task t : tasks) {
            listView.getItems().add(
                    t.getTitle()
                            + "  |  " + t.getStatus()
                            + "  |  " + (t.getAssignedUser() != null ? t.getAssignedUser().getFullName() : "-")
                            + "  |  " + t.getDeadline()
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

    public int getTotalTasks() {
        return MainApp.taskService.getAllTasks().size();
    }

    public List<String> getUserNames() {
        return MainApp.userService.getAllUsers().stream()
                .map(User::getFullName)
                .toList();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void setSuccess(Label lbl, String msg) {
        lbl.setStyle(
                "-fx-text-fill: #065F46; -fx-background-color: #D1FAE5;" +
                        "-fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px; -fx-font-weight: bold;"
        );
        lbl.setText(msg);
    }

    private void setError(Label lbl, String msg) {
        lbl.setStyle(
                "-fx-text-fill: #991B1B; -fx-background-color: #FEE2E2;" +
                        "-fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px; -fx-font-weight: bold;"
        );
        lbl.setText(msg);
    }
}