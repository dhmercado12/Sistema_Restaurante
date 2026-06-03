package com.dhery.views;

import com.dhery.app.Router;
import com.dhery.models.user;

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

    private static user currentUser;

    public static Scene getScene(user user) {

        currentUser = user;

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

        // Badge con contador reactivo
        StackPane badge = new StackPane();
        badge.setTranslateX(12);
        badge.setTranslateY(-12);
        Circle badgeCircle = new Circle(8);
        badgeCircle.setFill(Color.web("#CC0000"));
        Label badgeNum = new Label("0");
        badgeNum.setStyle("-fx-font-size: 9px; -fx-text-fill: white; -fx-font-weight: bold;");
        badge.getChildren().addAll(badgeCircle, badgeNum);

        // Actualizar badge con notificaciones no leídas
        com.dhery.app.AppState.notificaciones.addListener(
            (javafx.collections.ListChangeListener<com.dhery.app.AppState.Notificacion>) c -> {
                long noLeidas = com.dhery.app.AppState.notificaciones.stream()
                    .filter(n -> !n.leida).count();
                badgeNum.setText(String.valueOf(noLeidas));
                badge.setVisible(noLeidas > 0);
            });
        long noLeidas = com.dhery.app.AppState.notificaciones.stream()
            .filter(n -> !n.leida).count();
        badgeNum.setText(String.valueOf(noLeidas));
        badge.setVisible(noLeidas > 0);

        stack.getChildren().addAll(circle, bell, badge);
        stack.setMaxSize(52, 52);
        stack.setStyle("-fx-cursor: hand;");

        // Popup de notificaciones
        stack.setOnMouseClicked(e -> mostrarPopupNotificaciones(stack));
        return stack;
    }

    private static void mostrarPopupNotificaciones(javafx.scene.Node anchor) {
        javafx.stage.Popup popup = new javafx.stage.Popup();
        popup.setAutoHide(true);

        javafx.scene.layout.VBox container = new javafx.scene.layout.VBox(0);
        container.setPrefWidth(340);
        container.setMaxHeight(400);
        container.setStyle("-fx-background-color: white; -fx-border-color: #DDDDDD;" +
            "-fx-border-radius: 12; -fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.18),16,0,0,4);");

        // Encabezado del popup
        javafx.scene.layout.HBox header = new javafx.scene.layout.HBox(10);
        header.setPadding(new javafx.geometry.Insets(14, 16, 12, 16));
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #CC0000; -fx-background-radius: 12 12 0 0;");
        Label hTitle = new Label("🔔  NOTIFICACIONES");
        hTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        javafx.scene.layout.Region sp = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(sp, javafx.scene.layout.Priority.ALWAYS);
        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white;" +
            "-fx-font-size: 11px; -fx-cursor: hand; -fx-background-radius: 6;");
        btnLimpiar.setOnAction(ev -> {
            com.dhery.app.AppState.notificaciones.forEach(n -> n.leida = true);
            popup.hide();
        });
        header.getChildren().addAll(hTitle, sp, btnLimpiar);

        // Lista de notificaciones
        javafx.scene.layout.VBox lista = new javafx.scene.layout.VBox(0);
        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(lista);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: white;");
        scroll.setPrefHeight(300);

        if (com.dhery.app.AppState.notificaciones.isEmpty()) {
            Label empty = new Label("No hay notificaciones");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 13px; -fx-padding: 20;");
            lista.getChildren().add(empty);
        } else {
            int i = 0;
            for (com.dhery.app.AppState.Notificacion n : com.dhery.app.AppState.notificaciones) {
                javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(10);
                row.setPadding(new javafx.geometry.Insets(10, 14, 10, 14));
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                row.setStyle("-fx-background-color: " + (n.leida ? "white" : "#FFF5F5") + ";" +
                    "-fx-border-color: transparent transparent #F0F0F0 transparent; -fx-border-width: 1;");

                String ico = n.tipo.equals("STOCK_BAJO") ? "⚠️" :
                             n.tipo.equals("PEDIDO_LISTO") ? "✅" : "📦";
                Label icoLbl = new Label(ico);
                icoLbl.setStyle("-fx-font-size: 16px;");

                javafx.scene.layout.VBox textBox = new javafx.scene.layout.VBox(2);
                Label msgLbl = new Label(n.mensaje);
                msgLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #1A1A1A;");
                msgLbl.setWrapText(true);
                Label horaLbl = new Label(n.hora);
                horaLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");
                textBox.getChildren().addAll(msgLbl, horaLbl);

                n.leida = true;
                row.getChildren().addAll(icoLbl, textBox);
                lista.getChildren().add(row);
                if (i++ >= 20) break;
            }
        }

        container.getChildren().addAll(header, scroll);

        popup.getContent().add(container);
        javafx.geometry.Bounds bounds = anchor.localToScreen(anchor.getBoundsInLocal());
        popup.show(anchor.getScene().getWindow(),
            bounds.getMaxX() - 340,
            bounds.getMaxY() + 8);
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
            {"🛵", "CONTROL DE DELIVERY",    "Gestionar entregas\ny repartidores"},
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
        card.setOnMouseClicked(e -> Router.goTakeOrderView(currentUser));
    } else if (title.equals("HISTORIAL DE VENTAS")) {
    card.setOnMouseClicked(e -> Router.goHistorialVentasView());
    } else if (title.equals("CLIENTES REGISTRADOS")) {
    card.setOnMouseClicked(e -> Router.goClientesRegistradosView(currentUser));
    } else if (title.equals("FACTURAS GUARDADAS")) {
    card.setOnMouseClicked(e -> Router.goFacturasGuardadasView());
    } else if (title.equals("VER MENÚ")){
    card.setOnMouseClicked(e -> Router.goMostrarMenuCajera());
    } else if (title.equals("VER ESTADO DE COCINA")){
    card.setOnMouseClicked(e -> Router.goEstadoCocinaView());
    } else if (title.equals("CONTROL DE DELIVERY")){
    card.setOnMouseClicked(e -> Router.goControlDeliveryView());
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
        btn.setOnAction(e -> Router.goClientLogin());
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