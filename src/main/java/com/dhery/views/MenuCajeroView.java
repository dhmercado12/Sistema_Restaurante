package com.dhery.views;

import com.dhery.app.Router;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.TextAlignment;

public class MenuCajeroView {

    public static Scene getScene() {

        // ── ROOT ──────────────────────────────────────────────────────────────
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #FFFFFF;");

        // ── CONTENIDO PRINCIPAL ───────────────────────────────────────────────
        VBox content = new VBox(22);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(30, 60, 30, 60));

        // Barra superior con notificación
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        StackPane notifBtn = buildNotifButton();
        topBar.getChildren().add(notifBtn);

        // Título
        VBox titleBox = buildTitleBox();

        // Grid 4x2
        GridPane grid = buildMenuGrid();

        // Cerrar sesión
        Button btnLogout = buildLogoutButton();

        // Tagline con decoración
        VBox taglineBox = buildTaglineBox();

        content.getChildren().addAll(topBar, titleBox, grid, btnLogout, taglineBox);
        root.getChildren().add(content);
        return new Scene(root, 1280, 720);
    }

    private static StackPane buildNotifButton() {
        StackPane stack = new StackPane();
        Circle circle = new Circle(26);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.web("#DDDDDD"));
        circle.setStrokeWidth(1.5);

        Label bell = new Label("🔔");
        bell.setStyle("-fx-font-size: 17px;");

        StackPane badge = new StackPane();
        badge.setTranslateX(12);
        badge.setTranslateY(-12);
        Circle badgeCircle = new Circle(7);
        badgeCircle.setFill(Color.web("#CC0000"));
        Label badgeNum = new Label("1");
        badgeNum.setStyle("-fx-font-size: 9px; -fx-text-fill: white; -fx-font-weight: bold;");
        badge.getChildren().addAll(badgeCircle, badgeNum);

        stack.getChildren().addAll(circle, bell, badge);
        stack.setMaxSize(52, 52);
        return stack;
    }

    private static VBox buildTitleBox() {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);

        // Ícono de cubiertos usando texto unicode limpio
        Label chefIcon = new Label("🍴 🥄");
        chefIcon.setStyle("-fx-font-size: 28px; -fx-text-fill: #CC0000;");

        Label title = new Label("CAJERO MENU");
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;");

        // Línea con texto centrado
        HBox lineBox = new HBox(10);
        lineBox.setAlignment(Pos.CENTER);

        Region leftLine = new Region();
        leftLine.setPrefSize(50, 2);
        leftLine.setStyle("-fx-background-color: #CC0000;");

        Label subtitle = new Label("TACABRÓN RESTAURANTE");
        subtitle.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-text-fill: #CC0000; -fx-letter-spacing: 2px;"
        );

        Region rightLine = new Region();
        rightLine.setPrefSize(50, 2);
        rightLine.setStyle("-fx-background-color: #CC0000;");

        lineBox.getChildren().addAll(leftLine, subtitle, rightLine);
        box.getChildren().addAll(chefIcon, title, lineBox);
        return box;
    }

    private static GridPane buildMenuGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        String[][] items = {
            {"🍽️", "VER MENÚ",             "Consultar productos\ny precios del menú"},
            {"🛒", "TOMAR EL PEDIDO",       "Registrar pedidos\nde los clientes"},
            {"📊", "HISTORIAL DE VENTAS",   "Ver ventas realizadas\ny reportes"},
            {"📄", "FACTURAS GUARDADAS",    "Consultar facturas\nguardadas"},
            {"👥", "CLIENTES REGISTRADOS",  "Ver lista de clientes\ny registrar nuevos"},
            {"📦", "STOCK",                 "Ver y administrar\ninventario de productos"},
            {"⚙️", "PANEL DE CONTROL",      "Configuración y ajustes\ndel sistema"},
            {"🍳", "VER ESTADO DE COCINA",  "Monitorear pedidos\nen preparación"},
        };

        for (int i = 0; i < items.length; i++) {
            VBox card = buildMenuCard(items[i][0], items[i][1], items[i][2]);
            grid.add(card, i % 4, i / 4);
        }
        return grid;
    }

    private static VBox buildMenuCard(String icon, String title, String desc) {
         VBox card = new VBox(6);
    card.setAlignment(Pos.CENTER);
    card.setPrefSize(260, 175);          // ← más alto para texto completo
    card.setMinSize(260, 175);
    card.setPadding(new Insets(20, 16, 20, 16));

    String normalStyle =
        "-fx-background-color: #FFFFFF;" +
        "-fx-border-color: #EEEEEE;" +
        "-fx-border-width: 1.5;" +
        "-fx-border-radius: 14;" +
        "-fx-background-radius: 14;" +
        "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.08),10,0,0,3);" +
        "-fx-cursor: hand;";
    String hoverStyle =
        "-fx-background-color: #FFF5F5;" +
        "-fx-border-color: #CC0000;" +
        "-fx-border-width: 2;" +
        "-fx-border-radius: 14;" +
        "-fx-background-radius: 14;" +
        "-fx-effect: dropshadow(gaussian,rgba(204,0,0,0.18),14,0,0,4);" +
        "-fx-cursor: hand;";

    card.setStyle(normalStyle);
    card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
    card.setOnMouseExited(e -> card.setStyle(normalStyle));

    if (title.equals("STOCK")) {
        card.setOnMouseClicked(e -> Router.goStockView());
    } else if (title.equals("TOMAR EL PEDIDO")) {
        card.setOnMouseClicked(e -> Router.goTakeOrderView());
    }

    // ── Ícono con fondo circular ──
    StackPane iconContainer = new StackPane();
    Circle iconBg = new Circle(28);
    iconBg.setFill(Color.web("#FFEAEA"));
    Label iconLbl = new Label(icon);
    iconLbl.setStyle("-fx-font-size: 20px;");
    iconContainer.getChildren().addAll(iconBg, iconLbl);

    // ── Título centrado ──
    Label titleLbl = new Label(title);
    titleLbl.setStyle(
        "-fx-font-size: 12px; -fx-font-weight: bold;" +
        "-fx-text-fill: #1A1A1A;"
    );
    titleLbl.setTextAlignment(TextAlignment.CENTER);   // ← centrado
    titleLbl.setAlignment(Pos.CENTER);                 // ← centrado en Label
    titleLbl.setWrapText(true);
    titleLbl.setMaxWidth(230);

    // ── Línea roja delgada ──
    Region titleLine = new Region();
    titleLine.setPrefSize(36, 2);
    titleLine.setMaxSize(36, 2);
    titleLine.setStyle("-fx-background-color: #CC0000; -fx-background-radius: 2;");

    // ── Descripción centrada y completa ──
    Label descLbl = new Label(desc);
    descLbl.setStyle(
        "-fx-font-size: 11px; -fx-text-fill: #888888;"
    );
    descLbl.setTextAlignment(TextAlignment.CENTER);    // ← centrado
    descLbl.setAlignment(Pos.CENTER);                  // ← centrado en Label
    descLbl.setWrapText(true);
    descLbl.setMaxWidth(230);
    descLbl.setMinHeight(Region.USE_PREF_SIZE);        // ← evita truncado

    card.getChildren().addAll(iconContainer, titleLbl, titleLine, descLbl);
    return card;
}

    private static Button buildLogoutButton() {
        Button btn = new Button("➜  CERRAR SESIÓN");
        btn.setPrefSize(280, 48);
        String normal =
            "-fx-background-color: #CC0000;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 28;" +
            "-fx-cursor: hand;";
        String hover =
            "-fx-background-color: #AA0000;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 28;" +
            "-fx-cursor: hand;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
        btn.setOnAction(e -> Router.goLogin());
        return btn;
    }

    // Tagline con líneas decorativas y ícono central (como imagen 1)
    private static VBox buildTaglineBox() {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);

        HBox lineRow = new HBox(12);
        lineRow.setAlignment(Pos.CENTER);

        Region leftLine = new Region();
        leftLine.setPrefSize(80, 1);
        leftLine.setStyle("-fx-background-color: #DDDDDD;");

        Label chefMini = new Label("🍴");
        chefMini.setStyle("-fx-font-size: 14px; -fx-text-fill: #CC0000;");

        Region rightLine = new Region();
        rightLine.setPrefSize(80, 1);
        rightLine.setStyle("-fx-background-color: #DDDDDD;");

        lineRow.getChildren().addAll(leftLine, chefMini, rightLine);

        Label tagline = new Label("Sabor que enamora");
        tagline.setStyle(
            "-fx-font-size: 15px; -fx-font-style: italic; -fx-text-fill: #777777;"
        );

        box.getChildren().addAll(lineRow, tagline);
        return box;
    }
}