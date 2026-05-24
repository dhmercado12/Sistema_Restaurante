package com.dhery.views;

import com.dhery.app.Router;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class CashierLoginView {

    public static Scene getScene() {

        // =====================================================
        // TITULO PREMIUM
        // =====================================================
        Text text1 = new Text("Panel Administrativo ");

        text1.setFill(Color.web("#1E1E1E"));

        text1.setFont(
                Font.font("Arial", FontWeight.EXTRA_BOLD, 40)
        );

        Text text2 = new Text("Tacabrón");

        text2.setFill(Color.web("#E76F51"));

        text2.setFont(
                Font.font("Arial", FontWeight.EXTRA_BOLD, 40)
        );

        TextFlow title = new TextFlow(text1, text2);

        title.setTextAlignment(TextAlignment.CENTER);

        // =====================================================
        // DESCRIPCIÓN
        // =====================================================
        Label description = new Label("""
                Accede al sistema administrativo
                para gestionar pedidos, ventas,
                inventario, clientes y reportes
                de la taquería Tacabrón.
                """);

        description.setTextFill(Color.web("#8A817C"));

        description.setFont(Font.font("Arial", 17));

        description.setWrapText(true);

        description.setMaxWidth(500);

        description.setAlignment(Pos.CENTER);

        description.setTextAlignment(TextAlignment.CENTER);

        // =====================================================
        // INPUT USUARIO
        // =====================================================
        Label userLabel = new Label("Usuario Administrador");

        userLabel.setTextFill(Color.web("#4A403A"));

        userLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 15)
        );

        TextField usernameField = new TextField();

        usernameField.setPromptText("Ingrese su usuario");

        usernameField.setPrefWidth(360);

        usernameField.setPrefHeight(52);

        usernameField.setStyle("""
            -fx-background-color: #FFFDF9;

            -fx-text-fill: #333333;

            -fx-prompt-text-fill: #A0A0A0;

            -fx-font-size: 15px;

            -fx-background-radius: 14;

            -fx-border-radius: 14;

            -fx-border-color: #E5D3B3;

            -fx-border-width: 1.5;

            -fx-padding: 0 15;
        """);

        // =====================================================
        // INPUT PASSWORD
        // =====================================================
        Label passLabel = new Label("Contraseña");

        passLabel.setTextFill(Color.web("#4A403A"));

        passLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 15)
        );

        PasswordField passwordField = new PasswordField();

        passwordField.setPromptText("Ingrese su contraseña");

        passwordField.setPrefWidth(360);

        passwordField.setPrefHeight(52);

        passwordField.setStyle("""
            -fx-background-color: #FFFDF9;

            -fx-text-fill: #333333;

            -fx-prompt-text-fill: #A0A0A0;

            -fx-font-size: 15px;

            -fx-background-radius: 14;

            -fx-border-radius: 14;

            -fx-border-color: #E5D3B3;

            -fx-border-width: 1.5;

            -fx-padding: 0 15;
        """);

        // =====================================================
        // BOTÓN LOGIN
        // =====================================================
        Button loginBtn = new Button("INGRESAR AL PANEL");

        loginBtn.setPrefWidth(360);

        loginBtn.setPrefHeight(58);

        loginBtn.setTextFill(Color.WHITE);

        loginBtn.setFont(
                Font.font("Arial", FontWeight.BOLD, 18)
        );

        loginBtn.setStyle("""
            -fx-background-color:
                linear-gradient(
                    to right,
                    #FF9F1C,
                    #FF5E3A,
                    #FF2E63
                );

            -fx-text-fill: white;

            -fx-font-size: 18px;

            -fx-font-weight: bold;

            -fx-background-radius: 18;

            -fx-cursor: hand;

            -fx-effect: dropshadow(
                gaussian,
                rgba(255,120,60,0.35),
                15,
                0.2,
                0,
                5
            );
        """);

        // HOVER
        loginBtn.setOnMouseEntered(e -> {

            loginBtn.setStyle("""
                -fx-background-color:
                    linear-gradient(
                        to right,
                        #FFB347,
                        #FF7043,
                        #FF4F81
                    );

                -fx-text-fill: white;

                -fx-font-size: 18px;

                -fx-font-weight: bold;

                -fx-background-radius: 18;

                -fx-cursor: hand;
            """);
        });

        loginBtn.setOnMouseExited(e -> {

            loginBtn.setStyle("""
                -fx-background-color:
                    linear-gradient(
                        to right,
                        #FF9F1C,
                        #FF5E3A,
                        #FF2E63
                    );

                -fx-text-fill: white;

                -fx-font-size: 18px;

                -fx-font-weight: bold;

                -fx-background-radius: 18;

                -fx-cursor: hand;

                -fx-effect: dropshadow(
                    gaussian,
                    rgba(255,120,60,0.35),
                    15,
                    0.2,
                    0,
                    5
                );
            """);
        });

        // ACCIÓN DEL BOTÓN LOGIN PARA CAJERO
        loginBtn.setOnAction(e -> Router.goMenuCajeroView());

        // =====================================================
        // TEXTO ADMIN
        // =====================================================
        Label adminLabel = new Label("""
                Acceso exclusivo para personal autorizado.
                """);

        adminLabel.setTextFill(Color.web("#9A8F87"));

        adminLabel.setFont(
                Font.font("Arial", FontWeight.SEMI_BOLD, 14)
        );

        // =====================================================
        // BOTÓN VOLVER
        // =====================================================
        Button backBtn = new Button("VOLVER");

        backBtn.setPrefWidth(220);

        backBtn.setPrefHeight(52);

        backBtn.setTextFill(Color.web("#E76F51"));

        backBtn.setFont(
                Font.font("Arial", FontWeight.BOLD, 18)
        );

        backBtn.setStyle("""
            -fx-background-color: transparent;

            -fx-border-color: #E76F51;

            -fx-border-width: 2;

            -fx-background-radius: 18;

            -fx-border-radius: 18;

            -fx-cursor: hand;
        """);

        backBtn.setOnMouseEntered(e -> {

            backBtn.setStyle("""
                -fx-background-color: #FFF3ED;

                -fx-text-fill: #E76F51;

                -fx-font-size: 18px;

                -fx-font-weight: bold;

                -fx-border-color: #E76F51;

                -fx-border-width: 2;

                -fx-background-radius: 18;

                -fx-border-radius: 18;

                -fx-cursor: hand;
            """);
        });

        backBtn.setOnMouseExited(e -> {

            backBtn.setStyle("""
                -fx-background-color: transparent;

                -fx-text-fill: #E76F51;

                -fx-font-size: 18px;

                -fx-font-weight: bold;

                -fx-border-color: #E76F51;

                -fx-border-width: 2;

                -fx-background-radius: 18;

                -fx-border-radius: 18;

                -fx-cursor: hand;
            """);
        });

        backBtn.setOnAction(e -> {
            Router.goLogin();
        });

        // =====================================================
        // FORM
        // =====================================================
        VBox form = new VBox(
                15,
                userLabel,
                usernameField,
                passLabel,
                passwordField,
                loginBtn,
                adminLabel,
                backBtn
        );

        form.setAlignment(Pos.CENTER);

        // =====================================================
        // CARD PRINCIPAL
        // =====================================================
        VBox card = new VBox(
                35,
                title,
                description,
                form
        );

        card.setAlignment(Pos.CENTER);

        card.setPadding(new Insets(70));

        card.setMaxWidth(650);

        card.setStyle("""
            -fx-background-color: white;

            -fx-background-radius: 40;

            -fx-border-color: #F3D19C;

            -fx-border-radius: 40;

            -fx-border-width: 2;

            -fx-effect: dropshadow(
                gaussian,
                rgba(0,0,0,0.10),
                25,
                0.2,
                0,
                8
            );
        """);

        // =====================================================
        // ROOT
        // =====================================================
        StackPane root = new StackPane(card);

        root.setPadding(new Insets(40));

        root.setStyle("""
            -fx-background-color:
                linear-gradient(
                    to bottom right,
                    #F8F5F0,
                    #F4EEE6,
                    #EFE7DC
                );
        """);

        return new Scene(root, 1280, 720);
    }
}