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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialVentasView {

    // ── Modelo de Venta ───────────────────────────────────────────────────────
    public static class Venta {
        private final IntegerProperty numero   = new SimpleIntegerProperty();
        private final StringProperty  fecha    = new SimpleStringProperty();
        private final StringProperty  hora     = new SimpleStringProperty();
        private final StringProperty  cliente  = new SimpleStringProperty();
        private final DoubleProperty  total    = new SimpleDoubleProperty();
        private final IntegerProperty tiempo   = new SimpleIntegerProperty();
        private final StringProperty  detalle  = new SimpleStringProperty();

        public Venta(int n, String fecha, String hora, String cliente, double total, int tiempo, String detalle) {
            this.numero.set(n); this.fecha.set(fecha); this.hora.set(hora);
            this.cliente.set(cliente); this.total.set(total);
            this.tiempo.set(tiempo); this.detalle.set(detalle);
        }
        public IntegerProperty numeroProperty()  { return numero;  }
        public StringProperty  fechaProperty()   { return fecha;   }
        public StringProperty  horaProperty()    { return hora;    }
        public StringProperty  clienteProperty() { return cliente; }
        public DoubleProperty  totalProperty()   { return total;   }
        public IntegerProperty tiempoProperty()  { return tiempo;  }
        public StringProperty  detalleProperty() { return detalle; }
        public int    getNumero()  { return numero.get();  }
        public String getFecha()   { return fecha.get();   }
        public String getHora()    { return hora.get();    }
        public String getCliente() { return cliente.get(); }
        public double getTotal()   { return total.get();   }
        public int    getTiempo()  { return tiempo.get();  }
        public String getDetalle() { return detalle.get(); }
    }

    // Colores del tema (blanco/rojo como la imagen)
    private static final String RED      = "#CC0000";
    private static final String RED_DARK = "#AA0000";
    private static final String BG       = "#FFFFFF";
    private static final String GRAY_BG  = "#F8F8F8";
    private static final String TEXT_D   = "#1A1A1A";
    private static final String TEXT_G   = "#777777";
    private static final String ROW_ALT  = "#FAFAFA";

    // Paginación
    private static final int PAGE_SIZE = 15;
    private static int currentPage = 0;

    // Datos completos y filtrados
    private static ObservableList<Venta> allVentas;
    private static ObservableList<Venta> filteredVentas;
    private static ObservableList<Venta> pageVentas;

    // Labels del resumen (para actualizar)
    private static Label lblTotalVentas, lblVentasTotales, lblTicketProm, lblTiempoProm, lblFechaRep;
    private static Label lblPaginacion;
    private static HBox paginacionBox;

    @SuppressWarnings("unchecked")
    public static Scene getScene() {
        currentPage = 0;
        allVentas      = buildSampleVentas();
        filteredVentas = FXCollections.observableArrayList(allVentas);
        pageVentas     = FXCollections.observableArrayList();
        refreshPage();

        // ── ROOT principal ────────────────────────────────────────────────────
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color: " + BG + ";");

        // Panel izquierdo (tabla + filtros superiores)
        VBox leftPanel = buildLeftPanel();
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        // Panel derecho (filtros + resumen)
        VBox rightPanel = buildRightPanel();
        rightPanel.setPrefWidth(310);
        rightPanel.setMinWidth(310);

        root.getChildren().addAll(leftPanel, rightPanel);
        return new Scene(root, 1280, 720);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PANEL IZQUIERDO
    // ═══════════════════════════════════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private static VBox buildLeftPanel() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: " + BG + ";");

        // ── Encabezado ──
        HBox header = new HBox(14);
        header.setPadding(new Insets(28, 28, 10, 28));
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane iconCircle = makeIconCircle("📊");
        VBox titleGroup = new VBox(3);
        Label title = new Label("HISTORIAL DE VENTAS");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        Label sub = new Label("Consulta el historial de todas las ventas realizadas");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_G + ";");
        titleGroup.getChildren().addAll(title, sub);
        header.getChildren().addAll(iconCircle, titleGroup);

        // ── Tabla ──
        TableView<Venta> table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        // ── Paginación ──
        VBox paginacionArea = buildPaginacion(table);

        // ── Botón Atrás ──
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(12, 28, 16, 28));
        Button btnBack = redOutlineButton("← ATRÁS");
        btnBack.setOnAction(e -> Router.goMenuCajeroView());
        bottom.getChildren().add(btnBack);

        panel.getChildren().addAll(header, table, paginacionArea, bottom);
        return panel;
    }

    @SuppressWarnings("unchecked")
    private static TableView<Venta> buildTable() {
        TableView<Venta> table = new TableView<>(pageVentas);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle(
            "-fx-background-color: white;" +
            "-fx-control-inner-background: white;" +
            "-fx-control-inner-background-alt: " + ROW_ALT + ";" +
            "-fx-table-cell-border-color: #EEEEEE;"
        );
        table.setFixedCellSize(46);

        // Columna Nº
        TableColumn<Venta, Integer> colN = new TableColumn<>("N°");
        colN.setCellValueFactory(cd -> cd.getValue().numeroProperty().asObject());
        colN.setPrefWidth(50);
        colN.setCellFactory(col -> numCell());

        // Columna FECHA
        TableColumn<Venta, String> colFecha = new TableColumn<>("FECHA");
        colFecha.setCellValueFactory(cd -> cd.getValue().fechaProperty());
        colFecha.setPrefWidth(130);
        colFecha.setCellFactory(col -> iconTextCell("📅", false));

        // Columna HORA
        TableColumn<Venta, String> colHora = new TableColumn<>("HORA");
        colHora.setCellValueFactory(cd -> cd.getValue().horaProperty());
        colHora.setPrefWidth(90);
        colHora.setCellFactory(col -> iconTextCell("🕐", false));

        // Columna CLIENTE
        TableColumn<Venta, String> colCliente = new TableColumn<>("CLIENTE");
        colCliente.setCellValueFactory(cd -> cd.getValue().clienteProperty());
        colCliente.setPrefWidth(130);
        colCliente.setCellFactory(col -> plainTextCell());

        // Columna TOTAL
        TableColumn<Venta, Double> colTotal = new TableColumn<>("TOTAL (Bs)");
        colTotal.setCellValueFactory(cd -> cd.getValue().totalProperty().asObject());
        colTotal.setPrefWidth(110);
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(String.format("%.2f", item));
                setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_D + "; -fx-alignment: CENTER-LEFT;");
            }
        });

        // Columna TIEMPO
        TableColumn<Venta, Integer> colTiempo = new TableColumn<>("TIEMPO");
        colTiempo.setCellValueFactory(cd -> cd.getValue().tiempoProperty().asObject());
        colTiempo.setPrefWidth(100);
        colTiempo.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                HBox hb = new HBox(5);
                hb.setAlignment(Pos.CENTER_LEFT);
                Label ico = new Label("🕐");
                ico.setStyle("-fx-font-size: 12px; -fx-text-fill: #E8890C;");
                Label lbl = new Label(item + " min");
                lbl.setStyle("-fx-text-fill: " + TEXT_D + ";");
                hb.getChildren().addAll(ico, lbl);
                setGraphic(hb); setText(null);
            }
        });

        // Columna DETALLE
        TableColumn<Venta, String> colDet = new TableColumn<>("DETALLE");
        colDet.setCellValueFactory(cd -> cd.getValue().detalleProperty());
        colDet.setPrefWidth(90);
        colDet.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Button btn = new Button("👁 Ver");
                btn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: " + RED + ";" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;" +
                    "-fx-font-size: 12px;"
                );
                Venta v = getTableRow() != null ? getTableRow().getItem() : null;
                btn.setOnAction(e -> { if (v != null) mostrarDetalleVenta(v); });
                setGraphic(btn); setText(null);
            }
        });

        // Encabezado rojo
        styleTableHeader(table);

        table.getColumns().addAll(colN, colFecha, colHora, colCliente, colTotal, colTiempo, colDet);

        // Filas alternas
        table.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Venta item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setStyle("-fx-background-color: white;");
                else setStyle("-fx-background-color: " + (getIndex() % 2 == 0 ? "white" : ROW_ALT) + ";");
            }
        });

        return table;
    }

    private static VBox buildPaginacion(TableView<Venta> table) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(10, 28, 6, 28));

        lblPaginacion = new Label();
        lblPaginacion.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_G + ";");
        updatePaginacionLabel();

        paginacionBox = new HBox(6);
        paginacionBox.setAlignment(Pos.CENTER);
        buildPageButtons();

        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        row.getChildren().addAll(lblPaginacion, sp, paginacionBox);
        box.getChildren().add(row);
        return box;
    }

    private static void buildPageButtons() {
        paginacionBox.getChildren().clear();
        int totalPages = (int) Math.ceil((double) filteredVentas.size() / PAGE_SIZE);

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
        int from = currentPage * PAGE_SIZE + 1;
        int to   = Math.min(from + PAGE_SIZE - 1, filteredVentas.size());
        lblPaginacion.setText("Mostrando " + from + " a " + to + " de " + filteredVentas.size() + " registros");
    }

    private static void refreshPage() {
        pageVentas.clear();
        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, filteredVentas.size());
        pageVentas.addAll(filteredVentas.subList(from, to));
        updateResumen();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PANEL DERECHO
    // ═══════════════════════════════════════════════════════════════════════════
    private static VBox buildRightPanel() {
        VBox panel = new VBox(18);
        panel.setPadding(new Insets(24, 20, 20, 20));
        panel.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #EEEEEE;" +
            "-fx-border-width: 0 0 0 1;"
        );

        // ── Filtros ──
        VBox filtrosBox = new VBox(12);
        Label filtrosTitle = sectionTitle("≡ FILTROS");

        Label lblFechaInicio = fieldLabel("Fecha inicial:");
        DatePicker dpInicio = new DatePicker(LocalDate.of(2025, 5, 1));
        dpInicio.setMaxWidth(Double.MAX_VALUE);
        styleDatePicker(dpInicio);

        Label lblFechaFin = fieldLabel("Fecha final:");
        DatePicker dpFin = new DatePicker(LocalDate.of(2025, 5, 24));
        dpFin.setMaxWidth(Double.MAX_VALUE);
        styleDatePicker(dpFin);

        Label lblCliente = fieldLabel("Cliente:");
        ComboBox<String> cbCliente = new ComboBox<>();
        cbCliente.getItems().addAll("Todos los clientes", "jhon alan", "omar mirko", "dhery", "marcelo", "ale");
        cbCliente.setValue("Todos los clientes");
        cbCliente.setMaxWidth(Double.MAX_VALUE);
        styleCombo(cbCliente);

        Label lblMetodo = fieldLabel("Método de pago:");
        ComboBox<String> cbMetodo = new ComboBox<>();
        cbMetodo.getItems().addAll("Todos", "Efectivo", "QR", "Tarjeta");
        cbMetodo.setValue("Todos");
        cbMetodo.setMaxWidth(Double.MAX_VALUE);
        styleCombo(cbMetodo);

        HBox btnsFiltro = new HBox(10);
        Button btnLimpiar = redOutlineButton("↺ LIMPIAR");
        btnLimpiar.setOnAction(e -> {
            dpInicio.setValue(LocalDate.of(2025, 5, 1));
            dpFin.setValue(LocalDate.of(2025, 5, 24));
            cbCliente.setValue("Todos los clientes");
            cbMetodo.setValue("Todos");
            filteredVentas.setAll(allVentas);
            currentPage = 0;
            refreshPage();
            buildPageButtons();
        });

        Button btnFiltrar = redSolidButton("🔍 FILTRAR");
        btnFiltrar.setOnAction(e -> {
            String clienteSel = cbCliente.getValue();
            List<Venta> result = allVentas.stream()
                .filter(v -> clienteSel.equals("Todos los clientes") || v.getCliente().equalsIgnoreCase(clienteSel))
                .collect(Collectors.toList());
            filteredVentas.setAll(result);
            currentPage = 0;
            refreshPage();
            buildPageButtons();
            updatePaginacionLabel();
        });

        HBox.setHgrow(btnLimpiar, Priority.ALWAYS);
        HBox.setHgrow(btnFiltrar, Priority.ALWAYS);
        btnLimpiar.setMaxWidth(Double.MAX_VALUE);
        btnFiltrar.setMaxWidth(Double.MAX_VALUE);
        btnsFiltro.getChildren().addAll(btnLimpiar, btnFiltrar);

        filtrosBox.getChildren().addAll(filtrosTitle, lblFechaInicio, dpInicio,
            lblFechaFin, dpFin, lblCliente, cbCliente, lblMetodo, cbMetodo, btnsFiltro);
        filtrosBox.setStyle(
            "-fx-border-color: #EEEEEE; -fx-border-width: 1;" +
            "-fx-border-radius: 10; -fx-background-radius: 10;" +
            "-fx-padding: 14;"
        );

        // ── Resumen general ──
        VBox resumenBox = new VBox(10);
        Label resTitle = sectionTitle("● RESUMEN GENERAL");

        lblTotalVentas  = new Label("57");
        lblVentasTotales = new Label("Bs 24,189.25");
        lblTicketProm   = new Label("Bs 424.37");
        lblTiempoProm   = new Label("171 min");
        lblFechaRep     = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        resumenBox.getChildren().addAll(
            resTitle,
            resumenRow("📊", "Total de ventas:",   lblTotalVentas),
            resumenRow("💰", "Ventas totales:",    lblVentasTotales),
            resumenRow("🧾", "Ticket promedio:",   lblTicketProm),
            resumenRow("🕐", "Tiempo promedio:",   lblTiempoProm),
            resumenRow("📅", "Fecha del reporte:", lblFechaRep)
        );
        resumenBox.setStyle(
            "-fx-border-color: #EEEEEE; -fx-border-width: 1;" +
            "-fx-border-radius: 10; -fx-background-radius: 10;" +
            "-fx-padding: 14;"
        );

        // ── Exportar ──
        Button btnExportar = new Button("⬇  EXPORTAR REPORTE");
        btnExportar.setMaxWidth(Double.MAX_VALUE);
        btnExportar.setPrefHeight(44);
        btnExportar.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: " + RED + ";" +
            "-fx-font-weight: bold; -fx-font-size: 13px;" +
            "-fx-border-color: " + RED + ";" +
            "-fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        btnExportar.setOnAction(e -> exportarCSV());

        updateResumen();
        panel.getChildren().addAll(filtrosBox, resumenBox, btnExportar);
        return panel;
    }

    // ── Actualizar resumen ────────────────────────────────────────────────────
    private static void updateResumen() {
        if (lblTotalVentas == null) return;
        int total = filteredVentas.size();
        double suma = filteredVentas.stream().mapToDouble(Venta::getTotal).sum();
        double ticket = total > 0 ? suma / total : 0;
        double tiempoProm = filteredVentas.stream().mapToInt(Venta::getTiempo).average().orElse(0);
        lblTotalVentas.setText(String.valueOf(total));
        lblVentasTotales.setText(String.format("Bs %,.2f", suma));
        lblTicketProm.setText(String.format("Bs %.2f", ticket));
        lblTiempoProm.setText(String.format("%.0f min", tiempoProm));
        lblFechaRep.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    // ── Popup detalle venta ───────────────────────────────────────────────────
    private static void mostrarDetalleVenta(Venta v) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Detalle de Venta N° " + v.getNumero());

        VBox content = new VBox(14);
        content.setPadding(new Insets(28));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("DETALLE DE VENTA N° " + v.getNumero());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");

        content.getChildren().addAll(
            title,
            detalleRow("📅 Fecha:",   v.getFecha()),
            detalleRow("🕐 Hora:",    v.getHora()),
            detalleRow("👤 Cliente:", v.getCliente()),
            detalleRow("💰 Total:",   String.format("Bs %.2f", v.getTotal())),
            detalleRow("⏱ Tiempo:",   v.getTiempo() + " min"),
            detalleRow("📝 Detalle:", v.getDetalle())
        );

        Button btnCerrar = redSolidButton("Cerrar");
        btnCerrar.setOnAction(e -> popup.close());
        content.getChildren().add(btnCerrar);

        popup.setScene(new Scene(content, 400, 380));
        popup.show();
    }

    private static HBox detalleRow(String label, String value) {
        HBox row = new HBox(10);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #555; -fx-min-width: 100;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + TEXT_D + ";");
        val.setWrapText(true);
        row.getChildren().addAll(lbl, val);
        return row;
    }

    // ── Exportar CSV ─────────────────────────────────────────────────────────
    private static void exportarCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append("N°,Fecha,Hora,Cliente,Total (Bs),Tiempo (min),Detalle\n");
        for (Venta v : filteredVentas) {
            sb.append(v.getNumero()).append(",")
              .append(v.getFecha()).append(",")
              .append(v.getHora()).append(",")
              .append(v.getCliente()).append(",")
              .append(String.format("%.2f", v.getTotal())).append(",")
              .append(v.getTiempo()).append(",")
              .append(v.getDetalle()).append("\n");
        }
        try {
            java.io.File f = new java.io.File("historial_ventas.csv");
            java.io.FileWriter fw = new java.io.FileWriter(f);
            fw.write(sb.toString());
            fw.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Reporte exportado: " + f.getAbsolutePath(), ButtonType.OK);
            alert.setHeaderText("Exportación exitosa");
            alert.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HELPERS DE ESTILO
    // ═══════════════════════════════════════════════════════════════════════════
    private static StackPane makeIconCircle(String emoji) {
        StackPane sp = new StackPane();
        Circle bg = new Circle(32);
        bg.setFill(Color.web("#FFEAEA"));
        Label lbl = new Label(emoji);
        lbl.setStyle("-fx-font-size: 22px;");
        sp.getChildren().addAll(bg, lbl);
        sp.setMaxSize(64, 64);
        return sp;
    }

    private static void styleTableHeader(TableView<?> table) {
        table.skinProperty().addListener((obs, o, skin) -> {
            javafx.scene.Node h = table.lookup("TableHeaderRow");
            if (h != null) h.setStyle("-fx-background-color: " + RED + ";");
            table.lookupAll(".column-header").forEach(n ->
                n.setStyle("-fx-background-color: " + RED + "; -fx-text-fill: white;")
            );
            table.lookupAll(".column-header .label").forEach(n -> {
                if (n instanceof Label l) {
                    l.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
                }
            });
        });
    }

    private static <T> TableCell<Venta, T> numCell() {
        return new TableCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                StackPane sp = new StackPane();
                Circle c = new Circle(14);
                c.setFill(Color.web("#FFEAEA"));
                Label l = new Label(item.toString());
                l.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");
                sp.getChildren().addAll(c, l);
                setGraphic(sp); setText(null);
            }
        };
    }

    private static <T> TableCell<Venta, T> iconTextCell(String icon, boolean bold) {
        return new TableCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                HBox hb = new HBox(5); hb.setAlignment(Pos.CENTER_LEFT);
                Label ico = new Label(icon); ico.setStyle("-fx-font-size: 12px; -fx-text-fill: " + RED + ";");
                Label lbl = new Label(item.toString());
                lbl.setStyle("-fx-text-fill: " + TEXT_D + ";" + (bold ? "-fx-font-weight: bold;" : ""));
                hb.getChildren().addAll(ico, lbl); setGraphic(hb); setText(null);
            }
        };
    }

    private static <T> TableCell<Venta, T> plainTextCell() {
        return new TableCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                setStyle("-fx-text-fill: " + TEXT_D + ";");
            }
        };
    }

    private static HBox resumenRow(String icon, String label, Label valueLabel) {
        HBox row = new HBox(8); row.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label(icon); ico.setStyle("-fx-font-size: 13px; -fx-text-fill: " + RED + ";");
        Label lbl = new Label(label); lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        valueLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        row.getChildren().addAll(ico, lbl, sp, valueLabel);
        return row;
    }

    private static Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        return l;
    }

    private static Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        return l;
    }

    private static void styleDatePicker(DatePicker dp) {
        dp.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;" +
            "-fx-border-color: #DDDDDD; -fx-font-size: 13px;");
    }

    private static void styleCombo(ComboBox<?> cb) {
        cb.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;" +
            "-fx-border-color: #DDDDDD; -fx-font-size: 13px;");
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

    private static Button redSolidButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(40);
        String normal = "-fx-background-color: " + RED + "; -fx-text-fill: white;" +
            " -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";
        String hover  = "-fx-background-color: " + RED_DARK + "; -fx-text-fill: white;" +
            " -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
        return btn;
    }

    private static Button pageBtn(String text, boolean active) {
        Button btn = new Button(text);
        btn.setPrefSize(34, 34);
        btn.setStyle(active
            ? "-fx-background-color: " + RED + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-font-weight: bold; -fx-cursor: hand;"
            : "-fx-background-color: white; -fx-text-fill: " + TEXT_D + "; -fx-border-color: #DDD; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        return btn;
    }

    private static Button pageNavBtn(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(34, 34);
        btn.setStyle("-fx-background-color: white; -fx-text-fill: " + RED + ";" +
            " -fx-border-color: #DDD; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        return btn;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DATOS DE MUESTRA
    // ═══════════════════════════════════════════════════════════════════════════
    private static ObservableList<Venta> buildSampleVentas() {
        return FXCollections.observableArrayList(
            new Venta(1,  "24/05/2025","14:35","jhon alan", 45.50, 4,  "Birria x2, Jamaica x1"),
            new Venta(2,  "24/05/2025","14:28","jhon alan",  8.00, 0,  "Jamaica x1"),
            new Venta(3,  "24/05/2025","14:16","jhon alan", 98.75,12,  "Megaburrito x2, Ramen Birria x1"),
            new Venta(4,  "24/05/2025","14:12","jhon alan", 41.00, 4,  "Quesabirria x1, Horchata x1"),
            new Venta(5,  "24/05/2025","13:45","jhon alan",23000.00,3000,"Pedido especial grande"),
            new Venta(6,  "24/05/2025","13:39","jhon alan", 40.00, 6,  "Suadero x2"),
            new Venta(7,  "24/05/2025","13:21","jhon alan", 45.00, 4,  "Birria x2, Nachos x1"),
            new Venta(8,  "23/05/2025","12:05","omar mirko",13.00, 0,  "Jamaica x1, Tortilla x1"),
            new Venta(9,  "23/05/2025","11:58","dhery",     30.00, 6,  "Pastor x1, Suadero x1"),
            new Venta(10, "23/05/2025","11:40","jhon alan", 45.00, 0,  "Megaburrito x1"),
            new Venta(11, "23/05/2025","11:15","marcelo",   50.00, 4,  "Ramen Birria x1, Horchata x1"),
            new Venta(12, "23/05/2025","10:50","jhon alan",138.00,21,  "Combo familiar"),
            new Venta(13, "22/05/2025","10:45","ale",       45.00, 5,  "Birria x2"),
            new Venta(14, "22/05/2025","10:30","ale",       15.00, 4,  "Nachos x1"),
            new Venta(15, "22/05/2025","10:12","dhery",    110.00,21,  "Nachos Combo x2"),
            new Venta(16, "22/05/2025","09:50","marcelo",   35.00, 5,  "Birria x1"),
            new Venta(17, "22/05/2025","09:30","jhon alan", 90.00, 8,  "Quesabirria x2, Jamaica x1"),
            new Venta(18, "21/05/2025","15:00","ale",       55.00,10,  "Megaburrito Combo"),
            new Venta(19, "21/05/2025","14:30","dhery",     30.00, 5,  "Pastor x1"),
            new Venta(20, "21/05/2025","13:00","omar mirko",45.00, 6,  "Birria x2")
        );
    }
}
