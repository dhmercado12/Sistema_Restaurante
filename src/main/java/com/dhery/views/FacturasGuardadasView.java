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
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FacturasGuardadasView {
private static user currentUser;
    // ── Modelo ────────────────────────────────────────────────────────────────
    public static class Factura {
        private final IntegerProperty numero    = new SimpleIntegerProperty();
        private final StringProperty  nombre    = new SimpleStringProperty();
        private final StringProperty  fecha     = new SimpleStringProperty();
        private final StringProperty  tamanio   = new SimpleStringProperty();
        private final StringProperty  rutaReal  = new SimpleStringProperty();

        public Factura(int n, String nombre, String fecha, String tamanio, String ruta) {
            this.numero.set(n); this.nombre.set(nombre); this.fecha.set(fecha);
            this.tamanio.set(tamanio); this.rutaReal.set(ruta);
        }
        public IntegerProperty numeroProperty()   { return numero;   }
        public StringProperty  nombreProperty()   { return nombre;   }
        public StringProperty  fechaProperty()    { return fecha;    }
        public StringProperty  tamanioProperty()  { return tamanio;  }
        public StringProperty  rutaRealProperty() { return rutaReal; }
        public int    getNumero()   { return numero.get();   }
        public String getNombre()   { return nombre.get();   }
        public String getFecha()    { return fecha.get();    }
        public String getTamanio()  { return tamanio.get();  }
        public String getRutaReal() { return rutaReal.get(); }
    }

    private static final String RED      = "#CC0000";
    private static final String RED_DARK = "#AA0000";
    private static final String TEXT_D   = "#1A1A1A";
    private static final String TEXT_G   = "#777777";
    private static final String ROW_ALT  = "#FAFAFA";
    private static final int    PAGE_SIZE = 15;

    private static ObservableList<Factura> allFacturas;
    private static ObservableList<Factura> filteredFacturas;
    private static ObservableList<Factura> pageFacturas;
    private static int currentPage = 0;
    private static Label lblPaginacion;
    private static HBox paginacionBox;
    private static Label lblTotal;

    @SuppressWarnings("unchecked")
    public static Scene getScene(user user) {
    currentUser = user;
        allFacturas      = loadFacturas();
        filteredFacturas = FXCollections.observableArrayList(allFacturas);
        pageFacturas     = FXCollections.observableArrayList();
        refreshPage();

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: white;");

        // ── Encabezado ──
        HBox header = new HBox(14);
        header.setPadding(new Insets(28, 28, 14, 28));
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane iconCircle = makeIconCircle("📄");
        VBox titleGroup = new VBox(3);
        Label title = new Label("FACTURAS GUARDADAS");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        Label sub = new Label("Consulta y descarga las facturas guardadas en el sistema");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_G + ";");
        titleGroup.getChildren().addAll(title, sub);
        header.getChildren().addAll(iconCircle, titleGroup);

        // ── Barra búsqueda + total ──
        HBox searchBar = new HBox(12);
        searchBar.setPadding(new Insets(0, 28, 14, 28));
        searchBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍  Buscar factura por nombre o fecha...");
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
                filteredFacturas.setAll(allFacturas);
            } else {
                filteredFacturas.setAll(allFacturas.stream()
                    .filter(f -> f.getNombre().toLowerCase().contains(q)
                              || f.getFecha().toLowerCase().contains(q))
                    .collect(Collectors.toList()));
            }
            currentPage = 0;
            refreshPage();
            buildPageButtons();
            updatePaginacionLabel();
            if (lblTotal != null)
                lblTotal.setText("Total de facturas: " + filteredFacturas.size());
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox totalBox = new HBox(8);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        Label icoTotal = new Label("📄");
        icoTotal.setStyle("-fx-font-size: 16px; -fx-text-fill: " + RED + ";");
        lblTotal = new Label("Total de facturas: " + allFacturas.size());
        lblTotal.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        totalBox.getChildren().addAll(icoTotal, lblTotal);

        searchBar.getChildren().addAll(searchField, spacer, totalBox);

        // ── Tabla ──
        TableView<Factura> table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        // ── Paginación ──
        HBox pagRow = buildPaginacionRow();

        // ── Botón Atrás ──
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(12, 28, 16, 28));
        Button btnBack = redOutlineButton("← ATRÁS");
        btnBack.setOnAction(e -> Router.goMenuCajeroView(currentUser));
        bottom.getChildren().add(btnBack);

        root.getChildren().addAll(header, searchBar, table, pagRow, bottom);
        return new Scene(root, 1280, 720);
    }

    @SuppressWarnings("unchecked")
    private static TableView<Factura> buildTable() {
        TableView<Factura> table = new TableView<>(pageFacturas);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(46);
        table.setStyle(
            "-fx-background-color: white;" +
            "-fx-control-inner-background: white;" +
            "-fx-control-inner-background-alt: " + ROW_ALT + ";" +
            "-fx-table-cell-border-color: #EEEEEE;"
        );

        // N°
        TableColumn<Factura, Integer> colN = new TableColumn<>("N°");
        colN.setCellValueFactory(cd -> cd.getValue().numeroProperty().asObject());
        colN.setPrefWidth(60);
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

        // NOMBRE DEL ARCHIVO
        TableColumn<Factura, String> colNombre = new TableColumn<>("NOMBRE DEL ARCHIVO");
        colNombre.setCellValueFactory(cd -> cd.getValue().nombreProperty());
        colNombre.setPrefWidth(330);
        colNombre.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                HBox hb = new HBox(8); hb.setAlignment(Pos.CENTER_LEFT);
                Label ico = new Label("📋"); ico.setStyle("-fx-font-size: 13px; -fx-text-fill: #999;");
                Label lbl = new Label(item); lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_D + ";");
                lbl.setWrapText(false);
                hb.getChildren().addAll(ico, lbl); setGraphic(hb); setText(null);
            }
        });

        // FECHA DE GUARDADO
        TableColumn<Factura, String> colFecha = new TableColumn<>("FECHA DE GUARDADO");
        colFecha.setCellValueFactory(cd -> cd.getValue().fechaProperty());
        colFecha.setPrefWidth(180);
        colFecha.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                HBox hb = new HBox(6); hb.setAlignment(Pos.CENTER_LEFT);
                Label ico = new Label("📅"); ico.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
                Label lbl = new Label(item); lbl.setStyle("-fx-text-fill: " + TEXT_D + ";");
                hb.getChildren().addAll(ico, lbl); setGraphic(hb); setText(null);
            }
        });

        // TAMAÑO
        TableColumn<Factura, String> colTam = new TableColumn<>("TAMAÑO");
        colTam.setCellValueFactory(cd -> cd.getValue().tamanioProperty());
        colTam.setPrefWidth(100);
        colTam.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle("-fx-text-fill: " + TEXT_D + "; -fx-alignment: CENTER;");
            }
        });

        // ACCIONES
        TableColumn<Factura, String> colAcciones = new TableColumn<>("ACCIONES");
        colAcciones.setCellValueFactory(cd -> cd.getValue().nombreProperty());
        colAcciones.setPrefWidth(220);
        colAcciones.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Factura f = getTableRow() != null ? getTableRow().getItem() : null;

                HBox hb = new HBox(10); hb.setAlignment(Pos.CENTER_LEFT);

                Button btnDescargar = new Button("⬇ Descargar");
                btnDescargar.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: " + TEXT_D + ";" +
                    "-fx-cursor: hand; -fx-font-size: 12px; -fx-font-weight: bold;"
                );
                btnDescargar.setOnAction(e -> { if (f != null) descargarFactura(f); });

                Button btnVer = new Button("👁 Ver");
                btnVer.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: " + RED + ";" +
                    "-fx-cursor: hand; -fx-font-size: 12px; -fx-font-weight: bold;"
                );
                btnVer.setOnAction(e -> { if (f != null) verFactura(f); });

                hb.getChildren().addAll(btnDescargar, btnVer);
                setGraphic(hb); setText(null);
            }
        });

        table.getColumns().addAll(colN, colNombre, colFecha, colTam, colAcciones);

        // Filas alternas
        table.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Factura item, boolean empty) {
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

    // ── Paginación ────────────────────────────────────────────────────────────
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
        int totalPages = (int) Math.ceil((double) filteredFacturas.size() / PAGE_SIZE);
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
        int to   = Math.min(from + PAGE_SIZE - 1, filteredFacturas.size());
        if (filteredFacturas.isEmpty()) lblPaginacion.setText("Sin resultados");
        else lblPaginacion.setText("Mostrando " + from + " a " + to + " de " + filteredFacturas.size() + " facturas");
    }

    private static void refreshPage() {
        pageFacturas.clear();
        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, filteredFacturas.size());
        if (from < filteredFacturas.size()) pageFacturas.addAll(filteredFacturas.subList(from, to));
    }

    // ── Acciones ─────────────────────────────────────────────────────────────
    private static void verFactura(Factura f) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Vista previa: " + f.getNombre());

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label(f.getNombre());
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");

        TextArea ta = new TextArea();
        ta.setEditable(false);
        ta.setPrefSize(580, 400);
        ta.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        // Leer el archivo real si existe (directorio facturas/)
        String ruta = f.getRutaReal();
        if (ruta != null && !ruta.isEmpty() && new File(ruta).exists()) {
            try {
                ta.setText(new String(Files.readAllBytes(Paths.get(ruta))));
            } catch (Exception ex) {
                ta.setText("Error al leer el archivo: " + ex.getMessage());
            }
        } else if (ruta != null && ruta.contains("|")) {
            // La ruta contiene la línea de facturas.txt — parsear y mostrar
            ta.setText(generarContenidoDesdeLinea(f, ruta));
        } else {
            ta.setText(generarContenidoMuestra(f));
        }

        Button btnCerrar = redOutlineButton("Cerrar");
        btnCerrar.setOnAction(e -> popup.close());

        content.getChildren().addAll(title, ta, btnCerrar);
        popup.setScene(new Scene(content, 620, 500));
        popup.show();
    }

    private static void descargarFactura(Factura f) {
        String ruta = f.getRutaReal();
        if (ruta != null && !ruta.isEmpty() && new File(ruta).exists()) {
            // Si el archivo real existe, mostrar su ubicación
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Archivo disponible en:\n" + ruta, ButtonType.OK);
            alert.setHeaderText("Archivo encontrado");
            alert.show();
        } else {
            // Guardar contenido de muestra en el directorio actual
            try {
                File destino = new File(f.getNombre());
                FileWriter fw = new FileWriter(destino);
                fw.write(generarContenidoMuestra(f));
                fw.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Factura guardada en:\n" + destino.getAbsolutePath(), ButtonType.OK);
                alert.setHeaderText("Descarga exitosa");
                alert.show();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Error al descargar: " + ex.getMessage(), ButtonType.OK);
                alert.show();
            }
        }
    }

    /** Genera el contenido de la factura parseando una línea de facturas.txt */
    // Formato nuevo: id|userId|fecha|total|tipo|hora|prod1 xN, prod2 xN, ...
    // Formato viejo: id|userId|fecha|total|tipo|prod1 xN, prod2 xN, ...
    private static String generarContenidoDesdeLinea(Factura f, String linea) {
        try {
            String[] p = linea.split("\\|", -1);
            if (p.length < 5) return generarContenidoMuestra(f);

            String fecha  = p[2].trim();   // yyyy-MM-dd
            String total  = p[3].trim();
            String tipo   = p[4].trim();

            // Detectar si p[5] es hora (HH:mm) o inicio de productos
            String hora = "";
            int prodStart = 5;
            if (p.length > 5) {
                String campo5 = p[5].trim();
                if (campo5.matches("\\d{2}:\\d{2}")) {
                    hora = campo5;
                    prodStart = 6;
                }
            }

            // Convertir fecha: yyyy-MM-dd → dd/MM/yyyy
            String[] pf = fecha.split("-");
            String fechaDisplay = pf.length == 3
                ? pf[2] + "/" + pf[1] + "/" + pf[0]
                : fecha;
            if (!hora.isEmpty()) fechaDisplay += " " + hora;

            // Calcular subtotal
            int totalInt = Integer.parseInt(total);
            int delivery = tipo.equals("DELIVERY") ? 10 : 0;
            int subtotal = totalInt - delivery;

            // Parsear productos
            java.util.List<String[]> itemsParsed = new java.util.ArrayList<>();
            for (int i = prodStart; i < p.length; i++) {
                String seg = p[i].trim();
                if (seg.isEmpty()) continue;
                String[] partes = seg.split(",");
                for (String prod : partes) {
                    prod = prod.trim();
                    if (prod.isEmpty()) continue;
                    String nombre = prod;
                    String cant = "1";
                    if (prod.toLowerCase().contains(" x")) {
                        int ix = prod.toLowerCase().lastIndexOf(" x");
                        nombre = prod.substring(0, ix).trim();
                        cant = prod.substring(ix + 2).trim();
                    }
                    itemsParsed.add(new String[]{nombre, cant});
                }
            }

            // Construir bloque de productos
            StringBuilder itemsBlock = new StringBuilder();
            for (String[] item : itemsParsed) {
                String nombre = item[0];
                int cantInt = 1;
                try { cantInt = Integer.parseInt(item[1]); } catch (Exception ignored) {}
                int precioUnit = obtenerPrecioProducto(nombre);
                String precioStr = precioUnit > 0
                    ? String.format("%.2f Bs", (double) precioUnit)
                    : "-- Bs";
                String nombreTrunc = nombre.length() > 20 ? nombre.substring(0, 20) : nombre;
                itemsBlock.append(String.format("  %-20s %3d  %10s%n", nombreTrunc, cantInt, precioStr));
            }

            return "===========================================" + "\n" +
                   "       TACABRÓN RESTAURANTE" + "\n" +
                   "       Sabor que enamora" + "\n" +
                   "===========================================" + "\n" +
                   "Factura: " + f.getNombre() + "\n" +
                   "Fecha:   " + fechaDisplay + "\n" +
                   "Tipo:    " + tipo + "\n" +
                   "Tamaño:  " + f.getTamanio() + "\n" +
                   "-------------------------------------------" + "\n" +
                   "  PRODUCTO              CANT      PRECIO" + "\n" +
                   "-------------------------------------------" + "\n" +
                   itemsBlock.toString() +
                   "-------------------------------------------" + "\n" +
                   String.format("  Subtotal:               %12.2f Bs%n", (double) subtotal) +
                   (delivery > 0 ? String.format("  Delivery:               %12.2f Bs%n", (double) delivery) : "") +
                   String.format("  TOTAL:                  %12.2f Bs%n", (double) totalInt) +
                   "===========================================" + "\n" +
                   "  ¡Gracias por su preferencia!" + "\n" +
                   "===========================================" + "\n";
        } catch (Exception e) {
            return generarContenidoMuestra(f);
        }
    }

    /** Intenta obtener el precio unitario de un producto leyendo el stock/menú si disponible */
    private static int obtenerPrecioProducto(String nombreProducto) {
        // Tabla de precios base del menú (sincronizada con MostrarMenu)
        java.util.Map<String, Integer> precios = new java.util.HashMap<>();
        precios.put("BIRRIA", 35); precios.put("QUESABIRRIA", 40);
        precios.put("SUADERO", 20); precios.put("PASTOR", 20);
        precios.put("MEGABURRITO", 45); precios.put("RAMEN BIRRIA", 45);
        precios.put("NACHOS SUPREMOS", 40); precios.put("NACHOS", 40);
        precios.put("JAMAICA", 8); precios.put("HORCHATA", 10);
        precios.put("TORTILLA EXTRA", 5);
        precios.put("MEGABURRITO COMBO 2", 65); precios.put("NACHOS SUPREMOS COMBO 2", 55);
        String key = nombreProducto.toUpperCase().trim();
        return precios.getOrDefault(key, 0);
    }

    private static String generarContenidoMuestra(Factura f) {
        return "===========================================\n" +
               "       TACABRÓN RESTAURANTE\n" +
               "       Sabor que enamora\n" +
               "===========================================\n" +
               "Factura: " + f.getNombre() + "\n" +
               "Fecha:   " + f.getFecha() + "\n" +
               "Tamaño:  " + f.getTamanio() + "\n" +
               "-------------------------------------------\n" +
               "  PRODUCTO          CANT   PRECIO\n" +
               "-------------------------------------------\n" +
               "  Birria              2    70.00 Bs\n" +
               "  Jamaica             1     8.00 Bs\n" +
               "  Nachos              1    40.00 Bs\n" +
               "-------------------------------------------\n" +
               "  Subtotal:               118.00 Bs\n" +
               "  Delivery:                10.00 Bs\n" +
               "  TOTAL:                  128.00 Bs\n" +
               "===========================================\n" +
               "  ¡Gracias por su preferencia!\n" +
               "===========================================\n";
    }

    // ── Cargar facturas ───────────────────────────────────────────────────────
    // Formato facturas.txt: id|userId|fecha|total|tipo|prod1 xN, prod2 xN, ...
    private static ObservableList<Factura> loadFacturas() {
        ObservableList<Factura> lista = FXCollections.observableArrayList();

        // Primero intentar leer archivos físicos del directorio facturas/
        File dir = new File("facturas");
        List<File> archivos = new ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, n) -> n.endsWith(".txt"));
            if (files != null) {
                for (File f : files) archivos.add(f);
            }
            archivos.sort((a, b) -> a.getName().compareTo(b.getName()));
        }

        if (!archivos.isEmpty()) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (int i = 0; i < archivos.size(); i++) {
                File f = archivos.get(i);
                String fecha = LocalDateTime.ofInstant(
                    java.nio.file.attribute.FileTime.fromMillis(f.lastModified()).toInstant(),
                    java.time.ZoneId.systemDefault()
                ).format(fmt);
                long bytes = f.length();
                String tam = bytes < 1024 ? bytes + " B"
                    : String.format("%.2f KB", bytes / 1024.0);
                lista.add(new Factura(i + 1, f.getName(), fecha, tam, f.getAbsolutePath()));
            }
            return lista;
        }

        // Si no hay directorio físico, leer desde facturas.txt
        com.dhery.GestorArchivo.ArchivoManager arch = new com.dhery.GestorArchivo.ArchivoManager();
        List<String> lineas = arch.leerLineas("src/main/java/com/dhery/GestorArchivo/facturas.txt");

        // Cargar mapa de usuarios para resolver nombre por id
        java.util.Map<String, String> usuarioNombres = cargarNombresUsuarios();

        int contadorVisual = 1;
        for (String linea : lineas) {
            if (linea == null || linea.isBlank()) continue;
            String[] p = linea.split("\\|", -1);
            if (p.length < 5) continue;
            try {
                String idFact  = p[0].trim();
                String userId  = p[1].trim();
                String fecha   = p[2].trim();   // yyyy-MM-dd
                String total   = p[3].trim();
                String tipo    = p[4].trim();
                String nombreUsuario = usuarioNombres.getOrDefault(userId, "Cliente #" + userId);

                // Formato nombre archivo: factura_XXXXXX_AAAAMMDD_HHmmss.txt
                String idPad = String.format("%06d", Integer.parseInt(idFact));
                String fechaTag = fecha.replace("-", "");
                String nombreArchivo = "factura_" + idPad + "_" + fechaTag + "_000000.txt";

                // Fecha mostrada: dd/MM/yyyy
                String[] partesFecha = fecha.split("-");
                String fechaMostrada = partesFecha.length == 3
                    ? partesFecha[2] + "/" + partesFecha[1] + "/" + partesFecha[0] + " 00:00"
                    : fecha;

                // Tamaño aproximado
                String tam = "1.25 KB";

                // Guardamos en rutaReal el contenido serializado para mostrarlo luego
                String rutaReal = linea; // guardamos la línea original como "ruta"

                lista.add(new Factura(contadorVisual, nombreArchivo, fechaMostrada, tam, rutaReal));
                contadorVisual++;
            } catch (Exception ignored) {}
        }
        return lista;
    }

    /** Carga mapa id -> "Nombre Apellido" desde usuarios.txt */
    private static java.util.Map<String, String> cargarNombresUsuarios() {
        java.util.Map<String, String> mapa = new java.util.HashMap<>();
        com.dhery.GestorArchivo.ArchivoManager arch = new com.dhery.GestorArchivo.ArchivoManager();
        List<String> lineas = arch.leerLineas("src/main/java/com/dhery/GestorArchivo/usuarios.txt");
        for (String l : lineas) {
            if (l == null || l.isBlank()) continue;
            String[] p = l.split("\\|", -1);
            if (p.length >= 2) {
                String id = p[0].trim();
                String nombre = p[1].trim();
                if (p.length >= 5 && !p[4].isBlank())
                    nombre = nombre + " " + p[4].trim();
                mapa.put(id, nombre);
            }
        }
        return mapa;
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
        Button btn = new Button(text); btn.setPrefHeight(40);
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
}