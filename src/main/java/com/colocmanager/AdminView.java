package com.colocmanager;

import com.colocmanager.controller.AdminController;
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

public class AdminView {

    private static final String NAVY = "#1A2B4A";
    private static final String TEAL = "#0D9488";
    private static final String GREY = "#F8FAFC";

    public AdminView(Stage stage, User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            SceneManager.showDashboard(currentUser);
            return;
        }
        build(stage, currentUser);
    }

    private void build(Stage stage, User currentUser) {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(28));
        pane.setStyle("-fx-background-color: " + GREY + ";");

        Button btnRetour = new Button("← Retour");
        btnRetour.setStyle("-fx-background-color: transparent; -fx-text-fill: " + NAVY + "; -fx-font-size: 13; -fx-cursor: hand;");
        btnRetour.setOnAction(e -> SceneManager.showDashboard(currentUser));

        Text titre = new Text("Gestion des utilisateurs");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        titre.setFill(Color.web(NAVY));

        AdminController controller = new AdminController();

        // Formulaire création
        TextField tfNom    = new TextField(); tfNom.setPromptText("Nom complet");   styleField(tfNom);
        TextField tfEmail  = new TextField(); tfEmail.setPromptText("Email");        styleField(tfEmail);
        TextField tfPass   = new TextField(); tfPass.setPromptText("Mot de passe"); styleField(tfPass);

        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("USER", "ADMIN");
        cbRole.setValue("USER");

        Label lblResult = new Label();

        ListView<String> userList = new ListView<>();
        controller.refreshUserList(userList);
        VBox.setVgrow(userList, Priority.ALWAYS);

        Button btnCreer = actionBtn("+ Créer");
        btnCreer.setOnAction(e -> {
            controller.handleCreateUser(
                    tfNom.getText().trim(),
                    tfEmail.getText().trim(),
                    tfPass.getText().trim(),
                    cbRole.getValue(),
                    lblResult,
                    userList
            );
            tfNom.clear();
            tfEmail.clear();
            tfPass.clear();
        });

        Button btnSupprimer = new Button("🗑 Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-size: 12; -fx-background-radius: 6; -fx-cursor: hand;");
        btnSupprimer.setOnAction(e ->
                controller.handleDeleteUser(
                        userList.getSelectionModel().getSelectedIndex(),
                        userList,
                        lblResult
                )
        );

        HBox formRow = new HBox(10, tfNom, tfEmail, tfPass, cbRole, btnCreer, btnSupprimer);
        formRow.setAlignment(Pos.CENTER_LEFT);

        pane.getChildren().addAll(btnRetour, titre, formRow, lblResult, userList);

        Scene scene = new Scene(pane, 1100, 700);
        stage.setTitle("ColocManager - Administration");
        stage.setScene(scene);
        stage.show();
    }

    private Button actionBtn(String label) {
        Button btn = new Button(label);
        btn.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: white; -fx-font-size: 12; -fx-background-radius: 6; -fx-cursor: hand;");
        return btn;
    }

    private void styleField(TextField tf) {
        tf.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #CBD5E1; -fx-font-size: 12; -fx-pref-height: 34;");
    }
}