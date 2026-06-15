package com.dhery.utils;

import java.util.List;
import com.dhery.GestorArchivo.ArchivoManager;
import com.dhery.app.AppState;
import com.dhery.app.Router;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * TakeOrderView – Toma de Pedidos (Cajero)
 *
 * Formato pedidos.txt (ÚNICO formato compartido con Cocina y Delivery):
 *   idPedido|cliente|telefono|direccion|tipo|total|metodo|estado_cocina|prod1 xN|prod2 xN|...
 *
 * estado_cocina: EN_PROCESO → LISTO → LISTO_PARA_ENVIO (delivery) → ENTREGADO
 */
public class TakeOrderView {

    private static com.dhery.models.user currentUser;

    // ── Modelo ────────────────────────────────────────────────────────────────
    public static class OrderItem {
        private final StringProperty  producto = new SimpleStringProperty();
        private final IntegerProperty cantidad = new SimpleIntegerProperty();
        private final IntegerProperty precio   = new SimpleIntegerProperty();
        public OrderItem(String p, int c, int pr) { producto.set(p); cantidad.set(c); precio.set(pr); }
        public StringProperty  productoProperty() { return producto; }
        public IntegerProperty cantidadProperty() { return cantidad; }
        public IntegerProperty precioProperty()   { return precio; }
        public String getProducto() { return producto.get(); }
        public int    getCantidad() { return cantidad.get(); }
        public int    getPrecio()   { return precio.get(); }
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

    private static TextField tfDirGlobal;
    private static TextField tfNombreGlobal;
    private static TextField tfCelGlobal;

    // ── Ruta única del archivo compartido ────────────────────────────────────
    private static final String PEDIDOS_TXT   = "src/main/java/com/dhery/GestorArchivo/pedidos.txt";
    private static final String FACTURAS_TXT  = "src/main/java/com/dhery/GestorArchivo/facturas.txt";

    // ── Colores ───────────────────────────────────────────────────────────────
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

    // ════════════════════════════════════════════════════════════════════════
    public static Scene getScene(com.dhery.models.user user) {
        currentUser = user;
        orderItems.clear();

        subtotalLbl  = new Label("0 Bs");
        deliveryLbl  = new Label("10 Bs");
        totalLbl     = new Label("10 Bs");
        entValLbl    = new Label("Delivery");
        nombreValLbl = new Label("—");
        dirValLbl    = new Label("—");
        celValLbl    = new Label("—");
        isDelivery   = true;
        tfDirGlobal = tfNombreGlobal = tfCelGlobal = null;

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

    // ═══════════════════════════════════════════════════════════════════════
    // PANEL IZQUIERDO
    // ═══════════════════════════════════════════════════════════════════════
    private static VBox buildLeftPanel() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: " + WHITE + ";");

        HBox header = new HBox(14);
        header.setPadding(new Insets(20, 24, 16, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + WHITE + "; -fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");
        Label cartIcon = new Label("🛒");
        cartIcon.setStyle("-fx-font-size:20px; -fx-background-color:" + RED_LIGHT + "; -fx-background-radius:50;" +
            " -fx-min-width:48; -fx-min-height:48; -fx-max-width:48; -fx-max-height:48; -fx-alignment:center;");
        VBox titleBox = new VBox(2);
        Label titleLbl = new Label("TOMAR EL PEDIDO");
        titleLbl.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:" + DARK_TXT + ";");
        Label subtitleLbl2 = new Label("Registrar pedidos de los clientes");
        subtitleLbl2.setStyle("-fx-font-size:12px; -fx-text-fill:" + GRAY_TXT + ";");
        Region underline = new Region();
        underline.setPrefHeight(3); underline.setPrefWidth(60);
        underline.setStyle("-fx-background-color:" + RED + "; -fx-background-radius:2;");
        titleBox.getChildren().addAll(titleLbl, subtitleLbl2, underline);
        header.getChildren().addAll(cartIcon, titleBox);

        HBox tabs = buildTabs();
        ScrollPane scrollPane = new ScrollPane(buildProductGrid());
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent; -fx-background:" + PAGE_BG + ";");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        panel.getChildren().addAll(header, tabs, scrollPane, buildDeliverySection(), buildBottomButtons());
        return panel;
    }

    private static HBox buildTabs() {
        HBox tabs = new HBox(0);
        tabs.setPadding(new Insets(14, 24, 0, 24));
        tabs.setStyle("-fx-background-color:" + WHITE + ";");
        String[][] tabData = {{"🌮","TACOS"},{"🍽️","PLATOS EXTRA"},{"🥤","BEBIDAS"},{"🎁","PROMOCIONES"}};
        String active = "-fx-background-color:" + RED + "; -fx-text-fill:white;" +
            " -fx-font-size:12px; -fx-font-weight:bold; -fx-background-radius:8 8 0 0; -fx-padding:10 18; -fx-cursor:hand;";
        String inact = "-fx-background-color:" + TAB_INACT + "; -fx-text-fill:" + DARK_TXT + ";" +
            " -fx-font-size:12px; -fx-font-weight:bold; -fx-background-radius:8 8 0 0;" +
            " -fx-border-color:" + BORDER + "; -fx-border-radius:8 8 0 0; -fx-border-width:1 1 0 1;" +
            " -fx-padding:10 18; -fx-cursor:hand;";
        for (int i = 0; i < tabData.length; i++) {
            Button tab = new Button(tabData[i][0] + "  " + tabData[i][1]);
            tab.setStyle(i == 0 ? active : inact);
            tab.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(tab, Priority.ALWAYS);
            if (i < tabData.length - 1) HBox.setMargin(tab, new Insets(0, 4, 0, 0));
            tabs.getChildren().add(tab);
        }
        return tabs;
    }

    private static GridPane buildProductGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(16, 24, 16, 24));
        grid.setStyle("-fx-background-color:" + PAGE_BG + ";");

