package com.colocmanager;

import com.colocmanager.controller.TaskController;
import com.colocmanager.enums.Role;
import com.colocmanager.model.User;
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

public class TaskView {

    private static final String NAVY  = "#1A2B4A";
    private static final String TEAL  = "#0D9488";
    private static final String GREY  = "#F8FAFC";

    private final TaskController controller;

    public TaskView(Stage stage, User user) {
        this.controller = new TaskController(user);
        build(stage, user);
    }

    private void build(Stage stage, User user) {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(28));
        pane.setStyle("-fx-background-color: " + GREY + ";");

        // Bouton retour
        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle("-fx-background-color: transparent; -fx-text-fill: " + NAVY + "; -fx-font-size: 13; -fx-cursor: hand;");
        btnRetour.setOnAction(e -> SceneManager.showDashboard(user));

        // Titre
        Text titre = new Text("Gestion des tâches");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        // Liste des tâches
        ListView<String> taskList = new ListView<>();
        controller.refreshTaskList(taskList);
        VBox.setVgrow(taskList, Priority.ALWAYS);

        Label lblAction = new Label();
        lblAction.setStyle("-fx-text-fill: #0D9488; -fx-font-size: 12;");

        // Boutons actions USER
        Button btnStart    = actionBtn("▶ Démarrer");
        Button btnTerminer = actionBtn("✓ Terminer");

        btnStart.setOnAction(e ->
                controller.handleStartTask(taskList.getSelectionModel().getSelectedIndex(), taskList, lblAction));
        btnTerminer.setOnAction(e ->
                controller.handleCompleteTask(taskList.getSelectionModel().getSelectedIndex(), taskList, lblAction));

        HBox actionsRow = new HBox(10, btnStart, btnTerminer, lblAction);
        actionsRow.setAlignment(Pos.CENTER_LEFT);

        pane.getChildren().addAll(btnRetour, titre, taskList, actionsRow);

        // Section admin uniquement
        if (user.getRole() == Role.ADMIN) {
            Label lblSeparator = new Label("── Actions administrateur ──");
            lblSeparator.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12;");

            // Formulaire création
            TextField tfTitre = new TextField();
            tfTitre.setPromptText("Titre de la tâche");
            styleField(tfTitre);

            TextField tfDescription = new TextField();
            tfDescription.setPromptText("Description");
            styleField(tfDescription);

            TextField tfJours = new TextField("7");
            tfJours.setPromptText("Deadline (jours)");
            tfJours.setPrefWidth(120);
            styleField(tfJours);

            ComboBox<String> cbImportance = new ComboBox<>();
            cbImportance.getItems().addAll("LOW", "MEDIUM", "HIGH");
            cbImportance.setValue("MEDIUM");

            ComboBox<String> cbUser = new ComboBox<>();
            cbUser.getItems().addAll(controller.getUserNames());
            cbUser.getSelectionModel().selectFirst();

            Label lblResult = new Label();
            lblResult.setStyle("-fx-text-fill: #0D9488; -fx-font-size: 12;");

            Button btnCreer = actionBtn("+ Créer");
            btnCreer.setOnAction(e -> {
                controller.handleCreateTask(
                        tfTitre.getText().trim(),
                        tfDescription.getText().trim(),
                        tfJours.getText().trim(),
                        cbImportance.getValue(),
                        cbUser.getValue(),
                        lblResult,
                        taskList
                );
                tfTitre.clear();
                tfDescription.clear();
            });

            TextField tfComment = new TextField();
            tfComment.setPromptText("Commentaire (rejet obligatoire)");
            styleField(tfComment);

            Button btnValider = actionBtn("✅ Valider");
            Button btnRejeter = dangerBtn("✗ Rejeter");
            Button btnSuppr   = dangerBtn("🗑 Supprimer");

            btnValider.setOnAction(e ->
                    controller.handleValidateTask(taskList.getSelectionModel().getSelectedIndex(), taskList, lblAction));
            btnRejeter.setOnAction(e ->
                    controller.handleRejectTask(taskList.getSelectionModel().getSelectedIndex(), tfComment.getText(), taskList, lblAction));
            btnSuppr.setOnAction(e ->
                    controller.handleDeleteTask(taskList.getSelectionModel().getSelectedIndex(), taskList, lblAction));

            HBox formRow   = new HBox(10, tfTitre, tfDescription, tfJours, cbImportance, cbUser, btnCreer);
            HBox adminRow  = new HBox(10, btnValider, btnRejeter, tfComment, btnSuppr);
            formRow.setAlignment(Pos.CENTER_LEFT);
            adminRow.setAlignment(Pos.CENTER_LEFT);

            pane.getChildren().addAll(lblSeparator, formRow, lblResult, adminRow);
        }

        Scene scene = new Scene(pane, 1100, 700);
        stage.setTitle("ColocManager - Tâches");
        stage.setScene(scene);
        stage.show();
    }

    private Button actionBtn(String label) {
        Button btn = new Button(label);
        btn.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: white; -fx-font-size: 12; -fx-background-radius: 6; -fx-cursor: hand;");
        return btn;
    }

    private Button dangerBtn(String label) {
        Button btn = new Button(label);
        btn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-size: 12; -fx-background-radius: 6; -fx-cursor: hand;");
        return btn;
    }

    private void styleField(TextField tf) {
        tf.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #CBD5E1; -fx-font-size: 12; -fx-pref-height: 34;");
    }
}