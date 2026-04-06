package com.colocmanager;

import com.colocmanager.controller.DashboardController;
import com.colocmanager.enums.Role;
import com.colocmanager.model.*;
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
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DashboardView {

    private final Stage stage;
    private final User currentUser;
    private final StackPane content;
    private final DashboardController controller;

    public DashboardView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.content = new StackPane();
        this.controller = new DashboardController(user);
        build();
    }

    private void build() {
        VBox sidebar = buildSidebar();
        content.setStyle("-fx-background-color: #F1F5F9;");
        content.getChildren().add(buildHomePane());

        HBox root = new HBox(sidebar, content);
        HBox.setHgrow(content, Priority.ALWAYS);

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
        );
        stage.setTitle("ColocManager — " + currentUser.getFullName());
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildSidebar() {
        Label logoLabel = new Label("C");
        logoLabel.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #6366F1, #8B5CF6);" +
                        "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 10; -fx-min-width: 36; -fx-min-height: 36;" +
                        "-fx-max-width: 36; -fx-max-height: 36; -fx-alignment: CENTER;"
        );
        Label appNameLabel = new Label("ColocManager");
        appNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        HBox logoRow = new HBox(10, logoLabel, appNameLabel);
        logoRow.setAlignment(Pos.CENTER_LEFT);
        logoRow.setPadding(new Insets(0, 0, 20, 0));

        StackPane avatar = new StackPane();
        String avatarColor = currentUser.getRole() == Role.ADMIN ? "#6366F1" : "#10B981";
        avatar.setStyle(
                "-fx-background-color: " + avatarColor + ";" +
                        "-fx-background-radius: 50;"
        );
        avatar.setPrefSize(44, 44);
        avatar.setMaxSize(44, 44);
        Label avatarLbl = new Label(currentUser.getFullName().substring(0, 1).toUpperCase());
        avatarLbl.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        avatar.getChildren().add(avatarLbl);

        Label userName = new Label(currentUser.getFullName());
        userName.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label roleBadge = new Label(currentUser.getRole() == Role.ADMIN ? "Admin" : "Colocataire");
        roleBadge.setStyle(
                "-fx-background-color: " + (currentUser.getRole() == Role.ADMIN ? "#6366F120" : "#10B98120") + ";" +
                        "-fx-text-fill: " + (currentUser.getRole() == Role.ADMIN ? "#818CF8" : "#34D399") + ";" +
                        "-fx-background-radius: 20; -fx-padding: 2 10; -fx-font-size: 11px; -fx-font-weight: bold;"
        );

        VBox userInfo = new VBox(4, userName, roleBadge);
        HBox userHeader = new HBox(12, avatar, userInfo);
        userHeader.setAlignment(Pos.CENTER_LEFT);
        userHeader.setPadding(new Insets(0, 0, 20, 0));
        userHeader.setStyle(
                "-fx-border-color: transparent transparent #1E2749 transparent;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        Label navLabel = new Label("NAVIGATION");
        navLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 16 0 8 4;");

        Button btnAccueil  = sideBtn("⌂", "Accueil",         "#6366F1");
        Button btnTaches   = sideBtn("≡", "Tâches",          "#8B5CF6");
        Button btnDepenses = sideBtn("€", "Dépenses",        "#10B981");
        Button btnNotifs   = sideBtn("!", "Notifications",   "#F59E0B");
        Button btnRapport  = sideBtn("≈", "Rapport mensuel", "#3B82F6");

        VBox menu = new VBox(4, navLabel, btnAccueil, btnTaches, btnDepenses, btnNotifs, btnRapport);

        if (currentUser.getRole() == Role.ADMIN) {
            Label adminLabel = new Label("ADMINISTRATION");
            adminLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 16 0 8 4;");
            Button btnUsers = sideBtn("*", "Utilisateurs", "#EF4444");
            btnUsers.setOnAction(e -> SceneManager.showAdmin(currentUser));
            menu.getChildren().addAll(adminLabel, btnUsers);
        }

        btnAccueil.setOnAction(e  -> content.getChildren().setAll(buildHomePane()));
        btnTaches.setOnAction(e   -> SceneManager.showTasks(currentUser));
        btnDepenses.setOnAction(e -> SceneManager.showExpenses(currentUser));
        btnNotifs.setOnAction(e   -> SceneManager.showNotifications(currentUser));
        btnRapport.setOnAction(e  -> SceneManager.showMonthlyReport(currentUser));

        int unread = controller.getNotifCount();
        if (unread > 0) {
            Label badge = new Label(unread + " nouvelle(s)");
            badge.setStyle(
                    "-fx-background-color: rgba(99,102,241,0.15);" +
                            "-fx-text-fill: #818CF8;" +
                            "-fx-background-radius: 8; -fx-padding: 8 12;" +
                            "-fx-font-size: 11px; -fx-font-weight: bold;" +
                            "-fx-max-width: infinity;"
            );
            badge.setMaxWidth(Double.MAX_VALUE);
            menu.getChildren().add(badge);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("↩  Déconnexion");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setStyle(
                "-fx-background-color: rgba(239,68,68,0.1);" +
                        "-fx-text-fill: #EF4444; -fx-font-size: 13px;" +
                        "-fx-alignment: CENTER_LEFT; -fx-cursor: hand;" +
                        "-fx-padding: 11 16; -fx-background-radius: 10;"
        );
        btnLogout.setOnMouseEntered(e -> btnLogout.setStyle(
                "-fx-background-color: rgba(239,68,68,0.2);" +
                        "-fx-text-fill: #EF4444; -fx-font-size: 13px;" +
                        "-fx-alignment: CENTER_LEFT; -fx-cursor: hand;" +
                        "-fx-padding: 11 16; -fx-background-radius: 10;"
        ));
        btnLogout.setOnMouseExited(e -> btnLogout.setStyle(
                "-fx-background-color: rgba(239,68,68,0.1);" +
                        "-fx-text-fill: #EF4444; -fx-font-size: 13px;" +
                        "-fx-alignment: CENTER_LEFT; -fx-cursor: hand;" +
                        "-fx-padding: 11 16; -fx-background-radius: 10;"
        ));
        btnLogout.setOnAction(e -> SceneManager.showLogin());

        VBox sidebar = new VBox(0, logoRow, userHeader, menu, spacer, btnLogout);
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom, #0F1225, #1A1F3C);");
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        return sidebar;
    }

    private VBox buildHomePane() {
        Circle deco1 = new Circle(100);
        deco1.setFill(Color.web("#FFFFFF", 0.06));
        deco1.setTranslateX(400);
        deco1.setTranslateY(-20);
        deco1.setEffect(new GaussianBlur(20));

        Circle deco2 = new Circle(70);
        deco2.setFill(Color.web("#FFFFFF", 0.05));
        deco2.setTranslateX(600);
        deco2.setTranslateY(20);
        deco2.setEffect(new GaussianBlur(15));

        Circle deco3 = new Circle(140);
        deco3.setFill(Color.web("#FFFFFF", 0.04));
        deco3.setTranslateX(700);
        deco3.setTranslateY(-40);
        deco3.setEffect(new GaussianBlur(30));

        String date = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH)
        );

        Text titleText = new Text("Bonjour, " + currentUser.getFullName() + " !");
        titleText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        titleText.setFill(Color.WHITE);

        Text dateText = new Text(date);
        dateText.setFont(Font.font("Segoe UI", 14));
        dateText.setFill(Color.web("#FFFFFF", 0.65));

        Text subtitleText = new Text("Voici un aperçu complet de votre colocation");
        subtitleText.setFont(Font.font("Segoe UI", 14));
        subtitleText.setFill(Color.web("#FFFFFF", 0.8));

        Button quickTask = new Button("+ Nouvelle tâche");
        quickTask.setStyle(
                "-fx-background-color: rgba(255,255,255,0.15);" +
                        "-fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;" +
                        "-fx-padding: 9 18; -fx-font-size: 13px; -fx-font-weight: bold;" +
                        "-fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 8; -fx-border-width: 1;"
        );
        quickTask.setOnAction(e -> SceneManager.showTasks(currentUser));

        Button quickExpense = new Button("+ Nouvelle dépense");
        quickExpense.setStyle(
                "-fx-background-color: rgba(255,255,255,0.15);" +
                        "-fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;" +
                        "-fx-padding: 9 18; -fx-font-size: 13px; -fx-font-weight: bold;" +
                        "-fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 8; -fx-border-width: 1;"
        );
        quickExpense.setOnAction(e -> SceneManager.showExpenses(currentUser));

        HBox quickBtns = new HBox(12, quickTask, quickExpense);

        VBox headerContent = new VBox(8, titleText, dateText, subtitleText,
                new Region() {{ setPrefHeight(4); }}, quickBtns);
        headerContent.setAlignment(Pos.CENTER_LEFT);

        StackPane header = new StackPane(deco1, deco2, deco3, headerContent);
        header.setStyle("-fx-background-color: linear-gradient(to right, #0F1225, #1A1F3C, #312E81);");
        header.setPadding(new Insets(36, 40, 36, 40));
        StackPane.setAlignment(headerContent, Pos.CENTER_LEFT);

        // Stat cards
        HBox statsRow = new HBox(16,
                richStatCard("≡", "Tâches totales",
                        String.valueOf(controller.getTotalTasks()), "#6366F1", "#EEF2FF"),
                richStatCard("✓", "Mes tâches",
                        String.valueOf(controller.getMyTasksCount()), "#10B981", "#D1FAE5"),
                richStatCard("!", "Notifications",
                        String.valueOf(controller.getNotifCount()), "#F59E0B", "#FEF3C7"),
                richStatCard("€", "Mon solde",
                        String.format("%.2f €", controller.getMyDue()), "#EF4444", "#FEE2E2")
        );
        statsRow.setPadding(new Insets(24, 36, 0, 36));

        HBox mainSection = new HBox(20);
        mainSection.setPadding(new Insets(20, 36, 36, 36));

        // Actions rapides
        VBox actionsCard = buildCard("Actions rapides");
        actionsCard.setPrefWidth(260);

        Button[] actionBtns = {
                actionMenuBtn("≡  Gérer les tâches",      "#6366F1"),
                actionMenuBtn("€  Gérer les dépenses",    "#10B981"),
                actionMenuBtn("!  Voir les notifications", "#F59E0B"),
                actionMenuBtn("≈  Rapport mensuel",        "#3B82F6")
        };
        actionBtns[0].setOnAction(e -> SceneManager.showTasks(currentUser));
        actionBtns[1].setOnAction(e -> SceneManager.showExpenses(currentUser));
        actionBtns[2].setOnAction(e -> SceneManager.showNotifications(currentUser));
        actionBtns[3].setOnAction(e -> SceneManager.showMonthlyReport(currentUser));

        for (Button b : actionBtns) {
            b.setMaxWidth(Double.MAX_VALUE);
            actionsCard.getChildren().add(b);
        }

        // Activité récente
        VBox recentCard = buildCard("Activité récente");
        HBox.setHgrow(recentCard, Priority.ALWAYS);

        List<Task> tasks = MainApp.taskService.getAllTasks();
        if (tasks.isEmpty()) {
            Label empty = new Label("Aucune tâche pour le moment.");
            empty.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 13px; -fx-padding: 16;");
            recentCard.getChildren().add(empty);
        } else {
            tasks.stream().limit(5).forEach(t ->
                    recentCard.getChildren().add(buildTaskRow(t))
            );
        }

        // Résumé financier — CORRIGÉ
        VBox financeCard = buildCard("Résumé financier");
        financeCard.setPrefWidth(260);

        double totalExp = MainApp.expenseService.getAllExpenses()
                .stream().mapToDouble(e -> e.getAmount()).sum();
        double myDue = controller.getMyDue();
        int nbUsers = MainApp.userService.getAllUsers().size();

        financeCard.getChildren().addAll(
                financeRow("Total dépenses", String.format("%.2f €", totalExp), "#6366F1"),
                financeRow("Mon solde",      String.format("%.2f €", myDue),
                        myDue > 0 ? "#EF4444" : "#10B981"),
                financeRow("Colocataires",   String.valueOf(nbUsers), "#8B5CF6"),
                financeRow("Moy./personne",
                        nbUsers > 0 ? String.format("%.2f €", totalExp / nbUsers) : "0.00 €", "#F59E0B")
        );

        mainSection.getChildren().addAll(actionsCard, recentCard, financeCard);

        ScrollPane scroll = new ScrollPane();
        VBox mainContent = new VBox(0, header, statsRow, mainSection);
        scroll.setContent(mainContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #F1F5F9; -fx-background: #F1F5F9;");

        VBox wrapper = new VBox(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return wrapper;
    }

    private HBox buildTaskRow(Task task) {
        String statusColor, statusText, statusBg;
        switch (task.getStatus()) {
            case TO_DO              -> { statusColor = "#6B7280"; statusText = "À faire";    statusBg = "#F3F4F6"; }
            case IN_PROGRESS        -> { statusColor = "#D97706"; statusText = "En cours";   statusBg = "#FEF3C7"; }
            case PENDING_VALIDATION -> { statusColor = "#2563EB"; statusText = "En attente"; statusBg = "#DBEAFE"; }
            case VALIDATED          -> { statusColor = "#059669"; statusText = "Validée";    statusBg = "#D1FAE5"; }
            case REJECTED           -> { statusColor = "#DC2626"; statusText = "Rejetée";    statusBg = "#FEE2E2"; }
            default                 -> { statusColor = "#6B7280"; statusText = "-";          statusBg = "#F3F4F6"; }
        }

        Circle dot = new Circle(5);
        dot.setFill(Color.web(statusColor));

        Label titleLbl = new Label(task.getTitle());
        titleLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151; -fx-font-weight: bold;");
        HBox.setHgrow(titleLbl, Priority.ALWAYS);

        Label statusLbl = new Label(statusText);
        statusLbl.setStyle(
                "-fx-background-color: " + statusBg + "; -fx-text-fill: " + statusColor + ";" +
                        "-fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold;"
        );

        HBox row = new HBox(10, dot, titleLbl, statusLbl);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle(
                "-fx-background-color: #F9FAFB; -fx-background-radius: 8;" +
                        "-fx-border-color: #F3F4F6; -fx-border-radius: 8; -fx-border-width: 1;"
        );
        return row;
    }

    // CORRECTION : libellé et valeur bien séparés
    private HBox financeRow(String label, String value, String color) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
        lbl.setMinWidth(120);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label val = new Label(value);
        val.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        HBox row = new HBox(lbl, spacer, val);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 10;");
        return row;
    }

    private VBox buildCard(String title) {
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        Separator sep = new Separator();

        VBox card = new VBox(12, titleLbl, sep);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 3);" +
                        "-fx-padding: 20;"
        );
        return card;
    }

    private VBox richStatCard(String icon, String title, String value, String color, String bg) {
        StackPane iconBox = new StackPane();
        iconBox.setStyle(
                "-fx-background-color: " + color + "20;" +
                        "-fx-background-radius: 12;" +
                        "-fx-min-width: 48; -fx-min-height: 48;" +
                        "-fx-max-width: 48; -fx-max-height: 48;"
        );
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 22px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");
        iconBox.getChildren().add(iconLbl);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");

        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Rectangle bar = new Rectangle(40, 3);
        bar.setFill(Color.web(color));
        bar.setArcWidth(3);
        bar.setArcHeight(3);

        VBox card = new VBox(8, iconBox, titleLbl, valueLbl, bar);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-color: " + color + "25;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 4);" +
                        "-fx-padding: 20 24;"
        );
        card.setPrefWidth(200);
        HBox.setHgrow(card, Priority.ALWAYS);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-background-radius: 16; -fx-border-radius: 16;" +
                        "-fx-border-color: " + color + "; -fx-border-width: 2;" +
                        "-fx-effect: dropshadow(gaussian, " + color + "40, 18, 0, 0, 5);" +
                        "-fx-padding: 20 24; -fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16; -fx-border-radius: 16;" +
                        "-fx-border-color: " + color + "25; -fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 4);" +
                        "-fx-padding: 20 24;"
        ));

        return card;
    }

    private Button actionMenuBtn(String label, String color) {
        Button btn = new Button(label);
        btn.setStyle(
                "-fx-background-color: " + color + "12;" +
                        "-fx-text-fill: " + color + "; -fx-font-size: 13px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 10; -fx-cursor: hand;" +
                        "-fx-padding: 12 16; -fx-alignment: CENTER_LEFT;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 10; -fx-cursor: hand;" +
                        "-fx-padding: 12 16; -fx-alignment: CENTER_LEFT;" +
                        "-fx-effect: dropshadow(gaussian, " + color + "50, 8, 0, 0, 3);"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + color + "12;" +
                        "-fx-text-fill: " + color + "; -fx-font-size: 13px; -fx-font-weight: bold;" +
                        "-fx-background-radius: 10; -fx-cursor: hand;" +
                        "-fx-padding: 12 16; -fx-alignment: CENTER_LEFT;"
        ));
        return btn;
    }

    private Button sideBtn(String icon, String label, String color) {
        Button btn = new Button(icon + "  " + label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #9CA3AF; -fx-font-size: 13px;" +
                        "-fx-alignment: CENTER_LEFT; -fx-cursor: hand;" +
                        "-fx-padding: 11 16; -fx-background-radius: 10;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + color + "20;" +
                        "-fx-text-fill: white; -fx-font-size: 13px;" +
                        "-fx-alignment: CENTER_LEFT; -fx-cursor: hand;" +
                        "-fx-padding: 11 16; -fx-background-radius: 10;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #9CA3AF; -fx-font-size: 13px;" +
                        "-fx-alignment: CENTER_LEFT; -fx-cursor: hand;" +
                        "-fx-padding: 11 16; -fx-background-radius: 10;"
        ));
        return btn;
    }
}