        Producto[] tacos = {
            new Producto("BIRRIA", 35, "🌮"), new Producto("QUESABIRRIA", 40, "🌮"),
            new Producto("SUADERO", 30, "🌮"), new Producto("PASTOR", 10, "🌮"),
        };
        Producto[] extras = {
            new Producto("RAMEN BIRRIA", 45, "🍲"), new Producto("NACHOS SUPREMOS", 40, "🍟"),
            new Producto("MEGABURRITO", 5, "🌯"),  new Producto("TORTILLA EXTRA", 10, "🌮"),
        };
        Producto[] bebidas = { new Producto("HORCHATA", 10, "🥛"), new Producto("JAMAICA", 8, "🥛") };

        for (int i = 0; i < tacos.length;   i++) grid.add(buildProductCard(tacos[i]),   0, i);
        for (int i = 0; i < extras.length;  i++) grid.add(buildProductCard(extras[i]),  1, i);
        for (int i = 0; i < bebidas.length; i++) grid.add(buildProductCard(bebidas[i]), 2, i);
        grid.add(buildPromoCard("NACHOS SUPREMOS COMBO 2", "Nachos Supremos + Jamaica", 45), 3, 0);
        grid.add(buildPromoCard("MEGABURRITO COMBO 2",     "MegaBurrito + Horchata",    55), 3, 1);
        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25); cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }
        return grid;
    }

    private static VBox buildProductCard(Producto p) {
        VBox card = new VBox(8); card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16, 12, 14, 12));
        card.setStyle("-fx-background-color:" + WHITE + "; -fx-background-radius:12;" +
            "-fx-border-color:" + BORDER + "; -fx-border-radius:12; -fx-border-width:1;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.06),6,0,0,2);");
        Label emo = new Label(p.emoji); emo.setStyle("-fx-font-size:30px;");
        Label nameLbl = new Label(p.name);
        nameLbl.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + DARK_TXT + ";");
        nameLbl.setAlignment(Pos.CENTER); nameLbl.setTextAlignment(TextAlignment.CENTER);
        nameLbl.setWrapText(true); nameLbl.setMaxWidth(150);
        Label priceLbl = new Label(p.price + " Bs");
        priceLbl.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + GREEN_LT + ";");
        card.getChildren().addAll(emo, nameLbl, priceLbl, buildCounter(p.name, p.price));
        return card;
    }

    private static VBox buildPromoCard(String name, String desc, int price) {
        VBox card = new VBox(6); card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(14, 12, 14, 12));
        card.setStyle("-fx-background-color:" + WHITE + "; -fx-background-radius:12;" +
            "-fx-border-color:" + RED + "; -fx-border-width:2; -fx-border-radius:12;" +
            "-fx-effect:dropshadow(gaussian,rgba(211,47,47,0.1),8,0,0,2);");
        Label badge = new Label("PROMO");
        badge.setStyle("-fx-background-color:" + RED + "; -fx-text-fill:white;" +
            " -fx-font-size:9px; -fx-font-weight:bold; -fx-padding:3 10; -fx-background-radius:4;");
        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + DARK_TXT + ";");
        nameLbl.setWrapText(true); nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setTextAlignment(TextAlignment.CENTER); nameLbl.setMaxWidth(170);
        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-font-size:10px; -fx-text-fill:" + GRAY_TXT + ";");
        descLbl.setAlignment(Pos.CENTER); descLbl.setTextAlignment(TextAlignment.CENTER);
        Label priceLbl = new Label(price + " Bs");
        priceLbl.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:" + GREEN_LT + ";");
        card.getChildren().addAll(badge, nameLbl, descLbl, priceLbl, buildCounter(name, price));
        return card;
    }

    private static HBox buildCounter(String itemName, int itemPrice) {
        HBox hb = new HBox(6); hb.setAlignment(Pos.CENTER);
        String btnStyle = "-fx-background-color:" + GRAY_BG + "; -fx-text-fill:" + DARK_TXT + ";" +
            " -fx-font-size:15px; -fx-font-weight:bold; -fx-background-radius:8; -fx-cursor:hand; -fx-padding:0;";
        Button minus = new Button("−"); Button plus = new Button("+");
        for (Button b : new Button[]{minus, plus}) {
            b.setStyle(btnStyle); b.setPrefWidth(32); b.setPrefHeight(32);
            b.setMinWidth(32); b.setMaxWidth(32); b.setMinHeight(32); b.setMaxHeight(32);
        }
        Label countLbl = new Label("0"); countLbl.setPrefWidth(36); countLbl.setPrefHeight(32);
        countLbl.setAlignment(Pos.CENTER);
        countLbl.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + DARK_TXT + ";" +
            " -fx-background-color:" + GRAY_BG + "; -fx-background-radius:6; -fx-padding:0;");

        plus.setOnAction(e -> {
            int c = Integer.parseInt(countLbl.getText()) + 1;
            countLbl.setText(String.valueOf(c));
            boolean found = false;
            for (OrderItem oi : orderItems) {
                if (oi.getProducto().equals(itemName)) {
                    oi.setCantidad(c); orderItems.set(orderItems.indexOf(oi), oi); found = true; break;
                }
            }
            if (!found) orderItems.add(new OrderItem(itemName, c, itemPrice));
            refreshTotals();
        });
        minus.setOnAction(e -> {
            int c = Integer.parseInt(countLbl.getText());
            if (c > 0) {
                c--; countLbl.setText(String.valueOf(c));
                if (c == 0) orderItems.removeIf(oi -> oi.getProducto().equals(itemName));
                else for (OrderItem oi : orderItems)
                    if (oi.getProducto().equals(itemName)) { oi.setCantidad(c); orderItems.set(orderItems.indexOf(oi), oi); break; }
                refreshTotals();
            }
        });
        hb.getChildren().addAll(minus, countLbl, plus);
        return hb;
    }

    // ── Sección de entrega ────────────────────────────────────────────────────
    private static VBox buildDeliverySection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(14, 24, 12, 24));
        section.setStyle("-fx-background-color:" + WHITE + "; -fx-border-color:" + BORDER + "; -fx-border-width:1 0 0 0;");

        HBox titleRow = new HBox(8); titleRow.setAlignment(Pos.CENTER_LEFT);
        Label truck = new Label("🚚"); truck.setStyle("-fx-font-size:16px;");
        Label titleLbl = new Label("TIPO DE ENTREGA");
        titleLbl.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:" + DARK_TXT + ";");
        titleRow.getChildren().addAll(truck, titleLbl);

        HBox row = new HBox(32); row.setAlignment(Pos.CENTER_LEFT);

        ToggleGroup tg = new ToggleGroup();
        RadioButton rbLocal    = new RadioButton("LOCAL");
        RadioButton rbDelivery = new RadioButton("DELIVERY");
        String rbStyle = "-fx-text-fill:" + DARK_TXT + "; -fx-font-size:13px; -fx-font-weight:bold; -fx-cursor:hand;";
        rbLocal.setStyle(rbStyle); rbDelivery.setStyle(rbStyle);
        rbLocal.setToggleGroup(tg); rbDelivery.setToggleGroup(tg);
        rbDelivery.setSelected(true);
        VBox radios = new VBox(10, rbLocal, rbDelivery); radios.setAlignment(Pos.CENTER_LEFT);

        GridPane fields = new GridPane(); fields.setHgap(12); fields.setVgap(8);

        tfDirGlobal    = buildTF("Escribe la dirección");
        tfNombreGlobal = buildTF("Nombre del cliente");
        tfCelGlobal    = buildTF("Número de contacto");

        // Celular: solo dígitos
        tfCelGlobal.textProperty().addListener((obs, o, nv) -> {
            String f = nv.replaceAll("[^\\d]", "");
            if (f.length() > 15) f = f.substring(0, 15);
            if (!f.equals(nv)) tfCelGlobal.setText(f);
            celValLbl.setText(tfCelGlobal.getText().isEmpty() ? "—" : tfCelGlobal.getText());
        });
        // Nombre: solo letras y espacios
        tfNombreGlobal.textProperty().addListener((obs, o, nv) -> {
            String f = nv.replaceAll("[^a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]", "");
            if (f.length() > 50) f = f.substring(0, 50);
            if (!f.equals(nv)) tfNombreGlobal.setText(f);
            nombreValLbl.setText(tfNombreGlobal.getText().isEmpty() ? "—" : tfNombreGlobal.getText());
        });
        // Dirección
        tfDirGlobal.textProperty().addListener((obs, o, nv) -> {
            if (nv.length() > 100) tfDirGlobal.setText(nv.substring(0, 100));
            dirValLbl.setText(tfDirGlobal.getText().isEmpty() ? "—" : tfDirGlobal.getText());
        });

        addFR(fields, "Dirección:", tfDirGlobal,    0);
        addFR(fields, "Nombre:",    tfNombreGlobal, 1);
        addFR(fields, "Celular:",   tfCelGlobal,    2);

        VBox costBox = new VBox(4); costBox.setAlignment(Pos.CENTER);
        Label costTitle = new Label("Costo Delivery:");
        costTitle.setStyle("-fx-font-size:11px; -fx-text-fill:" + GRAY_TXT + ";");
        Label costAmt = new Label("10 Bs");
        costAmt.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white;" +
            " -fx-background-color:" + RED + "; -fx-padding:10 20; -fx-background-radius:8;");
        costBox.getChildren().addAll(costTitle, costAmt);

        row.getChildren().addAll(radios, fields, costBox);

        rbLocal.setOnAction(ev -> {
            isDelivery = false; fields.setDisable(false);
            costBox.setVisible(false); entValLbl.setText("Local");
            refreshTotals();
        });
        rbDelivery.setOnAction(ev -> {
            isDelivery = true; fields.setDisable(false);
            costBox.setVisible(true); entValLbl.setText("Delivery");
            refreshTotals();
        });

        section.getChildren().addAll(titleRow, row);
        return section;
    }

    private static TextField buildTF(String prompt) {
        TextField tf = new TextField(); tf.setPromptText(prompt); tf.setPrefWidth(240);
        tf.setStyle("-fx-background-color:" + GRAY_BG + "; -fx-text-fill:" + DARK_TXT + ";" +
            " -fx-prompt-text-fill:" + GRAY_TXT + "; -fx-background-radius:8; -fx-padding:7 12;" +
            " -fx-border-color:" + BORDER + "; -fx-border-radius:8; -fx-border-width:1;");
        return tf;
    }
    private static void addFR(GridPane g, String lbl, TextField tf, int row) {
        Label l = new Label(lbl); l.setStyle("-fx-text-fill:" + DARK_TXT + "; -fx-font-size:12px; -fx-font-weight:bold;");
        g.add(l, 0, row); g.add(tf, 1, row);
    }

    private static HBox buildBottomButtons() {
        HBox hb = new HBox(12); hb.setPadding(new Insets(12, 24, 14, 24)); hb.setAlignment(Pos.CENTER_LEFT);
        hb.setStyle("-fx-background-color:" + WHITE + "; -fx-border-color:" + BORDER + "; -fx-border-width:1 0 0 0;");
        Button btnBack = new Button("← ATRÁS"); btnBack.setPrefHeight(42); btnBack.setPrefWidth(120);
        btnBack.setStyle("-fx-background-color:" + WHITE + "; -fx-text-fill:" + RED + ";" +
            " -fx-font-weight:bold; -fx-font-size:13px; -fx-background-radius:8; -fx-cursor:hand;" +
            " -fx-border-color:" + RED + "; -fx-border-radius:8; -fx-border-width:1.5;");
        btnBack.setOnAction(e -> Router.goMenuCajeroView(currentUser));
        hb.getChildren().add(btnBack);
        return hb;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PANEL DERECHO – RESUMEN
    // ═══════════════════════════════════════════════════════════════════════
    private static VBox buildRightPanel() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color:" + WHITE + "; -fx-border-color:" + BORDER + "; -fx-border-width:0 0 0 1;");

        HBox header = new HBox(10); header.setPadding(new Insets(20, 20, 14, 20)); header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color:" + WHITE + "; -fx-border-color:" + BORDER + "; -fx-border-width:0 0 1 0;");
        Label cartIcon = new Label("🛒"); cartIcon.setStyle("-fx-font-size:18px; -fx-text-fill:" + RED + ";");
        Label title = new Label("RESUMEN DE PEDIDO");
        title.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + DARK_TXT + ";");
        Region spacerH = new Region(); HBox.setHgrow(spacerH, Priority.ALWAYS);
        Button clearAll = new Button("🗑");
        clearAll.setStyle("-fx-background-color:transparent; -fx-text-fill:" + RED + "; -fx-font-size:18px; -fx-cursor:hand;");
        clearAll.setOnAction(e -> { orderItems.clear(); refreshTotals(); });
        header.getChildren().addAll(cartIcon, title, spacerH, clearAll);

        HBox colHeader = new HBox(0); colHeader.setPadding(new Insets(8, 20, 8, 20));
        colHeader.setStyle("-fx-background-color:" + RED + ";"); colHeader.setAlignment(Pos.CENTER);
        colHeader.getChildren().addAll(colLbl("PRODUCTO", 150), colLbl("CANT.", 60), colLbl("PRECIO", 80), colLbl("", 46));

        VBox itemsList = new VBox(0); itemsList.setStyle("-fx-background-color:" + WHITE + ";");
        ScrollPane listScroll = new ScrollPane(itemsList); listScroll.setFitToWidth(true);
        listScroll.setStyle("-fx-background-color:transparent; -fx-background:white;");
        listScroll.setPrefHeight(190); VBox.setVgrow(listScroll, Priority.ALWAYS);

        orderItems.addListener((ListChangeListener<OrderItem>) c -> {
            itemsList.getChildren().clear();
            for (OrderItem oi : orderItems) itemsList.getChildren().add(buildOrderRow(oi));
        });

        Region sep = new Region(); sep.setPrefHeight(1); sep.setStyle("-fx-background-color:" + BORDER + ";");
        panel.getChildren().addAll(header, colHeader, listScroll, sep,
            buildTotalsBox(), buildDeliveryInfoBox(), buildActionButtons());
        return panel;
    }

    private static Label colLbl(String text, double w) {
        Label l = new Label(text); l.setPrefWidth(w); l.setAlignment(Pos.CENTER);
        l.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:white;"); return l;
    }
    private static HBox buildOrderRow(OrderItem oi) {
        HBox row = new HBox(0); row.setPadding(new Insets(9, 20, 9, 20)); row.setAlignment(Pos.CENTER);
        row.setStyle("-fx-border-color:transparent transparent " + BORDER + " transparent; -fx-border-width:1; -fx-background-color:" + WHITE + ";");
        Label prod = new Label(oi.getProducto()); prod.setPrefWidth(150);
        prod.setStyle("-fx-font-size:11px; -fx-text-fill:" + DARK_TXT + ";"); prod.setWrapText(true);
        Label cant = new Label(); cant.textProperty().bind(oi.cantidadProperty().asString());
        cant.setPrefWidth(60); cant.setAlignment(Pos.CENTER);
        cant.setStyle("-fx-font-size:12px; -fx-text-fill:" + DARK_TXT + ";");
        Label precio = new Label(oi.getPrecio() + " Bs"); precio.setPrefWidth(80); precio.setAlignment(Pos.CENTER);
        precio.setStyle("-fx-font-size:12px; -fx-text-fill:" + DARK_TXT + ";");
        Button del = new Button("🗑"); del.setPrefWidth(46);
        del.setStyle("-fx-background-color:transparent; -fx-cursor:hand; -fx-font-size:14px; -fx-text-fill:" + RED + ";");
        del.setOnAction(e -> { orderItems.remove(oi); refreshTotals(); });
        row.getChildren().addAll(prod, cant, precio, del);
        return row;
    }

    private static VBox buildTotalsBox() {
        VBox box = new VBox(8); box.setPadding(new Insets(14, 20, 12, 20));
        box.setStyle("-fx-background-color:" + WHITE + "; -fx-border-color:" + BORDER + "; -fx-border-width:1 0 0 0;");
        subtotalLbl.setStyle("-fx-font-size:13px; -fx-text-fill:" + DARK_TXT + ";");
        deliveryLbl.setStyle("-fx-font-size:13px; -fx-text-fill:" + RED + ";");
        totalLbl.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
        Region sep = new Region(); sep.setPrefHeight(1); sep.setStyle("-fx-background-color:" + BORDER + ";");
        box.getChildren().addAll(totRow("Subtotal:", subtotalLbl), totRow("Costo Delivery:", deliveryLbl), sep, totRow("TOTAL:", totalLbl));
        return box;
    }
    private static HBox totRow(String title, Label val) {
        HBox row = new HBox(); row.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(title); l.setPrefWidth(145);
        l.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + DARK_TXT + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        row.getChildren().addAll(l, sp, val); return row;
    }

    private static VBox buildDeliveryInfoBox() {
        VBox box = new VBox(8); box.setPadding(new Insets(12, 20, 12, 20));
        box.setStyle("-fx-background-color:" + WHITE + "; -fx-border-color:" + BORDER + "; -fx-border-width:1 0 1 0;");

        entValLbl.setStyle("-fx-font-size:11px; -fx-text-fill:" + RED + "; -fx-font-weight:bold;");
        dirValLbl.setStyle("-fx-font-size:11px; -fx-text-fill:#333;"); dirValLbl.setWrapText(true);
        nombreValLbl.setStyle("-fx-font-size:11px; -fx-text-fill:#333;");
        celValLbl.setStyle("-fx-font-size:11px; -fx-text-fill:#333;");

        box.getChildren().addAll(
            infoRow("🚚", "Entrega:",   entValLbl),
            infoRow("📍", "Dirección:", dirValLbl),
            infoRow("👤", "Nombre:",    nombreValLbl),
            infoRow("📞", "Celular:",   celValLbl));
        return box;
    }
    private static HBox infoRow(String ico, String label, Label val) {
        HBox row = new HBox(8); row.setAlignment(Pos.CENTER_LEFT);
        Label i = new Label(ico); i.setStyle("-fx-font-size:13px;");
        VBox info = new VBox(2);
        Label t = new Label(label); t.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:#555;");
        info.getChildren().addAll(t, val); row.getChildren().addAll(i, info); return row;
    }

    // ─── Botones CONFIRMAR / CANCELAR ────────────────────────────────────────
    private static HBox buildActionButtons() {
        HBox hb = new HBox(10); hb.setPadding(new Insets(14, 20, 18, 20));
        hb.setStyle("-fx-background-color:" + WHITE + ";");

        Button confirm = new Button("✔  CONFIRMAR PEDIDO"); confirm.setPrefHeight(46);
        HBox.setHgrow(confirm, Priority.ALWAYS); confirm.setMaxWidth(Double.MAX_VALUE);
        confirm.setStyle("-fx-background-color:" + GREEN + "; -fx-text-fill:white;" +
            " -fx-font-size:11px; -fx-font-weight:bold; -fx-background-radius:8; -fx-cursor:hand;");

        confirm.setOnAction(e -> {
            // ── Validaciones ──────────────────────────────────────────────
            if (orderItems.isEmpty()) { err("Pedido vacío","Agregue al menos un producto."); return; }

            String nombre  = tfNombreGlobal != null ? tfNombreGlobal.getText().trim() : "";
            String dir     = tfDirGlobal    != null ? tfDirGlobal.getText().trim()    : "";
            String celular = tfCelGlobal    != null ? tfCelGlobal.getText().trim()    : "";

            // Nombre: siempre requerido (local y delivery)
            if (nombre.isEmpty()) { err("Nombre requerido","El nombre del cliente es obligatorio."); if (tfNombreGlobal!=null) tfNombreGlobal.requestFocus(); return; }
            if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+")) { err("Nombre inválido","Solo letras y espacios. No se permiten puntos ni símbolos."); tfNombreGlobal.requestFocus(); return; }
            if (nombre.length() < 3) { err("Nombre muy corto","Mínimo 3 caracteres."); tfNombreGlobal.requestFocus(); return; }

            if (isDelivery) {
                // Dirección: solo requerida en DELIVERY
                if (dir.isEmpty()) { err("Dirección requerida","La dirección es obligatoria para delivery."); if (tfDirGlobal!=null) tfDirGlobal.requestFocus(); return; }
                if (dir.length() < 5) { err("Dirección muy corta","Mínimo 5 caracteres."); tfDirGlobal.requestFocus(); return; }
                // Celular: solo requerido en DELIVERY
                if (celular.isEmpty()) { err("Celular requerido","El número es obligatorio para delivery."); if (tfCelGlobal!=null) tfCelGlobal.requestFocus(); return; }
                if (!celular.matches("\\d+")) { err("Celular inválido","Solo dígitos."); tfCelGlobal.requestFocus(); return; }
                if (celular.length() < 7) { err("Celular muy corto","Mínimo 7 dígitos."); tfCelGlobal.requestFocus(); return; }
            }

            mostrarFactura(nombre, dir, celular);
        });

        Button cancel = new Button("✖  CANCELAR"); cancel.setPrefHeight(46);
        HBox.setHgrow(cancel, Priority.ALWAYS); cancel.setMaxWidth(Double.MAX_VALUE);
        cancel.setStyle("-fx-background-color:" + RED + "; -fx-text-fill:white;" +
            " -fx-font-size:11px; -fx-font-weight:bold; -fx-background-radius:8; -fx-cursor:hand;");
        cancel.setOnAction(e -> { orderItems.clear(); refreshTotals(); });

        hb.getChildren().addAll(confirm, cancel);
        return hb;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // FACTURA Y GUARDADO
    // ═══════════════════════════════════════════════════════════════════════
    private static void mostrarFactura(String nombre, String dir, String celular) {
        Stage facturaStage = new Stage();
        VBox root = new VBox(10); root.setPadding(new Insets(24)); root.setStyle("-fx-background-color:white;");

        Label title = new Label("TACABRÓN RESTAURANTE");
        title.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:" + DARK_TXT + ";");
        Label subtitle = new Label("FACTURA DE PEDIDO");
        subtitle.setStyle("-fx-font-size:14px; -fx-text-fill:" + GRAY_TXT + ";");

        Label lCliente  = info("Cliente: "   + nombre);
        Label lDir      = info("Dirección: " + (dir.isEmpty() ? "—" : dir));
        Label lCel      = info("Tel: "       + (celular.isEmpty() ? "—" : celular));
        Label lEntrega  = info("Entrega: "   + (isDelivery ? "DELIVERY" : "LOCAL"));

        VBox itemsBox = new VBox(5);
        int total = 0;
        for (OrderItem item : orderItems) {
            int sub = item.getCantidad() * item.getPrecio();
            total += sub;
            Label line = new Label(item.getProducto() + " x" + item.getCantidad() + "   →   " + sub + " Bs");
            line.setStyle("-fx-font-size:12px;"); itemsBox.getChildren().add(line);
        }
        int delivery = isDelivery ? 10 : 0;
        final int totalFinal = total + delivery;

        Label totalLblFinal = new Label("TOTAL: " + totalFinal + " Bs");
        totalLblFinal.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:" + GREEN + ";");
        Label nota = new Label("Verifique su pedido. ¿Confirmar?");
        nota.setStyle("-fx-font-size:12px; -fx-text-fill:#555;");

        Button btnOk = new Button("CONFIRMAR COMPRA");
        btnOk.setStyle("-fx-background-color:" + GREEN + "; -fx-text-fill:white;" +
            " -fx-font-weight:bold; -fx-font-size:13px; -fx-background-radius:8; -fx-cursor:hand;");
        btnOk.setOnAction(e -> {
            guardarPedido(nombre, dir, celular, totalFinal);
            Alert ex = new Alert(Alert.AlertType.INFORMATION);
            ex.setTitle("Pedido registrado");
            ex.setHeaderText("¡Pedido enviado a cocina!");
            ex.setContentText("El pedido de " + nombre + " fue registrado.\nApareció automáticamente en Estado de Cocina 🍳");
            ex.showAndWait();
            orderItems.clear(); refreshTotals(); facturaStage.close();
        });

        Button btnCancelar = new Button("CANCELAR");

btnCancelar.setStyle(
    "-fx-background-color:" + RED + ";" +
    "-fx-text-fill:white;" +
    "-fx-font-weight:bold;" +
    "-fx-font-size:13px;" +
    "-fx-background-radius:8;" +
    "-fx-cursor:hand;"
);

btnCancelar.setOnAction(e -> {
    facturaStage.close();
});

HBox botones = new HBox(10);
botones.setAlignment(Pos.CENTER);
botones.getChildren().addAll(btnOk, btnCancelar);

root.getChildren().addAll(
    title,
    subtitle,
    new Separator(),
    lCliente,
    lDir,
    lCel,
    lEntrega,
    new Separator(),
    itemsBox,
    new Separator(),
    totalLblFinal,
    nota,
    botones
);
        facturaStage.setScene(new Scene(root, 360, 540));
        facturaStage.setTitle("Factura Tacabrón"); facturaStage.show();
    }

    /**
     * Guarda el pedido en pedidos.txt con el formato que leen
     * EstadoCocinaView y ControlDeliveryView:
     *
     *   idPedido|cliente|telefono|direccion|tipo|total|metodo|estado_cocina|prod1 xN|prod2 xN
     */
    private static void guardarPedido(String nombre, String dir, String celular, int total) {
        ArchivoManager arch = new ArchivoManager();

        int idPedido = nextId(PEDIDOS_TXT);
        String tipo   = isDelivery ? "DELIVERY" : "LOCAL";
        String metodo = "Efectivo";  // se puede extender con selector

        // Productos separados: "BIRRIA x2|HORCHATA x1"
        StringBuilder prods = new StringBuilder();
        for (OrderItem oi : orderItems) {
            if (prods.length() > 0) prods.append("|");
            prods.append(oi.getProducto()).append(" x").append(oi.getCantidad());
        }

        // idPedido|cliente|telefono|direccion|tipo|total|metodo|EN_PROCESO|prod1 xN|...
        String linea = idPedido + "|" + nombre + "|" + celular + "|" + dir + "|"
            + tipo + "|" + total + "|" + metodo + "|EN_PROCESO|" + prods;

        arch.agregarLinea(PEDIDOS_TXT, linea);

        // Descontar ingredientes del inventario
        for (OrderItem oi : orderItems)
            AppState.descontarIngredientesPorProducto(oi.getProducto(), oi.getCantidad());

        // Notificación global
        AppState.agregarNotificacion("PEDIDO_LISTO",
            "Nuevo pedido #" + idPedido + " de " + nombre + " → Cocina 🍳");

        // Guardar también en facturas.txt (para historial del cliente)
        int idFact = nextId(FACTURAS_TXT);
        String horaActual = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        arch.agregarLinea(FACTURAS_TXT,
            idFact + "|" + (currentUser != null ? currentUser.getId() : 0) + "|"
            + java.time.LocalDate.now() + "|" + total + "|" + tipo + "|" + horaActual + "|" + prods);
    }

    private static int nextId(String ruta) {
        List<String> ls = new ArchivoManager().leerLineas(ruta);
        int max = 0;
        for (String l : ls) {
            if (l.isBlank()) continue;
            try { int id = Integer.parseInt(l.split("\\|")[0].trim()); if (id > max) max = id; }
            catch (Exception ignored) {}
        }
        return max + 1;
    }

    private static void refreshTotals() {
        int sub = 0;
        for (OrderItem oi : orderItems) sub += oi.getCantidad() * oi.getPrecio();
        int del = isDelivery ? 10 : 0;
        subtotalLbl.setText(sub + " Bs"); deliveryLbl.setText(del + " Bs"); totalLbl.setText((sub + del) + " Bs");
    }

    private static Label info(String t) { Label l = new Label(t); l.setStyle("-fx-font-size:13px;"); return l; }
    private static void err(String t, String m) { Alert a = new Alert(Alert.AlertType.WARNING); a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait(); }
}