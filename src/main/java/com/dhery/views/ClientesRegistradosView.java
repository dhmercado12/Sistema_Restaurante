package com.dhery.views;

import com.dhery.app.Router;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ClientesRegistradosView {

    // ── Modelo ────────────────────────────────────────────────────────────────
    public static class Cliente {
        private final IntegerProperty numero  = new SimpleIntegerProperty();
        private final StringProperty  nombre  = new SimpleStringProperty();
        private final StringProperty  celular = new SimpleStringProperty();

        public Cliente(int n, String nombre, String celular) {
            this.numero.set(n); this.nombre.set(nombre); this.celular.set(celular);
        }
        public IntegerProperty numeroProperty()  { return numero;  }
        public StringProperty  nombreProperty()  { return nombre;  }
        public StringProperty  celularProperty() { return celular; }
        public String getNombre()  { return nombre.get();  }
        public String getCelular() { return celular.get(); }
    }

    private static final String RED      = "#CC0000";
    private static final String RED_DARK = "#AA0000";
    private static final String TEXT_D   = "#1A1A1A";
    private static final String TEXT_G   = "#777777";
    private static final String ROW_ALT  = "#FAFAFA";
    private static final int    PAGE_SIZE = 15;

    private static ObservableList<Cliente> allClientes;
    private static ObservableList<Cliente> filteredClientes;
    private static ObservableList<Cliente> pageClientes;
    private static int currentPage = 0;
    private static Label lblPaginacion;
    private static HBox paginacionBox;
    private static Label lblTotal;

    @SuppressWarnings("unchecked")
    public static Scene getScene() {
        currentPage    = 0;
        allClientes    = buildSampleClientes();
        filteredClientes = FXCollections.observableArrayList(allClientes);
        pageClientes   = FXCollections.observableArrayList();
        refreshPage();

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: white;");

        // ── Encabezado ──
        HBox header = new HBox(14);
        header.setPadding(new Insets(28, 28, 14, 28));
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane iconCircle = makeIconCircle("👥");
        VBox titleGroup = new VBox(3);
        Label title = new Label("CLIENTES REGISTRADOS");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        Label sub = new Label("Consulta la lista de todos los clientes registrados en el sistema");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_G + ";");
        titleGroup.getChildren().addAll(title, sub);
        header.getChildren().addAll(iconCircle, titleGroup);

        // ── Barra de búsqueda + total ──
        HBox searchBar = new HBox(12);
        searchBar.setPadding(new Insets(0, 28, 14, 28));
        searchBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Buscar cliente por nombre o celular...");
        searchField.setPrefWidth(320);
        searchField.setPrefHeight(40);
        searchField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #DDDDDD;" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-padding: 0 12 0 12; -fx-font-size: 13px;" +
            "-fx-prompt-text-fill: #AAAAAA;"
        );

        // Filtro en tiempo real
        searchField.textProperty().addListener((obs, old, val) -> {
            String q = val.toLowerCase().trim();
            if (q.isEmpty()) {
                filteredClientes.setAll(allClientes);
            } else {
                filteredClientes.setAll(allClientes.stream()
                    .filter(c -> c.getNombre().toLowerCase().contains(q)
                              || c.getCelular().contains(q))
                    .collect(java.util.stream.Collectors.toList()));
            }
            currentPage = 0;
            refreshPage();
            buildPageButtons();
            updatePaginacionLabel();
            if (lblTotal != null)
                lblTotal.setText("Total de clientes: " + filteredClientes.size());
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox totalBox = new HBox(8);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        Label icoTotal = new Label("👥");
        icoTotal.setStyle("-fx-font-size: 16px; -fx-text-fill: " + RED + ";");
        lblTotal = new Label("Total de clientes: " + allClientes.size());
        lblTotal.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        totalBox.getChildren().addAll(icoTotal, lblTotal);

        searchBar.getChildren().addAll(searchField, spacer, totalBox);

        // ── Tabla ──
        TableView<Cliente> table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        // ── Paginación ──
        HBox pagRow = buildPaginacionRow();

        // ── Botón Atrás ──
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(12, 28, 16, 28));
        Button btnBack = redOutlineButton("← ATRÁS");
        btnBack.setOnAction(e -> Router.goMenuCajeroView());
        bottom.getChildren().add(btnBack);

        root.getChildren().addAll(header, searchBar, table, pagRow, bottom);
        return new Scene(root, 1280, 720);
    }

    @SuppressWarnings("unchecked")
    private static TableView<Cliente> buildTable() {
        TableView<Cliente> table = new TableView<>(pageClientes);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(46);
        table.setStyle(
            "-fx-background-color: white;" +
            "-fx-control-inner-background: white;" +
            "-fx-control-inner-background-alt: " + ROW_ALT + ";" +
            "-fx-table-cell-border-color: #EEEEEE;"
        );

        // N°
        TableColumn<Cliente, Integer> colN = new TableColumn<>("N°");
        colN.setCellValueFactory(cd -> cd.getValue().numeroProperty().asObject());
        colN.setPrefWidth(70);
        colN.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                StackPane sp = new StackPane();
                Circle c = new Circle(14); c.setFill(Color.web("#FFEAEA"));
                Label l = new Label(item.toString());
                l.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");
                sp.getChildren().addAll(c, l); setGraphic(sp); setText(null);
            }
        });

        // NOMBRE DEL CLIENTE
        TableColumn<Cliente, String> colNombre = new TableColumn<>("NOMBRE DEL CLIENTE");
        colNombre.setCellValueFactory(cd -> cd.getValue().nombreProperty());
        colNombre.setPrefWidth(600);
        colNombre.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                HBox hb = new HBox(10); hb.setAlignment(Pos.CENTER_LEFT);
                Label ico = new Label("👤"); ico.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
                Label lbl = new Label(item);
                lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
                hb.getChildren().addAll(ico, lbl); setGraphic(hb); setText(null);
            }
        });

        // NÚMERO DE CELULAR
        TableColumn<Cliente, String> colCel = new TableColumn<>("NÚMERO DE CELULAR");
        colCel.setCellValueFactory(cd -> cd.getValue().celularProperty());
        colCel.setPrefWidth(300);
        colCel.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                HBox hb = new HBox(8); hb.setAlignment(Pos.CENTER_LEFT);
                Label ico = new Label("📞"); ico.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
                Label lbl = new Label(item); lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_D + ";");
                hb.getChildren().addAll(ico, lbl); setGraphic(hb); setText(null);
            }
        });

        table.getColumns().addAll(colN, colNombre, colCel);

        // Filas alternas
        table.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setStyle("-fx-background-color: white;");
                else setStyle("-fx-background-color: " + (getIndex() % 2 == 0 ? "white" : ROW_ALT) + ";");
            }
        });

        // Header rojo
        table.skinProperty().addListener((obs, o, skin) -> {
            javafx.scene.Node h = table.lookup("TableHeaderRow");
            if (h != null) h.setStyle("-fx-background-color: " + RED + ";");
            table.lookupAll(".column-header .label").forEach(n -> {
                if (n instanceof Label l)
                    l.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
            });
        });

        return table;
    }

    private static HBox buildPaginacionRow() {
        HBox row = new HBox(12);
        row.setPadding(new Insets(10, 28, 6, 28));
        row.setAlignment(Pos.CENTER_LEFT);

        lblPaginacion = new Label();
        lblPaginacion.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_G + ";");
        updatePaginacionLabel();

        paginacionBox = new HBox(6);
        paginacionBox.setAlignment(Pos.CENTER);
        buildPageButtons();

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        row.getChildren().addAll(lblPaginacion, sp, paginacionBox);
        return row;
    }

    private static void buildPageButtons() {
        paginacionBox.getChildren().clear();
        int totalPages = (int) Math.ceil((double) filteredClientes.size() / PAGE_SIZE);
        if (totalPages <= 1) return;

        Button prev = pageNavBtn("←");
        prev.setDisable(currentPage == 0);
        prev.setOnAction(e -> { currentPage--; refreshPage(); buildPageButtons(); updatePaginacionLabel(); });
        paginacionBox.getChildren().add(prev);

        for (int i = 0; i < totalPages; i++) {
            if (i > 3 && i < totalPages - 1) {
                if (i == 4) paginacionBox.getChildren().add(new Label("..."));
                continue;
            }
            final int pg = i;
            Button btn = pageBtn(String.valueOf(i + 1), i == currentPage);
            btn.setOnAction(e -> { currentPage = pg; refreshPage(); buildPageButtons(); updatePaginacionLabel(); });
            paginacionBox.getChildren().add(btn);
        }

        Button next = pageNavBtn("→");
        next.setDisable(currentPage >= totalPages - 1);
        next.setOnAction(e -> { currentPage++; refreshPage(); buildPageButtons(); updatePaginacionLabel(); });
        paginacionBox.getChildren().add(next);
    }

    private static void updatePaginacionLabel() {
        if (lblPaginacion == null) return;
        int from = currentPage * PAGE_SIZE + 1;
        int to   = Math.min(from + PAGE_SIZE - 1, filteredClientes.size());
        if (filteredClientes.isEmpty()) lblPaginacion.setText("Sin resultados");
        else lblPaginacion.setText("Mostrando " + from + " a " + to + " de " + filteredClientes.size() + " clientes");
    }

    private static void refreshPage() {
        pageClientes.clear();
        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, filteredClientes.size());
        if (from < filteredClientes.size()) pageClientes.addAll(filteredClientes.subList(from, to));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private static StackPane makeIconCircle(String emoji) {
        StackPane sp = new StackPane();
        Circle bg = new Circle(32); bg.setFill(Color.web("#FFEAEA"));
        Label lbl = new Label(emoji); lbl.setStyle("-fx-font-size: 22px;");
        sp.getChildren().addAll(bg, lbl); sp.setMaxSize(64, 64);
        return sp;
    }

    private static Button redOutlineButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(40);
        String normal = "-fx-background-color: white; -fx-text-fill: " + RED + ";" +
            " -fx-border-color: " + RED + "; -fx-border-width: 1.5; -fx-border-radius: 8;" +
            " -fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand;";
        String hover  = "-fx-background-color: #FFF5F5; -fx-text-fill: " + RED_DARK + ";" +
            " -fx-border-color: " + RED_DARK + "; -fx-border-width: 1.5; -fx-border-radius: 8;" +
            " -fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
        return btn;
    }

    private static Button pageBtn(String text, boolean active) {
        Button btn = new Button(text); btn.setPrefSize(34, 34);
        btn.setStyle(active
            ? "-fx-background-color: " + RED + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"
            : "-fx-background-color: white; -fx-text-fill: " + TEXT_D + "; -fx-border-color: #DDD; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        return btn;
    }

    private static Button pageNavBtn(String text) {
        Button btn = new Button(text); btn.setPrefSize(34, 34);
        btn.setStyle("-fx-background-color: white; -fx-text-fill: " + RED + ";" +
            " -fx-border-color: #DDD; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        return btn;
    }

    // ── Datos de muestra ─────────────────────────────────────────────────────
    private static ObservableList<Cliente> buildSampleClientes() {
        return FXCollections.observableArrayList(
            new Cliente(1,  "Jhon Alan",        "71234567"),
            new Cliente(2,  "Omar Mirko",        "73098765"),
            new Cliente(3,  "Dhery",             "71567890"),
            new Cliente(4,  "Marcelo",           "70123456"),
            new Cliente(5,  "Alejandra Gomez",   "68901234"),
            new Cliente(6,  "Luis Fernando",     "77700123"),
            new Cliente(7,  "Maria Jose",        "75654321"),
            new Cliente(8,  "Carlos Eduardo",    "70876543"),
            new Cliente(9,  "Daniela Torres",    "74561234"),
            new Cliente(10, "Pedro Antonio",     "76098712"),
            new Cliente(11, "Vanessa Rodriguez", "69912345"),
            new Cliente(12, "Miguel Angel",      "72233445"),
            new Cliente(13, "Sofia Martinez",    "67345678"),
            new Cliente(14, "Juan Pablo",        "74112233"),
            new Cliente(15, "Eliana Gutierrez",  "68877655"),
            new Cliente(16, "Roberto Flores",    "71234890"),
            new Cliente(17, "Carmen Vargas",     "76543210"),
            new Cliente(18, "Diego Mendez",      "70011223"),
            new Cliente(19, "Lucia Perez",       "77889900"),
            new Cliente(20, "Andres Castro",     "68776655")
        );
    }
}
