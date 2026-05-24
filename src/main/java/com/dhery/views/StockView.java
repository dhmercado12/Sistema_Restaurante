package com.dhery.views;

import com.dhery.app.Router;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class StockView {

    // ── Modelo ────────────────────────────────────────────────────────────────
    public static class Ingrediente {
        private final IntegerProperty numero   = new SimpleIntegerProperty();
        private final StringProperty  nombre   = new SimpleStringProperty();
        private final DoubleProperty  cantidad = new SimpleDoubleProperty();
        private final StringProperty  unidad   = new SimpleStringProperty();
        private final StringProperty  estado   = new SimpleStringProperty();

        public Ingrediente(int num, String nombre, double cantidad, String unidad, String estado) {
            this.numero.set(num);
            this.nombre.set(nombre);
            this.cantidad.set(cantidad);
            this.unidad.set(unidad);
            this.estado.set(estado);
        }
        public IntegerProperty numeroProperty()   { return numero;   }
        public StringProperty  nombreProperty()   { return nombre;   }
        public DoubleProperty  cantidadProperty() { return cantidad; }
        public StringProperty  unidadProperty()   { return unidad;   }
        public StringProperty  estadoProperty()   { return estado;   }
        public int    getNumero()   { return numero.get();   }
        public String getNombre()   { return nombre.get();   }
        public double getCantidad() { return cantidad.get(); }
        public String getUnidad()   { return unidad.get();   }
        public String getEstado()   { return estado.get();   }
        public void   setCantidad(double v) { cantidad.set(v); }
    }

    private static final String DARK_BG  = "#121212";
    private static final String ORANGE   = "#E8890C";
    private static final String GREEN    = "#4CAF50";
    private static final String TEXT_W   = "#FFFFFF";
    private static final String TEXT_G   = "#AAAAAA";
    private static final String CAT_BG   = "#F5B700";

    public static Scene getScene() {
        ObservableList<Ingrediente> stockData = buildStockData();

        HBox root = new HBox(0);
        root.setStyle("-fx-background-color: " + DARK_BG + ";");

        VBox leftPanel = buildLeftPanel(stockData);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        VBox rightPanel = buildRightPanel(stockData);
        rightPanel.setPrefWidth(390);
        rightPanel.setMinWidth(390);

        root.getChildren().addAll(leftPanel, rightPanel);
        return new Scene(root, 1280, 720);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PANEL IZQUIERDO – Tabla
    // ═══════════════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private static VBox buildLeftPanel(ObservableList<Ingrediente> data) {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: " + DARK_BG + ";");

        // Encabezado
        HBox header = new HBox(12);
        header.setPadding(new Insets(20, 24, 16, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        Label boxIcon = new Label("📦");
        boxIcon.setStyle("-fx-font-size: 24px;");
        Label title = new Label("STOCK ACTUAL");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_W + ";");
        header.getChildren().addAll(boxIcon, title);

        // Tabla
        TableView<Ingrediente> table = new TableView<>(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle(
            "-fx-background-color: " + DARK_BG + ";" +
            "-fx-control-inner-background: " + DARK_BG + ";" +
            "-fx-control-inner-background-alt: #1A1A1A;" +
            "-fx-table-cell-border-color: transparent;"
        );
        VBox.setVgrow(table, Priority.ALWAYS);

        // Columna Nº
        TableColumn<Ingrediente, Integer> colNum = new TableColumn<>("Nº");
        colNum.setCellValueFactory(cd -> cd.getValue().numeroProperty().asObject());
        colNum.setPrefWidth(45);
        colNum.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item == 0) { setText(null); }
                else { setText(String.valueOf(item)); setStyle("-fx-text-fill: " + TEXT_G + "; -fx-alignment: CENTER;"); }
            }
        });

        // Columna INGREDIENTE
        TableColumn<Ingrediente, String> colNombre = new TableColumn<>("INGREDIENTE");
        colNombre.setCellValueFactory(cd -> cd.getValue().nombreProperty());
        colNombre.setPrefWidth(230);
        colNombre.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                Ingrediente ing = getTableRow() != null ? getTableRow().getItem() : null;
                if (ing != null && ing.getNumero() == 0) {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #1A1A1A; -fx-font-size: 12px;");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: " + TEXT_W + ";");
                }
            }
        });

        // Columna CANTIDAD
        TableColumn<Ingrediente, Double> colCant = new TableColumn<>("CANTIDAD");
        colCant.setCellValueFactory(cd -> cd.getValue().cantidadProperty().asObject());
        colCant.setPrefWidth(120);
        colCant.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                Ingrediente ing = getTableRow() != null ? getTableRow().getItem() : null;
                if (empty || item == null || (ing != null && ing.getNumero() == 0)) {
                    setText(null);
                } else {
                    setText(item == Math.floor(item)
                        ? String.format("%,.0f", item)
                        : String.format("%,.3f", item));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-alignment: CENTER;");
                }
            }
        });

        // Columna UND
        TableColumn<Ingrediente, String> colUnd = new TableColumn<>("UND");
        colUnd.setCellValueFactory(cd -> cd.getValue().unidadProperty());
        colUnd.setPrefWidth(70);
        colUnd.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null || item.isBlank()) ? null : item);
                setStyle("-fx-text-fill: " + TEXT_G + "; -fx-alignment: CENTER;");
            }
        });

        // Columna ESTADO
        TableColumn<Ingrediente, String> colEstado = new TableColumn<>("ESTADO");
        colEstado.setCellValueFactory(cd -> cd.getValue().estadoProperty());
        colEstado.setPrefWidth(130);
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isBlank()) { setGraphic(null); setText(null); return; }
                boolean ok = item.equalsIgnoreCase("DISPONIBLE");
                HBox hb = new HBox(6);
                hb.setAlignment(Pos.CENTER_LEFT);
                javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(5);
                dot.setFill(ok ? Color.web(GREEN) : Color.web("#E53935"));
                Label lbl = new Label(item.toUpperCase());
                lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " +
                    (ok ? GREEN : "#E53935") + ";");
                hb.getChildren().addAll(dot, lbl);
                setGraphic(hb);
                setText(null);
            }
        });

        table.getColumns().addAll(colNum, colNombre, colCant, colUnd, colEstado);

        // Row factory para categorías
        table.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Ingrediente item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("-fx-background-color: transparent;");
                } else if (item.getNumero() == 0) {
                    setStyle("-fx-background-color: " + CAT_BG + ";");
                } else {
                    setStyle("-fx-background-color: " + (getIndex() % 2 == 0 ? DARK_BG : "#1A1A1A") + ";");
                }
            }
        });

        // Encabezado tabla oscuro
        table.skinProperty().addListener((obs, old, skin) -> {
            javafx.scene.Node header2 = table.lookup("TableHeaderRow");
            if (header2 != null)
                header2.setStyle("-fx-background-color: #1A1A1A;");
        });

        // Botón atrás
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(12, 24, 14, 24));
        bottom.setStyle("-fx-background-color: " + DARK_BG + ";");
        Button btnBack = new Button("← ATRÁS");
        btnBack.setPrefHeight(42);
        btnBack.setPrefWidth(120);
        btnBack.setStyle("-fx-background-color: " + ORANGE + "; -fx-text-fill: white;" +
            " -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8; -fx-cursor: hand;");
        btnBack.setOnAction(e -> Router.goMenuCajeroView());
        bottom.getChildren().add(btnBack);

        panel.getChildren().addAll(header, table, bottom);
        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PANEL DERECHO – Reponer stock
    // ═══════════════════════════════════════════════════════════════════════════
    private static VBox buildRightPanel(ObservableList<Ingrediente> data) {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: #F5ECD5;");

        // Encabezado
        VBox headerContent = new VBox(5);
        headerContent.setPadding(new Insets(20, 24, 14, 24));
        headerContent.setStyle("-fx-background-color: #F5ECD5;");

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label boxIco = new Label("📦➕");
        boxIco.setStyle("-fx-font-size: 22px;");
        Label repTitle = new Label("REPONER STOCK");
        repTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + ORANGE + ";");
        titleRow.getChildren().addAll(boxIco, repTitle);

        Label subtitle = new Label("Ingresa el nombre del producto y la cantidad a reponer.");
        subtitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");
        subtitle.setWrapText(true);

        headerContent.getChildren().addAll(titleRow, subtitle);

        // Formulario
        VBox form = new VBox(22);
        form.setPadding(new Insets(10, 24, 16, 24));
        form.setStyle("-fx-background-color: #F5ECD5;");
        VBox.setVgrow(form, Priority.ALWAYS);

        TextField productoTF = buildFormTF("Escribe el nombre del producto...");
        VBox productoField = buildFormField("🏷️  Producto:", productoTF);

        TextField cantidadTF = buildFormTF("Ingresa la cantidad...");
        VBox cantidadField = buildFormField("🔢  Cantidad a reponer:", cantidadTF);

        ComboBox<String> unidadCombo = new ComboBox<>();
        unidadCombo.getItems().addAll("gr", "kg", "ml", "l", "und");
        unidadCombo.setPromptText("Selecciona la unidad");
        unidadCombo.setMaxWidth(Double.MAX_VALUE);
        unidadCombo.setPrefHeight(42);
        unidadCombo.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC;" +
            " -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px;");
        VBox unidadField = buildFormField("📏  Unidad (UND):", unidadCombo);

        // Cuadro de ejemplos
        VBox ejemplosBox = new VBox(5);
        ejemplosBox.setPadding(new Insets(12, 14, 12, 14));
        ejemplosBox.setStyle("-fx-background-color: #EAF0FB; -fx-background-radius: 8;" +
            " -fx-border-color: #B3C8EF; -fx-border-width: 1; -fx-border-radius: 8;");
        HBox infoRow = new HBox(8);
        infoRow.setAlignment(Pos.TOP_LEFT);
        Label infoIco = new Label("ℹ️");
        infoIco.setStyle("-fx-font-size: 13px;");
        Label ejemplosLbl = new Label("Ejemplos: Nachos, Carne de birria, Horchata, Jamaica,\nTortilla de maíz, Queso mozzarella...");
        ejemplosLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #3A5A8C;");
        ejemplosLbl.setWrapText(true);
        infoRow.getChildren().addAll(infoIco, ejemplosLbl);
        ejemplosBox.getChildren().add(infoRow);

        form.getChildren().addAll(productoField, cantidadField, unidadField, ejemplosBox);

        // Botón confirmar
        VBox btnBox = new VBox();
        btnBox.setPadding(new Insets(0, 24, 24, 24));
        btnBox.setStyle("-fx-background-color: #F5ECD5;");
        Button btnConfirm = new Button("✔  CONFIRMAR REPONER STOCK");
        btnConfirm.setPrefHeight(50);
        btnConfirm.setMaxWidth(Double.MAX_VALUE);
        btnConfirm.setStyle("-fx-background-color: " + GREEN + "; -fx-text-fill: white;" +
            " -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");

        btnConfirm.setOnAction(e -> {
            String nombreInput = productoTF.getText().trim();
            String cantidadInput = cantidadTF.getText().trim();
            if (nombreInput.isEmpty() || cantidadInput.isEmpty() || unidadCombo.getValue() == null) return;
            try {
                double cant = Double.parseDouble(cantidadInput);
                for (Ingrediente ing : data) {
                    if (ing.getNombre().equalsIgnoreCase(nombreInput)) {
                        ing.setCantidad(ing.getCantidad() + cant);
                        break;
                    }
                }
                productoTF.clear();
                cantidadTF.clear();
                unidadCombo.setValue(null);
            } catch (NumberFormatException ex) { /* ignorar */ }
        });

        btnBox.getChildren().add(btnConfirm);

        panel.getChildren().addAll(headerContent, form, btnBox);
        return panel;
    }

    private static VBox buildFormField(String label, javafx.scene.Node input) {
        VBox box = new VBox(8);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        box.getChildren().addAll(lbl, input);
        return box;
    }

    private static TextField buildFormTF(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefHeight(42);
        tf.setMaxWidth(Double.MAX_VALUE);
        tf.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC;" +
            " -fx-border-radius: 8; -fx-background-radius: 8;" +
            " -fx-padding: 0 12 0 12; -fx-font-size: 13px;" +
            " -fx-text-fill: #333333; -fx-prompt-text-fill: #AAAAAA;");
        return tf;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DATOS
    // ═══════════════════════════════════════════════════════════════════════════
    private static ObservableList<Ingrediente> buildStockData() {
        return FXCollections.observableArrayList(
            new Ingrediente(0, "🍟  SNACKS",                    0,       "",   ""),
            new Ingrediente(1, "Nachos",                     99400,      "gr", "DISPONIBLE"),

            new Ingrediente(0, "🥩  CARNES",                    0,       "",   ""),
            new Ingrediente(2, "Carne chilli",              199000,     "gr", "DISPONIBLE"),
            new Ingrediente(3, "Carne de birria",           299740,     "gr", "DISPONIBLE"),
            new Ingrediente(4, "Carne de cerdo al pastor",  139520,     "gr", "DISPONIBLE"),
            new Ingrediente(5, "Lengua de res",             149640,     "gr", "DISPONIBLE"),

            new Ingrediente(0, "🥑  VERDURAS Y FRUTAS",         0,       "",   ""),
            new Ingrediente(6, "Guacamole",                  99590,     "gr", "DISPONIBLE"),
            new Ingrediente(7, "Cebolla y cilantro",         89830,     "gr", "DISPONIBLE"),
            new Ingrediente(8, "Piña picada",                89920,     "gr", "DISPONIBLE"),

            new Ingrediente(0, "🧀  LÁCTEOS",                   0,       "",   ""),
            new Ingrediente(9, "Queso cheddar líquido",      99700,     "ml", "DISPONIBLE"),
            new Ingrediente(10,"Queso cheddar mozzarella",  149840,     "gr", "DISPONIBLE"),
            new Ingrediente(11,"Queso mozzarella",          150000,     "gr", "DISPONIBLE"),

            new Ingrediente(0, "🫓  MASAS Y TORTILLAS",         0,       "",   ""),
            new Ingrediente(12,"Tortilla de harina XL",       9996,    "und", "DISPONIBLE"),
            new Ingrediente(13,"Tortilla de maíz",           48983,    "und", "DISPONIBLE"),
            new Ingrediente(14,"Tortilla de harina",         30000,    "und", "DISPONIBLE"),
            new Ingrediente(15,"Masa de pizza",             200000,     "gr", "DISPONIBLE"),

            new Ingrediente(0, "🍲  CALDOS Y COCIDOS",          0,       "",   ""),
            new Ingrediente(16,"Arroz cocido",               199760,    "gr", "DISPONIBLE"),
            new Ingrediente(17,"Fideo ramen (cocido)",       199850,    "gr", "DISPONIBLE"),
            new Ingrediente(18,"Caldo de res",               299650,    "ml", "DISPONIBLE"),
            new Ingrediente(19,"Caldo de cocción",           199940,    "ml", "DISPONIBLE"),

            new Ingrediente(0, "🥤  BEBIDAS",                   0,       "",   ""),
            new Ingrediente(20,"Horchata",                   499500,    "ml", "DISPONIBLE"),
            new Ingrediente(21,"Jamaica",                       510,    "ml", "DISPONIBLE")
        );
    }
}
