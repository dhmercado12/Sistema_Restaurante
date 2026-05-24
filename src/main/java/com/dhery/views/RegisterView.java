package com.dhery.views;

import com.dhery.app.Router;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class RegisterView {

    public static Scene getScene() {

        // =====================================================
        // TITULO PREMIUM
        // =====================================================
        Text text1 = new Text("Crear cuenta en ");

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
                Regístrate para realizar pedidos,
                disfrutar promociones y vivir
                la experiencia Tacabrón 🌮
                """);

        description.setTextFill(Color.web("#8A817C"));

        description.setFont(Font.font("Arial", 17));

        description.setWrapText(true);

        description.setMaxWidth(500);

        description.setAlignment(Pos.CENTER);

        description.setTextAlignment(TextAlignment.CENTER);

        // =====================================================
// NOMBRES
// =====================================================
Label namesLabel = new Label("Nombres");

namesLabel.setTextFill(Color.web("#4A403A"));

namesLabel.setFont(
        Font.font("Arial", FontWeight.BOLD, 15)
);

TextField namesField = new TextField();

namesField.setPromptText("Ingrese sus nombres");

styleField(namesField);

// =====================================================
// APELLIDOS
// =====================================================
Label lastNameLabel = new Label("Apellidos");

lastNameLabel.setTextFill(Color.web("#4A403A"));

lastNameLabel.setFont(
        Font.font("Arial", FontWeight.BOLD, 15)
);

TextField lastNameField = new TextField();

lastNameField.setPromptText("Ingrese sus apellidos");

styleField(lastNameField);

// =====================================================
// TELÉFONO
// =====================================================
Label phoneLabel = new Label("Teléfono");

phoneLabel.setTextFill(Color.web("#4A403A"));

phoneLabel.setFont(
        Font.font("Arial", FontWeight.BOLD, 15)
);

TextField phoneField = new TextField();

phoneField.setPromptText("Ingrese su teléfono");

styleField(phoneField);

// =====================================================
// DIRECCIÓN
// =====================================================
Label addressLabel = new Label("Dirección");

addressLabel.setTextFill(Color.web("#4A403A"));

addressLabel.setFont(
        Font.font("Arial", FontWeight.BOLD, 15)
);

TextField addressField = new TextField();

addressField.setPromptText("Ingrese su dirección");

styleField(addressField);

// =====================================================
// CONTRASEÑA
// =====================================================
Label passLabel = new Label("Contraseña");

passLabel.setTextFill(Color.web("#4A403A"));

passLabel.setFont(
        Font.font("Arial", FontWeight.BOLD, 15)
);

PasswordField passwordField = new PasswordField();

passwordField.setPromptText("Ingrese una contraseña");

styleField(passwordField);

// =====================================================
// CONFIRMAR CONTRASEÑA
// =====================================================
Label confirmLabel = new Label("Confirmar contraseña");

confirmLabel.setTextFill(Color.web("#4A403A"));

confirmLabel.setFont(
        Font.font("Arial", FontWeight.BOLD, 15)
);

PasswordField confirmField = new PasswordField();

confirmField.setPromptText("Repita la contraseña");

styleField(confirmField);

        // =====================================================
        // BOTÓN REGISTRO
        // =====================================================
        Button registerBtn = new Button("CREAR CUENTA");

        registerBtn.setPrefWidth(360);

        registerBtn.setPrefHeight(58);

        registerBtn.setTextFill(Color.WHITE);

        registerBtn.setFont(
                Font.font("Arial", FontWeight.BOLD, 18)
        );

        registerBtn.setStyle("""
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

        registerBtn.setOnMouseEntered(e -> {

            registerBtn.setStyle("""
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

        registerBtn.setOnMouseExited(e -> {

            registerBtn.setStyle("""
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

        // =====================================================
        // TEXTO LOGIN
        // =====================================================
        Label loginText = new Label("¿Ya tienes una cuenta?");

        loginText.setTextFill(Color.web("#8A817C"));

        loginText.setFont(Font.font("Arial", 15));

        Hyperlink loginLink = new Hyperlink("Iniciar sesión");

        loginLink.setTextFill(Color.web("#4A90E2"));

        loginLink.setFont(
                Font.font("Arial", FontWeight.BOLD, 15)
        );

        loginLink.setBorder(Border.EMPTY);

        loginLink.setPadding(Insets.EMPTY);

        loginLink.setOnMouseEntered(e -> {
            loginLink.setTextFill(Color.web("#6FB6FF"));
        });

        loginLink.setOnMouseExited(e -> {
            loginLink.setTextFill(Color.web("#4A90E2"));
        });

        loginLink.setOnAction(e -> {
            Router.goClientLogin();
        });

        HBox loginBox = new HBox(
                5,
                loginText,
                loginLink
        );

        loginBox.setAlignment(Pos.CENTER);

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
            Router.goClientLogin();
        });

        // =====================================================
        // FORM
        // =====================================================
        VBox form = new VBox(
        12,

        namesLabel,
        namesField,

        lastNameLabel,
        lastNameField,

        phoneLabel,
        phoneField,

        addressLabel,
        addressField,

        passLabel,
        passwordField,

        confirmLabel,
        confirmField,

        registerBtn,
        loginBox,
        backBtn
);

        form.setAlignment(Pos.CENTER);

        // =====================================================
        // CARD PRINCIPAL
        // =====================================================
        VBox card = new VBox(
                30,
                title,
                description,
                form
        );

        card.setAlignment(Pos.CENTER);

        card.setPadding(new Insets(50));

        card.setMaxWidth(750);

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

    // =====================================================
    // ESTILO INPUTS
    // =====================================================
    private static void styleField(TextField field) {

        field.setPrefWidth(360);

        field.setPrefHeight(52);

        field.setStyle("""
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
    }
}