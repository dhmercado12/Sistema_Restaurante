package com.dhery.views;

import com.dhery.app.Router;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class TakeOrderViewC {

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

    private static final String DARK_BG = "#1E2226";
    private static final String CARD_BG = "#FFF3D1";
    private static final String BTN_BG  = "#DDC98A";
    private static final String GREEN   = "#4CAF50";
    private static final String ORANGE  = "#E8890C";
    private static final String TEXT_W  = "#FFFFFF";
    private static final String TEXT_G  = "#AAAAAA";
    private static final String TEXT_DK = "#1A1A1A";

    public static Scene getScene() {
        orderItems.clear();
        subtotalLbl  = new Label("0 Bs");
        deliveryLbl  = new Label("10 Bs");
        totalLbl     = new Label("10 Bs");
        entValLbl    = new Label("Delivery");
        dirValLbl    = new Label("—");
        nombreValLbl = new Label("—");
        celValLbl    = new Label("—");
        isDelivery   = true;

        HBox root = new HBox(0);
        root.setStyle("-fx-background-color: " + DARK_BG + ";");

        VBox leftPanel = buildLeftPanel();
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        VBox rightPanel = buildRightPanel();
        rightPanel.setPrefWidth(380);
        rightPanel.setMinWidth(380);

        root.getChildren().addAll(leftPanel, rightPanel);
        return new Scene(root, 1280, 720);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PANEL IZQUIERDO
    // ═══════════════════════════════════════════════════════════════════════════
    private static VBox buildLeftPanel() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: " + DARK_BG + ";");

        HBox header = new HBox(10);
        header.setPadding(new Insets(18, 20, 12, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("📋");
        icon.setStyle("-fx-font-size: 22px;");
        Label title = new Label("Pedido");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_W + ";");
        header.getChildren().addAll(icon, title);

        HBox tabs = buildTabs();

        ScrollPane scrollPane = new ScrollPane(buildProductGrid());
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox deliverySection = buildDeliverySection();
        HBox bottomBtns = buildBottomButtons();

        panel.getChildren().addAll(header, tabs, scrollPane, deliverySection, bottomBtns);
        return panel;
    }

    private static HBox buildTabs() {
        HBox tabs = new HBox(0);
        tabs.setPadding(new Insets(0, 20, 12, 20));

        String[][] tabData = {
            {"🌮",  "TACOS"},
            {"🍽️", "PLATOS EXTRA"},
            {"🥤",  "BEBIDAS"},
            {"🎁",  "PROMOCIONES"},
        };

        for (int i = 0; i < tabData.length; i++) {
            boolean active = (i == 3);
            Button tab = new Button(tabData[i][0] + "  " + tabData[i][1]);

            String activeStyle =
                "-fx-background-color: " + GREEN + "; -fx-text-fill: white;" +
                " -fx-font-size: 12px; -fx-font-weight: bold;" +
                " -fx-background-radius: 8 8 0 0;" +
                " -fx-padding: 9 16 9 16; -fx-cursor: hand;";
            String inactiveStyle =
                "-fx-background-color: #2C3238; -fx-text-fill: " + TEXT_W + ";" +
                " -fx-font-size: 12px; -fx-font-weight: bold;" +
                " -fx-background-radius: 8 8 0 0;" +
                " -fx-padding: 9 16 9 16; -fx-cursor: hand;";

            tab.setStyle(active ? activeStyle : inactiveStyle);
            tab.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(tab, Priority.ALWAYS);
            if (i < tabData.length - 1) {
                HBox.setMargin(tab, new Insets(0, 4, 0, 0));
            }
            tabs.getChildren().add(tab);
        }
        return tabs;
    }

    private static GridPane buildProductGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 20, 10, 20));

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
            new Producto("JAMAICA",   8, "🧃"),
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
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(14, 10, 14, 10));
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: 12;"
        );

        Label emojiLbl = new Label(p.emoji);
        emojiLbl.setStyle("-fx-font-size: 26px;");

        Label nameLbl = new Label(p.name);
        nameLbl.setStyle(
            "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DK + ";");
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setTextAlignment(TextAlignment.CENTER);
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(150);

        Label priceLbl = new Label(p.price + " Bs");
        priceLbl.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + GREEN + ";");

        HBox counter = buildCounter(p.name, p.price);
        card.getChildren().addAll(emojiLbl, nameLbl, priceLbl, counter);
        return card;
    }

    private static VBox buildPromoCard(String name, String desc, int price) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12, 12, 12, 12));
        card.setStyle(
            "-fx-background-color: #2E2A1A;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + ORANGE + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;"
        );

        Label badge = new Label("PROMO");
        badge.setStyle(
            "-fx-background-color: " + ORANGE + "; -fx-text-fill: white;" +
            " -fx-font-size: 9px; -fx-font-weight: bold;" +
            " -fx-padding: 2 8 2 8; -fx-background-radius: 4;");

        Label nameLbl = new Label(name);
        nameLbl.setStyle(
            "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_W + ";");
        nameLbl.setWrapText(true);
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setTextAlignment(TextAlignment.CENTER);
        nameLbl.setMaxWidth(170);

        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_G + ";");
        descLbl.setAlignment(Pos.CENTER);
        descLbl.setTextAlignment(TextAlignment.CENTER);

        Label priceLbl = new Label(price + " Bs");
        priceLbl.setStyle(
            "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + ORANGE + ";");

        HBox counter = buildCounter(name, price);
        card.getChildren().addAll(badge, nameLbl, descLbl, priceLbl, counter);
        return card;
    }

    private static HBox buildCounter(String itemName, int itemPrice) {
        HBox hb = new HBox(6);
        hb.setAlignment(Pos.CENTER);

        String btnStyle =
            "-fx-background-color: " + BTN_BG + ";" +
            "-fx-text-fill: " + TEXT_DK + ";" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-min-width: 34; -fx-max-width: 34;" +
            "-fx-min-height: 34; -fx-max-height: 34;";

        Button minus = new Button("−");
        minus.setStyle(btnStyle);

        Label countLbl = new Label("0");
        countLbl.setPrefWidth(36);
        countLbl.setPrefHeight(34);
        countLbl.setAlignment(Pos.CENTER);
        countLbl.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT_DK + ";" +
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 0;"
        );

        Button plus = new Button("+");
        plus.setStyle(btnStyle);

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

    private static VBox buildDeliverySection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(12, 20, 10, 20));
        section.setStyle("-fx-background-color: #222222;");

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label truck = new Label("🚚");
        truck.setStyle("-fx-font-size: 16px;");
        Label titleLbl = new Label("TIPO DE ENTREGA");
        titleLbl.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_W + ";");
        titleRow.getChildren().addAll(truck, titleLbl);

        HBox row = new HBox(28);
        row.setAlignment(Pos.CENTER_LEFT);

        ToggleGroup tg = new ToggleGroup();
        RadioButton rbLocal    = new RadioButton("Local");
        RadioButton rbDelivery = new RadioButton("Delivery");
        rbLocal.setStyle("-fx-text-fill: " + TEXT_W + "; -fx-font-size: 13px;");
        rbDelivery.setStyle("-fx-text-fill: " + TEXT_W + "; -fx-font-size: 13px;");
        rbLocal.setToggleGroup(tg);
        rbDelivery.setToggleGroup(tg);
        rbDelivery.setSelected(true);
        VBox radios = new VBox(8, rbLocal, rbDelivery);

        GridPane fields = new GridPane();
        fields.setHgap(10);
        fields.setVgap(7);

        TextField tfDir = buildDeliveryTextField("Escribe la dirección");
        tfDir.textProperty().addListener((obs, o, nv) ->
            dirValLbl.setText(nv.isEmpty() ? "—" : nv));

        TextField tfNombre = buildDeliveryTextField("Nombre del cliente");
        tfNombre.textProperty().addListener((obs, o, nv) ->
            nombreValLbl.setText(nv.isEmpty() ? "—" : nv));

        TextField tfCel = buildDeliveryTextField("Número de contacto");
        tfCel.textProperty().addListener((obs, o, nv) ->
            celValLbl.setText(nv.isEmpty() ? "—" : nv));

        addFieldRow(fields, "Dirección:", tfDir,    0);
        addFieldRow(fields, "Nombre:",    tfNombre, 1);
        addFieldRow(fields, "Celular:",   tfCel,    2);

        VBox costBox = new VBox(4);
        costBox.setAlignment(Pos.CENTER);
        Label costTitle = new Label("Costo Delivery:");
        costTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_G + ";");
        Label costAmt = new Label("10 Bs");
        costAmt.setStyle(
            "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;" +
            "-fx-background-color: #1A3A6A; -fx-padding: 10 20 10 20; -fx-background-radius: 8;");
        costBox.getChildren().addAll(costTitle, costAmt);

        row.getChildren().addAll(radios, fields, costBox);

        rbLocal.setOnAction(ev -> {
            isDelivery = false;
            fields.setDisable(true);
            entValLbl.setText("Local");
            refreshTotals();
        });
        rbDelivery.setOnAction(ev -> {
            isDelivery = true;
            fields.setDisable(false);
            entValLbl.setText("Delivery");
            refreshTotals();
        });

        section.getChildren().addAll(titleRow, row);
        return section;
    }

    private static TextField buildDeliveryTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(230);
        tf.setStyle(
            "-fx-background-color: #333333; -fx-text-fill: white;" +
            " -fx-prompt-text-fill: #666666; -fx-background-radius: 6; -fx-padding: 6 10 6 10;");
        return tf;
    }

    private static void addFieldRow(GridPane grid, String label, TextField tf, int row) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: " + TEXT_G + "; -fx-font-size: 12px;");
        grid.add(lbl, 0, row);
        grid.add(tf,  1, row);
    }

    private static HBox buildBottomButtons() {
        HBox hb = new HBox(12);
        hb.setPadding(new Insets(12, 20, 14, 20));
        hb.setAlignment(Pos.CENTER_LEFT);
        hb.setStyle("-fx-background-color: " + DARK_BG + ";");

        Button btnBack = new Button("← ATRÁS");
        btnBack.setPrefHeight(44);
        btnBack.setPrefWidth(120);
        btnBack.setStyle(
            "-fx-background-color: #333333; -fx-text-fill: white;" +
            " -fx-font-weight: bold; -fx-font-size: 13px;" +
            " -fx-background-radius: 8; -fx-cursor: hand;");
        btnBack.setOnAction(e -> Router.goMenuClienteView());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnStatus = new Button("📋  ESTADO DE PEDIDO");
        btnStatus.setPrefHeight(44);
        btnStatus.setPrefWidth(220);
        btnStatus.setStyle(
            "-fx-background-color: " + ORANGE + "; -fx-text-fill: white;" +
            " -fx-font-weight: bold; -fx-font-size: 13px;" +
            " -fx-background-radius: 8; -fx-cursor: hand;");
        btnStatus.setOnAction(e -> Router.goOrderStatusView());

        hb.getChildren().addAll(btnBack, spacer, btnStatus);
        return hb;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PANEL DERECHO – RESUMEN
    // ═══════════════════════════════════════════════════════════════════════════
    private static VBox buildRightPanel() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: #F5F5F5;");

        HBox header = new HBox(10);
        header.setPadding(new Insets(18, 20, 14, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: white;");
        Label cartIcon = new Label("🛒");
        cartIcon.setStyle("-fx-font-size: 20px;");
        Label title = new Label("RESUMEN DE PEDIDO");
        title.setStyle(
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;");
        header.getChildren().addAll(cartIcon, title);

        HBox colHeader = new HBox(0);
        colHeader.setPadding(new Insets(8, 20, 8, 20));
        colHeader.setStyle("-fx-background-color: #1A1A1A;");
        colHeader.setAlignment(Pos.CENTER);
        Label cProd   = colLbl("PRODUCTO", 150);
        Label cCant   = colLbl("CANT.",     60);
        Label cPrecio = colLbl("PRECIO",    80);
        Label cDel    = colLbl("",          40);
        colHeader.getChildren().addAll(cProd, cCant, cPrecio, cDel);

        VBox itemsList = new VBox(0);
        itemsList.setStyle("-fx-background-color: white;");
        ScrollPane listScroll = new ScrollPane(itemsList);
        listScroll.setFitToWidth(true);
        listScroll.setStyle("-fx-background-color: transparent; -fx-background: white;");
        listScroll.setPrefHeight(210);
        VBox.setVgrow(listScroll, Priority.ALWAYS);

        orderItems.addListener((ListChangeListener<OrderItem>) c -> {
            itemsList.getChildren().clear();
            for (OrderItem oi : orderItems)
                itemsList.getChildren().add(buildOrderRow(oi));
        });

        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: #DDDDDD;");

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
            "-fx-border-color: transparent transparent #EEEEEE transparent;" +
            "-fx-border-width: 1; -fx-background-color: white;");

        Label prod = new Label(oi.getProducto());
        prod.setPrefWidth(150);
        prod.setStyle("-fx-font-size: 11px; -fx-text-fill: #1A1A1A;");
        prod.setWrapText(true);
        prod.setAlignment(Pos.CENTER_LEFT);

        Label cant = new Label();
        cant.textProperty().bind(oi.cantidadProperty().asString());
        cant.setPrefWidth(60);
        cant.setAlignment(Pos.CENTER);
        cant.setStyle("-fx-font-size: 12px; -fx-text-fill: #1A1A1A;");

        Label precio = new Label(oi.getPrecio() + " Bs");
        precio.setPrefWidth(80);
        precio.setAlignment(Pos.CENTER);
        precio.setStyle("-fx-font-size: 12px; -fx-text-fill: #1A1A1A;");

        Button del = new Button("🗑");
        del.setPrefWidth(40);
        del.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 15px;" +
            "-fx-text-fill: #CC0000;");
        del.setOnAction(e -> { orderItems.remove(oi); refreshTotals(); });

        row.getChildren().addAll(prod, cant, precio, del);
        return row;
    }

    private static VBox buildTotalsBox() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(14, 20, 12, 20));
        box.setStyle("-fx-background-color: white;");

        subtotalLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #1A1A1A;");
        deliveryLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #1565C0;");
        totalLbl.setStyle(
            "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        Region sepLine = new Region();
        sepLine.setPrefHeight(1);
        sepLine.setStyle("-fx-background-color: #DDDDDD;");

        box.getChildren().addAll(
            buildTotalRow("Subtotal:",       subtotalLbl, "#1A1A1A"),
            buildTotalRow("Costo Delivery:", deliveryLbl, "#1565C0"),
            sepLine,
            buildTotalRow("TOTAL:",          totalLbl,    "#1A1A1A"));
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
            "-fx-background-color: white;" +
            "-fx-border-color: #DDDDDD; -fx-border-width: 1 0 0 0;");

        HBox row1 = new HBox(8);
        row1.setAlignment(Pos.CENTER_LEFT);
        Label truck = new Label("🚚");
        truck.setStyle("-fx-font-size: 15px;");
        Label entTitle = new Label("Entrega:");
        entTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #555;");
        entValLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #1565C0;");
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
        Label nombreTitle = new Label("Cliente:");
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
        hb.setPadding(new Insets(12, 20, 18, 20));
        hb.setStyle("-fx-background-color: white;");

        Button confirm = new Button("✔  CONFIRMAR PEDIDO");
        confirm.setPrefHeight(46);
        HBox.setHgrow(confirm, Priority.ALWAYS);
        confirm.setMaxWidth(Double.MAX_VALUE);
        confirm.setStyle(
            "-fx-background-color: #2E7D32; -fx-text-fill: white;" +
            " -fx-font-size: 11px; -fx-font-weight: bold;" +
            " -fx-background-radius: 8; -fx-cursor: hand;");

        Button cancel = new Button("✖  CANCELAR");
        cancel.setPrefHeight(46);
        HBox.setHgrow(cancel, Priority.ALWAYS);
        cancel.setMaxWidth(Double.MAX_VALUE);
        cancel.setStyle(
            "-fx-background-color: #C62828; -fx-text-fill: white;" +
            " -fx-font-size: 11px; -fx-font-weight: bold;" +
            " -fx-background-radius: 8; -fx-cursor: hand;");
        cancel.setOnAction(e -> { items.clear(); refreshTotals(); });

        hb.getChildren().addAll(confirm, cancel);
        return hb;
    }

    private static void refreshTotals() {
        int sub = 0;
        for (OrderItem oi : orderItems) sub += oi.getCantidad() * oi.getPrecio();
        int delivery = isDelivery ? 10 : 0;
        subtotalLbl.setText(sub + " Bs");
        deliveryLbl.setText(delivery + " Bs");
        totalLbl.setText((sub + delivery) + " Bs");
    }
}