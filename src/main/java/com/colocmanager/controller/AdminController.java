package com.colocmanager.controller;

import com.colocmanager.MainApp;
import com.colocmanager.enums.Role;
import com.colocmanager.model.User;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class AdminController {

    public List<User> getAllUsers() {
        return MainApp.userService.getAllUsers();
    }

    public void handleCreateUser(String fullName, String email,
                                 String password, String roleVal,
                                 Label lblResult, ListView<String> userList) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            lblResult.setStyle(
                    "-fx-text-fill: #991B1B; -fx-background-color: #FEE2E2;" +
                            "-fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px; -fx-font-weight: bold;"
            );
            lblResult.setText("✗ Veuillez remplir tous les champs.");
            return;
        }
        try {
            Role role = Role.valueOf(roleVal);
            MainApp.userService.createUser(fullName, email, password, role);
            lblResult.setStyle(
                    "-fx-text-fill: #065F46; -fx-background-color: #D1FAE5;" +
                            "-fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px; -fx-font-weight: bold;"
            );
            lblResult.setText("✓ Utilisateur créé : " + fullName);
            if (userList != null) refreshUserList(userList);
        } catch (IllegalArgumentException e) {
            lblResult.setStyle(
                    "-fx-text-fill: #991B1B; -fx-background-color: #FEE2E2;" +
                            "-fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px; -fx-font-weight: bold;"
            );
            lblResult.setText("✗ Un compte avec cet email existe déjà.");
        }
    }

    public void handleDeleteUser(int index, ListView<String> userList, Label lblResult) {
        List<User> users = MainApp.userService.getAllUsers();
        if (index < 0 || index >= users.size()) {
            lblResult.setText("✗ Sélectionnez un utilisateur.");
            return;
        }
        User user = users.get(index);
        MainApp.userService.deleteUser(user.getId());
        lblResult.setText("✓ Utilisateur supprimé.");
        if (userList != null) refreshUserList(userList);
    }

    public void refreshUserList(ListView<String> listView) {
        if (listView == null) return;
        listView.getItems().clear();
        for (User u : MainApp.userService.getAllUsers()) {
            listView.getItems().add(
                    u.getFullName() + "  |  " + u.getEmail() + "  |  " + u.getRole()
            );
        }
    }
}