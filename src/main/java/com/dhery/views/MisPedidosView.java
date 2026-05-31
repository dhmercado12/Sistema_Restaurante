package com.dhery.views;

import com.dhery.GestorArchivo.ArchivoManager;
import com.dhery.app.Router;
import com.dhery.models.user;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class MisPedidosView {

    // ─────────────────────────────────────────────
    // MODELO  (sin cambios)
    // ─────────────────────────────────────────────
    public static class Pedido {
        private final int idFactura;
        private final int idUser;
        private final String fecha;
        private final String tipo;
        private final String detalle;
        private final int total;

        public Pedido(int idFactura, int idUser, String fecha, String tipo, String detalle, int total) {
            this.idFactura = idFactura; this.idUser = idUser;
            this.fecha = fecha; this.tipo = tipo;
            this.detalle = detalle; this.total = total;
        }
        public int    getIdFactura() { return idFactura; }
        public int    getIdUser()    { return idUser;    }
        public String getFecha()     { return fecha;     }
        public String getTipo()      { return tipo;      }
        public String getDetalle()   { return detalle;   }
        public int    getTotal()     { return total;     }
    }

    // ─────────────────────────────────────────────
    // ESTADO
    // ─────────────────────────────────────────────
    private static user currentUser;
    private static ObservableList<Pedido> allPedidos;
    private static ObservableList<Pedido> filteredPedidos;

    // ─────────────────────────────────────────────
    // PALETA
    // ─────────────────────────────────────────────
    private static final String RED       = "#CC0000";
    private static final String RED_DARK  = "#AA0000";
    private static final String RED_BG    = "#FFEAEA";
    private static final String TEXT_D    = "#1A1A1A";
    private static final String TEXT_G    = "#888888";
    private static final String BG        = "#F0F2F5";
    private static final String CARD_BG   = "white";
    private static final String BORDER    = "#E8E8E8";
    private static final String ROW_ALT   = "#FAFAFA";

    private static Label lblTotal;
    private static Label lblGasto;

    // ─────────────────────────────────────────────
    // SCENE PRINCIPAL
    // ─────────────────────────────────────────────
    public static Scene getScene(user user) {
        currentUser     = user;
        allPedidos      = loadPedidos();
        filteredPedidos = FXCollections.observableArrayList(allPedidos);

        // Root con fondo gris
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: " + BG + ";");
        root.setPadding(new Insets(28));

        // ── TOP BAR ─────────────────────────────
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 20, 0));

        // Brand pill
        HBox brand = new HBox(8);
        brand.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(5, Color.web(RED));
        Label brandName = new Label("TACABRÓN");
        brandName.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;" +
            " -fx-text-fill: " + TEXT_D + "; -fx-letter-spacing: 0.08em;");
        brand.getChildren().addAll(dot, brandName);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Button btnBack = new Button();
        btnBack.setGraphic(makeIcon("←", 13, TEXT_G));
        btnBack.setText("  Volver al menú");
        btnBack.setStyle(
            "-fx-background-color: white; -fx-text-fill: " + TEXT_G + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-width: 1.5;" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;" +
            "-fx-padding: 6 14 6 14;"
        );
        btnBack.setOnAction(e -> Router.goMenuClienteView());

        topBar.getChildren().addAll(brand, topSpacer, btnBack);

        // ── CARD PRINCIPAL ───────────────────────
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16; -fx-background-radius: 16;" +
            "-fx-border-width: 1;"
        );

        // ── CARD HEADER ROJO ─────────────────────
        HBox cardHeader = new HBox(18);
        cardHeader.setPadding(new Insets(26, 32, 26, 32));
        cardHeader.setAlignment(Pos.CENTER_LEFT);
        cardHeader.setStyle(
            "-fx-background-color: " + RED + ";" +
            "-fx-background-radius: 16 16 0 0;"
        );

        // Icono cuadrado redondeado
        StackPane headerIcon = new StackPane();
        Rectangle iconBg = new Rectangle(52, 52);
        iconBg.setArcWidth(12); iconBg.setArcHeight(12);
        iconBg.setFill(Color.web("#FFFFFF2E"));
        Label iconLbl = new Label("🧾");
        iconLbl.setStyle("-fx-font-size: 24px;");
        headerIcon.getChildren().addAll(iconBg, iconLbl);

        VBox headerText = new VBox(4);
        Label hTitle = new Label("Mis Pedidos");
        hTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label hSub = new Label("Historial de compras de tu cuenta");
        hSub.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.78);");
        headerText.getChildren().addAll(hTitle, hSub);

        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);

        // Stats pills
        HBox statsBox = new HBox(12);
        statsBox.setAlignment(Pos.CENTER_RIGHT);

        lblTotal = new Label(allPedidos.size() + "\nPedidos");
        lblTotal.setAlignment(Pos.CENTER);
        lblTotal.setStyle(
            "-fx-background-color: rgba(255,255,255,0.18);" +
            "-fx-background-radius: 10; -fx-padding: 10 18 10 18;" +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;" +
            "-fx-text-alignment: center;"
        );

        int gastoTotal = allPedidos.stream().mapToInt(Pedido::getTotal).sum();
        lblGasto = new Label("Bs " + gastoTotal + "\nTotal gastado");
        lblGasto.setAlignment(Pos.CENTER);
        lblGasto.setStyle(
            "-fx-background-color: rgba(255,255,255,0.18);" +
            "-fx-background-radius: 10; -fx-padding: 10 18 10 18;" +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;" +
            "-fx-text-alignment: center;"
        );

        statsBox.getChildren().addAll(lblTotal, lblGasto);
        cardHeader.getChildren().addAll(headerIcon, headerText, hSpacer, statsBox);

        // ── CARD BODY ────────────────────────────
        VBox cardBody = new VBox(0);
        cardBody.setPadding(new Insets(24, 32, 24, 32));

        // Búsqueda + filtros
        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        searchRow.setPadding(new Insets(0, 0, 18, 0));

        TextField search = new TextField();
        search.setPromptText("🔍  Buscar por fecha o tipo...");
        search.setPrefHeight(40);
        search.setPrefWidth(340);
        search.setStyle(
            "-fx-background-color: " + ROW_ALT + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-radius: 10; -fx-background-radius: 10;" +
            "-fx-padding: 0 12 0 12; -fx-font-size: 13px; -fx-prompt-text-fill: #BBBBBB;"
        );

        // Filtros tipo
        Button[] filterBtns = new Button[3];
        String[] filterLabels = {"Todos", "Delivery", "Local"};
        String[] filterStyles = {
            styleFilterActive(), styleFilterInactive(), styleFilterInactive()
        };
        for (int i = 0; i < 3; i++) {
            filterBtns[i] = new Button(filterLabels[i]);
            filterBtns[i].setPrefHeight(40);
            filterBtns[i].setStyle(i == 0 ? styleFilterActive() : styleFilterInactive());
        }

        // Lógica filtros
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            filterBtns[i].setOnAction(e -> {
                for (Button b : filterBtns) b.setStyle(styleFilterInactive());
                filterBtns[idx].setStyle(styleFilterActive());
                applyFilter(search.getText(), filterLabels[idx]);
            });
        }

        search.textProperty().addListener((obs, o, v) -> {
            String activeFilter = "Todos";
            for (int i = 0; i < filterBtns.length; i++)
                if (filterBtns[i].getStyle().contains("#FFEAEA")) activeFilter = filterLabels[i];
            applyFilter(v, activeFilter);
        });

        Region searchSpacer = new Region();
        HBox.setHgrow(searchSpacer, Priority.ALWAYS);

        searchRow.getChildren().addAll(search, searchSpacer,
            filterBtns[0], filterBtns[1], filterBtns[2]);

        // ── TABLA ────────────────────────────────
        TableView<Pedido> table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        cardBody.getChildren().addAll(searchRow, table);
        card.getChildren().addAll(cardHeader, cardBody);

        VBox.setVgrow(card, Priority.ALWAYS);
        root.getChildren().addAll(topBar, card);

        return new Scene(root, 1280, 720);
    }

    // ─────────────────────────────────────────────
    // FILTRO
    // ─────────────────────────────────────────────
    private static void applyFilter(String query, String tipo) {
        String q = query == null ? "" : query.toLowerCase().trim();
        filteredPedidos.setAll(allPedidos.stream().filter(p -> {
            boolean matchQ = q.isEmpty()
                || p.getFecha().toLowerCase().contains(q)
                || p.getTipo().toLowerCase().contains(q);
            boolean matchT = tipo.equals("Todos")
                || p.getTipo().equalsIgnoreCase(tipo);
            return matchQ && matchT;
        }).collect(Collectors.toList()));

        if (lblTotal != null)
            lblTotal.setText(filteredPedidos.size() + "\nPedidos");
    }

    // ─────────────────────────────────────────────
    // TABLA
    // ─────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private static TableView<Pedido> buildTable() {

        TableView<Pedido> table = new TableView<>(filteredPedidos);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(54);
        table.setStyle(
            "-fx-background-color: white;" +
            "-fx-control-inner-background: white;" +
            "-fx-control-inner-background-alt: " + ROW_ALT + ";" +
            "-fx-table-cell-border-color: #F0F0F0;" +
            "-fx-border-color: transparent;"
        );
        table.setPlaceholder(new Label("No se encontraron pedidos"));

        // N°
        TableColumn<Pedido, Integer> colN = new TableColumn<>("#");
        colN.setMaxWidth(60); colN.setMinWidth(60); colN.setPrefWidth(60);
        colN.setCellValueFactory(d -> new SimpleIntegerProperty(
            filteredPedidos.indexOf(d.getValue()) + 1).asObject());
        colN.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                StackPane sp = new StackPane();
                Rectangle bg = new Rectangle(30, 30);
                bg.setArcWidth(8); bg.setArcHeight(8);
                bg.setFill(Color.web(RED_BG));
                Label l = new Label(item.toString());
                l.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");
                sp.getChildren().addAll(bg, l);
                sp.setAlignment(Pos.CENTER);
                setGraphic(sp); setText(null);
                setAlignment(Pos.CENTER);
            }
        });

        // NOMBRE ARCHIVO
        TableColumn<Pedido, String> colNombre = new TableColumn<>("ARCHIVO");
        colNombre.setPrefWidth(280);
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(
            "factura_" + String.format("%06d", d.getValue().getIdFactura()) + ".txt"));
        colNombre.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                HBox hb = new HBox(8);
                hb.setAlignment(Pos.CENTER_LEFT);
                StackPane iconSp = new StackPane();
                Rectangle iconBg = new Rectangle(24, 24);
                iconBg.setArcWidth(6); iconBg.setArcHeight(6);
                iconBg.setFill(Color.web(RED_BG));
                Label iconL = new Label("\uD83D\uDCC4");
                iconL.setStyle("-fx-font-size: 11px;");
                iconSp.getChildren().addAll(iconBg, iconL);
                Label lbl = new Label(item);
                lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_D + "; -fx-font-family: 'Courier New';");
                hb.getChildren().addAll(iconSp, lbl);
                StackPane wrapper = new StackPane(hb);
                wrapper.setAlignment(Pos.CENTER_LEFT);
                setGraphic(wrapper); setText(null);
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        // FECHA — compacta
        TableColumn<Pedido, String> colFecha = new TableColumn<>("FECHA");
        colFecha.setPrefWidth(140);
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFecha()));
        colFecha.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                String[] parts = item.split(" ");
                VBox vb = new VBox(1);
                vb.setAlignment(Pos.CENTER_LEFT);
                Label fecha = new Label(parts[0]);
                fecha.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
                vb.getChildren().add(fecha);
                if (parts.length > 1) {
                    Label hora = new Label(parts[1]);
                    hora.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_G + ";");
                    vb.getChildren().add(hora);
                }
                setGraphic(vb); setText(null);
                setAlignment(Pos.CENTER_LEFT);
            }
        });

        // TIPO
        TableColumn<Pedido, String> colTipo = new TableColumn<>("TIPO");
        colTipo.setPrefWidth(130);
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo()));
        colTipo.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                boolean isDelivery = item.equalsIgnoreCase("DELIVERY");
                Label pill = new Label(item);
                pill.setStyle(
                    "-fx-background-color: " + (isDelivery ? "#FFF3E0" : "#E8F5E9") + ";" +
                    "-fx-text-fill: " + (isDelivery ? "#E65100" : "#2E7D32") + ";" +
                    "-fx-font-weight: bold; -fx-font-size: 11px;" +
                    "-fx-background-radius: 20; -fx-padding: 4 12 4 12;"
                );
                StackPane sp = new StackPane(pill);
                sp.setAlignment(Pos.CENTER);
                setGraphic(sp); setText(null);
                setAlignment(Pos.CENTER);
            }
        });

        // TOTAL
        TableColumn<Pedido, Number> colTotal = new TableColumn<>("TOTAL");
        colTotal.setPrefWidth(120);
        colTotal.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getTotal()));
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                HBox hb = new HBox(3);
                hb.setAlignment(Pos.CENTER_RIGHT);
                Label bs = new Label("Bs ");
                bs.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_G + ";");
                Label val = new Label(item.toString());
                val.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
                hb.getChildren().addAll(bs, val);
                setGraphic(hb); setText(null);
                setAlignment(Pos.CENTER_RIGHT);
                setPadding(new Insets(0, 16, 0, 0));
            }
        });

        // ACCIONES
        TableColumn<Pedido, String> colAcc = new TableColumn<>("ACCIONES");
        colAcc.setPrefWidth(150);
        colAcc.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Pedido p = getTableView().getItems().get(getIndex());

                Button ver = new Button("Ver factura");
                ver.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-text-fill: " + RED + ";" +
                    "-fx-border-color: #FFBBBB; -fx-border-width: 1.5;" +
                    "-fx-border-radius: 8; -fx-background-radius: 8;" +
                    "-fx-font-weight: bold; -fx-font-size: 12px;" +
                    "-fx-cursor: hand; -fx-padding: 6 14 6 14;"
                );
                ver.setOnMouseEntered(e -> ver.setStyle(
                    "-fx-background-color: " + RED_BG + ";" +
                    "-fx-text-fill: " + RED + ";" +
                    "-fx-border-color: #FFBBBB; -fx-border-width: 1.5;" +
                    "-fx-border-radius: 8; -fx-background-radius: 8;" +
                    "-fx-font-weight: bold; -fx-font-size: 12px;" +
                    "-fx-cursor: hand; -fx-padding: 6 14 6 14;"
                ));
                ver.setOnMouseExited(e -> ver.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-text-fill: " + RED + ";" +
                    "-fx-border-color: #FFBBBB; -fx-border-width: 1.5;" +
                    "-fx-border-radius: 8; -fx-background-radius: 8;" +
                    "-fx-font-weight: bold; -fx-font-size: 12px;" +
                    "-fx-cursor: hand; -fx-padding: 6 14 6 14;"
                ));
                ver.setOnAction(e -> verFacturaPro(p));

                StackPane sp = new StackPane(ver);
                sp.setAlignment(Pos.CENTER);
                setGraphic(sp); setText(null);
                setAlignment(Pos.CENTER);
            }
        });

        table.getColumns().addAll(colN, colNombre, colFecha, colTipo, colTotal, colAcc);

        // Filas alternas
        table.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Pedido item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setStyle("-fx-background-color: white;");
                else
                    setStyle("-fx-background-color: " + (getIndex() % 2 == 0 ? "white" : ROW_ALT) + ";");
            }
        });

        // Header tabla gris claro
        table.skinProperty().addListener((obs, o, skin) -> {
            javafx.scene.Node h = table.lookup("TableHeaderRow");
            if (h != null) h.setStyle("-fx-background-color: #F7F8FA; -fx-border-color: #F0F0F0; -fx-border-width: 0 0 1 0;");
            table.lookupAll(".column-header .label").forEach(n -> {
                if (n instanceof Label lbl)
                    lbl.setStyle("-fx-text-fill: " + TEXT_G + "; -fx-font-weight: bold; -fx-font-size: 11px;");
            });
        });

        return table;
    }

    // ─────────────────────────────────────────────
    // MODAL FACTURA PROFESIONAL
    // ─────────────────────────────────────────────
    private static void verFacturaPro(Pedido p) {
        Stage st = new Stage();
        st.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: white;");

        // Header rojo
        HBox hdr = new HBox(14);
        hdr.setPadding(new Insets(20, 24, 20, 24));
        hdr.setAlignment(Pos.CENTER_LEFT);
        hdr.setStyle("-fx-background-color: " + RED + ";");

        StackPane iconSp = new StackPane();
        Rectangle iconBg = new Rectangle(42, 42);
        iconBg.setArcWidth(10); iconBg.setArcHeight(10);
        iconBg.setFill(Color.web("#FFFFFF2E"));
        Label iconLbl = new Label("🧾");
        iconLbl.setStyle("-fx-font-size: 18px;");
        iconSp.getChildren().addAll(iconBg, iconLbl);

        VBox hdrText = new VBox(3);
        Label hdrTitle = new Label("Tacabrón Restaurante");
        hdrTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label hdrSub = new Label("Factura N° " + String.format("%06d", p.getIdFactura()));
        hdrSub.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.78);");
        hdrText.getChildren().addAll(hdrTitle, hdrSub);

        Region hdrSp = new Region(); HBox.setHgrow(hdrSp, Priority.ALWAYS);

        VBox tipoBox = new VBox(2);
        tipoBox.setAlignment(Pos.CENTER_RIGHT);
        Label tipoLbl = new Label("Tipo");
        tipoLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(255,255,255,0.75);");
        Label tipoVal = new Label(p.getTipo());
        tipoVal.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");
        tipoBox.getChildren().addAll(tipoLbl, tipoVal);

        hdr.getChildren().addAll(iconSp, hdrText, hdrSp, tipoBox);

        // Meta
        HBox meta = new HBox(28);
        meta.setPadding(new Insets(16, 24, 0, 24));

        VBox fechaBox = new VBox(2);
        Label fechaLbl = new Label("Fecha");
        fechaLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_G + ";");
        Label fechaVal = new Label("📅  " + p.getFecha());
        fechaVal.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        fechaBox.getChildren().addAll(fechaLbl, fechaVal);

        VBox numBox = new VBox(2);
        Label numLbl = new Label("N° Factura");
        numLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_G + ";");
        Label numVal = new Label("#  " + p.getIdFactura());
        numVal.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        numBox.getChildren().addAll(numLbl, numVal);

        meta.getChildren().addAll(fechaBox, numBox);

        // Sección detalle
        VBox detSec = new VBox(8);
        detSec.setPadding(new Insets(14, 24, 0, 24));

        Separator sep = new Separator();

        Label detTitle = new Label("DETALLE DEL PEDIDO");
        detTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_G + ";");

        // Tabla productos
        TableView<String[]> tbl = new TableView<>();
        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tbl.setFixedCellSize(40);
        tbl.setStyle(
            "-fx-background-color: white;" +
            "-fx-control-inner-background: white;" +
            "-fx-control-inner-background-alt: " + ROW_ALT + ";" +
            "-fx-table-cell-border-color: #F5F5F5;"
        );

        TableColumn<String[], String> colProd = new TableColumn<>("PRODUCTO");
        colProd.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        colProd.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_D + "; -fx-font-size: 13px;");
            }
        });

        TableColumn<String[], String> colCant = new TableColumn<>("CANT.");
        colCant.setMaxWidth(90); colCant.setMinWidth(80);
        colCant.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        colCant.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label("x" + item.trim());
                badge.setStyle(
                    "-fx-background-color: " + RED_BG + ";" +
                    "-fx-text-fill: " + RED_DARK + ";" +
                    "-fx-font-weight: bold; -fx-font-size: 12px;" +
                    "-fx-padding: 2 10 2 10; -fx-background-radius: 12;"
                );
                StackPane sp = new StackPane(badge);
                sp.setAlignment(Pos.CENTER);
                setGraphic(sp); setText(null);
                setAlignment(Pos.CENTER);
            }
        });

        TableColumn<String[], String> colSub = new TableColumn<>("SUBTOTAL");
        colSub.setMaxWidth(100); colSub.setMinWidth(90);
        colSub.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        colSub.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setStyle("-fx-text-fill: " + TEXT_G + "; -fx-alignment: CENTER-RIGHT;");
            }
        });

        tbl.getColumns().addAll(colProd, colCant, colSub);

        ObservableList<String[]> items = FXCollections.observableArrayList();
        if (p.getDetalle() != null && !p.getDetalle().isBlank()) {
            for (String item : p.getDetalle().split(",")) {

    item = item.trim();

    if (item.isEmpty()) {
        continue;
    }

    int xIdx = item.lastIndexOf(" x");

    if (xIdx != -1) {

        String nombre = capitalize(
                item.substring(0, xIdx).trim()
        );

        String cant = item.substring(
                xIdx + 2
        ).trim();

        items.add(new String[]{
                nombre,
                cant,
                "—"
        });

    } else {

        items.add(new String[]{
                capitalize(item),
                "1",
                "—"
        });
    }
}
        }
        tbl.setItems(items);
        tbl.setPrefHeight(items.size() * 40 + 35);

        tbl.skinProperty().addListener((obs, o, skin) -> {
            javafx.scene.Node h = tbl.lookup("TableHeaderRow");
            if (h != null) h.setStyle("-fx-background-color: #F7F8FA;");
            tbl.lookupAll(".column-header .label").forEach(n -> {
                if (n instanceof Label lbl)
                    lbl.setStyle("-fx-text-fill: " + TEXT_G + "; -fx-font-weight: bold; -fx-font-size: 11px;");
            });
        });

        detSec.getChildren().addAll(sep, detTitle, tbl);

        // Total
        HBox totalRow = new HBox();
        totalRow.setPadding(new Insets(14, 24, 4, 24));
        totalRow.setAlignment(Pos.CENTER_LEFT);
        totalRow.setStyle("-fx-border-color: #EEEEEE; -fx-border-width: 1 0 0 0;");

        Label totalLbl = new Label("Total a pagar");
        totalLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_G + ";");
        Region totalSp = new Region(); HBox.setHgrow(totalSp, Priority.ALWAYS);
        Label totalVal = new Label("Bs " + p.getTotal());
        totalVal.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1A7A3C;");
        totalRow.getChildren().addAll(totalLbl, totalSp, totalVal);

        // Botón cerrar
        HBox bottomRow = new HBox();
        bottomRow.setPadding(new Insets(12, 24, 20, 24));
        bottomRow.setAlignment(Pos.CENTER_RIGHT);
        Button close = redOutlineButton("Cerrar");
        close.setOnAction(e -> st.close());
        bottomRow.getChildren().add(close);

        root.getChildren().addAll(hdr, meta, detSec, totalRow, bottomRow);

        st.setScene(new Scene(root, 480, 380 + items.size() * 40));
        st.setTitle("Factura #" + p.getIdFactura());
        st.show();
    }

    // ─────────────────────────────────────────────
    // CARGA TXT  (sin cambios)
    // ─────────────────────────────────────────────
    private static ObservableList<Pedido> loadPedidos() {
        ObservableList<Pedido> list = FXCollections.observableArrayList();
        ArchivoManager archivo = new ArchivoManager();
        List<String> lineas = archivo.leerLineas(
            "src/main/java/com/dhery/GestorArchivo/facturas.txt");
        for (String l : lineas) {
            String[] parts = l.split("\\|");
            int    idFactura = Integer.parseInt(parts[0]);
            int    idUser    = Integer.parseInt(parts[1]);
            String fecha     = parts[2];
            int    total     = Integer.parseInt(parts[3]);
            String tipo      = parts[4];
            String detalle   = parts.length > 5 ? parts[5] : "";
            if (idUser == currentUser.getId())
                list.add(new Pedido(idFactura, idUser, fecha, tipo, detalle, total));
        }
        return list;
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    private static String styleFilterActive() {
        return "-fx-background-color: " + RED_BG + "; -fx-text-fill: " + RED + ";" +
            "-fx-border-color: #FFBBBB; -fx-border-width: 1.5;" +
            "-fx-border-radius: 10; -fx-background-radius: 10;" +
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 14 0 14;";
    }
    private static String styleFilterInactive() {
        return "-fx-background-color: " + ROW_ALT + "; -fx-text-fill: " + TEXT_G + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-width: 1.5;" +
            "-fx-border-radius: 10; -fx-background-radius: 10;" +
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 14 0 14;";
    }
    private static Button redOutlineButton(String text) {
        Button btn = new Button(text); btn.setPrefHeight(36);
        String n = "-fx-background-color: white; -fx-text-fill: " + RED + ";" +
            "-fx-border-color: " + RED + "; -fx-border-width: 1.5; -fx-border-radius: 8;" +
            "-fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 18 0 18;";
        String h = "-fx-background-color: " + RED_BG + "; -fx-text-fill: " + RED_DARK + ";" +
            "-fx-border-color: " + RED_DARK + "; -fx-border-width: 1.5; -fx-border-radius: 8;" +
            "-fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 18 0 18;";
        btn.setStyle(n);
        btn.setOnMouseEntered(e -> btn.setStyle(h));
        btn.setOnMouseExited(e  -> btn.setStyle(n));
        return btn;
    }
    private static Label makeIcon(String text, int size, String color) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: " + size + "px; -fx-text-fill: " + color + ";");
        return l;
    }
    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        String[] words = s.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words)
            if (!w.isEmpty()) sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        return sb.toString().trim();
    }
}