package com.colocmanager;

import com.colocmanager.enums.Role;
import com.colocmanager.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class DashboardView {

    private static final String NAVY = "#1A2B4A";
    private static final String TEAL = "#0D9488";
    private static final String GREY = "#F8FAFC";
    private static final String SLATE = "#64748B";

    private final Stage stage;
    private final User currentUser;
    private final StackPane content;

    public DashboardView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.content = new StackPane();
        build();
    }

    private void build() {
        VBox sidebar = buildSidebar();

        content.setStyle("-fx-background-color: " + GREY + ";");
        content.getChildren().add(buildHomePane());

        HBox root = new HBox(sidebar, content);
        HBox.setHgrow(content, Priority.ALWAYS);

        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("ColocManager - " + currentUser.getFullName());
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildSidebar() {
        Text appName = new Text("ColocManager");
        appName.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        appName.setFill(Color.WHITE);

        Text userName = new Text("👤 " + currentUser.getFullName());
        userName.setFont(Font.font("Arial", 13));
        userName.setFill(Color.web("#14B8A6"));

        Text userRole = new Text(currentUser.getRole() == Role.ADMIN ? "🛡 Administrateur" : "Colocataire");
        userRole.setFont(Font.font("Arial", 11));
        userRole.setFill(Color.web("#94A3B8"));

        VBox userInfo = new VBox(4, userName, userRole);
        userInfo.setPadding(new Insets(0, 0, 16, 0));
        userInfo.setStyle("-fx-border-color: transparent transparent #2D4A6E transparent; -fx-border-width: 1;");

        Button btnAccueil = sideBtn("🏠 Accueil");
        Button btnTaches = sideBtn("📋 Tâches");
        Button btnDepenses = sideBtn("💰 Dépenses");
        Button btnNotifs = sideBtn("🔔 Notifications");
        Button btnLogout = sideBtn("🚪 Déconnexion");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #F87171; -fx-font-size: 13; -fx-alignment: CENTER_LEFT; -fx-cursor: hand; -fx-padding: 10 16;");

        VBox menu = new VBox(6, btnAccueil, btnTaches, btnDepenses, btnNotifs);

        if (currentUser.getRole() == Role.ADMIN) {
            Button btnUsers = sideBtn("👥 Utilisateurs");
            btnUsers.setOnAction(e -> content.getChildren().setAll(buildUsersPane()));
            menu.getChildren().add(btnUsers);
        }

        btnAccueil.setOnAction(e -> content.getChildren().setAll(buildHomePane()));
        btnTaches.setOnAction(e -> content.getChildren().setAll(buildTachesPane()));
        btnDepenses.setOnAction(e -> content.getChildren().setAll(buildDepensesPane()));
        btnNotifs.setOnAction(e -> content.getChildren().setAll(buildNotifsPane()));
        btnLogout.setOnAction(e -> new LoginView(stage));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox sidebar = new VBox(18, appName, userInfo, menu, spacer, btnLogout);
        sidebar.setPrefWidth(230);
        sidebar.setPadding(new Insets(24, 14, 24, 14));
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");

        return sidebar;
    }

    private VBox buildHomePane() {
        VBox pane = new VBox(18);
        pane.setPadding(new Insets(28));

        Text title = new Text("Bienvenue, " + currentUser.getFullName());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setFill(Color.web(NAVY));

        Label subtitle = new Label("Voici un aperçu rapide de votre colocation.");
        subtitle.setStyle("-fx-text-fill: " + SLATE + "; -fx-font-size: 13px;");

        int totalTasks = MainApp.taskService.getAllTasks().size();
        int myTasks = MainApp.taskService.getTasksByAssignedUser(currentUser).size();
        int notifCount = currentUser.getNotifications().size();
        double myDue = MainApp.expenseService.getTotalDueByUser(currentUser.getId());

        HBox cards = new HBox(16,
                statCard("Tâches totales", String.valueOf(totalTasks)),
                statCard("Mes tâches", String.valueOf(myTasks)),
                statCard("Notifications", String.valueOf(notifCount)),
                statCard("Mon solde", String.format("%.2f €", myDue))
        );

        VBox recap = new VBox(10);
        recap.getChildren().add(new Label("Actions rapides"));
        recap.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button goTasks = actionBtn("Voir les tâches");
        goTasks.setOnAction(e -> content.getChildren().setAll(buildTachesPane()));

        Button goExpenses = actionBtn("Voir les dépenses");
        goExpenses.setOnAction(e -> content.getChildren().setAll(buildDepensesPane()));

        HBox quick = new HBox(10, goTasks, goExpenses);

        pane.getChildren().addAll(title, subtitle, cards, quick);
        return pane;
    }

    private VBox buildTachesPane() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(28));

        Text titre = new Text("Gestion des tâches");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        TextField tfTitre = new TextField();
        tfTitre.setPromptText("Titre de la tâche");
        styleField(tfTitre);

        TextField tfDescription = new TextField();
        tfDescription.setPromptText("Description");
        styleField(tfDescription);

        TextField tfJours = new TextField("7");
        tfJours.setPromptText("Deadline (jours)");
        styleField(tfJours);

        ComboBox<String> cbImportance = new ComboBox<>();
        cbImportance.getItems().addAll("LOW", "MEDIUM", "HIGH");
        cbImportance.setValue("MEDIUM");

        ComboBox<String> cbUser = new ComboBox<>();
        MainApp.userService.getAllUsers().forEach(u -> cbUser.getItems().add(u.getFullName()));
        cbUser.getSelectionModel().selectFirst();

        Button btnCreer = actionBtn("+ Créer la tâche");
        Label lblResult = new Label();
        lblResult.setStyle("-fx-text-fill: " + TEAL + "; -fx-font-size: 12;");

        btnCreer.setOnAction(e -> {
            try {
                String titreVal = tfTitre.getText().trim();
                String descriptionVal = tfDescription.getText().trim();
                int jours = Integer.parseInt(tfJours.getText().trim());
                String importanceVal = cbImportance.getValue();
                String selectedUserName = cbUser.getValue();

                User assignee = MainApp.userService.getAllUsers().stream()
                        .filter(u -> u.getFullName().equals(selectedUserName))
                        .findFirst()
                        .orElse(currentUser);

                MainApp.taskService.createTask(
                        titreVal,
                        descriptionVal,
                        LocalDate.now().plusDays(jours),
                        com.colocmanager.enums.ImportanceLevel.valueOf(importanceVal),
                        1,
                        assignee,
                        currentUser
                );

                lblResult.setText("✓ Tâche créée avec succès");
                tfTitre.clear();
                tfDescription.clear();
            } catch (Exception ex) {
                lblResult.setText("✗ " + ex.getMessage());
            }
        });

        HBox form = new HBox(10, tfTitre, tfDescription, tfJours, cbImportance, cbUser, btnCreer);
        form.setAlignment(Pos.CENTER_LEFT);

        ListView<String> taskList = new ListView<>();
        refreshTachesList(taskList);

        Button btnStart = actionBtn("▶ Démarrer");
        Button btnTerminer = actionBtn("✓ Terminer");
        Button btnValider = actionBtn("✅ Valider");
        Button btnRejeter = new Button("✗ Rejeter");
        btnRejeter.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-size: 12; -fx-background-radius: 6; -fx-cursor: hand;");

        Label lblAction = new Label();
        lblAction.setStyle("-fx-text-fill: " + TEAL + "; -fx-font-size: 12;");

        btnStart.setOnAction(e -> {
            int idx = taskList.getSelectionModel().getSelectedIndex();
            if (idx < 0) return;
            Task t = MainApp.taskService.getAllTasks().get(idx);
            MainApp.taskService.startTask(t.getId());
            refreshTachesList(taskList);
            lblAction.setText("✓ Tâche démarrée");
        });

        btnTerminer.setOnAction(e -> {
            int idx = taskList.getSelectionModel().getSelectedIndex();
            if (idx < 0) return;
            Task t = MainApp.taskService.getAllTasks().get(idx);
            MainApp.taskService.markTaskCompleted(t.getId());
            refreshTachesList(taskList);
            lblAction.setText("✓ Tâche terminée");
        });

        btnValider.setOnAction(e -> {
            if (currentUser.getRole() != Role.ADMIN) {
                lblAction.setText("✗ Seul l'admin peut valider");
                return;
            }
            int idx = taskList.getSelectionModel().getSelectedIndex();
            if (idx < 0) return;
            Task t = MainApp.taskService.getAllTasks().get(idx);
            MainApp.taskService.validateTask(t.getId(), currentUser, "Validation OK");
            refreshTachesList(taskList);
            lblAction.setText("✓ Tâche validée");
        });

        btnRejeter.setOnAction(e -> {
            if (currentUser.getRole() != Role.ADMIN) {
                lblAction.setText("✗ Seul l'admin peut rejeter");
                return;
            }
            int idx = taskList.getSelectionModel().getSelectedIndex();
            if (idx < 0) return;
            Task t = MainApp.taskService.getAllTasks().get(idx);
            MainApp.taskService.rejectTask(t.getId(), currentUser, "À refaire");
            refreshTachesList(taskList);
            lblAction.setText("✓ Tâche rejetée");
        });

        HBox actions = new HBox(10, btnStart, btnTerminer, btnValider, btnRejeter, lblAction);

        pane.getChildren().addAll(titre, form, lblResult, taskList, actions);
        VBox.setVgrow(taskList, Priority.ALWAYS);
        return pane;
    }

    private void refreshTachesList(ListView<String> listView) {
        listView.getItems().clear();
        for (Task t : MainApp.taskService.getAllTasks()) {
            listView.getItems().add(
                    t.getTitle()
                            + " | " + t.getStatus()
                            + " | priorité: " + t.getCalculatedPriority()
                            + " | assignée à: " + (t.getAssignedUser() != null ? t.getAssignedUser().getFullName() : "-")
                            + " | deadline: " + t.getDeadline()
            );
        }
    }

    private VBox buildDepensesPane() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(28));

        Text titre = new Text("Gestion des dépenses");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        TextField tfLabel = new TextField();
        tfLabel.setPromptText("Libellé");
        styleField(tfLabel);

        TextField tfMontant = new TextField();
        tfMontant.setPromptText("Montant (€)");
        styleField(tfMontant);

        Button btnAjouter = actionBtn("+ Ajouter une dépense");
        Label lblResult = new Label();
        lblResult.setStyle("-fx-text-fill: " + TEAL + "; -fx-font-size: 12;");

        btnAjouter.setOnAction(e -> {
            try {
                String label = tfLabel.getText().trim();
                double montant = Double.parseDouble(tfMontant.getText().replace(",", ".").trim());
                List<User> users = MainApp.userService.getAllUsers();

                MainApp.expenseService.createExpense(
                        label,
                        montant,
                        "",
                        LocalDate.now(),
                        currentUser,
                        users
                );

                lblResult.setText("✓ Dépense ajoutée : " + label);
                tfLabel.clear();
                tfMontant.clear();
            } catch (Exception ex) {
                lblResult.setText("✗ " + ex.getMessage());
            }
        });

        HBox form = new HBox(10, tfLabel, tfMontant, btnAjouter);

        Label lblSolde = new Label("💰 Mon solde : " +
                String.format("%.2f €", MainApp.expenseService.getTotalDueByUser(currentUser.getId())));
        lblSolde.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        ListView<String> expenseList = new ListView<>();
        refreshExpensesList(expenseList);

        pane.getChildren().addAll(titre, form, lblResult, lblSolde, expenseList);
        VBox.setVgrow(expenseList, Priority.ALWAYS);
        return pane;
    }

    private void refreshExpensesList(ListView<String> listView) {
        listView.getItems().clear();

        for (Expense ex : MainApp.expenseService.getAllExpenses()) {
            listView.getItems().add(
                    ex.getLabel() + " | " +
                            ex.getAmount() + "€ | payé par " +
                            ex.getPaidBy().getFullName() + " | " +
                            ex.getExpenseDate()
            );

            for (ExpenseShare share : ex.getShares()) {
                listView.getItems().add("   → " + share.getUser().getFullName()
                        + " doit " + String.format("%.2f", share.getAmountDue()) + "€");
            }
        }
    }

    private VBox buildNotifsPane() {
        VBox pane = new VBox(12);
        pane.setPadding(new Insets(28));

        Text titre = new Text("Notifications");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        List<Notification> notifications = currentUser.getNotifications();

        if (notifications.isEmpty()) {
            Label empty = new Label("Aucune notification.");
            empty.setStyle("-fx-text-fill: " + SLATE + "; -fx-font-size: 13px;");
            pane.getChildren().addAll(titre, empty);
            return pane;
        }

        pane.getChildren().add(titre);

        for (Notification n : notifications) {
            Label lbl = new Label((n.isRead() ? "[LU] " : "[NEW] ") + n.getTitle() + " - " + n.getMessage());
            lbl.setWrapText(true);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setStyle("-fx-padding: 10 14; -fx-font-size: 12px; -fx-background-color: "
                    + (n.isRead() ? "#F8FAFC" : "#F0FDF9")
                    + "; -fx-border-color: " + (n.isRead() ? "#CBD5E1" : TEAL)
                    + "; -fx-background-radius: 6; -fx-border-radius: 6;");
            pane.getChildren().add(lbl);
        }

        return pane;
    }

    private VBox buildUsersPane() {
        VBox pane = new VBox(12);
        pane.setPadding(new Insets(28));

        Text titre = new Text("Utilisateurs");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        ListView<String> usersList = new ListView<>();
        for (User u : MainApp.userService.getAllUsers()) {
            usersList.getItems().add(u.getFullName() + " | " + u.getEmail() + " | " + u.getRole());
        }

        pane.getChildren().addAll(titre, usersList);
        VBox.setVgrow(usersList, Priority.ALWAYS);
        return pane;
    }

    private VBox statCard(String title, String value) {
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: " + SLATE + "; -fx-font-size: 13px;");

        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-text-fill: " + NAVY + "; -fx-font-size: 20px; -fx-font-weight: bold;");

        VBox card = new VBox(8, lblTitle, lblValue);
        card.setPadding(new Insets(16));
        card.setPrefWidth(190);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #E2E8F0;");
        return card;
    }

    private Button sideBtn(String label) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 13; -fx-alignment: CENTER_LEFT; -fx-cursor: hand; -fx-padding: 10 16;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #2D4A6E; -fx-text-fill: white; -fx-font-size: 13; -fx-alignment: CENTER_LEFT; -fx-cursor: hand; -fx-padding: 10 16; -fx-background-radius: 6;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 13; -fx-alignment: CENTER_LEFT; -fx-cursor: hand; -fx-padding: 10 16;"));
        return btn;
    }

    private Button actionBtn(String label) {
        Button btn = new Button(label);
        btn.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: white; -fx-font-size: 12; -fx-background-radius: 6; -fx-cursor: hand;");
        return btn;
    }

    private void styleField(TextField textField) {
        textField.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #CBD5E1; -fx-font-size: 12; -fx-pref-height: 34;");
    }
}