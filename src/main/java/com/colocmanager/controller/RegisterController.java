package com.colocmanager.controller;

import com.colocmanager.MainApp;
import com.colocmanager.SceneManager;
import com.colocmanager.enums.Role;
import javafx.scene.control.Label;

public class RegisterController {

    public void handleRegister(String fullName, String email,
                               String password, String confirmPassword,
                               Label errorLabel) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }
        if (password.length() < 4) {
            errorLabel.setText("Le mot de passe doit contenir au moins 4 caractères.");
            return;
        }
        try {
            MainApp.userService.createUser(fullName, email, password, Role.USER);
            SceneManager.showLogin();
        } catch (IllegalArgumentException e) {
            errorLabel.setText("Un compte avec cet email existe déjà.");
        }
    }
}