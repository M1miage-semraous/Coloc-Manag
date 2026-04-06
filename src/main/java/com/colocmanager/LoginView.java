package com.colocmanager;

import com.colocmanager.controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class LoginView {

    public LoginView(Stage stage) {
        LoginController controller = new LoginController();

        StackPane leftPanel = buildLeftPanel();
        StackPane rightPanel = buildRightPanel(stage, controller);

        HBox root = new HBox(leftPanel, rightPanel);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
        );

        stage.setTitle("ColocManager — Connexion");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    private StackPane buildLeftPanel() {
        Rectangle bg = new Rectangle(440, 700);
        bg.setFill(Color.web("#0F0C29"));

        Circle c1 = new Circle(200);
        c1.setFill(Color.web("#6366F1", 0.3));
        c1.setTranslateX(-80);
        c1.setTranslateY(-120);
        c1.setEffect(new GaussianBlur(60));

        Circle c2 = new Circle(150);
        c2.setFill(Color.web("#8B5CF6", 0.25));
        c2.setTranslateX(120);
        c2.setTranslateY(150);
        c2.setEffect(new GaussianBlur(50));

        Circle c3 = new Circle(100);
        c3.setFill(Color.web("#06B6D4", 0.2));
        c3.setTranslateX(-50);
        c3.setTranslateY(200);
        c3.setEffect(new GaussianBlur(40));

        // Logo
        StackPane logoBox = new StackPane();
        logoBox.setStyle(
                "-fx-background-color: rgba(255,255,255,0.12);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: rgba(255,255,255,0.2);" +
                        "-fx-border-width: 1;"
        );
        logoBox.setPrefSize(80, 80);
        logoBox.setMaxSize(80, 80);

        Label logoIcon = new Label("⌂");
        logoIcon.setStyle("-fx-font-size: 36px; -fx-text-fill: white;");
        logoBox.getChildren().add(logoIcon);

        Text appName = new Text("ColocManager");
        appName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 34));
        appName.setFill(Color.WHITE);

        Text tagline = new Text("Gérez votre colocation\nen toute simplicité");
        tagline.setFont(Font.font("Segoe UI", 16));
        tagline.setFill(Color.web("#FFFFFF", 0.7));
        tagline.setTextAlignment(TextAlignment.CENTER);

        VBox features = new VBox(10,
                featureItem("✓", "Tâches partagées",      "#10B981"),
                featureItem("€", "Dépenses communes",     "#F59E0B"),
                featureItem("!", "Notifications",          "#6366F1"),
                featureItem("≡", "Rapports mensuels",      "#3B82F6")
        );
        features.setAlignment(Pos.CENTER_LEFT);

        HBox stats = new HBox(24,
                statItem("3+",    "Colocataires"),
                statItem("100%",  "Satisfaction"),
                statItem("24/7",  "Disponible")
        );
        stats.setAlignment(Pos.CENTER);
        stats.setPadding(new Insets(16));
        stats.setStyle(
                "-fx-background-color: rgba(255,255,255,0.08);" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: rgba(255,255,255,0.1);" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;"
        );

        VBox content = new VBox(18, logoBox, appName, tagline, features, stats);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));

        StackPane panel = new StackPane(bg, c1, c2, c3, content);
        panel.setPrefWidth(440);
        panel.setMinWidth(440);
        panel.setMaxWidth(440);
        StackPane.setAlignment(content, Pos.CENTER);
        return panel;
    }

    private HBox featureItem(String icon, String text, String color) {
        StackPane iconBox = new StackPane();
        iconBox.setStyle(
                "-fx-background-color: " + color + "30;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: " + color + "60;" +
                        "-fx-border-width: 1;"
        );
        iconBox.setPrefSize(36, 36);
        iconBox.setMaxSize(36, 36);

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        iconBox.getChildren().add(iconLbl);

        Label textLbl = new Label(text);
        textLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.85); -fx-font-size: 14px;");

        HBox item = new HBox(14, iconBox, textLbl);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 16, 10, 16));
        item.setStyle(
                "-fx-background-color: rgba(255,255,255,0.05);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: rgba(255,255,255,0.08);" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );
        return item;
    }

    private VBox statItem(String value, String label) {
        Label valLbl = new Label(value);
        valLbl.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label lblLbl = new Label(label);
        lblLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 11px;");

        VBox box = new VBox(2, valLbl, lblLbl);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private StackPane buildRightPanel(Stage stage, LoginController controller) {
        Circle deco1 = new Circle(120);
        deco1.setFill(Color.web("#6366F1", 0.04));
        deco1.setTranslateX(280);
        deco1.setTranslateY(-200);

        Circle deco2 = new Circle(80);
        deco2.setFill(Color.web("#10B981", 0.04));
        deco2.setTranslateX(-200);
        deco2.setTranslateY(220);

        // Avatar
        StackPane avatar = new StackPane();
        avatar.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #6366F1, #8B5CF6);" +
                        "-fx-background-radius: 50;"
        );
        avatar.setPrefSize(72, 72);
        avatar.setMaxSize(72, 72);
        Label avatarIcon = new Label("⚿");
        avatarIcon.setStyle("-fx-font-size: 28px; -fx-text-fill: white;");
        avatar.getChildren().add(avatarIcon);

        Text title = new Text("Bon retour !");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        title.setFill(Color.web("#111827"));

        Text subtitle = new Text("Connectez-vous à votre espace colocation");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setFill(Color.web("#6B7280"));

        Label emailLabel = new Label("Adresse email");
        emailLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");

        TextField emailField = new TextField();
        emailField.setPromptText("exemple@email.com");
        emailField.setMaxWidth(360);
        emailField.setPrefHeight(48);
        emailField.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 0 16;"
        );

        Label passwordLabel = new Label("Mot de passe");
        passwordLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("••••••••");
        passwordField.setMaxWidth(360);
        passwordField.setPrefHeight(48);
        passwordField.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 0 16;"
        );

        Label errorLabel = new Label();
        errorLabel.setStyle(
                "-fx-text-fill: #991B1B; -fx-font-size: 12px;" +
                        "-fx-background-color: #FEE2E2; -fx-background-radius: 8;" +
                        "-fx-border-color: #FECACA; -fx-border-radius: 8; -fx-border-width: 1;" +
                        "-fx-padding: 10 14; -fx-font-weight: bold;"
        );
        errorLabel.setMaxWidth(360);
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Button loginButton = new Button("Se connecter  →");
        loginButton.setMaxWidth(360);
        loginButton.setPrefHeight(50);
        loginButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #4F46E5, #7C3AED);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(79,70,229,0.4), 15, 0, 0, 5);"
        );
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #4338CA, #6D28D9);" +
                        "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 12; -fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(79,70,229,0.5), 20, 0, 0, 6);"
        ));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #4F46E5, #7C3AED);" +
                        "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 12; -fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(79,70,229,0.4), 15, 0, 0, 5);"
        ));

        Label orLabel = new Label("── ou ──");
        orLabel.setStyle("-fx-text-fill: #D1D5DB; -fx-font-size: 12px;");

        Label registerLink = new Label("Pas encore de compte ?  S'inscrire →");
        registerLink.setStyle(
                "-fx-text-fill: #6366F1; -fx-font-size: 13px; -fx-cursor: hand; -fx-font-weight: bold;"
        );
        registerLink.setOnMouseClicked(e -> new RegisterView(stage));
        registerLink.setOnMouseEntered(e -> registerLink.setStyle(
                "-fx-text-fill: #4F46E5; -fx-font-size: 13px; -fx-cursor: hand;" +
                        "-fx-font-weight: bold; -fx-underline: true;"
        ));
        registerLink.setOnMouseExited(e -> registerLink.setStyle(
                "-fx-text-fill: #6366F1; -fx-font-size: 13px; -fx-cursor: hand; -fx-font-weight: bold;"
        ));

        loginButton.setOnAction(e -> {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            controller.handleLogin(
                    emailField.getText().trim(),
                    passwordField.getText().trim(),
                    errorLabel
            );
            if (errorLabel.getText() != null && !errorLabel.getText().isEmpty()) {
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            }
        });
        passwordField.setOnAction(e -> loginButton.fire());

        VBox form = new VBox(14,
                avatar, title, subtitle,
                new Region() {{ setPrefHeight(4); }},
                emailLabel, emailField,
                passwordLabel, passwordField,
                loginButton, errorLabel,
                orLabel, registerLink
        );
        form.setAlignment(Pos.CENTER_LEFT);
        form.setMaxWidth(360);

        StackPane rightPanel = new StackPane(deco1, deco2, form);
        rightPanel.setStyle("-fx-background-color: #FFFFFF;");
        rightPanel.setPadding(new Insets(60));
        StackPane.setAlignment(form, Pos.CENTER_LEFT);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        return rightPanel;
    }
}