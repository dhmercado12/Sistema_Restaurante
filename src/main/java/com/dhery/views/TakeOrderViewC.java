package com.dhery.views;

import java.util.List;

import com.dhery.GestorArchivo.ArchivoManager;
import com.dhery.app.Router;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class TakeOrderViewC {
    private static com.dhery.models.user currentUser;

    // ── Modelo ────────────────────────────────────────────────────────────────
    public static class OrderItem {
        private final StringProperty  producto = new SimpleStringProperty();
        private final IntegerProperty cantidad = new SimpleIntegerProperty();
        private final IntegerProperty precio   = new SimpleIntegerProperty();

        public OrderItem(String producto, int cantidad, int precio) {
            this.producto.set(producto);
            this.cantidad.set(cantidad);
            this.precio.set(precio);
        }
        public StringProperty  productoProperty() { return producto; }
        public IntegerProperty cantidadProperty() { return cantidad; }
        public IntegerProperty precioProperty()   { return precio;   }
        public String getProducto() { return producto.get(); }
        public int    getCantidad() { return cantidad.get(); }
        public int    getPrecio()   { return precio.get();   }
        public void   setCantidad(int v) { cantidad.set(v); }
    }

    private static class Producto {
        String name; int price; String emoji;
        Producto(String n, int p, String e) { name = n; price = p; emoji = e; }
    }

    private static final ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    private static Label subtotalLbl  = new Label("0 Bs");
    private static Label deliveryLbl  = new Label("10 Bs");
    private static Label totalLbl     = new Label("10 Bs");
    private static boolean isDelivery = true;

    private static Label entValLbl    = new Label("Delivery");
    private static Label dirValLbl    = new Label("—");
    private static Label nombreValLbl = new Label("—");
    private static Label celValLbl    = new Label("—");

    // Campos globales para validación
    private static TextField tfDirGlobal;
    private static TextField tfNombreGlobal;
    private static TextField tfCelGlobal;

    // ── Paleta de colores (diseño claro moderno, rojo institucional) ───────────
    private static final String PAGE_BG   = "#F7F7F7";
    private static final String WHITE     = "#FFFFFF";
    private static final String RED       = "#D32F2F";
    private static final String RED_LIGHT = "#FFEBEE";
    private static final String GREEN     = "#2E7D32";
    private static final String GREEN_LT  = "#4CAF50";
    private static final String GRAY_BG   = "#F0F0F0";
    private static final String GRAY_TXT  = "#888888";
    private static final String DARK_TXT  = "#1A1A1A";
    private static final String BORDER    = "#E0E0E0";
    private static final String TAB_INACT = "#F5F5F5";

    // ─────────────────────────────────────────────────────────────────────────
    public static Scene getScene(com.dhery.models.user user) {
        currentUser = user;
        orderItems.clear();

        subtotalLbl  = new Label("0 Bs");
        deliveryLbl  = new Label("10 Bs");
        totalLbl     = new Label("10 Bs");
        entValLbl    = new Label("Delivery");

        // Para clientes logueados, pre-cargar sus datos como sugerencia
        nombreValLbl = new Label(user.getUsername() + " " + user.getApellidos());
        dirValLbl    = new Label(user.getDireccion());
        celValLbl    = new Label(user.getTelefono());
        isDelivery   = true;

        tfDirGlobal    = null;
        tfNombreGlobal = null;
        tfCelGlobal    = null;

        HBox root = new HBox(0);
        root.setStyle("-fx-background-color: " + PAGE_BG + ";");

        VBox leftPanel  = buildLeftPanel();
        VBox rightPanel = buildRightPanel();

        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        rightPanel.setPrefWidth(390);
        rightPanel.setMinWidth(390);

        root.getChildren().addAll(leftPanel, rightPanel);
        return new Scene(root, 1280, 720);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PANEL IZQUIERDO
    // ═══════════════════════════════════════════════════════════════════════════
    private static VBox buildLeftPanel() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: " + WHITE + ";");

        // ── Header ────────────────────────────────────────────────────────────
        HBox header = new HBox(14);
        header.setPadding(new Insets(20, 24, 16, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        Label cartIcon = new Label("🛒");
        cartIcon.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-background-color: " + RED_LIGHT + ";" +
            "-fx-background-radius: 50;" +
            "-fx-min-width: 48; -fx-min-height: 48;" +
            "-fx-max-width: 48; -fx-max-height: 48;" +
            "-fx-alignment: center;");

        VBox titleBox = new VBox(2);
        Label titleLbl = new Label("TOMAR EL PEDIDO");
        titleLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TXT + ";");
        Label subtitleLbl2 = new Label("Registrar pedidos de los clientes");
        subtitleLbl2.setStyle("-fx-font-size: 12px; -fx-text-fill: " + GRAY_TXT + ";");
        Region underline = new Region();
        underline.setPrefHeight(3);
        underline.setPrefWidth(60);
        underline.setStyle("-fx-background-color: " + RED + "; -fx-background-radius: 2;");
        titleBox.getChildren().addAll(titleLbl, subtitleLbl2, underline);

        header.getChildren().addAll(cartIcon, titleBox);

        // ── Tabs ──────────────────────────────────────────────────────────────
        HBox tabs = buildTabs();

        // ── Grid de productos ─────────────────────────────────────────────────
        ScrollPane scrollPane = new ScrollPane(buildProductGrid());
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + PAGE_BG + ";");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox deliverySection = buildDeliverySection();
        HBox bottomBtns      = buildBottomButtons();

        panel.getChildren().addAll(header, tabs, scrollPane, deliverySection, bottomBtns);
        return panel;
    }

    private static HBox buildTabs() {
        HBox tabs = new HBox(0);
        tabs.setPadding(new Insets(14, 24, 0, 24));
        tabs.setStyle("-fx-background-color: " + WHITE + ";");

        String[][] tabData = {
            {"🌮",  "TACOS"},
            {"🍽️", "PLATOS EXTRA"},
            {"🥤",  "BEBIDAS"},
            {"🎁",  "PROMOCIONES"},
        };

        for (int i = 0; i < tabData.length; i++) {
            boolean active = (i == 0);
            Button tab = new Button(tabData[i][0] + "  " + tabData[i][1]);

            String activeStyle =
                "-fx-background-color: " + RED + "; -fx-text-fill: white;" +
                " -fx-font-size: 12px; -fx-font-weight: bold;" +
                " -fx-background-radius: 8 8 0 0;" +
                " -fx-padding: 10 18 10 18; -fx-cursor: hand;";
            String inactiveStyle =
                "-fx-background-color: " + TAB_INACT + "; -fx-text-fill: " + DARK_TXT + ";" +
                " -fx-font-size: 12px; -fx-font-weight: bold;" +
                " -fx-background-radius: 8 8 0 0;" +
                " -fx-border-color: " + BORDER + "; -fx-border-radius: 8 8 0 0; -fx-border-width: 1 1 0 1;" +
                " -fx-padding: 10 18 10 18; -fx-cursor: hand;";

            tab.setStyle(active ? activeStyle : inactiveStyle);
            tab.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(tab, Priority.ALWAYS);
            if (i < tabData.length - 1)
                HBox.setMargin(tab, new Insets(0, 4, 0, 0));
            tabs.getChildren().add(tab);
        }
        return tabs;
    }

    private static GridPane buildProductGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(16, 24, 16, 24));
        grid.setStyle("-fx-background-color: " + PAGE_BG + ";");

        Producto[] tacos = {
            new Producto("BIRRIA",       35, "🌮"),
            new Producto("QUESABIRRIA",  40, "🌮"),
            new Producto("SUADERO",      30, "🌮"),
            new Producto("PASTOR",       30, "🌮"),
        };
        Producto[] extras = {
            new Producto("RAMEN BIRRIA",    45, "🍲"),
            new Producto("NACHOS SUPREMOS", 40, "🍟"),
            new Producto("MEGABURRITO",     45, "🌯"),
            new Producto("TORTILLA EXTRA",  10, "🫓"),
        };
        Producto[] bebidas = {
            new Producto("HORCHATA", 10, "🥛"),
            new Producto("JAMAICA",   8, "🥤"),
        };

        for (int i = 0; i < tacos.length;   i++) grid.add(buildProductCard(tacos[i]),   0, i);
        for (int i = 0; i < extras.length;  i++) grid.add(buildProductCard(extras[i]),  1, i);
        for (int i = 0; i < bebidas.length; i++) grid.add(buildProductCard(bebidas[i]), 2, i);

        grid.add(buildPromoCard("NACHOS SUPREMOS COMBO 2", "Nachos Supremos + Jamaica", 45), 3, 0);
        grid.add(buildPromoCard("MEGABURRITO COMBO 2",     "MegaBurrito + Horchata",    55), 3, 1);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }
        return grid;
    }

    private static VBox buildProductCard(Producto p) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16, 12, 14, 12));
        card.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);"
        );

        Label emojiLbl = new Label(p.emoji);
        emojiLbl.setStyle("-fx-font-size: 30px;");

        Label nameLbl = new Label(p.name);
        nameLbl.setStyle(
            "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TXT + ";");
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setTextAlignment(TextAlignment.CENTER);
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(150);

        Label priceLbl = new Label(p.price + " Bs");
        priceLbl.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_LT + ";");

        HBox counter = buildCounter(p.name, p.price);
        card.getChildren().addAll(emojiLbl, nameLbl, priceLbl, counter);
        return card;
    }

    private static VBox buildPromoCard(String name, String desc, int price) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(14, 12, 14, 12));
        card.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + RED + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(211,47,47,0.1), 8, 0, 0, 2);"
        );

        Label badge = new Label("PROMO");
        badge.setStyle(
            "-fx-background-color: " + RED + "; -fx-text-fill: white;" +
            " -fx-font-size: 9px; -fx-font-weight: bold;" +
            " -fx-padding: 3 10 3 10; -fx-background-radius: 4;");

        Label nameLbl = new Label(name);
        nameLbl.setStyle(
            "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TXT + ";");
        nameLbl.setWrapText(true);
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setTextAlignment(TextAlignment.CENTER);
        nameLbl.setMaxWidth(170);

        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + GRAY_TXT + ";");
        descLbl.setAlignment(Pos.CENTER);
        descLbl.setTextAlignment(TextAlignment.CENTER);

        Label priceLbl = new Label(price + " Bs");
        priceLbl.setStyle(
            "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + GREEN_LT + ";");

        HBox counter = buildCounter(name, price);
        card.getChildren().addAll(badge, nameLbl, descLbl, priceLbl, counter);
        return card;
    }

    private static HBox buildCounter(String itemName, int itemPrice) {
        HBox hb = new HBox(6);
        hb.setAlignment(Pos.CENTER);

        String btnStyle =
            "-fx-background-color: " + GRAY_BG + ";" +
            "-fx-text-fill: " + DARK_TXT + ";" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0;";

        Button minus = new Button("−");
        minus.setStyle(btnStyle);
        minus.setPrefWidth(32);
        minus.setPrefHeight(32);
        minus.setMinWidth(32);
        minus.setMaxWidth(32);
        minus.setMinHeight(32);
        minus.setMaxHeight(32);

        Label countLbl = new Label("0");
        countLbl.setPrefWidth(36);
        countLbl.setPrefHeight(32);
        countLbl.setAlignment(Pos.CENTER);
        countLbl.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + DARK_TXT + ";" +
            "-fx-background-color: " + GRAY_BG + ";" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 0;");

        Button plus = new Button("+");
        plus.setStyle(btnStyle);
        plus.setPrefWidth(32);
        plus.setPrefHeight(32);
        plus.setMinWidth(32);
        plus.setMaxWidth(32);
        plus.setMinHeight(32);
        plus.setMaxHeight(32);

        plus.setOnAction(e -> {
            int c = Integer.parseInt(countLbl.getText()) + 1;
            countLbl.setText(String.valueOf(c));
            boolean found = false;
            for (OrderItem oi : orderItems) {
                if (oi.getProducto().equals(itemName)) {
                    oi.setCantidad(c);
                    int idx = orderItems.indexOf(oi);
                    orderItems.set(idx, oi);
                    found = true;
                    break;
                }
            }
            if (!found) orderItems.add(new OrderItem(itemName, c, itemPrice));
            refreshTotals();
        });

        minus.setOnAction(e -> {
            int c = Integer.parseInt(countLbl.getText());
            if (c > 0) {
                c--;
                countLbl.setText(String.valueOf(c));
                if (c == 0) {
                    orderItems.removeIf(oi -> oi.getProducto().equals(itemName));
                } else {
                    for (OrderItem oi : orderItems) {
                        if (oi.getProducto().equals(itemName)) {
                            oi.setCantidad(c);
                            int idx = orderItems.indexOf(oi);
                            orderItems.set(idx, oi);
                            break;
                        }
                    }
                }
                refreshTotals();
            }
        });

        hb.getChildren().addAll(minus, countLbl, plus);
        return hb;
    }

    // ── Sección tipo de entrega ────────────────────────────────────────────────
    private static VBox buildDeliverySection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(14, 24, 12, 24));
        section.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-width: 1 0 0 0;");

        // Título de sección
        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label truck = new Label("🚚");
        truck.setStyle("-fx-font-size: 16px;");
        Label titleLbl = new Label("TIPO DE ENTREGA");
        titleLbl.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TXT + ";");
        titleRow.getChildren().addAll(truck, titleLbl);

        HBox row = new HBox(32);
        row.setAlignment(Pos.CENTER_LEFT);

        // Radio buttons
        ToggleGroup tg = new ToggleGroup();
        RadioButton rbLocal    = new RadioButton("LOCAL");
        RadioButton rbDelivery = new RadioButton("DELIVERY");
        String rbStyle =
            "-fx-text-fill: " + DARK_TXT + "; -fx-font-size: 13px;" +
            " -fx-font-weight: bold; -fx-cursor: hand;";
        rbLocal.setStyle(rbStyle);
        rbDelivery.setStyle(rbStyle);
        rbLocal.setToggleGroup(tg);
        rbDelivery.setToggleGroup(tg);
        rbDelivery.setSelected(true);
        VBox radios = new VBox(10, rbLocal, rbDelivery);
        radios.setAlignment(Pos.CENTER_LEFT);

        // Campos de datos del cliente
        GridPane fields = new GridPane();
        fields.setHgap(12);
        fields.setVgap(8);

        tfDirGlobal    = buildDeliveryTextField("Escribe la dirección");
        tfNombreGlobal = buildDeliveryTextField("Nombre del cliente");
        tfCelGlobal    = buildDeliveryTextField("Número de contacto");

        // Pre-cargar datos del usuario logueado si los tiene
        if (currentUser != null) {
            String nombrePrev = (currentUser.getUsername() + " " + currentUser.getApellidos()).trim();
            String dirPrev    = currentUser.getDireccion() != null ? currentUser.getDireccion() : "";
            String celPrev    = currentUser.getTelefono()  != null ? currentUser.getTelefono()  : "";
            if (!nombrePrev.isBlank()) tfNombreGlobal.setText(nombrePrev);
            if (!dirPrev.isBlank())    tfDirGlobal.setText(dirPrev);
            if (!celPrev.isBlank())    tfCelGlobal.setText(celPrev);
        }

        // Celular: solo dígitos, máx 15
        tfCelGlobal.textProperty().addListener((obs, o, nv) -> {
            String filtrado = nv.replaceAll("[^\\d]", "");
            if (filtrado.length() > 15) filtrado = filtrado.substring(0, 15);
            if (!filtrado.equals(nv)) tfCelGlobal.setText(filtrado);
            celValLbl.setText(tfCelGlobal.getText().isEmpty() ? "—" : tfCelGlobal.getText());
        });

        // Nombre: solo letras y espacios, máx 50
        tfNombreGlobal.textProperty().addListener((obs, o, nv) -> {
            String filtrado = nv.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]", "");
            if (filtrado.length() > 50) filtrado = filtrado.substring(0, 50);
            if (!filtrado.equals(nv)) tfNombreGlobal.setText(filtrado);
            nombreValLbl.setText(tfNombreGlobal.getText().isEmpty() ? "—" : tfNombreGlobal.getText());
        });

        // Dirección: máx 100 caracteres
        tfDirGlobal.textProperty().addListener((obs, o, nv) -> {
            if (nv.length() > 100) tfDirGlobal.setText(nv.substring(0, 100));
            dirValLbl.setText(tfDirGlobal.getText().isEmpty() ? "—" : tfDirGlobal.getText());
        });

        addFieldRow(fields, "Dirección:", tfDirGlobal,    0);
        addFieldRow(fields, "Nombre:",    tfNombreGlobal, 1);
        addFieldRow(fields, "Celular:",   tfCelGlobal,    2);

        // Badge costo delivery
        VBox costBox = new VBox(4);
        costBox.setAlignment(Pos.CENTER);
        Label costTitle = new Label("Costo Delivery:");
        costTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GRAY_TXT + ";");
        Label costAmt = new Label("10 Bs");
        costAmt.setStyle(
            "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;" +
            "-fx-background-color: " + RED + ";" +
            "-fx-padding: 10 20 10 20; -fx-background-radius: 8;");
        costBox.getChildren().addAll(costTitle, costAmt);

        row.getChildren().addAll(radios, fields, costBox);

        // LOCAL → campos habilitados (también necesita nombre/celular para cocina),
        //         pero sin costo delivery visible
        rbLocal.setOnAction(ev -> {
            isDelivery = false;
            fields.setDisable(false);
            costBox.setVisible(false);
            entValLbl.setText("Local");
            refreshTotals();
        });
        rbDelivery.setOnAction(ev -> {
            isDelivery = true;
            fields.setDisable(false);
            costBox.setVisible(true);
            entValLbl.setText("Delivery");
            refreshTotals();
        });

        section.getChildren().addAll(titleRow, row);
        return section;
    }

    private static TextField buildDeliveryTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(240);
        tf.setStyle(
            "-fx-background-color: " + GRAY_BG + "; -fx-text-fill: " + DARK_TXT + ";" +
            " -fx-prompt-text-fill: " + GRAY_TXT + ";" +
            " -fx-background-radius: 8; -fx-padding: 7 12 7 12;" +
            " -fx-border-color: " + BORDER + "; -fx-border-radius: 8; -fx-border-width: 1;");
        return tf;
    }

    private static void addFieldRow(GridPane grid, String label, TextField tf, int row) {
        Label lbl = new Label(label);
        lbl.setStyle(
            "-fx-text-fill: " + DARK_TXT + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        grid.add(lbl, 0, row);
        grid.add(tf,  1, row);
    }

    private static HBox buildBottomButtons() {
        HBox hb = new HBox(12);
        hb.setPadding(new Insets(12, 24, 14, 24));
        hb.setAlignment(Pos.CENTER_LEFT);
        hb.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-width: 1 0 0 0;");

        Button btnBack = new Button("← ATRÁS");
        btnBack.setPrefHeight(42);
        btnBack.setPrefWidth(120);
        btnBack.setStyle(
            "-fx-background-color: " + WHITE + "; -fx-text-fill: " + RED + ";" +
            " -fx-font-weight: bold; -fx-font-size: 13px;" +
            " -fx-background-radius: 8; -fx-cursor: hand;" +
            " -fx-border-color: " + RED + "; -fx-border-radius: 8; -fx-border-width: 1.5;");
        btnBack.setOnAction(e -> Router.goMenuClienteView());

        hb.getChildren().addAll(btnBack);
        return hb;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PANEL DERECHO – RESUMEN DE PEDIDO
    // ═══════════════════════════════════════════════════════════════════════════
    private static VBox buildRightPanel() {
        VBox panel = new VBox(0);
        panel.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 0 1;");

        // ── Header ────────────────────────────────────────────────────────────
        HBox header = new HBox(10);
        header.setPadding(new Insets(20, 20, 14, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");
        Label cartIcon = new Label("🛒");
        cartIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: " + RED + ";");
        Label title = new Label("RESUMEN DE PEDIDO");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TXT + ";");
        Region spacerH = new Region();
        HBox.setHgrow(spacerH, Priority.ALWAYS);
        Button clearAll = new Button("🗑");
        clearAll.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: " + RED + ";" +
            " -fx-font-size: 18px; -fx-cursor: hand;");
        clearAll.setOnAction(e -> { orderItems.clear(); refreshTotals(); });
        header.getChildren().addAll(cartIcon, title, spacerH, clearAll);

        // ── Cabecera columnas (roja) ───────────────────────────────────────────
        HBox colHeader = new HBox(0);
        colHeader.setPadding(new Insets(8, 20, 8, 20));
        colHeader.setStyle("-fx-background-color: " + RED + ";");
        colHeader.setAlignment(Pos.CENTER);
        colHeader.getChildren().addAll(
            colLbl("PRODUCTO", 150),
            colLbl("CANT.",     60),
            colLbl("PRECIO",    80),
            colLbl("",          46)
        );

        VBox itemsList = new VBox(0);
        itemsList.setStyle("-fx-background-color: " + WHITE + ";");
        ScrollPane listScroll = new ScrollPane(itemsList);
        listScroll.setFitToWidth(true);
        listScroll.setStyle("-fx-background-color: transparent; -fx-background: white;");
        listScroll.setPrefHeight(190);
        VBox.setVgrow(listScroll, Priority.ALWAYS);

        orderItems.addListener((ListChangeListener<OrderItem>) c -> {
            itemsList.getChildren().clear();
            for (OrderItem oi : orderItems)
                itemsList.getChildren().add(buildOrderRow(oi));
        });

        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: " + BORDER + ";");

        VBox totalsBox       = buildTotalsBox();
        VBox deliveryInfoBox = buildDeliveryInfoBox();
        HBox actionBtns      = buildActionButtons(orderItems, itemsList);

        panel.getChildren().addAll(
            header, colHeader, listScroll, sep,
            totalsBox, deliveryInfoBox, actionBtns);
        return panel;
    }

    private static Label colLbl(String text, double w) {
        Label lbl = new Label(text);
        lbl.setPrefWidth(w);
        lbl.setAlignment(Pos.CENTER);
        lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white;");
        return lbl;
    }

    private static HBox buildOrderRow(OrderItem oi) {
        HBox row = new HBox(0);
        row.setPadding(new Insets(9, 20, 9, 20));
        row.setAlignment(Pos.CENTER);
        row.setStyle(
            "-fx-border-color: transparent transparent " + BORDER + " transparent;" +
            "-fx-border-width: 1; -fx-background-color: " + WHITE + ";");

        Label prod = new Label(oi.getProducto());
        prod.setPrefWidth(150);
        prod.setStyle("-fx-font-size: 11px; -fx-text-fill: " + DARK_TXT + ";");
        prod.setWrapText(true);
        prod.setAlignment(Pos.CENTER_LEFT);

        Label cant = new Label();
        cant.textProperty().bind(oi.cantidadProperty().asString());
        cant.setPrefWidth(60);
        cant.setAlignment(Pos.CENTER);
        cant.setStyle("-fx-font-size: 12px; -fx-text-fill: " + DARK_TXT + ";");

        Label precio = new Label(oi.getPrecio() + " Bs");
        precio.setPrefWidth(80);
        precio.setAlignment(Pos.CENTER);
        precio.setStyle("-fx-font-size: 12px; -fx-text-fill: " + DARK_TXT + ";");

        Button del = new Button("🗑");
        del.setPrefWidth(46);
        del.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + RED + ";");
        del.setOnAction(e -> { orderItems.remove(oi); refreshTotals(); });

        row.getChildren().addAll(prod, cant, precio, del);
        return row;
    }

    private static VBox buildTotalsBox() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(14, 20, 12, 20));
        box.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-width: 1 0 0 0;");

        subtotalLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + DARK_TXT + ";");
        deliveryLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + RED + ";");
        totalLbl.setStyle(
            "-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");

        Region sepLine = new Region();
        sepLine.setPrefHeight(1);
        sepLine.setStyle("-fx-background-color: " + BORDER + ";");

        box.getChildren().addAll(
            buildTotalRow("Subtotal:",       subtotalLbl, DARK_TXT),
            buildTotalRow("Costo Delivery:", deliveryLbl, RED),
            sepLine,
            buildTotalRow("TOTAL:",          totalLbl,    DARK_TXT));
        return box;
    }

    private static HBox buildTotalRow(String title, Label valueLabel, String color) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label(title);
        titleLbl.setPrefWidth(145);
        titleLbl.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        row.getChildren().addAll(titleLbl, sp, valueLabel);
        return row;
    }

    private static VBox buildDeliveryInfoBox() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(12, 20, 12, 20));
        box.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-width: 1 0 1 0;");

        HBox row1 = new HBox(8);
        row1.setAlignment(Pos.CENTER_LEFT);
        Label truck = new Label("🚚");
        truck.setStyle("-fx-font-size: 14px;");
        Label entTitle = new Label("Entrega:");
        entTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #555;");
        entValLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + RED + "; -fx-font-weight: bold;");
        row1.getChildren().addAll(truck, entTitle, entValLbl);

        HBox row2 = new HBox(8);
        row2.setAlignment(Pos.TOP_LEFT);
        Label pin = new Label("📍");
        pin.setStyle("-fx-font-size: 13px;");
        VBox dirInfo = new VBox(2);
        Label dirTitle = new Label("Dirección:");
        dirTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #555;");
        dirValLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");
        dirValLbl.setWrapText(true);
        dirInfo.getChildren().addAll(dirTitle, dirValLbl);
        row2.getChildren().addAll(pin, dirInfo);

        HBox row3 = new HBox(8);
        row3.setAlignment(Pos.CENTER_LEFT);
        Label personIcon = new Label("👤");
        personIcon.setStyle("-fx-font-size: 13px;");
        VBox nombreInfo = new VBox(2);
        Label nombreTitle = new Label("Nombre:");
        nombreTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #555;");
        nombreValLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");
        nombreInfo.getChildren().addAll(nombreTitle, nombreValLbl);
        row3.getChildren().addAll(personIcon, nombreInfo);

        HBox row4 = new HBox(8);
        row4.setAlignment(Pos.CENTER_LEFT);
        Label phone = new Label("📞");
        phone.setStyle("-fx-font-size: 13px;");
        VBox celInfo = new VBox(2);
        Label celTitle = new Label("Celular:");
        celTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #555;");
        celValLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");
        celInfo.getChildren().addAll(celTitle, celValLbl);
        row4.getChildren().addAll(phone, celInfo);

        box.getChildren().addAll(row1, row2, row3, row4);
        return box;
    }

    private static HBox buildActionButtons(ObservableList<OrderItem> items, VBox listContainer) {
        HBox hb = new HBox(10);
        hb.setPadding(new Insets(14, 20, 18, 20));
        hb.setStyle("-fx-background-color: " + WHITE + ";");

        Button confirm = new Button("✔  CONFIRMAR PEDIDO");
        confirm.setPrefHeight(46);
        HBox.setHgrow(confirm, Priority.ALWAYS);
        confirm.setMaxWidth(Double.MAX_VALUE);
        confirm.setStyle(
            "-fx-background-color: " + GREEN + "; -fx-text-fill: white;" +
            " -fx-font-size: 11px; -fx-font-weight: bold;" +
            " -fx-background-radius: 8; -fx-cursor: hand;");

        confirm.setOnAction(e -> {
            // ── V1: carrito no vacío ───────────────────────────────────────
            if (orderItems.isEmpty()) {
                mostrarError("Pedido vacío",
                    "Debe agregar al menos un producto antes de confirmar.");
                return;
            }

            String nombre  = tfNombreGlobal != null ? tfNombreGlobal.getText().trim() : "";
            String dir     = tfDirGlobal    != null ? tfDirGlobal.getText().trim()    : "";
            String celular = tfCelGlobal    != null ? tfCelGlobal.getText().trim()    : "";

            // ── V2: nombre ─────────────────────────────────────────────────
            if (nombre.isEmpty()) {
                mostrarError("Nombre requerido", "El nombre del cliente es obligatorio.");
                if (tfNombreGlobal != null) tfNombreGlobal.requestFocus();
                return;
            }
            if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+")) {
                mostrarError("Nombre inválido",
                    "El nombre solo puede contener letras y espacios.\n" +
                    "No se permiten números, puntos ni símbolos.");
                if (tfNombreGlobal != null) tfNombreGlobal.requestFocus();
                return;
            }
            if (nombre.length() < 3) {
                mostrarError("Nombre muy corto",
                    "El nombre debe tener al menos 3 caracteres.");
                if (tfNombreGlobal != null) tfNombreGlobal.requestFocus();
                return;
            }
            if (nombre.length() > 50) {
                mostrarError("Nombre muy largo",
                    "El nombre no puede superar los 50 caracteres.");
                if (tfNombreGlobal != null) tfNombreGlobal.requestFocus();
                return;
            }

            // ── V3: dirección ──────────────────────────────────────────────
            if (dir.isEmpty()) {
                mostrarError("Dirección requerida", "La dirección es obligatoria.");
                if (tfDirGlobal != null) tfDirGlobal.requestFocus();
                return;
            }
            if (dir.length() < 5) {
                mostrarError("Dirección muy corta",
                    "La dirección debe tener al menos 5 caracteres.\n" +
                    "Ejemplo: Av. Las Américas Nro 123");
                if (tfDirGlobal != null) tfDirGlobal.requestFocus();
                return;
            }
            if (dir.length() > 100) {
                mostrarError("Dirección muy larga",
                    "La dirección no puede superar los 100 caracteres.");
                if (tfDirGlobal != null) tfDirGlobal.requestFocus();
                return;
            }

            // ── V4: celular ────────────────────────────────────────────────
            if (celular.isEmpty()) {
                mostrarError("Celular requerido", "El número de celular es obligatorio.");
                if (tfCelGlobal != null) tfCelGlobal.requestFocus();
                return;
            }
            if (!celular.matches("\\d+")) {
                mostrarError("Celular inválido",
                    "El celular solo debe contener números.");
                if (tfCelGlobal != null) tfCelGlobal.requestFocus();
                return;
            }
            if (celular.length() < 7) {
                mostrarError("Celular muy corto",
                    "El número de celular debe tener al menos 7 dígitos.");
                if (tfCelGlobal != null) tfCelGlobal.requestFocus();
                return;
            }
            if (celular.length() > 15) {
                mostrarError("Celular muy largo",
                    "El número de celular no puede superar los 15 dígitos.");
                if (tfCelGlobal != null) tfCelGlobal.requestFocus();
                return;
            }

            generarFactura();
        });

        Button cancel = new Button("✖  CANCELAR");
        cancel.setPrefHeight(46);
        HBox.setHgrow(cancel, Priority.ALWAYS);
        cancel.setMaxWidth(Double.MAX_VALUE);
        cancel.setStyle(
            "-fx-background-color: " + RED + "; -fx-text-fill: white;" +
            " -fx-font-size: 11px; -fx-font-weight: bold;" +
            " -fx-background-radius: 8; -fx-cursor: hand;");
        cancel.setOnAction(e -> { items.clear(); refreshTotals(); });

        hb.getChildren().addAll(confirm, cancel);
        return hb;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private static void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private static void refreshTotals() {
        int sub = 0;
        for (OrderItem oi : orderItems) sub += oi.getCantidad() * oi.getPrecio();
        int delivery = isDelivery ? 10 : 0;
        subtotalLbl.setText(sub + " Bs");
        deliveryLbl.setText(delivery + " Bs");
        totalLbl.setText((sub + delivery) + " Bs");
    }

    // ── Factura de confirmación ────────────────────────────────────────────────
    private static void generarFactura() {
        Stage facturaStage = new Stage();

        VBox root = new VBox(10);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: white;");

        Label titleLbl  = new Label("TACABRÓN RESTAURANTE");
        titleLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TXT + ";");
        Label subtitle  = new Label("FACTURA DE PEDIDO");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: " + GRAY_TXT + ";");
        Separator sep1  = new Separator();

        Label cliente   = new Label("Cliente: "   + nombreValLbl.getText());
        Label direccion = new Label("Dirección: " + dirValLbl.getText());
        Label celular   = new Label("Tel: "        + celValLbl.getText());
        Label entrega   = new Label("Entrega: "    + (isDelivery ? "DELIVERY" : "LOCAL"));
        for (Label l : new Label[]{cliente, direccion, celular, entrega})
            l.setStyle("-fx-font-size: 13px;");

        Separator sep2 = new Separator();

        VBox itemsBox = new VBox(5);
        for (OrderItem item : orderItems) {
            Label line = new Label(
                item.getProducto() + " x" + item.getCantidad() +
                "   →   " + (item.getCantidad() * item.getPrecio()) + " Bs");
            line.setStyle("-fx-font-size: 12px;");
            itemsBox.getChildren().add(line);
        }

        Separator sep3 = new Separator();

        int total = 0;
        for (OrderItem item : orderItems) total += item.getCantidad() * item.getPrecio();
        int delivery = isDelivery ? 10 : 0;

        Label totalLblFinal = new Label("TOTAL: " + (total + delivery) + " Bs");
        totalLblFinal.setStyle(
            "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + GREEN + ";");

        Label gracias = new Label("Por favor verifique que el pedido sea correcto.\n¿Confirmar pedido?");
        gracias.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

        Button btnConfirmar = new Button("CONFIRMAR COMPRA");
        btnConfirmar.setStyle(
            "-fx-background-color: " + GREEN + "; -fx-text-fill: white;" +
            " -fx-font-weight: bold; -fx-font-size: 13px;" +
            " -fx-background-radius: 8; -fx-cursor: hand;");

        btnConfirmar.setOnAction(e -> {
            guardarFactura();
            // ── DESCONTAR INGREDIENTES DEL INVENTARIO ──────────────────────
            for (OrderItem item : orderItems) {
                com.dhery.app.AppState.descontarIngredientesPorProducto(
                    item.getProducto(), item.getCantidad());
            }
            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.setTitle("Pedido realizado");
            exito.setHeaderText("¡Gracias por su compra!");
            exito.setContentText(
                "Su pedido fue registrado correctamente.\n\nTacabrón agradece su preferencia 🌮");
            exito.showAndWait();
            orderItems.clear();
            refreshTotals();
            facturaStage.close();
        });

        root.getChildren().addAll(
            titleLbl, subtitle, sep1,
            cliente, direccion, celular, entrega,
            sep2, itemsBox, sep3,
            totalLblFinal, gracias, btnConfirmar);

        Scene scene = new Scene(root, 360, 520);
        facturaStage.setTitle("Factura Tacabrón");
        facturaStage.setScene(scene);
        facturaStage.show();
    }

    private static void guardarFactura() {
        ArchivoManager archivo = new ArchivoManager();
        String fecha = java.time.LocalDate.now().toString();
        int subtotal = 0;
        StringBuilder detalle = new StringBuilder();

        for (OrderItem item : orderItems) {
            subtotal += item.getCantidad() * item.getPrecio();
            detalle.append(item.getProducto())
                   .append(" x").append(item.getCantidad())
                   .append(", ");
        }

        int delivery  = isDelivery ? 10 : 0;
        int total     = subtotal + delivery;
        int idFactura = generarIdFactura();

        String linea = idFactura + "|"
            + currentUser.getId() + "|"
            + fecha + "|"
            + total + "|"
            + (isDelivery ? "DELIVERY" : "LOCAL") + "|"
            + detalle;

        archivo.agregarLinea(
            "src/main/java/com/dhery/GestorArchivo/facturas.txt", linea);
    }

    private static int generarIdFactura() {
        ArchivoManager archivo = new ArchivoManager();
        List<String> lineas = archivo.leerLineas(
            "src/main/java/com/dhery/GestorArchivo/facturas.txt");
        int max = 0;
        for (String l : lineas) {
            try {
                String[] p = l.split("\\|");
                int id = Integer.parseInt(p[0]);
                if (id > max) max = id;
            } catch (Exception ignored) {}
        }
        return max + 1;
    }
}