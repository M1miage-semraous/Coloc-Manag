package com.colocmanager;

import com.colocmanager.enums.Role;
import com.colocmanager.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class DashboardView {

    private static final String NAVY  = "#1A2B4A";
    private static final String TEAL  = "#0D9488";
    private static final String WHITE = "#FFFFFF";
    private static final String GREY  = "#F8FAFC";
    private static final String SLATE = "#64748B";

    private Stage stage;
    private User currentUser;

    public DashboardView(Stage stage, User user) {
        this.stage       = stage;
        this.currentUser = user;
        build();
    }

    private void build() {

        // ── Sidebar ────────────────────────────────────
        Text appName = new Text("ColocManager");
        appName.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        appName.setFill(Color.WHITE);

        Text userName = new Text("👤  " + currentUser.getFullName());
        userName.setFont(Font.font("Arial", 13));
        userName.setFill(Color.web("#14B8A6"));

        Text userRole = new Text(currentUser.getRole() == Role.ADMIN ? "🛡  Administrateur" : "Colocataire");
        userRole.setFont(Font.font("Arial", 11));
        userRole.setFill(Color.web("#94A3B8"));

        VBox userInfo = new VBox(4, userName, userRole);
        userInfo.setPadding(new Insets(0, 0, 16, 0));
        userInfo.setStyle("-fx-border-color: transparent transparent #2D4A6E transparent; -fx-border-width: 1;");

        // Boutons sidebar
        Button btnTaches    = sideBtn("📋  Tâches");
        Button btnDepenses  = sideBtn("💰  Dépenses");
        Button btnNotifs    = sideBtn("🔔  Notifications");
        Button btnDeconnect = sideBtn("🚪  Se déconnecter");
        btnDeconnect.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #F87171; " +
                        "-fx-font-size: 13; -fx-alignment: CENTER_LEFT; " +
                        "-fx-cursor: hand; -fx-padding: 10 16;"
        );

        VBox sidebarBtns = new VBox(4, btnTaches, btnDepenses, btnNotifs);
        if (currentUser.getRole() == Role.ADMIN) {
            Button btnUsers = sideBtn("👥  Utilisateurs");
            btnUsers.setOnAction(e -> afficherUtilisateurs());
            sidebarBtns.getChildren().add(btnUsers);
        }

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(24, 12, 24, 12));
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        sidebar.getChildren().addAll(appName, new Region(), userInfo, sidebarBtns);
        VBox.setVgrow(new Region(), javafx.scene.layout.Priority.ALWAYS);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(spacer, btnDeconnect);

        // ── Zone principale ────────────────────────────
        StackPane content = new StackPane();
        content.setStyle("-fx-background-color: " + GREY + ";");

        // Afficher les tâches par défaut
        content.getChildren().add(buildTachesPane());

        // Actions sidebar
        btnTaches.setOnAction(e -> {
            content.getChildren().setAll(buildTachesPane());
        });
        btnDepenses.setOnAction(e -> {
            content.getChildren().setAll(buildDepensesPane());
        });
        btnNotifs.setOnAction(e -> {
            content.getChildren().setAll(buildNotifsPane());
        });
        btnDeconnect.setOnAction(e -> {
            new LoginView(stage);
        });

        // ── Layout principal ───────────────────────────
        HBox root = new HBox(sidebar, content);
        HBox.setHgrow(content, Priority.ALWAYS);

        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("ColocManager — " + currentUser.getFullName());
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    // ── Tâches ─────────────────────────────────────────────────

    private VBox buildTachesPane() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(28));

        Text titre = new Text("Tâches");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        // Formulaire de création
        TextField tfTitre = new TextField();
        tfTitre.setPromptText("Titre de la tâche");
        styleField(tfTitre);

        TextField tfJours = new TextField("7");
        tfJours.setPromptText("Deadline (jours)");
        styleField(tfJours);

        ComboBox<String> cbImportance = new ComboBox<>();
        cbImportance.getItems().addAll("LOW", "MEDIUM", "HIGH");
        cbImportance.setValue("MEDIUM");
        cbImportance.setStyle("-fx-font-size: 13;");

        ComboBox<String> cbUser = new ComboBox<>();
        MainApp.userService.getAllUsers()
                .forEach(u -> cbUser.getItems().add(u.getFullName()));
        cbUser.getSelectionModel().selectFirst();
        cbUser.setStyle("-fx-font-size: 13;");

        Button btnCreer = actionBtn("+ Créer la tâche");
        Label lblResult = new Label("");
        lblResult.setStyle("-fx-text-fill: " + TEAL + "; -fx-font-size: 12;");

        btnCreer.setOnAction(e -> {
            try {
                String titreVal = tfTitre.getText().trim();
                int jours = Integer.parseInt(tfJours.getText().trim());
                String impVal = cbImportance.getValue();
                String nomUser = cbUser.getValue();

                User assignee = MainApp.userService.getAllUsers().stream()
                        .filter(u -> u.getFullName().equals(nomUser))
                        .findFirst().orElse(currentUser);

                MainApp.taskService.createTask(
                        titreVal, "", LocalDate.now().plusDays(jours),
                        com.colocmanager.enums.ImportanceLevel.valueOf(impVal),
                        1, assignee, currentUser
                );
                lblResult.setText("✓ Tâche créée : " + titreVal);
                tfTitre.clear();
            } catch (Exception ex) {
                lblResult.setText("✗ " + ex.getMessage());
            }
        });

        HBox form = new HBox(10, tfTitre, tfJours, cbImportance, cbUser, btnCreer);
        form.setAlignment(Pos.CENTER_LEFT);

        // Liste des tâches
        ListView<String> listView = new ListView<>();
        listView.setStyle("-fx-font-size: 12;");
        refreshTachesList(listView);

        // Boutons action sur tâche sélectionnée
        Button btnStart    = actionBtn("▶ Démarrer");
        Button btnTerminer = actionBtn("✓ Terminer");
        Button btnValider  = actionBtn("✅ Valider");
        Button btnRejeter  = new Button("✗ Rejeter");
        btnRejeter.setStyle(
                "-fx-background-color: #DC2626; -fx-text-fill: white; " +
                        "-fx-font-size: 12; -fx-background-radius: 6; -fx-cursor: hand;"
        );

        Label lblAction = new Label("");
        lblAction.setStyle("-fx-text-fill: " + TEAL + "; -fx-font-size: 12;");

        btnStart.setOnAction(e -> {
            int idx = listView.getSelectionModel().getSelectedIndex();
            if (idx < 0) return;
            Task t = MainApp.taskService.getAllTasks().get(idx);
            MainApp.taskService.startTask(t.getId());
            refreshTachesList(listView);
            lblAction.setText("✓ Tâche démarrée.");
        });

        btnTerminer.setOnAction(e -> {
            int idx = listView.getSelectionModel().getSelectedIndex();
            if (idx < 0) return;
            Task t = MainApp.taskService.getAllTasks().get(idx);
            MainApp.taskService.markTaskCompleted(t.getId());
            refreshTachesList(listView);
            lblAction.setText("✓ En attente de validation.");
        });

        btnValider.setOnAction(e -> {
            if (currentUser.getRole() != Role.ADMIN) {
                lblAction.setText("✗ Réservé à l'admin.");
                return;
            }
            int idx = listView.getSelectionModel().getSelectedIndex();
            if (idx < 0) return;
            Task t = MainApp.taskService.getAllTasks().get(idx);
            MainApp.taskService.validateTask(t.getId(), currentUser, "OK");
            refreshTachesList(listView);
            lblAction.setText("✓ Tâche validée !");
        });

        btnRejeter.setOnAction(e -> {
            if (currentUser.getRole() != Role.ADMIN) {
                lblAction.setText("✗ Réservé à l'admin.");
                return;
            }
            int idx = listView.getSelectionModel().getSelectedIndex();
            if (idx < 0) return;
            Task t = MainApp.taskService.getAllTasks().get(idx);
            MainApp.taskService.rejectTask(t.getId(), currentUser, "À refaire");
            refreshTachesList(listView);
            lblAction.setText("✓ Tâche rejetée.");
        });

        HBox actions = new HBox(10, btnStart, btnTerminer, btnValider, btnRejeter, lblAction);
        actions.setAlignment(Pos.CENTER_LEFT);

        pane.getChildren().addAll(titre, form, lblResult, listView, actions);
        VBox.setVgrow(listView, Priority.ALWAYS);
        return pane;
    }

    private void refreshTachesList(ListView<String> lv) {
        lv.getItems().clear();
        for (Task t : MainApp.taskService.getAllTasks()) {
            lv.getItems().add(
                    String.format("%-25s | %-18s | Priorité: %-6s | → %s | Deadline: %s",
                            t.getTitle(),
                            t.getStatus(),
                            t.getCalculatedPriority(),
                            t.getAssignedUser() != null ? t.getAssignedUser().getFullName() : "—",
                            t.getDeadline())
            );
        }
    }

    // ── Dépenses ───────────────────────────────────────────────

    private VBox buildDepensesPane() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(28));

        Text titre = new Text("Dépenses");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        TextField tfLabel = new TextField();
        tfLabel.setPromptText("Libellé (ex: Courses)");
        styleField(tfLabel);

        TextField tfMontant = new TextField();
        tfMontant.setPromptText("Montant (€)");
        styleField(tfMontant);

        Button btnAjouter = actionBtn("+ Ajouter");
        Label lblResult = new Label("");
        lblResult.setStyle("-fx-text-fill: " + TEAL + "; -fx-font-size: 12;");

        btnAjouter.setOnAction(e -> {
            try {
                String label = tfLabel.getText().trim();
                double montant = Double.parseDouble(tfMontant.getText().replace(",", ".").trim());
                List<User> tous = MainApp.userService.getAllUsers();
                MainApp.expenseService.createExpense(
                        label, montant, "", LocalDate.now(), currentUser, tous
                );
                lblResult.setText("✓ " + label + " (" + montant + "€) ajouté. Part : "
                        + String.format("%.2f", montant / tous.size()) + "€/personne");
                tfLabel.clear();
                tfMontant.clear();
            } catch (Exception ex) {
                lblResult.setText("✗ " + ex.getMessage());
            }
        });

        HBox form = new HBox(10, tfLabel, tfMontant, btnAjouter);
        form.setAlignment(Pos.CENTER_LEFT);

        // Mon solde
        double solde = MainApp.expenseService.getTotalDueByUser(currentUser.getId());
        Label lblSolde = new Label("💰  Mon solde total : " + String.format("%.2f", solde) + "€");
        lblSolde.setStyle(
                "-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: " + NAVY + "; " +
                        "-fx-background-color: #E0F2FE; -fx-padding: 10 16; -fx-background-radius: 8;"
        );

        ListView<String> listView = new ListView<>();
        listView.setStyle("-fx-font-size: 12;");
        for (Expense ex : MainApp.expenseService.getAllExpenses()) {
            listView.getItems().add(
                    String.format("%-20s | %.2f€ | Payé par: %-10s | %s",
                            ex.getLabel(), ex.getAmount(),
                            ex.getPaidBy().getFullName(), ex.getExpenseDate())
            );
            for (ExpenseShare share : ex.getShares()) {
                listView.getItems().add(
                        "      → " + share.getUser().getFullName()
                                + " doit : " + String.format("%.2f", share.getAmountDue()) + "€"
                );
            }
        }

        pane.getChildren().addAll(titre, form, lblResult, lblSolde, listView);
        VBox.setVgrow(listView, Priority.ALWAYS);
        return pane;
    }

    // ── Notifications ──────────────────────────────────────────

    private VBox buildNotifsPane() {
        VBox pane = new VBox(12);
        pane.setPadding(new Insets(28));

        Text titre = new Text("Notifications");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        List<Notification> notifs = currentUser.getNotifications();

        if (notifs.isEmpty()) {
            Label empty = new Label("Aucune notification.");
            empty.setStyle("-fx-text-fill: " + SLATE + "; -fx-font-size: 13;");
            pane.getChildren().addAll(titre, empty);
            return pane;
        }

        for (Notification n : notifs) {
            String tag = n.isRead() ? "LU" : "NEW";
            String bg  = n.isRead() ? "#F8FAFC" : "#F0FDF9";
            String border = n.isRead() ? "#CBD5E1" : TEAL;

            Label lbl = new Label("[" + tag + "]  " + n.getTitle() + " — " + n.getMessage());
            lbl.setStyle(
                    "-fx-font-size: 12; -fx-padding: 10 14; " +
                            "-fx-background-color: " + bg + "; " +
                            "-fx-border-color: " + border + "; -fx-border-radius: 6; " +
                            "-fx-background-radius: 6;"
            );
            lbl.setMaxWidth(Double.MAX_VALUE);
            pane.getChildren().add(lbl);
        }

        pane.getChildren().add(0, titre);
        return pane;
    }

    // ── Utilisateurs (admin) ────────────────────────────────────

    private void afficherUtilisateurs() {
        // Fenêtre modale simple
        Stage modal = new Stage();
        modal.setTitle("Utilisateurs");

        VBox pane = new VBox(10);
        pane.setPadding(new Insets(20));

        Text titre = new Text("Utilisateurs");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titre.setFill(Color.web(NAVY));

        ListView<String> lv = new ListView<>();
        for (User u : MainApp.userService.getAllUsers()) {
            lv.getItems().add(u.getFullName() + "  |  " + u.getEmail() + "  |  " + u.getRole());
        }

        pane.getChildren().addAll(titre, lv);
        modal.setScene(new Scene(pane, 500, 300));
        modal.show();
    }

    // ── Helpers ────────────────────────────────────────────────

    private Button sideBtn(String label) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #CBD5E1; " +
                        "-fx-font-size: 13; -fx-alignment: CENTER_LEFT; " +
                        "-fx-cursor: hand; -fx-padding: 10 16;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #2D4A6E; -fx-text-fill: white; " +
                        "-fx-font-size: 13; -fx-alignment: CENTER_LEFT; " +
                        "-fx-cursor: hand; -fx-padding: 10 16; -fx-background-radius: 6;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #CBD5E1; " +
                        "-fx-font-size: 13; -fx-alignment: CENTER_LEFT; " +
                        "-fx-cursor: hand; -fx-padding: 10 16;"
        ));
        return btn;
    }

    private Button actionBtn(String label) {
        Button btn = new Button(label);
        btn.setStyle(
                "-fx-background-color: " + TEAL + "; -fx-text-fill: white; " +
                        "-fx-font-size: 12; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        return btn;
    }

    private void styleField(TextField tf) {
        tf.setStyle(
                "-fx-background-radius: 6; -fx-border-radius: 6; " +
                        "-fx-border-color: #CBD5E1; -fx-font-size: 12; " +
                        "-fx-pref-height: 34;"
        );
    }
}