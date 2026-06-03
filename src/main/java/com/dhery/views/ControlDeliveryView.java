package com.dhery.views;

import com.dhery.app.AppState;
import com.dhery.app.AppState.PedidoCocina;
import com.dhery.app.AppState.Repartidor;
import com.dhery.app.Router;
import com.dhery.models.user;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ControlDeliveryView {

    private static final String RED      = "#CC0000";
    private static final String RED_DARK = "#AA0000";
    private static final String GREEN    = "#2E7D32";
    private static final String GREEN_L  = "#4CAF50";
    private static final String ORANGE   = "#E8890C";
    private static final String TEXT_D   = "#1A1A1A";
    private static final String TEXT_G   = "#777777";
    private static final String BG       = "#F8F8F8";

    private static Label lblReloj;
    private static Timeline relojTimeline;
    private static user currentUser;

    // Referencia a la vista del pedido seleccionado (panel inferior izq)
    private static VBox detallePanel;
    private static PedidoCocina pedidoSeleccionado = null;

    // Panel de proceso (derecha media)
    private static VBox procesoPanel;
    private static Label lblTimerGrande;
    private static Timeline timerDelivery;
    private static int timerSegundos = 0;

    // Panel notificación bottom
    private static VBox notifToast;

    // Cola de pedidos listos para delivery
    private static VBox colaLista;
    private static VBox repartidoresList;

    public static Scene getScene(user u) {
        currentUser = u;

        // No poblar datos de muestra aquí — EstadoCocinaView ya lo hace si es necesario

        // ── ROOT ──────────────────────────────────────────────────────────────
        VBox mainLayout = new VBox(0);
        mainLayout.setStyle("-fx-background-color: " + BG + ";");

        // Contenido scrollable
        HBox content = new HBox(16);
        content.setPadding(new Insets(20));
        VBox.setVgrow(content, Priority.ALWAYS);

        // ── COLUMNA IZQUIERDA ─────────────────────────────────────────────────
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        HBox header = buildHeader();
        VBox colaPanel = buildColaPanel();
        detallePanel = buildDetallePanel(null);

        leftCol.getChildren().addAll(header, colaPanel, detallePanel);

        // ── COLUMNA DERECHA ───────────────────────────────────────────────────
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(360);
        rightCol.setMinWidth(360);

        VBox repartidoresPanel = buildRepartidoresPanel();
        procesoPanel = buildProcesoPanel();
        notifToast = buildToast();

        rightCol.getChildren().addAll(repartidoresPanel, procesoPanel, notifToast);

        content.getChildren().addAll(leftCol, rightCol);

        // Botón atrás
        HBox bottom = buildBottomBar();

        mainLayout.getChildren().addAll(content, bottom);

        Scene scene = new Scene(mainLayout, 1280, 720);
        iniciarReloj();
        return scene;
    }

    // ── ENCABEZADO ────────────────────────────────────────────────────────────
    private static HBox buildHeader() {
        HBox hb = new HBox(14);
        hb.setAlignment(Pos.CENTER_LEFT);

        StackPane iconCircle = makeIconCircle("🛵");

        VBox titleGroup = new VBox(2);
        Label title = new Label("CONTROL DE DELIVERY");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        Label sub = new Label("Gestiona la cola de pedidos y el estado de entrega");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_G + ";");
        titleGroup.getChildren().addAll(title, sub);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        VBox clockBox = new VBox(2);
        clockBox.setAlignment(Pos.CENTER_RIGHT);
        clockBox.setPadding(new Insets(8, 14, 8, 14));
        clockBox.setStyle("-fx-background-color: white; -fx-border-color: #EEEEEE;" +
            " -fx-border-radius: 10; -fx-background-radius: 10;");
        lblReloj = new Label(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lblReloj.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        Label lblFecha = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblFecha.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_G + ";");
        clockBox.getChildren().addAll(lblReloj, lblFecha);

        hb.getChildren().addAll(iconCircle, titleGroup, sp, clockBox);
        return hb;
    }

    // ── COLA DE PEDIDOS ───────────────────────────────────────────────────────
    private static VBox buildColaPanel() {
        VBox panel = cardPanel();

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label("📋");
        ico.setStyle("-fx-font-size: 18px; -fx-text-fill: " + RED + ";");
        Label title = new Label("COLA DE PEDIDOS");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");
        titleRow.getChildren().addAll(ico, title);

        HBox colHeader = new HBox(0);
        colHeader.setStyle("-fx-background-color: " + RED + "; -fx-padding: 10 16 10 16;");
        String[] cols = {"N°","CLIENTE","PEDIDO","TOTAL (Bs)","TIEMPO RESTANTE","ESTADO"};
        double[] ws = {40, 100, 180, 90, 130, 130};
        for (int i = 0; i < cols.length; i++) {
            Label l = new Label(cols[i]);
            l.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
            l.setMinWidth(ws[i]);
            colHeader.getChildren().add(l);
        }

        colaLista = new VBox(0);
        colaLista.setStyle("-fx-background-color: white;");
        refreshCola();

        AppState.pedidosCocina.addListener((javafx.collections.ListChangeListener<PedidoCocina>) c -> refreshCola());

        panel.getChildren().addAll(titleRow, colHeader, colaLista);
        return panel;
    }

    private static void refreshCola() {
        if (colaLista == null) return;
        colaLista.getChildren().clear();
        int idx = 0;
        for (PedidoCocina p : AppState.pedidosCocina) {
            colaLista.getChildren().add(buildColaRow(p, idx % 2 == 0));
            idx++;
        }
    }

    private static HBox buildColaRow(PedidoCocina p, boolean par) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 16, 10, 16));
        row.setStyle("-fx-background-color: " + (par ? "white" : "#FAFAFA") + ";" +
            "-fx-border-color: transparent transparent #F0F0F0 transparent; -fx-border-width: 1;" +
            "-fx-cursor: hand;");

        // N°
        StackPane badge = new StackPane();
        Circle bg = new Circle(13); bg.setFill(Color.web("#FFEAEA"));
        Label num = new Label(String.valueOf(p.id));
        num.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");
        badge.getChildren().addAll(bg, num);
        badge.setMinWidth(40);

        // Cliente
        Label clienteLbl = new Label(p.cliente); clienteLbl.setMinWidth(100);
        clienteLbl.setStyle("-fx-text-fill: " + TEXT_D + ";");

        // Pedido
        Label pedidoLbl = new Label(p.plato); pedidoLbl.setMinWidth(180);
        pedidoLbl.setStyle("-fx-text-fill: " + TEXT_D + ";");
        pedidoLbl.setWrapText(false);

        // Total
        Label totalLbl = new Label(String.format("%.2f", p.total)); totalLbl.setMinWidth(90);
        totalLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");

        // Tiempo
        Label tiempoLbl = new Label(formatTiempo(p.tiempoSegundos)); tiempoLbl.setMinWidth(130);
        tiempoLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + ORANGE + ";");

        // Countdown
        Timeline cd = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            if (p.tiempoSegundos > 0) { p.tiempoSegundos--; tiempoLbl.setText(formatTiempo(p.tiempoSegundos)); }
        }));
        cd.setCycleCount(Animation.INDEFINITE);
        cd.play();

        // Estado
        Label estadoLbl = new Label(p.estado.equals("LISTO PARA ENTREGAR") ? "LISTO" : "EN PREPARACIÓN");
        estadoLbl.setMinWidth(130);
        boolean listo = p.estado.equals("LISTO PARA ENTREGAR");
        estadoLbl.setStyle("-fx-background-color: " + (listo ? "#E8F5E9" : "#FFF3E0") + ";" +
            "-fx-text-fill: " + (listo ? GREEN_L : ORANGE) + ";" +
            "-fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-padding: 3 8 3 8; -fx-background-radius: 10;");

        row.getChildren().addAll(badge, clienteLbl, pedidoLbl, totalLbl, tiempoLbl, estadoLbl);

        // Seleccionar fila
        row.setOnMouseClicked(e -> seleccionarPedido(p, row));

        return row;
    }

    // ── DETALLE DEL PEDIDO SELECCIONADO ──────────────────────────────────────
    private static VBox buildDetallePanel(PedidoCocina p) {
        VBox panel = cardPanel();

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label("📋");
        ico.setStyle("-fx-font-size: 16px; -fx-text-fill: " + RED + ";");
        Label title = new Label("DETALLE DEL PEDIDO SELECCIONADO");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");
        titleRow.getChildren().addAll(ico, title);

        if (p == null) {
            Label hint = new Label("Haz clic en un pedido de la cola para ver su detalle.");
            hint.setStyle("-fx-text-fill: " + TEXT_G + "; -fx-font-size: 13px;");
            panel.getChildren().addAll(titleRow, hint);
            return panel;
        }

        // Info del pedido
        GridPane info = new GridPane();
        info.setHgap(20); info.setVgap(10);

        addInfoRow(info, "Cliente:", "👤  " + p.cliente, 0, 0);
        addInfoRow(info, "Teléfono:", "📞  71234567", 0, 1);
        addInfoRow(info, "Dirección:", "📍  " + (p.esDelivery ? p.direccion : "Local"), 0, 2);

        // Productos
        StringBuilder prods = new StringBuilder();
        if (p.productos != null && !p.productos.isEmpty()) {
            for (String pr : p.productos) prods.append("• ").append(pr).append("\n");
        } else {
            prods.append("• 1 x ").append(p.plato);
        }
        VBox pedidoBox = new VBox(2);
        Label pedLbl = new Label("Pedido:"); pedLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        Label pedVal = new Label(prods.toString().trim());
        pedVal.setStyle("-fx-text-fill: " + TEXT_D + ";");
        pedidoBox.getChildren().addAll(pedLbl, pedVal);
        info.add(pedidoBox, 0, 3);

        Label totalLbl = new Label("Total:");
        totalLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        Label totalVal = new Label("Bs " + String.format("%.2f", p.total));
        totalVal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");
        info.add(totalLbl, 0, 4);
        info.add(totalVal, 1, 4);

        addInfoRow(info, "Método de pago:", "💳  " + p.metodoPago, 0, 5);

        // Columna derecha: tiempo
        VBox tiempoRight = new VBox(10);
        tiempoRight.setAlignment(Pos.TOP_LEFT);
        addInfoRow(info, "Tiempo estimado:", "1 minuto", 2, 0);
        addInfoRow(info, "Tiempo restante:", "", 2, 1);
        Label tiempoVal = new Label(formatTiempo(p.tiempoSegundos));
        tiempoVal.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + ORANGE + ";");
        info.add(tiempoVal, 2, 2);

        // Mini donut visual
        Label miniDonut = new Label("🛵");
        miniDonut.setStyle("-fx-font-size: 36px;");
        info.add(miniDonut, 3, 1, 1, 3);

        ColumnConstraints cc1 = new ColumnConstraints(); cc1.setMinWidth(120);
        ColumnConstraints cc2 = new ColumnConstraints(); cc2.setMinWidth(180);
        ColumnConstraints cc3 = new ColumnConstraints(); cc3.setMinWidth(120);
        ColumnConstraints cc4 = new ColumnConstraints(); cc4.setMinWidth(100);
        info.getColumnConstraints().addAll(cc1, cc2, cc3, cc4);

        panel.getChildren().addAll(titleRow, info);
        return panel;
    }

    private static void addInfoRow(GridPane grid, String label, String value, int col, int row) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #555; -fx-font-size: 12px;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + TEXT_D + "; -fx-font-size: 12px;");
        grid.add(lbl, col, row);
        grid.add(val, col + 1, row);
    }

    private static void seleccionarPedido(PedidoCocina p, HBox selectedRow) {
        pedidoSeleccionado = p;
        // Actualizar detalle
        if (detallePanel != null && detallePanel.getParent() instanceof VBox parentCol) {
            int idx = parentCol.getChildren().indexOf(detallePanel);
            VBox newDetalle = buildDetallePanel(p);
            parentCol.getChildren().set(idx, newDetalle);
            detallePanel = newDetalle;
        }
        // Actualizar proceso panel
        actualizarProcesoPanel(p);
    }

    // ── REPARTIDORES ─────────────────────────────────────────────────────────
    private static VBox buildRepartidoresPanel() {
        VBox panel = cardPanel();

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label("👤");
        ico.setStyle("-fx-font-size: 16px; -fx-text-fill: " + RED + ";");
        Label title = new Label("ESTADO DE REPARTIDORES");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");
        titleRow.getChildren().addAll(ico, title);

        repartidoresList = new VBox(8);
        refreshRepartidores();

        panel.getChildren().addAll(titleRow, repartidoresList);
        return panel;
    }

    private static void refreshRepartidores() {
        if (repartidoresList == null) return;
        repartidoresList.getChildren().clear();
        for (Repartidor r : AppState.repartidores) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Label moto = new Label("🛵");
            moto.setStyle("-fx-font-size: 18px;");
            Label nombre = new Label(r.nombre);
            nombre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
            nombre.setMinWidth(60);

            Label badgeEstado = new Label(r.ocupado ? "OCUPADO" : "LIBRE");
            badgeEstado.setStyle("-fx-background-color: " + (r.ocupado ? "#FFEAEA" : "#E8F5E9") + ";" +
                "-fx-text-fill: " + (r.ocupado ? RED : GREEN_L) + ";" +
                "-fx-font-size: 10px; -fx-font-weight: bold;" +
                "-fx-padding: 3 8 3 8; -fx-background-radius: 8;");

            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

            Label tiempoLbl = new Label(r.ocupado ? "Entrega en " + formatTiempo(r.tiempoRestanteSeg) : "Disponible");
            tiempoLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + (r.ocupado ? ORANGE : GREEN_L) + ";");

            row.getChildren().addAll(moto, nombre, badgeEstado, sp, tiempoLbl);
            repartidoresList.getChildren().add(row);
        }
    }

    // ── PANEL PROCESO (PEDIDO EN PROCESO) ─────────────────────────────────────
    private static VBox buildProcesoPanel() {
        VBox panel = cardPanel();
        panel.setAlignment(Pos.CENTER);

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label("📦");
        ico.setStyle("-fx-font-size: 16px; -fx-text-fill: " + RED + ";");
        Label title = new Label("PEDIDO EN PROCESO");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");
        titleRow.getChildren().addAll(ico, title);

        // Donut visual (arco SVG aproximado con StackPane)
        StackPane donutStack = new StackPane();
        donutStack.setPrefSize(140, 140);

        Arc arcBg = new Arc(70, 70, 55, 55, 0, 360);
        arcBg.setType(ArcType.OPEN);
        arcBg.setStroke(Color.web("#EEEEEE"));
        arcBg.setStrokeWidth(12);
        arcBg.setFill(Color.TRANSPARENT);

        Arc arcFg = new Arc(70, 70, 55, 55, 90, -270);
        arcFg.setType(ArcType.OPEN);
        arcFg.setStroke(Color.web(ORANGE));
        arcFg.setStrokeWidth(12);
        arcFg.setFill(Color.TRANSPARENT);
        arcFg.setId("arcFg");

        lblTimerGrande = new Label("--:--");
        lblTimerGrande.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + ORANGE + ";");
        VBox center = new VBox(2, lblTimerGrande, new Label("Tiempo restante\ndel pedido"){{
            setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_G + "; -fx-text-alignment: center;");
            setAlignment(Pos.CENTER);
        }});
        center.setAlignment(Pos.CENTER);

        donutStack.getChildren().addAll(arcBg, arcFg, center);

        // Botón marcar como entregado
        Button btnEntregado = new Button("✓  MARCAR COMO ENTREGADO");
        btnEntregado.setMaxWidth(Double.MAX_VALUE);
        btnEntregado.setPrefHeight(44);
        btnEntregado.setStyle("-fx-background-color: white; -fx-text-fill: " + RED + ";" +
            "-fx-border-color: " + RED + "; -fx-border-width: 1.5; -fx-border-radius: 8;" +
            "-fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand;");
        btnEntregado.setOnAction(e -> marcarEntregado(arcFg));

        panel.getChildren().addAll(titleRow, donutStack, btnEntregado);
        return panel;
    }

    private static void actualizarProcesoPanel(PedidoCocina p) {
        if (procesoPanel == null || p == null) return;
        lblTimerGrande.setText(formatTiempo(p.tiempoSegundos));
        double pct = (double) p.tiempoSegundos / p.tiempoInicial;

        // Animar arc
        Arc arcFg = (Arc) procesoPanel.lookup("#arcFg");
        if (arcFg != null) arcFg.setLength(-360 * pct);

        // Countdown del timer grande
        if (timerDelivery != null) timerDelivery.stop();
        timerDelivery = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            if (p.tiempoSegundos > 0) {
                p.tiempoSegundos--;
                lblTimerGrande.setText(formatTiempo(p.tiempoSegundos));
                double pct2 = (double) p.tiempoSegundos / p.tiempoInicial;
                if (arcFg != null) arcFg.setLength(-360 * pct2);
            }
        }));
        timerDelivery.setCycleCount(Animation.INDEFINITE);
        timerDelivery.play();

        // Asignar repartidor libre
        for (Repartidor r : AppState.repartidores) {
            if (!r.ocupado) {
                r.ocupado = true;
                r.tiempoRestanteSeg = p.tiempoSegundos;
                r.pedidoAsignado = p.plato;
                Timeline repTimeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
                    if (r.tiempoRestanteSeg > 0) r.tiempoRestanteSeg--;
                }));
                repTimeline.setCycleCount(p.tiempoSegundos);
                repTimeline.play();
                refreshRepartidores();
                break;
            }
        }
    }

    private static void marcarEntregado(Arc arcFg) {
        if (pedidoSeleccionado == null) return;

        // Liberar repartidor
        for (Repartidor r : AppState.repartidores) {
            if (r.ocupado && r.pedidoAsignado.equals(pedidoSeleccionado.plato)) {
                r.ocupado = false;
                r.tiempoRestanteSeg = 0;
                r.pedidoAsignado = "";
                break;
            }
        }

        // Notificación
        AppState.agregarNotificacion("DELIVERY_OK",
            "📦 Pedido entregado: " + pedidoSeleccionado.plato + " → " + pedidoSeleccionado.cliente);

        // Registrar en GestorArchivo
        new com.dhery.GestorArchivo.ArchivoManager().agregarLinea(
            "delivery_historial.txt",
            java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
            " | ENTREGADO | " + pedidoSeleccionado.plato + " | " + pedidoSeleccionado.cliente +
            " | Bs " + String.format("%.2f", pedidoSeleccionado.total)
        );

        // Remover de cola
        AppState.pedidosCocina.remove(pedidoSeleccionado);
        pedidoSeleccionado = null;
        lblTimerGrande.setText("--:--");
        if (arcFg != null) arcFg.setLength(-360);
        if (timerDelivery != null) timerDelivery.stop();
        refreshRepartidores();

        // Mostrar toast
        mostrarToast("¡PEDIDO REALIZADO!", "El pedido ha sido entregado correctamente.");
    }

    // ── TOAST ─────────────────────────────────────────────────────────────────
    private static VBox buildToast() {
        VBox toast = new VBox(5);
        toast.setPadding(new Insets(14, 18, 14, 18));
        toast.setStyle("-fx-background-color: white; -fx-background-radius: 12;" +
            "-fx-border-color: " + GREEN_L + "; -fx-border-radius: 12; -fx-border-width: 1.5;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.12),10,0,0,3);");
        toast.setVisible(false);
        toast.setManaged(false);
        return toast;
    }

    private static void mostrarToast(String titulo, String msg) {
        if (notifToast == null) return;
        notifToast.getChildren().clear();

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label checkIco = new Label("✅");
        checkIco.setStyle("-fx-font-size: 22px;");
        VBox textBox = new VBox(2);
        Label titLbl = new Label(titulo);
        titLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + GREEN + ";");
        Label msgLbl = new Label(msg);
        msgLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_G + ";");
        textBox.getChildren().addAll(titLbl, msgLbl);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #999;");
        closeBtn.setOnAction(e -> { notifToast.setVisible(false); notifToast.setManaged(false); });
        row.getChildren().addAll(checkIco, textBox, sp, closeBtn);
        notifToast.getChildren().add(row);
        notifToast.setVisible(true);
        notifToast.setManaged(true);

        // Auto-ocultar en 5s
        new Timeline(new KeyFrame(Duration.seconds(5), ev -> {
            notifToast.setVisible(false); notifToast.setManaged(false);
        })).play();
    }

    // ── BARRA INFERIOR ────────────────────────────────────────────────────────
    private static HBox buildBottomBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(12, 20, 12, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: white; -fx-border-color: #EEEEEE; -fx-border-width: 1 0 0 0;");

        Button btnBack = redOutlineButton("← ATRÁS");
        btnBack.setOnAction(e -> {
            if (relojTimeline != null) relojTimeline.stop();
            if (timerDelivery != null) timerDelivery.stop();
            Router.goMenuCajeroView(currentUser);
        });
        bar.getChildren().add(btnBack);
        return bar;
    }

    // ── RELOJ ─────────────────────────────────────────────────────────────────
    private static void iniciarReloj() {
        if (relojTimeline != null) relojTimeline.stop();
        relojTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (lblReloj != null)
                lblReloj.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }));
        relojTimeline.setCycleCount(Animation.INDEFINITE);
        relojTimeline.play();
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private static String formatTiempo(int seg) {
        return String.format("%02d:%02d", seg / 60, seg % 60);
    }

    private static VBox cardPanel() {
        VBox p = new VBox(12);
        p.setPadding(new Insets(16));
        p.setStyle("-fx-background-color: white; -fx-background-radius: 12;" +
            "-fx-border-color: #EEEEEE; -fx-border-radius: 12; -fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.05),8,0,0,2);");
        return p;
    }

    private static StackPane makeIconCircle(String emoji) {
        StackPane sp = new StackPane();
        Circle bg = new Circle(32); bg.setFill(Color.web("#FFEAEA"));
        Label lbl = new Label(emoji); lbl.setStyle("-fx-font-size: 22px;");
        sp.getChildren().addAll(bg, lbl); sp.setMaxSize(64, 64);
        return sp;
    }

    private static Button redOutlineButton(String text) {
        Button btn = new Button(text); btn.setPrefHeight(42);
        String normal = "-fx-background-color: white; -fx-text-fill: " + RED + ";" +
            " -fx-border-color: " + RED + "; -fx-border-width: 1.5; -fx-border-radius: 8;" +
            " -fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(normal.replace("white", "#FFF5F5")));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
        return btn;
    }
}
