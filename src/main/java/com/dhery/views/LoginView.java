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
        // TITULO SUPERIOR
        // =====================================================
        Label subtitle = new Label("Taquería");
        subtitle.setTextFill(Color.WHITE);

        subtitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 28)
        );

        Label title = new Label("TACABRÓN");

        title.setTextFill(Color.web("#00C2FF"));

        title.setFont(
                Font.font("Arial", FontWeight.EXTRA_BOLD, 70)
        );

        Label slogan = new Label(
                "Sistema Inteligente de Gestión"
        );

        slogan.setTextFill(Color.web("#FFE082"));

        slogan.setFont(
                Font.font("Arial", FontWeight.SEMI_BOLD, 18)
        );

        VBox logoSection = new VBox(
                -8,
                subtitle,
                title,
                slogan
        );

        logoSection.setAlignment(Pos.CENTER);

        // =====================================================
        // DESCRIPCIÓN
        // =====================================================
      
        Label description = new Label("""
            
    Bienvenido a Tacabrón 🌮

Disfruta una experiencia rápida, moderna y deliciosa.
¡Prepárate para antojarte!



""");

        description.setTextFill(Color.web("#F5F5F5"));

        description.setFont(Font.font("Arial", 17));

        description.setWrapText(true);

        description.setMaxWidth(500);

        description.setAlignment(Pos.CENTER);
        description.setTextAlignment(TextAlignment.CENTER);
        // =====================================================
        // BOTONES
        // =====================================================
        Button cashierBtn = createButton(
                "CAJERO",
                "#FFCA28"
        );

        cashierBtn.setOnAction(e -> {
            Router.goCashierLoginView();
        });

        Button clientBtn = createButton(
                "CLIENTE",
                "#00C2FF"
                
        );

        clientBtn.setOnAction(e -> {
          Router.goClientLogin();
           });

        Button exitBtn = createButton(
                "SALIR",
                "#FF5252"
        );

        // SALIR
        exitBtn.setOnAction(e -> {
            Router.goStart();
        });

        VBox buttons = new VBox(
                25,
                cashierBtn,
                clientBtn,
                exitBtn
        );

        buttons.setAlignment(Pos.CENTER);
    
        // =====================================================
        // CARD PRINCIPAL
        // =====================================================
        VBox card = new VBox(
                40,
                logoSection,
                description,
                buttons
        );

        card.setAlignment(Pos.CENTER);

        card.setPadding(new Insets(60));

        card.setMaxWidth(700);

        card.setStyle("""
            -fx-background-color: rgba(255,255,255,0.08);

            -fx-background-radius: 35;

            -fx-border-color: rgba(255,255,255,0.15);

            -fx-border-radius: 35;

            -fx-border-width: 2;
        """);

        // =====================================================
        // ROOT
        // =====================================================
        StackPane root = new StackPane(card);

        root.setPadding(new Insets(40));

        root.setStyle("""
            -fx-background-color:
                radial-gradient(
                    center 50% 30%,
                    radius 100%,
                    #FF2A2A 0%,
                    #D60000 45%,
                    #7A0000 100%
                );
        """);

        return new Scene(root, 1280, 720);
    }

    // =====================================================
    // BOTÓN MODERNO
    // =====================================================
    private static Button createButton(
            String text,
            String hoverColor
    ) {

        Button btn = new Button(text);

        btn.setPrefWidth(320);
        btn.setPrefHeight(70);

        btn.setTextFill(Color.WHITE);

        btn.setFont(
                Font.font("Arial", FontWeight.BOLD, 22)
        );

        btn.setStyle("""
            -fx-background-color: rgba(255,255,255,0.12);

            -fx-background-radius: 18;

            -fx-border-color: rgba(255,255,255,0.18);

            -fx-border-radius: 18;

            -fx-border-width: 2;

            -fx-cursor: hand;
        """);

        // HOVER
        btn.setOnMouseEntered(e -> {

            btn.setStyle(
    "-fx-background-color: " + hoverColor + ";" +
    "-fx-text-fill: black;" +
    "-fx-font-size: 22px;" +
    "-fx-font-weight: bold;" +
    "-fx-background-radius: 18;" +
    "-fx-border-radius: 18;" +
    "-fx-cursor: hand;"
);
        });

        btn.setOnMouseExited(e -> {

            btn.setStyle("""
                -fx-background-color: rgba(255,255,255,0.12);

                -fx-text-fill: white;

                -fx-font-size: 22px;

                -fx-font-weight: bold;

                -fx-background-radius: 18;

                -fx-border-color: rgba(255,255,255,0.18);

                -fx-border-radius: 18;

                -fx-border-width: 2;

                -fx-cursor: hand;
            """);
        });

        return btn;
    }
}