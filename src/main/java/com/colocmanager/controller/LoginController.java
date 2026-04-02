package com.colocmanager.controller;

import com.colocmanager.MainApp;
import com.colocmanager.SceneManager;
import com.colocmanager.model.User;
import javafx.scene.control.Label;

import java.util.Optional;

public class LoginController {

    public void handleLogin(String email, String password, Label errorLabel) {
        if (email.isBlank() || password.isBlank()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        Optional<User> user = MainApp.userService.login(email, password);

        if (user.isPresent()) {
            SceneManager.showDashboard(user.get());
        } else {
            errorLabel.setText("Email ou mot de passe incorrect.");
        }
    }
}