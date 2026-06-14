package com.dhery.views;

import com.dhery.app.Router;
import com.dhery.models.user;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class StockView {
private static user currentUser;
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

    public static Scene getScene(user user) {
    currentUser = user;
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
                String color;
                if (item.equalsIgnoreCase("DISPONIBLE"))    color = GREEN;
                else if (item.equalsIgnoreCase("BAJO"))     color = "#FFA000";   // Ámbar
                else                                        color = "#E53935";   // Rojo AGOTADO
                HBox hb = new HBox(6);
                hb.setAlignment(Pos.CENTER_LEFT);
                javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(5);
                dot.setFill(Color.web(color));
                Label lbl = new Label(item.toUpperCase());
                lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
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
        btnBack.setOnAction(e -> {
            if (Router.getRole() == Router.Role.ADMINISTRADOR) {
                Router.goAdminDashboardView(Router.getCurrentUser());
            } else {
                Router.goMenuCajeroView(currentUser);
            }
        });
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

        // ── Encabezado ────────────────────────────────────────────────────────
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

        // ── Formulario ────────────────────────────────────────────────────────
        VBox form = new VBox(18);
        form.setPadding(new Insets(10, 24, 16, 24));
        form.setStyle("-fx-background-color: #F5ECD5;");
        VBox.setVgrow(form, Priority.ALWAYS);

        // Campo Producto
        TextField productoTF = buildFormTF("Escribe el nombre del producto...");
        Label errorProductoLbl = buildErrorLabel();
        VBox productoField = buildFormFieldWithError("🏷️  Producto:", productoTF, errorProductoLbl);

        // Campo Cantidad (solo números positivos)
        TextField cantidadTF = buildFormTF("Ingresa la cantidad a reponer...");
        // Bloquear letras: solo dígitos y punto decimal
        cantidadTF.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[0-9]*\\.?[0-9]*")) cantidadTF.setText(oldVal);
        });
        Label errorCantidadLbl = buildErrorLabel();
        VBox cantidadField = buildFormFieldWithError("🔢  Cantidad a reponer:", cantidadTF, errorCantidadLbl);

        // ComboBox Unidad
        ComboBox<String> unidadCombo = new ComboBox<>();
        unidadCombo.getItems().addAll("gr", "kg", "ml", "l", "und");
        unidadCombo.setPromptText("Selecciona la unidad...");
        unidadCombo.setMaxWidth(Double.MAX_VALUE);
        unidadCombo.setPrefHeight(42);
        unidadCombo.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC;" +
            " -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13px;");
        Label errorUnidadLbl = buildErrorLabel();
        VBox unidadField = buildFormFieldWithError("📏  Unidad (UND):", unidadCombo, errorUnidadLbl);

        // Cuadro informativo
        VBox ejemplosBox = new VBox(5);
        ejemplosBox.setPadding(new Insets(12, 14, 12, 14));
        ejemplosBox.setStyle("-fx-background-color: #EAF0FB; -fx-background-radius: 8;" +
            " -fx-border-color: #B3C8EF; -fx-border-width: 1; -fx-border-radius: 8;");
        HBox infoRow = new HBox(8);
        infoRow.setAlignment(Pos.TOP_LEFT);
        Label infoIco = new Label("ℹ️");
        infoIco.setStyle("-fx-font-size: 13px;");
        Label ejemplosLbl = new Label(
            "Ejemplos: Nachos, Carne de birria, Horchata, Jamaica,\n" +
            "Tortilla de maíz, Queso mozzarella...\n" +
            "Cada producto tiene su unidad fija (gr, ml, und).");
        ejemplosLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #3A5A8C;");
        ejemplosLbl.setWrapText(true);
        infoRow.getChildren().addAll(infoIco, ejemplosLbl);
        ejemplosBox.getChildren().add(infoRow);

        form.getChildren().addAll(productoField, cantidadField, unidadField, ejemplosBox);

        // ── Botón confirmar ───────────────────────────────────────────────────
        VBox btnBox = new VBox();
        btnBox.setPadding(new Insets(0, 24, 24, 24));
        btnBox.setStyle("-fx-background-color: #F5ECD5;");
        Button btnConfirm = new Button("✔  CONFIRMAR REPONER STOCK");
        btnConfirm.setPrefHeight(50);
        btnConfirm.setMaxWidth(Double.MAX_VALUE);
        btnConfirm.setStyle("-fx-background-color: " + GREEN + "; -fx-text-fill: white;" +
            " -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");

        btnConfirm.setOnAction(e -> {
            // Limpiar errores previos
            errorProductoLbl.setText("");
            errorCantidadLbl.setText("");
            errorUnidadLbl.setText("");

            String nombreInput   = productoTF.getText().trim();
            String cantidadInput = cantidadTF.getText().trim();
            String unidadSel     = unidadCombo.getValue();
            boolean hayError     = false;

            // ── V1: producto no vacío ─────────────────────────────────────────
            if (nombreInput.isEmpty()) {
                errorProductoLbl.setText("⚠ Debes escribir el nombre del producto.");
                markFieldError(productoTF);
                hayError = true;
            }

            // ── V2: cantidad no vacía y > 0 ───────────────────────────────────
            if (cantidadInput.isEmpty()) {
                errorCantidadLbl.setText("⚠ Debes ingresar una cantidad.");
                markFieldError(cantidadTF);
                hayError = true;
            } else {
                try {
                    double cant = Double.parseDouble(cantidadInput);
                    if (cant <= 0) {
                        errorCantidadLbl.setText("⚠ La cantidad debe ser mayor a 0.");
                        markFieldError(cantidadTF);
                        hayError = true;
                    }
                } catch (NumberFormatException ex) {
                    errorCantidadLbl.setText("⚠ Ingresa solo números.");
                    markFieldError(cantidadTF);
                    hayError = true;
                }
            }

            // ── V3: unidad no vacía ───────────────────────────────────────────
            if (unidadSel == null || unidadSel.isBlank()) {
                errorUnidadLbl.setText("⚠ Debes seleccionar una unidad.");
                hayError = true;
            }

            if (hayError) return;

            // ── V4: producto existe en inventario ─────────────────────────────
            com.dhery.app.AppState.ItemStock itemEncontrado = null;
            for (com.dhery.app.AppState.ItemStock it : com.dhery.app.AppState.inventario) {
                if (it.nombre.trim().equalsIgnoreCase(nombreInput)) {
                    itemEncontrado = it;
                    break;
                }
            }
            if (itemEncontrado == null) {
                errorProductoLbl.setText("⚠ Producto no encontrado. Revisa el nombre.");
                markFieldError(productoTF);
                return;
            }

            // ── V5: unidad correcta para el producto ──────────────────────────
            if (!itemEncontrado.unidad.equalsIgnoreCase(unidadSel)) {
                errorUnidadLbl.setText(
                    "⚠ Unidad incorrecta. \"" + itemEncontrado.nombre +
                    "\" usa: " + itemEncontrado.unidad);
                return;
            }

            // ── Todo válido → reponer ─────────────────────────────────────────
            double cant = Double.parseDouble(cantidadInput);
            itemEncontrado.cantidad += cant;

            // Actualizar la fila en la ObservableList y recalcular estado
            for (Ingrediente ing : data) {
                if (ing.getNombre().trim().equalsIgnoreCase(nombreInput)) {
                    ing.setCantidad(itemEncontrado.cantidad);
                    ing.estadoProperty().set(calcularEstado(itemEncontrado.cantidad));
                    // Forzar refresh de la tabla
                    int idx = data.indexOf(ing);
                    if (idx >= 0) { data.remove(idx); data.add(idx, ing); }
                    break;
                }
            }

            // Limpiar campos
            productoTF.clear();
            cantidadTF.clear();
            unidadCombo.setValue(null);
            clearFieldError(productoTF);
            clearFieldError(cantidadTF);

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Stock actualizado");
            ok.setHeaderText(null);
            ok.setContentText("✅ " + itemEncontrado.nombre + " reponido correctamente.\n" +
                "Nueva cantidad: " + String.format("%.0f", itemEncontrado.cantidad) +
                " " + itemEncontrado.unidad + "  →  " + calcularEstado(itemEncontrado.cantidad));
            ok.showAndWait();
        });

        btnBox.getChildren().add(btnConfirm);

        panel.getChildren().addAll(headerContent, form, btnBox);
        return panel;
    }

    // ── Helpers de formulario ─────────────────────────────────────────────────
    private static Label buildErrorLabel() {
        Label lbl = new Label("");
        lbl.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 11px; -fx-font-weight: bold;");
        lbl.setWrapText(true);
        return lbl;
    }

    private static VBox buildFormFieldWithError(String label, javafx.scene.Node input, Label errorLbl) {
        VBox box = new VBox(5);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        box.getChildren().addAll(lbl, input, errorLbl);
        return box;
    }

    private static void markFieldError(TextField tf) {
        tf.setStyle(tf.getStyle().replace("-fx-border-color: #CCCCCC;", "") +
            " -fx-border-color: #D32F2F; -fx-border-width: 2;");
    }

    private static void clearFieldError(TextField tf) {
        tf.setStyle("-fx-background-color: white; -fx-border-color: #CCCCCC;" +
            " -fx-border-radius: 8; -fx-background-radius: 8;" +
            " -fx-padding: 0 12 0 12; -fx-font-size: 13px;" +
            " -fx-text-fill: #333333; -fx-prompt-text-fill: #AAAAAA;");
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
    /** Calcula el estado de un ítem según su cantidad */
    private static String calcularEstado(double cantidad) {
        if (cantidad <= 0) return "AGOTADO";
        if (cantidad < com.dhery.app.AppState.STOCK_BAJO_UMBRAL) return "BAJO";
        return "DISPONIBLE";
    }

    /**
     * Construye la lista de ingredientes leyendo desde AppState.inventario (datos en vivo).
     * Las categorías se insertan como filas separadoras con numero=0.
     */
    private static ObservableList<Ingrediente> buildStockData() {
        java.util.List<com.dhery.app.AppState.ItemStock> inv = com.dhery.app.AppState.inventario;

        // Mapa nombre → ItemStock para búsqueda rápida
        java.util.Map<String, com.dhery.app.AppState.ItemStock> mapa = new java.util.LinkedHashMap<>();
        for (com.dhery.app.AppState.ItemStock it : inv) mapa.put(it.nombre, it);

        ObservableList<Ingrediente> lista = FXCollections.observableArrayList();
        int num = 1;

        // ── SNACKS ────────────────────────────────────────────────────────────
        lista.add(new Ingrediente(0, "🍟  SNACKS", 0, "", ""));
        for (String n : new String[]{"Nachos"}) {
            com.dhery.app.AppState.ItemStock it = mapa.get(n);
            if (it != null) lista.add(new Ingrediente(num++, it.nombre, it.cantidad, it.unidad, calcularEstado(it.cantidad)));
        }

        // ── CARNES ───────────────────────────────────────────────────────────
        lista.add(new Ingrediente(0, "🥩  CARNES", 0, "", ""));
        for (String n : new String[]{"Carne chilli","Carne de birria","Carne de cerdo al pastor","Lengua de res"}) {
            com.dhery.app.AppState.ItemStock it = mapa.get(n);
            if (it != null) lista.add(new Ingrediente(num++, it.nombre, it.cantidad, it.unidad, calcularEstado(it.cantidad)));
        }

        // ── VERDURAS Y FRUTAS ─────────────────────────────────────────────────
        lista.add(new Ingrediente(0, "🥑  VERDURAS Y FRUTAS", 0, "", ""));
        for (String n : new String[]{"Guacamole","Cebolla y cilantro","Piña picada"}) {
            com.dhery.app.AppState.ItemStock it = mapa.get(n);
            if (it != null) lista.add(new Ingrediente(num++, it.nombre, it.cantidad, it.unidad, calcularEstado(it.cantidad)));
        }

        // ── LÁCTEOS ───────────────────────────────────────────────────────────
        lista.add(new Ingrediente(0, "🧀  LÁCTEOS", 0, "", ""));
        for (String n : new String[]{"Queso cheddar líquido","Queso cheddar mozzarella","Queso mozzarella"}) {
            com.dhery.app.AppState.ItemStock it = mapa.get(n);
            if (it != null) lista.add(new Ingrediente(num++, it.nombre, it.cantidad, it.unidad, calcularEstado(it.cantidad)));
        }

        // ── MASAS Y TORTILLAS ─────────────────────────────────────────────────
        lista.add(new Ingrediente(0, "🫓  MASAS Y TORTILLAS", 0, "", ""));
        for (String n : new String[]{"Tortilla de harina XL","Tortilla de maíz","Tortilla de harina","Masa de pizza"}) {
            com.dhery.app.AppState.ItemStock it = mapa.get(n);
            if (it != null) lista.add(new Ingrediente(num++, it.nombre, it.cantidad, it.unidad, calcularEstado(it.cantidad)));
        }

        // ── CALDOS Y COCIDOS ─────────────────────────────────────────────────
        lista.add(new Ingrediente(0, "🍲  CALDOS Y COCIDOS", 0, "", ""));
        for (String n : new String[]{"Arroz cocido","Fideo ramen (cocido)","Caldo de res","Caldo de cocción"}) {
            com.dhery.app.AppState.ItemStock it = mapa.get(n);
            if (it != null) lista.add(new Ingrediente(num++, it.nombre, it.cantidad, it.unidad, calcularEstado(it.cantidad)));
        }

        // ── BEBIDAS ───────────────────────────────────────────────────────────
        lista.add(new Ingrediente(0, "🥤  BEBIDAS", 0, "", ""));
        for (String n : new String[]{"Horchata","Jamaica"}) {
            com.dhery.app.AppState.ItemStock it = mapa.get(n);
            if (it != null) lista.add(new Ingrediente(num++, it.nombre, it.cantidad, it.unidad, calcularEstado(it.cantidad)));
        }

        return lista;
    }
}