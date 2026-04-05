package com.colocmanager;

import com.colocmanager.controller.RegisterController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RegisterView {

    public RegisterView(Stage stage) {
        RegisterController controller = new RegisterController();

        Text title = new Text("Créer un compte");
        title.getStyleClass().add("login-title");

        Text subtitle = new Text("Rejoignez votre colocation");
        subtitle.getStyleClass().add("login-subtitle");

        VBox header = new VBox(6, title, subtitle);
        header.setAlignment(Pos.CENTER);

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Nom complet");
        fullNameField.getStyleClass().add("text-field");
        fullNameField.setMaxWidth(320);

        TextField emailField = new TextField();
        emailField.setPromptText("Adresse email");
        emailField.getStyleClass().add("text-field");
        emailField.setMaxWidth(320);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.getStyleClass().add("password-field");
        passwordField.setMaxWidth(320);

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirmer le mot de passe");
        confirmField.getStyleClass().add("password-field");
        confirmField.setMaxWidth(320);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        Button registerButton = new Button("Créer mon compte →");
        registerButton.getStyleClass().add("btn-primary");
        registerButton.setMaxWidth(320);
        registerButton.setPrefHeight(44);

        registerButton.setOnAction(e -> controller.handleRegister(
                fullNameField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText(),
                confirmField.getText(),
                errorLabel
        ));

        Label loginLink = new Label("Déjà un compte ? Se connecter");
        loginLink.getStyleClass().add("link-label");
        loginLink.setOnMouseClicked(e -> SceneManager.showLogin());

        VBox card = new VBox(14, header, fullNameField, emailField, passwordField, confirmField, registerButton, errorLabel, loginLink);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(400);

        StackPane root = new StackPane(card);
        root.getStyleClass().add("login-container");
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 520, 560);
        SceneManager.applyCSS(scene);
        stage.setTitle("ColocManager — Inscription");
        stage.setScene(scene);
        stage.show();
    }
}