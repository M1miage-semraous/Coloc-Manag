package com.colocmanager;

import com.colocmanager.controller.DashboardController;
import com.colocmanager.enums.Role;
import com.colocmanager.model.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

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
        content.getStyleClass().add("content-area");
        content.getChildren().add(buildHomePane());

        HBox root = new HBox(sidebar, content);
        HBox.setHgrow(content, Priority.ALWAYS);

        Scene scene = new Scene(root, 1200, 750);
        scene.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
        );
        stage.setTitle("ColocManager — " + currentUser.getFullName());
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildSidebar() {
        Text appName = new Text("🏠 ColocManager");
        appName.getStyleClass().add("sidebar-title");

        Label userName = new Label("👤 " + currentUser.getFullName());
        userName.getStyleClass().add("sidebar-username");

        Label userRole = new Label(currentUser.getRole() == Role.ADMIN ? "🛡 Administrateur" : "🏡 Colocataire");
        userRole.getStyleClass().add("sidebar-role");

        VBox userInfo = new VBox(4, userName, userRole);
        userInfo.setPadding(new Insets(0, 0, 16, 0));
        userInfo.setStyle("-fx-border-color: transparent transparent #1E293B transparent; -fx-border-width: 1;");

        Button btnAccueil  = sideBtn("🏠  Accueil");
        Button btnTaches   = sideBtn("📋  Tâches");
        Button btnDepenses = sideBtn("💰  Dépenses");
        Button btnNotifs   = sideBtn("🔔  Notifications");
        Button btnRapport  = sideBtn("📊  Rapport mensuel");
        Button btnLogout   = new Button("🚪  Déconnexion");
        btnLogout.getStyleClass().add("sidebar-btn-logout");
        btnLogout.setMaxWidth(Double.MAX_VALUE);

        VBox menu = new VBox(4, btnAccueil, btnTaches, btnDepenses, btnNotifs, btnRapport);

        if (currentUser.getRole() == Role.ADMIN) {
            Button btnUsers = sideBtn("👥  Utilisateurs");
            btnUsers.setOnAction(e -> SceneManager.showAdmin(currentUser));
            menu.getChildren().add(btnUsers);
        }

        btnAccueil.setOnAction(e  -> content.getChildren().setAll(buildHomePane()));
        btnTaches.setOnAction(e   -> SceneManager.showTasks(currentUser));
        btnDepenses.setOnAction(e -> SceneManager.showExpenses(currentUser));
        btnNotifs.setOnAction(e   -> SceneManager.showNotifications(currentUser));
        btnRapport.setOnAction(e  -> SceneManager.showMonthlyReport(currentUser));
        btnLogout.setOnAction(e   -> SceneManager.showLogin());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox sidebar = new VBox(16, appName, userInfo, menu, spacer, btnLogout);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);
        sidebar.setPadding(new Insets(28, 16, 24, 16));
        return sidebar;
    }

    private VBox buildHomePane() {
        VBox pane = new VBox(24);
        pane.setPadding(new Insets(36));

        Text title = new Text("Bonjour, " + currentUser.getFullName() + " 👋");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Voici un aperçu de votre colocation aujourd'hui.");
        subtitle.getStyleClass().add("page-subtitle");

        HBox cards = new HBox(16,
                statCard("📋 Tâches totales",  String.valueOf(controller.getTotalTasks()), "#6366F1"),
                statCard("✅ Mes tâches",       String.valueOf(controller.getMyTasksCount()), "#10B981"),
                statCard("🔔 Notifications",    String.valueOf(controller.getNotifCount()), "#F59E0B"),
                statCard("💰 Mon solde",        String.format("%.2f €", controller.getMyDue()), "#EF4444")
        );

        // Actions rapides
        Text actionsTitle = new Text("Actions rapides");
        actionsTitle.getStyleClass().add("section-title");

        Button goTasks    = new Button("📋  Gérer les tâches");
        Button goExpenses = new Button("💰  Gérer les dépenses");
        Button goNotifs   = new Button("🔔  Voir les notifications");
        goTasks.getStyleClass().add("btn-primary");
        goExpenses.getStyleClass().add("btn-success");
        goNotifs.getStyleClass().add("btn-secondary");

        goTasks.setOnAction(e    -> SceneManager.showTasks(currentUser));
        goExpenses.setOnAction(e -> SceneManager.showExpenses(currentUser));
        goNotifs.setOnAction(e   -> SceneManager.showNotifications(currentUser));

        HBox actions = new HBox(12, goTasks, goExpenses, goNotifs);

        pane.getChildren().addAll(title, subtitle, cards, actionsTitle, actions);
        return pane;
    }

    private VBox buildUsersPane() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(36));

        Text titre = new Text("👥 Utilisateurs");
        titre.getStyleClass().add("page-title");

        ListView<String> usersList = new ListView<>();
        usersList.getStyleClass().add("list-view");
        for (User u : MainApp.userService.getAllUsers()) {
            usersList.getItems().add(
                    u.getFullName() + "   |   " + u.getEmail() + "   |   " + u.getRole()
            );
        }

        pane.getChildren().addAll(titre, usersList);
        VBox.setVgrow(usersList, Priority.ALWAYS);
        return pane;
    }

    private VBox statCard(String title, String value, String accentColor) {
        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("stat-title");

        Label lblValue = new Label(value);
        lblValue.getStyleClass().add("stat-value");
        lblValue.setStyle("-fx-text-fill: " + accentColor + ";");

        VBox card = new VBox(8, lblTitle, lblValue);
        card.getStyleClass().add("stat-card");
        return card;
    }

    private Button sideBtn(String label) {
        Button btn = new Button(label);
        btn.getStyleClass().add("sidebar-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }
}