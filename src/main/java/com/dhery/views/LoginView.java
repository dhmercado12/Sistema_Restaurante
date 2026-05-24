package com.dhery.views;

import com.dhery.app.Router;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class LoginView {

    public static Scene getScene() {

        // =====================================================
        // ROOT (LUZ MÁS SUAVE + PROFUNDIDAD)
        // =====================================================
        StackPane root = new StackPane();

        root.setStyle("""
            -fx-background-color:
                radial-gradient(
                    radius 130%,
                    #FFF9F3 0%,
                    #F6EDE3 50%,
                    #EDE0D1 100%
                );
        """);

        // =====================================================
        // CARD PRINCIPAL (ULTRA CLEAN PREMIUM)
        // =====================================================
        VBox card = new VBox(28);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(70));
        card.setMaxWidth(720);

        card.setStyle("""
            -fx-background-color: rgba(255,255,255,0.95);

            -fx-background-radius: 44;

            -fx-border-color: linear-gradient(to right, #F7C59F, #FF9F1C);

            -fx-border-radius: 44;

            -fx-border-width: 2;

            -fx-effect: dropshadow(
                gaussian,
                rgba(0,0,0,0.14),
                35,
                0.25,
                0,
                12
            );
        """);

        // =====================================================
        // HEADER
        // =====================================================
        Label subtitle = new Label("Taquería");
        subtitle.setTextFill(Color.web("#7A756F"));
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label title = new Label("TACABRÓN");
        title.setTextFill(Color.web("#D9482C"));
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 68));

        Label slogan = new Label("Sistema Inteligente de Gestión");
        slogan.setTextFill(Color.web("#8A817C"));
        slogan.setFont(Font.font("Arial", FontWeight.MEDIUM, 15));

        VBox header = new VBox(-6, subtitle, title, slogan);
        header.setAlignment(Pos.CENTER);

        // línea premium
        Region line = new Region();
        line.setPrefSize(150, 4);
        line.setStyle("""
            -fx-background-color: linear-gradient(to right, #FF9F1C, #FF5E3A, #FF2E63);
            -fx-background-radius: 12;
        """);

        // =====================================================
        // DESCRIPCIÓN (CORREGIDO CENTRADO REAL)
        // =====================================================
        Label description = new Label(
            "Bienvenido a Tacabrón 🌮\n\n" +
            "Una experiencia rápida, moderna y deliciosa.\n" +
            "Ordena fácil, recibe rápido y disfruta mejor."
        );

        description.setTextFill(Color.web("#6F6A63"));
        description.setFont(Font.font("Arial", 16));
        description.setWrapText(true);
        description.setMaxWidth(520);

        description.setAlignment(Pos.CENTER);
        description.setTextAlignment(TextAlignment.CENTER);

        // 🔥 FIX REAL: CENTRADO PERFECTO VISUAL
        StackPane descWrapper = new StackPane(description);
        descWrapper.setAlignment(Pos.CENTER);

        // =====================================================
        // BOTONES
        // =====================================================
        Button cashierBtn = createButton("CAJERO");
        cashierBtn.setOnAction(e -> Router.goCashierLoginView());

        Button clientBtn = createButton("CLIENTE");
        clientBtn.setOnAction(e -> Router.goClientLogin());

        Button exitBtn = createButton("SALIR");
        exitBtn.setOnAction(e -> Router.goStart());

        VBox buttons = new VBox(14, cashierBtn, clientBtn, exitBtn);
        buttons.setAlignment(Pos.CENTER);

        // =====================================================
        // BUILD FINAL
        // =====================================================
        card.getChildren().addAll(
            header,
            line,
            descWrapper,
            buttons
        );

        root.getChildren().add(card);
        root.setPadding(new Insets(40));

        return new Scene(root, 1280, 720);
    }

    // =====================================================
    // BOTÓN ULTRA POLISHED (MÁS SUAVE + NATURAL)
    // =====================================================
    private static Button createButton(String text) {

        Button btn = new Button(text);

        btn.setPrefWidth(360);
        btn.setPrefHeight(58);

        btn.setTextFill(Color.WHITE);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        String base = """
            -fx-background-color:
                linear-gradient(to right, #FF9F1C, #FF5E3A, #FF2E63);

            -fx-background-radius: 22;

            -fx-cursor: hand;

            -fx-effect: dropshadow(
                gaussian,
                rgba(255,90,50,0.30),
                16,
                0.25,
                0,
                5
            );
        """;

        String hover = """
            -fx-background-color:
                linear-gradient(to right, #FFB347, #FF7043, #FF3D6E);

            -fx-background-radius: 22;

            -fx-cursor: hand;

            -fx-effect: dropshadow(
                gaussian,
                rgba(255,90,50,0.45),
                22,
                0.30,
                0,
                7
            );

            -fx-scale-x: 1.03;
            -fx-scale-y: 1.03;
        """;

        btn.setStyle(base);

        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));

        return btn;
    }
}