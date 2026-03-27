package com.colocmanager;

import com.colocmanager.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginView {

    // Couleurs du projet
    private static final String NAVY  = "#1A2B4A";
    private static final String TEAL  = "#0D9488";
    private static final String WHITE = "#FFFFFF";
    private static final String GREY  = "#F8FAFC";
    private static final String SLATE = "#64748B";

    public LoginView(Stage stage) {

        // ── Titre ──────────────────────────────────────
        Text titre = new Text("ColocManager");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titre.setFill(Color.WHITE);

        Text sousTitre = new Text("Gestion collaborative de colocation");
        sousTitre.setFont(Font.font("Arial", 14));
        sousTitre.setFill(Color.web("#14B8A6"));

        VBox header = new VBox(6, titre, sousTitre);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(40, 20, 40, 20));
        header.setStyle("-fx-background-color: " + NAVY + ";");

        // ── Formulaire ─────────────────────────────────
        Label labelEmail = new Label("Email");
        labelEmail.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        TextField champEmail = new TextField();
        champEmail.setPromptText("adnan@gmail.com");
        champEmail.setPrefHeight(38);
        champEmail.setStyle(
                "-fx-background-radius: 6; -fx-border-radius: 6; " +
                        "-fx-border-color: #CBD5E1; -fx-font-size: 13;"
        );

        Label labelMdp = new Label("Mot de passe");
        labelMdp.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        PasswordField champMdp = new PasswordField();
        champMdp.setPromptText("••••••••");
        champMdp.setPrefHeight(38);
        champMdp.setStyle(
                "-fx-background-radius: 6; -fx-border-radius: 6; " +
                        "-fx-border-color: #CBD5E1; -fx-font-size: 13;"
        );

        Label erreur = new Label("");
        erreur.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12;");

        Button btnConnexion = new Button("Se connecter");
        btnConnexion.setPrefWidth(320);
        btnConnexion.setPrefHeight(42);
        btnConnexion.setStyle(
                "-fx-background-color: " + TEAL + "; -fx-text-fill: white; " +
                        "-fx-font-size: 14; -fx-font-weight: bold; -fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        );

        // Effet hover sur le bouton
        btnConnexion.setOnMouseEntered(e ->
                btnConnexion.setStyle(
                        "-fx-background-color: #0F766E; -fx-text-fill: white; " +
                                "-fx-font-size: 14; -fx-font-weight: bold; -fx-background-radius: 8; " +
                                "-fx-cursor: hand;"
                )
        );
        btnConnexion.setOnMouseExited(e ->
                btnConnexion.setStyle(
                        "-fx-background-color: " + TEAL + "; -fx-text-fill: white; " +
                                "-fx-font-size: 14; -fx-font-weight: bold; -fx-background-radius: 8; " +
                                "-fx-cursor: hand;"
                )
        );

        // Action connexion
        btnConnexion.setOnAction(e -> {
            String email = champEmail.getText().trim();
            String mdp   = champMdp.getText().trim();

            if (email.isEmpty() || mdp.isEmpty()) {
                erreur.setText("Veuillez remplir tous les champs.");
                return;
            }

            Optional<User> user = MainApp.userService.login(email, mdp);

            if (user.isPresent()) {
                new DashboardView(stage, user.get());
            } else {
                erreur.setText("Email ou mot de passe incorrect.");
                champMdp.clear();
            }
        });

        // Connexion aussi en appuyant Entrée
        champMdp.setOnAction(e -> btnConnexion.fire());
        champEmail.setOnAction(e -> champMdp.requestFocus());

        VBox form = new VBox(12,
                labelEmail, champEmail,
                labelMdp, champMdp,
                erreur,
                btnConnexion
        );
        form.setPadding(new Insets(30, 40, 30, 40));
        form.setAlignment(Pos.CENTER_LEFT);
        form.setStyle("-fx-background-color: " + WHITE + ";");
        form.setPrefWidth(320);

        // ── Carte centrale ─────────────────────────────
        VBox carte = new VBox(0, header, form);
        carte.setStyle(
                "-fx-background-color: white; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 4); " +
                        "-fx-background-radius: 12;"
        );
        carte.setPrefWidth(360);
        carte.setMaxWidth(360);

        // ── Fond de page ───────────────────────────────
        StackPane root = new StackPane(carte);
        root.setStyle("-fx-background-color: " + GREY + ";");
        root.setPadding(new Insets(60));

        // ── Scène ──────────────────────────────────────
        Scene scene = new Scene(root, 600, 520);
        stage.setTitle("ColocManager — Connexion");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}