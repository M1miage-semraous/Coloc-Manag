package com.colocmanager;

import com.colocmanager.controller.AdminController;
import com.colocmanager.enums.Role;
import com.colocmanager.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class AdminView {

    private VBox usersListBox;
    private Label statsTotal;
    private Label statsAdmins;
    private Label statsUsers;

    public AdminView(Stage stage, User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            SceneManager.showDashboard(currentUser);
            return;
        }
        build(stage, currentUser);
    }

    private void build(Stage stage, User currentUser) {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #F1F5F9;");

        StackPane header = buildHeader(currentUser);

        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));

        AdminController controller = new AdminController();

        VBox leftCol = new VBox(16);
        leftCol.setPrefWidth(340);
        leftCol.getChildren().addAll(
                buildStatsCard(),
                buildCreateUserCard(controller, currentUser)
        );

        VBox rightCol = new VBox(16);
        HBox.setHgrow(rightCol, Priority.ALWAYS);
        rightCol.getChildren().add(buildUsersListCard(currentUser));

        mainContent.getChildren().addAll(leftCol, rightCol);

        ScrollPane scroll = new ScrollPane(mainContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #F1F5F9; -fx-background: #F1F5F9;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, scroll);

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("ColocManager — Administration");
        stage.setScene(scene);
        stage.show();
    }

    private StackPane buildHeader(User currentUser) {
        Circle deco1 = new Circle(100);
        deco1.setFill(Color.web("#FFFFFF", 0.06));
        deco1.setTranslateX(500);
        deco1.setTranslateY(-15);
        deco1.setEffect(new GaussianBlur(20));

        Circle deco2 = new Circle(140);
        deco2.setFill(Color.web("#FFFFFF", 0.04));
        deco2.setTranslateX(750);
        deco2.setTranslateY(10);
        deco2.setEffect(new GaussianBlur(30));

        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle(
                "-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white;" +
                        "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 18;" +
                        "-fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 8;" +
                        "-fx-border-width: 1; -fx-font-size: 13px; -fx-font-weight: bold;"
        );
        btnRetour.setOnAction(e -> SceneManager.showDashboard(currentUser));

        Text title = new Text("Administration");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setFill(Color.WHITE);

        int nbUsers  = MainApp.userService.getAllUsers().size();
        int nbAdmins = (int) MainApp.userService.getAllUsers().stream()
                .filter(u -> u.getRole() == Role.ADMIN).count();
        int nbColo   = nbUsers - nbAdmins;

        Text subtitle = new Text(
                nbUsers + " utilisateur(s)  •  " +
                        nbAdmins + " admin(s)  •  " +
                        nbColo + " colocataire(s)"
        );
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setFill(Color.web("#FFFFFF", 0.75));

        HBox miniStats = new HBox(16,
                miniStat("Total",        String.valueOf(nbUsers)),
                miniStat("Admins",       String.valueOf(nbAdmins)),
                miniStat("Colocataires", String.valueOf(nbColo))
        );

        VBox headerContent = new VBox(8, btnRetour, title, subtitle, miniStats);
        headerContent.setAlignment(Pos.CENTER_LEFT);

        StackPane header = new StackPane(deco1, deco2, headerContent);
        header.setStyle(
                "-fx-background-color: linear-gradient(to right, #1E1B4B, #4338CA, #6366F1);"
        );
        header.setPadding(new Insets(28, 40, 28, 40));
        StackPane.setAlignment(headerContent, Pos.CENTER_LEFT);
        return header;
    }

    private HBox miniStat(String label, String value) {
        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.65); -fx-font-size: 11px;");

        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        VBox box = new VBox(2, lblLabel, lblValue);
        box.setAlignment(Pos.CENTER_LEFT);

        HBox stat = new HBox(box);
        stat.setStyle(
                "-fx-background-color: rgba(255,255,255,0.12);" +
                        "-fx-background-radius: 10; -fx-padding: 10 16;"
        );
        return stat;
    }

    private VBox buildStatsCard() {
        VBox card = buildCard("Statistiques");

        int total  = MainApp.userService.getAllUsers().size();
        int admins = (int) MainApp.userService.getAllUsers().stream()
                .filter(u -> u.getRole() == Role.ADMIN).count();
        int users  = total - admins;

        statsTotal  = new Label(String.valueOf(total));
        statsAdmins = new Label(String.valueOf(admins));
        statsUsers  = new Label(String.valueOf(users));

        card.getChildren().addAll(
                statRow("Total utilisateurs", statsTotal,  "#6366F1"),
                statRow("Administrateurs",    statsAdmins, "#EF4444"),
                statRow("Colocataires",       statsUsers,  "#10B981")
        );
        return card;
    }

    private HBox statRow(String label, Label valueLabel, String color) {
        StackPane iconBox = new StackPane();
        iconBox.setStyle(
                "-fx-background-color: " + color + "20;" +
                        "-fx-background-radius: 8;" +
                        "-fx-min-width: 32; -fx-min-height: 32;" +
                        "-fx-max-width: 32; -fx-max-height: 32;"
        );
        Label iconLbl = new Label("●");
        iconLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + color + ";");
        iconBox.getChildren().add(iconLbl);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #6B7280;");
        HBox.setHgrow(lbl, Priority.ALWAYS);

        valueLabel.setStyle(
                "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + ";"
        );

        HBox row = new HBox(10, iconBox, lbl, valueLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 10;");
        return row;
    }

    private VBox buildCreateUserCard(AdminController controller, User currentUser) {
        VBox card = buildCard("Ajouter un utilisateur");

        TextField tfNom = new TextField();
        tfNom.setPromptText("Nom complet");
        styleField(tfNom);

        TextField tfEmail = new TextField();
        tfEmail.setPromptText("Adresse email");
        styleField(tfEmail);

        PasswordField tfPass = new PasswordField();
        tfPass.setPromptText("Mot de passe");
        styleField(tfPass);

        Label roleLabel = new Label("Rôle");
        roleLabel.setStyle(
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #374151;"
        );

        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("USER", "ADMIN");
        cbRole.setValue("USER");
        cbRole.setMaxWidth(Double.MAX_VALUE);
        cbRole.setStyle(
                "-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;" +
                        "-fx-font-size: 13px; -fx-pref-height: 44;"
        );

        Label lblResult = new Label();
        lblResult.setWrapText(true);
        lblResult.setMaxWidth(Double.MAX_VALUE);

        Button btnCreer = new Button("✓  Créer l'utilisateur");
        btnCreer.setMaxWidth(Double.MAX_VALUE);
        btnCreer.setStyle(
                "-fx-background-color: linear-gradient(to right, #6366F1, #818CF8);" +
                        "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 12 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(99,102,241,0.3), 8, 0, 0, 3);"
        );

        btnCreer.setOnAction(e -> {
            controller.handleCreateUser(
                    tfNom.getText().trim(),
                    tfEmail.getText().trim(),
                    tfPass.getText().trim(),
                    cbRole.getValue(),
                    lblResult,
                    null
            );
            if (lblResult.getText().startsWith("✓")) {
                tfNom.clear();
                tfEmail.clear();
                tfPass.clear();
                // Rafraichir la liste et les stats sans recharger la page
                refreshUsersList(currentUser);
                updateStats();
            }
        });

        card.getChildren().addAll(
                tfNom, tfEmail, tfPass, roleLabel, cbRole, btnCreer, lblResult
        );
        return card;
    }

    private VBox buildUsersListCard(User currentUser) {
        VBox card = buildCard("Liste des utilisateurs");

        usersListBox = new VBox(10);
        refreshUsersList(currentUser);

        card.getChildren().add(usersListBox);
        return card;
    }

    private void refreshUsersList(User currentUser) {
        if (usersListBox == null) return;
        usersListBox.getChildren().clear();

        List<User> users = MainApp.userService.getAllUsers();
        if (users.isEmpty()) {
            Label empty = new Label("Aucun utilisateur.");
            empty.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 13px; -fx-padding: 20;");
            usersListBox.getChildren().add(empty);
            return;
        }

        for (User user : users) {
            usersListBox.getChildren().add(buildUserRow(user, currentUser));
        }
    }

    private void updateStats() {
        if (statsTotal == null) return;
        int total  = MainApp.userService.getAllUsers().size();
        int admins = (int) MainApp.userService.getAllUsers().stream()
                .filter(u -> u.getRole() == Role.ADMIN).count();
        int users  = total - admins;
        statsTotal.setText(String.valueOf(total));
        statsAdmins.setText(String.valueOf(admins));
        statsUsers.setText(String.valueOf(users));
    }

    private HBox buildUserRow(User user, User currentUser) {
        // Avatar
        StackPane avatar = new StackPane();
        String avatarColor = user.getRole() == Role.ADMIN ? "#6366F1" : "#10B981";
        avatar.setStyle(
                "-fx-background-color: " + avatarColor + ";" +
                        "-fx-background-radius: 50;" +
                        "-fx-min-width: 44; -fx-min-height: 44;" +
                        "-fx-max-width: 44; -fx-max-height: 44;"
        );
        Label avatarLbl = new Label(user.getFullName().substring(0, 1).toUpperCase());
        avatarLbl.setStyle(
                "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;"
        );
        avatar.getChildren().add(avatarLbl);

        // Infos
        Label nameLbl = new Label(user.getFullName());
        nameLbl.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;"
        );

        Label emailLbl = new Label(user.getEmail());
        emailLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");

        VBox info = new VBox(2, nameLbl, emailLbl);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Badge rôle
        Label roleBadge = new Label(user.getRole() == Role.ADMIN ? "Admin" : "Colocataire");
        roleBadge.setStyle(
                "-fx-background-color: " + (user.getRole() == Role.ADMIN ? "#EEF2FF" : "#D1FAE5") + ";" +
                        "-fx-text-fill: " + (user.getRole() == Role.ADMIN ? "#4338CA" : "#065F46") + ";" +
                        "-fx-background-radius: 20; -fx-padding: 4 14;" +
                        "-fx-font-size: 11px; -fx-font-weight: bold;"
        );

        // Bouton supprimer
        Button btnSuppr = new Button("✗  Supprimer");
        boolean isSelf = user.getId() != null &&
                user.getId().equals(currentUser.getId());

        if (isSelf) {
            btnSuppr.setStyle(
                    "-fx-background-color: #F3F4F6; -fx-text-fill: #9CA3AF;" +
                            "-fx-font-size: 11px; -fx-font-weight: bold;" +
                            "-fx-background-radius: 8; -fx-padding: 6 14;"
            );
            btnSuppr.setDisable(true);
        } else {
            btnSuppr.setStyle(
                    "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;" +
                            "-fx-font-size: 11px; -fx-font-weight: bold;" +
                            "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 6 14;"
            );
            btnSuppr.setOnMouseEntered(e -> btnSuppr.setStyle(
                    "-fx-background-color: #DC2626; -fx-text-fill: white;" +
                            "-fx-font-size: 11px; -fx-font-weight: bold;" +
                            "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 6 14;"
            ));
            btnSuppr.setOnMouseExited(e -> btnSuppr.setStyle(
                    "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;" +
                            "-fx-font-size: 11px; -fx-font-weight: bold;" +
                            "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 6 14;"
            ));
            btnSuppr.setOnAction(e -> {
                MainApp.userService.deleteUser(user.getId());
                refreshUsersList(currentUser);
                updateStats();
            });
        }

        HBox row = new HBox(14, avatar, info, roleBadge, btnSuppr);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 16, 14, 16));
        row.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12;" +
                        "-fx-border-color: #E5E7EB; -fx-border-radius: 12; -fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 6, 0, 0, 2);"
        );
        row.setOnMouseEntered(e -> row.setStyle(
                "-fx-background-color: #F8FAFC; -fx-background-radius: 12;" +
                        "-fx-border-color: #A5B4FC; -fx-border-radius: 12; -fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(99,102,241,0.1), 10, 0, 0, 3);"
        ));
        row.setOnMouseExited(e -> row.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12;" +
                        "-fx-border-color: #E5E7EB; -fx-border-radius: 12; -fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 6, 0, 0, 2);"
        ));

        return row;
    }

    private VBox buildCard(String title) {
        Label titleLbl = new Label(title);
        titleLbl.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;"
        );
        Separator sep = new Separator();

        VBox card = new VBox(12, titleLbl, sep);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 16;" +
                        "-fx-border-radius: 16; -fx-border-color: #E5E7EB; -fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 3);" +
                        "-fx-padding: 20;"
        );
        return card;
    }

    private void styleField(TextField tf) {
        tf.setStyle(
                "-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;" +
                        "-fx-padding: 10 14; -fx-font-size: 13px; -fx-pref-height: 44;"
        );
        tf.setMaxWidth(Double.MAX_VALUE);
    }

    private void styleField(PasswordField tf) {
        tf.setStyle(
                "-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;" +
                        "-fx-padding: 10 14; -fx-font-size: 13px; -fx-pref-height: 44;"
        );
        tf.setMaxWidth(Double.MAX_VALUE);
    }
}