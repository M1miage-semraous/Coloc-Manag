package com.colocmanager;

import com.colocmanager.controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginView {

    public LoginView(Stage stage) {
        LoginController controller = new LoginController();

        // Titre
        Text appTitle = new Text("ColocManager");
        appTitle.getStyleClass().add("sidebar-title");

        Text title = new Text("Bon retour 👋");
        title.getStyleClass().add("login-title");

        Text subtitle = new Text("Connectez-vous à votre espace colocation");
        subtitle.getStyleClass().add("login-subtitle");

        // Champs
        TextField emailField = new TextField();
        emailField.setPromptText("Adresse email");
        emailField.getStyleClass().add("text-field");
        emailField.setMaxWidth(Double.MAX_VALUE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.getStyleClass().add("password-field");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setWrapText(true);

        Button loginButton = new Button("Se connecter →");
        loginButton.getStyleClass().add("btn-primary");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(46);

        Label registerLink = new Label("Pas encore de compte ? S'inscrire");
        registerLink.getStyleClass().add("login-link");
        registerLink.setOnMouseClicked(e -> new RegisterView(stage));

        loginButton.setOnAction(e -> {
            errorLabel.setVisible(false);
            controller.handleLogin(
                    emailField.getText().trim(),
                    passwordField.getText().trim(),
                    errorLabel
            );
            if (!errorLabel.getText().isEmpty()) {
                errorLabel.setVisible(true);
            }
        });

        passwordField.setOnAction(e -> loginButton.fire());

        // Card
        VBox card = new VBox(16,
                appTitle,
                new Separator(),
                title,
                subtitle,
                emailField,
                passwordField,
                loginButton,
                errorLabel,
                registerLink
        );
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(420);

        StackPane root = new StackPane(card);
        root.getStyleClass().add("login-container");
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 520, 600);
        scene.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
        );

        stage.setTitle("ColocManager — Connexion");
        stage.setScene(scene);
        stage.show();
    }
}