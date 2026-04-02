package com.colocmanager;

import com.colocmanager.controller.DashboardController;
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

import java.util.List;

public class DashboardView {

    private static final String NAVY  = "#1A2B4A";
    private static final String TEAL  = "#0D9488";
    private static final String GREY  = "#F8FAFC";
    private static final String SLATE = "#64748B";

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

        Button btnAccueil  = sideBtn("🏠 Accueil");
        Button btnTaches   = sideBtn("📋 Tâches");
        Button btnDepenses = sideBtn("💰 Dépenses");
        Button btnNotifs   = sideBtn("🔔 Notifications");
        Button btnLogout   = sideBtn("🚪 Déconnexion");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #F87171; -fx-font-size: 13; -fx-alignment: CENTER_LEFT; -fx-cursor: hand; -fx-padding: 10 16;");

        VBox menu = new VBox(6, btnAccueil, btnTaches, btnDepenses, btnNotifs);

        if (currentUser.getRole() == Role.ADMIN) {
            Button btnUsers = sideBtn("👥 Utilisateurs");
            btnUsers.setOnAction(e -> content.getChildren().setAll(buildUsersPane()));
            menu.getChildren().add(btnUsers);
        }

        btnAccueil.setOnAction(e  -> content.getChildren().setAll(buildHomePane()));
        btnTaches.setOnAction(e   -> content.getChildren().setAll(buildTachesPane()));
        btnDepenses.setOnAction(e -> content.getChildren().setAll(buildDepensesPane()));
        btnNotifs.setOnAction(e   -> content.getChildren().setAll(buildNotifsPane()));
        btnLogout.setOnAction(e   -> SceneManager.showLogin());

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

        HBox cards = new HBox(16,
                statCard("Tâches totales",  String.valueOf(controller.getTotalTasks())),
                statCard("Mes tâches",       String.valueOf(controller.getMyTasksCount())),
                statCard("Notifications",    String.valueOf(controller.getNotifCount())),
                statCard("Mon solde",        String.format("%.2f €", controller.getMyDue()))
        );

        Button goTasks    = actionBtn("Voir les tâches");
        Button goExpenses = actionBtn("Voir les dépenses");
        goTasks.setOnAction(e    -> content.getChildren().setAll(buildTachesPane()));
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

        TextField tfTitre       = new TextField(); tfTitre.setPromptText("Titre");            styleField(tfTitre);
        TextField tfDescription = new TextField(); tfDescription.setPromptText("Description"); styleField(tfDescription);
        TextField tfJours       = new TextField("7"); tfJours.setPromptText("Deadline (jours)"); styleField(tfJours);

        ComboBox<String> cbImportance = new ComboBox<>();
        cbImportance.getItems().addAll("LOW", "MEDIUM", "HIGH");
        cbImportance.setValue("MEDIUM");

        ComboBox<String> cbUser = new ComboBox<>();
        cbUser.getItems().addAll(controller.getUserNames());
        cbUser.getSelectionModel().selectFirst();

        Label lblResult = new Label();
        lblResult.setStyle("-fx-text-fill: " + TEAL + "; -fx-font-size: 12;");

        ListView<String> taskList = new ListView<>();
        controller.refreshTachesList(taskList);

        Button btnCreer = actionBtn("+ Créer la tâche");
        btnCreer.setOnAction(e -> {
            controller.handleCreateTask(
                    tfTitre.getText().trim(),
                    tfDescription.getText().trim(),
                    tfJours.getText().trim(),
                    cbImportance.getValue(),
                    cbUser.getValue(),
                    lblResult
            );
            tfTitre.clear();
            tfDescription.clear();
            controller.refreshTachesList(taskList);
        });

        Label lblAction = new Label();
        lblAction.setStyle("-fx-text-fill: " + TEAL + "; -fx-font-size: 12;");

        Button btnStart    = actionBtn("▶ Démarrer");
        Button btnTerminer = actionBtn("✓ Terminer");
        Button btnValider  = actionBtn("✅ Valider");
        Button btnRejeter  = new Button("✗ Rejeter");
        btnRejeter.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-size: 12; -fx-background-radius: 6; -fx-cursor: hand;");

        btnStart.setOnAction(e    -> controller.handleStartTask(taskList.getSelectionModel().getSelectedIndex(), taskList, lblAction));
        btnTerminer.setOnAction(e -> controller.handleCompleteTask(taskList.getSelectionModel().getSelectedIndex(), taskList, lblAction));
        btnValider.setOnAction(e  -> controller.handleValidateTask(taskList.getSelectionModel().getSelectedIndex(), taskList, lblAction));
        btnRejeter.setOnAction(e  -> controller.handleRejectTask(taskList.getSelectionModel().getSelectedIndex(), taskList, lblAction));

        HBox form    = new HBox(10, tfTitre, tfDescription, tfJours, cbImportance, cbUser, btnCreer);
        HBox actions = new HBox(10, btnStart, btnTerminer, btnValider, btnRejeter, lblAction);
        form.setAlignment(Pos.CENTER_LEFT);

        pane.getChildren().addAll(titre, form, lblResult, taskList, actions);
        VBox.setVgrow(taskList, Priority.ALWAYS);
        return pane;
    }

    private VBox buildDepensesPane() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(28));

        Text titre = new Text("Gestion des dépenses");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        TextField tfLabel   = new TextField(); tfLabel.setPromptText("Libellé");       styleField(tfLabel);
        TextField tfMontant = new TextField(); tfMontant.setPromptText("Montant (€)"); styleField(tfMontant);

        Label lblResult = new Label();
        lblResult.setStyle("-fx-text-fill: " + TEAL + "; -fx-font-size: 12;");

        ListView<String> expenseList = new ListView<>();
        refreshExpensesList(expenseList);

        Button btnAjouter = actionBtn("+ Ajouter une dépense");
        btnAjouter.setOnAction(e -> {
            controller.handleCreateExpense(tfLabel.getText().trim(), tfMontant.getText().trim(), lblResult);
            tfLabel.clear();
            tfMontant.clear();
            refreshExpensesList(expenseList);
        });

        Label lblSolde = new Label("💰 Mon solde : " + String.format("%.2f €", controller.getMyDue()));
        lblSolde.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + NAVY + ";");

        HBox form = new HBox(10, tfLabel, tfMontant, btnAjouter);
        pane.getChildren().addAll(titre, form, lblResult, lblSolde, expenseList);
        VBox.setVgrow(expenseList, Priority.ALWAYS);
        return pane;
    }

    private void refreshExpensesList(ListView<String> listView) {
        listView.getItems().clear();
        for (Expense ex : MainApp.expenseService.getAllExpenses()) {
            String paidBy = ex.getPaidBy() != null ? ex.getPaidBy().getFullName() : "-";
            listView.getItems().add(
                    ex.getLabel() + " | " + ex.getAmount() + "€ | payé par " + paidBy + " | " + ex.getExpenseDate()
            );
            for (ExpenseShare share : ex.getShares()) {
                String userName = share.getUser() != null ? share.getUser().getFullName() : "-";
                listView.getItems().add("   → " + userName + " doit " + String.format("%.2f", share.getAmountDue()) + "€");
            }
        }
    }

    private VBox buildNotifsPane() {
        VBox pane = new VBox(12);
        pane.setPadding(new Insets(28));

        Text titre = new Text("Notifications");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        List<Notification> notifications = MainApp.notificationService.getNotificationsByUser(currentUser);

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
        btn.setOnMouseExited(e  -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CBD5E1; -fx-font-size: 13; -fx-alignment: CENTER_LEFT; -fx-cursor: hand; -fx-padding: 10 16;"));
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