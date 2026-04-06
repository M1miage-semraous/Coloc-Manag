package com.colocmanager;

import com.colocmanager.controller.TaskController;
import com.colocmanager.enums.Role;
import com.colocmanager.model.Task;
import com.colocmanager.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class TaskView {

    private final TaskController controller;
    private VBox taskListBox;
    private int selectedIndex = -1;

    public TaskView(Stage stage, User user) {
        this.controller = new TaskController(user);
        build(stage, user);
    }

    private void build(Stage stage, User user) {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #F8FAFC;");

        StackPane header = buildHeader(user);

        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(24, 32, 32, 32));

        VBox leftCol = new VBox(16);
        leftCol.setPrefWidth(320);

        if (user.getRole() == Role.ADMIN) {
            leftCol.getChildren().add(buildCreateTaskCard(user));
        }
        leftCol.getChildren().add(buildLegendCard());

        VBox rightCol = new VBox(16);
        HBox.setHgrow(rightCol, Priority.ALWAYS);
        rightCol.getChildren().add(buildTasksCard(user));

        mainContent.getChildren().addAll(leftCol, rightCol);

        ScrollPane scroll = new ScrollPane(mainContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #F8FAFC; -fx-background: #F8FAFC;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, scroll);

        Scene scene = new Scene(root, 1200, 750);
        scene.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
        );
        stage.setTitle("ColocManager — Tâches");
        stage.setScene(scene);
        stage.show();
    }

    private StackPane buildHeader(User user) {
        Circle deco1 = new Circle(60);
        deco1.setFill(Color.web("#FFFFFF", 0.06));
        deco1.setTranslateX(400);
        deco1.setTranslateY(-10);

        Circle deco2 = new Circle(40);
        deco2.setFill(Color.web("#FFFFFF", 0.05));
        deco2.setTranslateX(600);
        deco2.setTranslateY(15);

        Rectangle deco3 = new Rectangle(80, 80);
        deco3.setFill(Color.web("#FFFFFF", 0.04));
        deco3.setRotate(45);
        deco3.setTranslateX(700);
        deco3.setTranslateY(-20);

        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16; -fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 8; -fx-border-width: 1;");
        btnRetour.setOnAction(e -> SceneManager.showDashboard(user));

        int total = MainApp.taskService.getAllTasks().size();

        Text title = new Text("📋  Gestion des tâches");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setFill(Color.WHITE);

        Text subtitle = new Text(total + " tâche(s) au total • " +
                (user.getRole() == Role.ADMIN ? "Vue administrateur" : "Mes tâches assignées"));
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setFill(Color.web("#FFFFFF", 0.75));

        VBox headerContent = new VBox(6, btnRetour, title, subtitle);
        headerContent.setAlignment(Pos.CENTER_LEFT);

        StackPane header = new StackPane(deco1, deco2, deco3, headerContent);
        header.setStyle("-fx-background-color: linear-gradient(to right, #1A1F3C, #4338CA, #7C3AED);");
        header.setPadding(new Insets(28, 36, 28, 36));
        StackPane.setAlignment(headerContent, Pos.CENTER_LEFT);
        return header;
    }

    private VBox buildCreateTaskCard(User user) {
        VBox card = new VBox(14);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 4); -fx-padding: 20;");

        Label cardTitle = new Label("➕  Créer une tâche");
        cardTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        TextField tfTitre = new TextField();
        tfTitre.setPromptText("Titre de la tâche");
        styleField(tfTitre);

        TextField tfDesc = new TextField();
        tfDesc.setPromptText("Description (optionnelle)");
        styleField(tfDesc);

        TextField tfJours = new TextField("7");
        tfJours.setPromptText("Jours avant deadline");
        styleField(tfJours);

        ComboBox<String> cbImportance = new ComboBox<>();
        cbImportance.getItems().addAll("LOW — Faible", "MEDIUM — Moyenne", "HIGH — Haute");
        cbImportance.setValue("MEDIUM — Moyenne");
        cbImportance.setMaxWidth(Double.MAX_VALUE);
        styleCombo(cbImportance);

        ComboBox<String> cbUser = new ComboBox<>();
        cbUser.getItems().addAll(controller.getUserNames());
        cbUser.getSelectionModel().selectFirst();
        cbUser.setMaxWidth(Double.MAX_VALUE);
        styleCombo(cbUser);

        Label lblResult = new Label();
        lblResult.setWrapText(true);
        lblResult.setMaxWidth(Double.MAX_VALUE);

        Button btnCreer = new Button("✓  Créer la tâche");
        btnCreer.setMaxWidth(Double.MAX_VALUE);
        btnCreer.setStyle("-fx-background-color: linear-gradient(to right, #6366F1, #818CF8); -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 12 20; -fx-effect: dropshadow(gaussian, rgba(99,102,241,0.3), 8, 0, 0, 3);");

        btnCreer.setOnAction(e -> {
            String impVal = cbImportance.getValue().split(" — ")[0];
            controller.handleCreateTask(
                    tfTitre.getText().trim(),
                    tfDesc.getText().trim(),
                    tfJours.getText().trim(),
                    impVal,
                    cbUser.getValue(),
                    lblResult,
                    null
            );
            if (lblResult.getText().startsWith("✓")) {
                lblResult.setStyle("-fx-text-fill: #065F46; -fx-background-color: #D1FAE5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px;");
                tfTitre.clear();
                tfDesc.clear();
                refreshTaskList(user);
            } else {
                lblResult.setStyle("-fx-text-fill: #991B1B; -fx-background-color: #FEE2E2; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px;");
            }
        });

        card.getChildren().addAll(cardTitle, new Separator(), tfTitre, tfDesc, tfJours, cbImportance, cbUser, btnCreer, lblResult);
        return card;
    }

    private VBox buildLegendCard() {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 4); -fx-padding: 20;");

        Label title = new Label("🔍  Légende des statuts");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        VBox legend = new VBox(8,
                legendItem("À faire",       "#6B7280", "#F3F4F6"),
                legendItem("En cours",      "#D97706", "#FEF3C7"),
                legendItem("En attente",    "#2563EB", "#DBEAFE"),
                legendItem("Validée ✓",    "#059669", "#D1FAE5"),
                legendItem("Rejetée ✗",   "#DC2626", "#FEE2E2")
        );

        card.getChildren().addAll(title, new Separator(), legend);
        return card;
    }

    private HBox legendItem(String label, String color, String bg) {
        Circle dot = new Circle(6);
        dot.setFill(Color.web(color));

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + "; -fx-background-color: " + bg + "; -fx-background-radius: 20; -fx-padding: 3 10; -fx-font-weight: bold;");

        HBox row = new HBox(10, dot, lbl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox buildTasksCard(User user) {
        VBox card = new VBox(16);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 4); -fx-padding: 20;");

        Label cardTitle = new Label("📋  Liste des tâches");
        cardTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        taskListBox = new VBox(10);
        refreshTaskList(user);

        Label lblAction = new Label();
        lblAction.setWrapText(true);
        lblAction.setMaxWidth(Double.MAX_VALUE);

        HBox actionsRow = buildActionsRow(user, lblAction);

        card.getChildren().addAll(cardTitle, new Separator(), taskListBox, new Separator(), actionsRow, lblAction);
        return card;
    }

    private HBox buildActionsRow(User user, Label lblAction) {
        Button btnStart    = actionBtn("▶  Démarrer", "#F59E0B");
        Button btnTerminer = actionBtn("✓  Terminer", "#10B981");

        btnStart.setOnAction(e -> {
            controller.handleStartTask(selectedIndex, null, lblAction);
            refreshTaskList(user);
            updateActionLabel(lblAction, true);
        });

        btnTerminer.setOnAction(e -> {
            controller.handleCompleteTask(selectedIndex, null, lblAction);
            refreshTaskList(user);
            updateActionLabel(lblAction, true);
        });

        HBox row = new HBox(10, btnStart, btnTerminer);

        if (user.getRole() == Role.ADMIN) {
            Button btnValider = actionBtn("✅  Valider",    "#6366F1");
            Button btnRejeter = actionBtn("✗  Rejeter",    "#EF4444");
            Button btnSuppr   = actionBtn("🗑  Supprimer", "#6B7280");

            TextField tfComment = new TextField();
            tfComment.setPromptText("Commentaire (rejet)");
            tfComment.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px; -fx-pref-width: 160;");

            btnValider.setOnAction(e -> {
                controller.handleValidateTask(selectedIndex, null, lblAction);
                refreshTaskList(user);
                updateActionLabel(lblAction, true);
            });
            btnRejeter.setOnAction(e -> {
                controller.handleRejectTask(selectedIndex, tfComment.getText(), null, lblAction);
                refreshTaskList(user);
                updateActionLabel(lblAction, true);
            });
            btnSuppr.setOnAction(e -> {
                controller.handleDeleteTask(selectedIndex, null, lblAction);
                refreshTaskList(user);
                updateActionLabel(lblAction, true);
            });

            row.getChildren().addAll(btnValider, btnRejeter, tfComment, btnSuppr);
        }

        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void updateActionLabel(Label lbl, boolean success) {
        if (lbl.getText().startsWith("✓")) {
            lbl.setStyle("-fx-text-fill: #065F46; -fx-background-color: #D1FAE5; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px; -fx-font-weight: bold;");
        } else {
            lbl.setStyle("-fx-text-fill: #991B1B; -fx-background-color: #FEE2E2; -fx-background-radius: 8; -fx-padding: 8 12; -fx-font-size: 12px; -fx-font-weight: bold;");
        }
    }

    private void refreshTaskList(User user) {
        if (taskListBox == null) return;
        taskListBox.getChildren().clear();
        selectedIndex = -1;

        List<Task> tasks = user.getRole() == Role.ADMIN
                ? MainApp.taskService.getAllTasks()
                : MainApp.taskService.getTasksByAssignedUser(user);

        if (tasks.isEmpty()) {
            Label empty = new Label("Aucune tâche pour le moment.");
            empty.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 13px; -fx-padding: 20;");
            taskListBox.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            final int idx = i;
            HBox row = buildTaskRow(t, idx);
            taskListBox.getChildren().add(row);
        }
    }

    private HBox buildTaskRow(Task task, int idx) {
        String statusColor, statusText, statusBg;
        switch (task.getStatus()) {
            case TO_DO              -> { statusColor = "#6B7280"; statusText = "À faire";      statusBg = "#F3F4F6"; }
            case IN_PROGRESS        -> { statusColor = "#D97706"; statusText = "En cours";     statusBg = "#FEF3C7"; }
            case PENDING_VALIDATION -> { statusColor = "#2563EB"; statusText = "En attente";   statusBg = "#DBEAFE"; }
            case VALIDATED          -> { statusColor = "#059669"; statusText = "Validée ✓";   statusBg = "#D1FAE5"; }
            case REJECTED           -> { statusColor = "#DC2626"; statusText = "Rejetée ✗";  statusBg = "#FEE2E2"; }
            default                 -> { statusColor = "#6B7280"; statusText = task.getStatus().name(); statusBg = "#F3F4F6"; }
        }

        String priorityColor;
        switch (task.getCalculatedPriority()) {
            case URGENT -> priorityColor = "#DC2626";
            case HIGH   -> priorityColor = "#D97706";
            case MEDIUM -> priorityColor = "#2563EB";
            default     -> priorityColor = "#059669";
        }

        Circle priorityDot = new Circle(6);
        priorityDot.setFill(Color.web(priorityColor));

        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        Label assignedLabel = new Label("👤 " + (task.getAssignedUser() != null ? task.getAssignedUser().getFullName() : "-"));
        assignedLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");

        Label deadlineLabel = new Label("🗓 " + (task.getDeadline() != null ? task.getDeadline().toString() : "-"));
        deadlineLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");

        Label priorityLabel = new Label(task.getCalculatedPriority().name());
        priorityLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + priorityColor + "; -fx-background-color: " + priorityColor + "20; -fx-background-radius: 20; -fx-padding: 2 8; -fx-font-weight: bold;");

        Label statusLabel = new Label(statusText);
        statusLabel.setStyle("-fx-background-color: " + statusBg + "; -fx-text-fill: " + statusColor + "; -fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold;");

        HBox metaRow = new HBox(10, assignedLabel, deadlineLabel, priorityLabel);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(4, titleLabel, metaRow);
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox row = new HBox(12, priorityDot, info, statusLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: transparent; -fx-cursor: hand;");

        row.setOnMouseClicked(e -> {
            selectedIndex = idx;
            taskListBox.getChildren().forEach(n ->
                    n.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: transparent; -fx-cursor: hand;")
            );
            row.setStyle("-fx-background-color: #EEF2FF; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #6366F1; -fx-border-width: 1.5; -fx-cursor: hand;");
        });

        row.setOnMouseEntered(e -> {
            if (selectedIndex != idx)
                row.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-cursor: hand;");
        });
        row.setOnMouseExited(e -> {
            if (selectedIndex != idx)
                row.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: transparent; -fx-cursor: hand;");
        });

        return row;
    }

    private Button actionBtn(String label, String color) {
        Button btn = new Button(label);
        btn.setStyle("-fx-background-color: " + color + "20; -fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 9 16;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 9 16;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + "20; -fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 9 16;"));
        return btn;
    }

    private void styleField(TextField tf) {
        tf.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 14; -fx-font-size: 13px;");
        tf.setMaxWidth(Double.MAX_VALUE);
    }

    private void styleCombo(ComboBox<String> cb) {
        cb.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px;");
    }
}