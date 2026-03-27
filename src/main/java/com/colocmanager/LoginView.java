package com.colocmanager;

import com.colocmanager.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginView {

    public LoginView(Stage stage) {

        Label title = new Label("ColocManager - Connexion");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(320);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setMaxWidth(320);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        Button loginButton = new Button("Se connecter");
        loginButton.setStyle("-fx-font-size: 14px; -fx-padding: 8 18;");
        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            Optional<User> user = MainApp.userService.login(email, password);

            if (user.isPresent()) {
                new DashboardView(stage, user.get());
            } else {
                errorLabel.setText("Email ou mot de passe incorrect");
            }
        });

        VBox form = new VBox(12, title, emailField, passwordField, loginButton, errorLabel);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(25));

        StackPane root = new StackPane(form);
        root.setStyle("-fx-background-color: #F4F6F8;");

        Scene scene = new Scene(root, 520, 380);
        stage.setTitle("Connexion");
        stage.setScene(scene);
        stage.show();
    }
}