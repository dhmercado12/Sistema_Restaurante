package com.dhery.views;

import com.dhery.app.Router;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SuggestionView {

    public static Scene getScene() {

        // ───────────────── ROOT ─────────────────
        StackPane root = new StackPane();

        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #FFFFFF, #FFF5F5);"
        );

        // ───────────────── MAIN CONTAINER ─────────────────
        VBox mainContainer = new VBox(24);

        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(30));

        // ───────────────── TOP BAR ─────────────────
        BorderPane topBar = buildTopBar();

        // ───────────────── CARD ─────────────────
        HBox contentArea = new HBox(35);

        contentArea.setAlignment(Pos.CENTER);

        VBox leftPanel = buildLeftPanel();

        VBox suggestionCard = buildSuggestionCard();

        contentArea.getChildren().addAll(leftPanel, suggestionCard);

        // ───────────────── FOOTER ─────────────────
        VBox footer = buildFooter();

        mainContainer.getChildren().addAll(
            topBar,
            contentArea,
            footer
        );

        root.getChildren().add(mainContainer);

        return new Scene(root, 1280, 720);
    }

    // ───────────────── TOP BAR ─────────────────

    private static BorderPane buildTopBar() {

        BorderPane topBar = new BorderPane();

        // Botón volver
        Button btnBack = new Button("← VOLVER");

        btnBack.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: #CC0000;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: #FFCCCC;" +
            "-fx-border-radius: 20;" +
            "-fx-padding: 10 22;" +
            "-fx-cursor: hand;"
        );

        btnBack.setOnMouseEntered(e ->
            btnBack.setStyle(
                "-fx-background-color: #CC0000;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 10 22;" +
                "-fx-cursor: hand;"
            )
        );

        btnBack.setOnMouseExited(e ->
            btnBack.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #CC0000;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: #FFCCCC;" +
                "-fx-border-radius: 20;" +
                "-fx-padding: 10 22;" +
                "-fx-cursor: hand;"
            )
        );

        btnBack.setOnAction(e -> Router.goMenuClienteView());

        // Notificación
        StackPane notif = new StackPane();

        Circle circle = new Circle(24);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.web("#EEEEEE"));

        Label bell = new Label("🔔");
        bell.setStyle("-fx-font-size: 16px;");

        notif.getChildren().addAll(circle, bell);

        topBar.setLeft(btnBack);
        topBar.setRight(notif);

        return topBar;
    }

    // ───────────────── LEFT PANEL ─────────────────

    private static VBox buildLeftPanel() {

        VBox panel = new VBox(18);

        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setPrefWidth(400);

        Label icon = new Label("💬");

        icon.setStyle(
            "-fx-font-size: 72px;"
        );

        Label title = new Label("Tu opinión\nnos importa\n    ");

        title.setFont(Font.font("System", FontWeight.BOLD, 40));

        title.setStyle(
            "-fx-text-fill: #1A1A1A;"
        );

        Label desc = new Label(
            
            "Ayúdanos a mejorar tu experiencia.\n" +
            "Puedes enviarnos sugerencias.\n" +
            "mejorar nuestro restaurante.\n" +
             "¡Gracias por ser parte de Tacabrón!"
            
        );


        desc.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: #777777;" +
            "-fx-line-spacing: 5px;"
        );


        VBox tipsBox = buildTipsBox();
        
        panel.getChildren().addAll(
            icon,
            title,
            desc,
            tipsBox
        );

        return panel;
    }

    // ───────────────── TIPS BOX ─────────────────

    private static VBox buildTipsBox() {

        VBox tips = new VBox(12);

        tips.setPadding(new Insets(18));

        tips.setTranslateY(30);

        tips.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 18;" +
            "-fx-border-color: #FFE0E0;" +
            "-fx-border-radius: 18;"
        );

        Label title = new Label("✨ Puedes sugerir:");

        title.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #CC0000;"
        );

        Label t1 = buildTip("🍽️ Nuevos platos");
        Label t2 = buildTip("⚡ Mejoras en atención");
        Label t3 = buildTip("🎵 Ambiente y comodidad");
        Label t4 = buildTip("📱 Funciones del sistema");

        tips.getChildren().addAll(title, t1, t2, t3, t4);

        return tips;
    }

    private static Label buildTip(String text) {

        Label lbl = new Label(text);

        lbl.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #555555;"
        );

        return lbl;
    }

    // ───────────────── SUGGESTION CARD ─────────────────

    private static VBox buildSuggestionCard() {

        VBox card = new VBox(18);

        card.setAlignment(Pos.TOP_LEFT);

        card.setPadding(new Insets(35));

        card.setPrefSize(620, 520);

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 28;" +
            "-fx-border-color: #F2F2F2;" +
            "-fx-border-radius: 28;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 20, 0, 0, 6);"
        );

        // Header
        Label title = new Label("Enviar sugerencia");

        title.setStyle(
            "-fx-font-size: 30px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #1A1A1A;"
        );

        Label subtitle = new Label(
            "Comparte tu experiencia con nosotros"
        );

        subtitle.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #888888;"
        );

        // Nombre
        VBox fieldName = buildInputField(
            "Tu nombre",
            "Ejemplo: Dhery Mercado"
        );

        // Categoría
        VBox categoryBox = new VBox(8);

        Label categoryLbl = new Label("Categoría");

        categoryLbl.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #333333;"
        );

        ComboBox<String> categoryCombo = new ComboBox<>();

        categoryCombo.getItems().addAll(
            "Atención al cliente",
            "Comida",
            "Aplicación",
            "Ambiente",
            "Otros"
        );

        categoryCombo.setPromptText("Selecciona una categoría");

        categoryCombo.setPrefHeight(42);
        categoryCombo.setPrefWidth(520);

        categoryCombo.setStyle(
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-border-color: #DDDDDD;" +
            "-fx-background-color: #FAFAFA;"
        );

        categoryBox.getChildren().addAll(
            categoryLbl,
            categoryCombo
        );

        // Área sugerencia
        VBox areaBox = new VBox(8);

        Label areaLbl = new Label("Tu sugerencia");

        areaLbl.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #333333;"
        );

        TextArea txtSuggestion = new TextArea();

        txtSuggestion.setPromptText(
            "Escribe aquí tu sugerencia..."
        );

        txtSuggestion.setWrapText(true);

        txtSuggestion.setPrefHeight(160);

        txtSuggestion.setStyle(
            "-fx-background-radius: 16;" +
            "-fx-border-radius: 16;" +
            "-fx-border-color: #DDDDDD;" +
            "-fx-background-color: #FAFAFA;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 13px;"
        );

        areaBox.getChildren().addAll(
            areaLbl,
            txtSuggestion
        );

        // Botón enviar
        Button btnSend = new Button("✈ ENVIAR SUGERENCIA");

        btnSend.setPrefSize(260, 50);

        String normal =
            "-fx-background-color: linear-gradient(to right, #CC0000, #E53935);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 28;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(204,0,0,0.25), 12, 0, 0, 4);";

        String hover =
            "-fx-background-color: linear-gradient(to right, #B00000, #D32F2F);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 28;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(204,0,0,0.35), 16, 0, 0, 5);";

        btnSend.setStyle(normal);

        btnSend.setOnMouseEntered(e -> btnSend.setStyle(hover));
        btnSend.setOnMouseExited(e -> btnSend.setStyle(normal));

        btnSend.setOnAction(e -> {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Sugerencia enviada");

            alert.setHeaderText("¡Gracias por tu sugerencia! ❤️");

            alert.setContentText(
                "Tu comentario fue enviado correctamente."
            );

            alert.showAndWait();
        });

        HBox btnBox = new HBox(btnSend);

        btnBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
            title,
            subtitle,
            fieldName,
            categoryBox,
            areaBox,
            btnBox
        );

        return card;
    }

    // ───────────────── INPUT FIELD ─────────────────

    private static VBox buildInputField(
        String labelText,
        String placeholder
    ) {

        VBox box = new VBox(8);

        Label lbl = new Label(labelText);

        lbl.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #333333;"
        );

        TextField field = new TextField();

        field.setPromptText(placeholder);

        field.setPrefHeight(42);

        field.setStyle(
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-border-color: #DDDDDD;" +
            "-fx-background-color: #FAFAFA;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 0 12;"
        );

        box.getChildren().addAll(lbl, field);

        return box;
    }

    // ───────────────── FOOTER ─────────────────

    private static VBox buildFooter() {

        VBox box = new VBox(4);

        box.setAlignment(Pos.CENTER);

        HBox lineRow = new HBox(12);

        lineRow.setAlignment(Pos.CENTER);

        Region leftLine = new Region();

        leftLine.setPrefSize(80, 1);

        leftLine.setStyle(
            "-fx-background-color: #DDDDDD;"
        );

        Label icon = new Label("❤️");

        icon.setStyle(
            "-fx-font-size: 14px;"
        );

        Region rightLine = new Region();

        rightLine.setPrefSize(80, 1);

        rightLine.setStyle(
            "-fx-background-color: #DDDDDD;"
        );

        lineRow.getChildren().addAll(
            leftLine,
            icon,
            rightLine
        );

        Label text = new Label(
            "Gracias por ayudarnos a mejorar"
        );

        text.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-style: italic;" +
            "-fx-text-fill: #777777;"
        );

        box.getChildren().addAll(
            lineRow,
            text
        );

        return box;
    }
}