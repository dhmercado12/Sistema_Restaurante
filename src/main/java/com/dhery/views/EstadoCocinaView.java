package com.dhery.views;

import com.dhery.app.AppState;
import com.dhery.app.AppState.PedidoCocina;
import com.dhery.app.AppState.PedidoFinalizado;
import com.dhery.app.Router;
import com.dhery.models.user;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EstadoCocinaView {

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

    // Labels del resumen (actualizables)
    private static Label lblActivos, lblTiempoProm, lblListos, lblEnProceso;

    // Flag para evitar bucle con datos de muestra
    private static boolean datosMuestraCargados = false;

    public static Scene getScene(user u) {
        currentUser = u;

        // Poblar datos de muestra SOLO la primera vez
        if (AppState.pedidosCocina.isEmpty() && !datosMuestraCargados) {
            poblarDatosMuestra();
            datosMuestraCargados = true;
        }

        // ── ROOT ──────────────────────────────────────────────────────────────
        HBox root = new HBox(16);
        root.setStyle("-fx-background-color: " + BG + ";");
        root.setPadding(new Insets(20));

        // ── COLUMNA IZQUIERDA ─────────────────────────────────────────────────
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        // Encabezado
        HBox header = buildHeader();

        // Panel pedidos activos
        VBox activosPanel = buildActivosPanel();

        // Panel en espera
        VBox esperaPanel = buildEsperaPanel();

        leftCol.getChildren().addAll(header, activosPanel, esperaPanel);

        // ── COLUMNA DERECHA ───────────────────────────────────────────────────
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(380);
        rightCol.setMinWidth(380);

        VBox resumenPanel = buildResumenPanel();
        VBox historialPanel = buildHistorialPanel();

        rightCol.getChildren().addAll(resumenPanel, historialPanel);
        VBox.setVgrow(historialPanel, Priority.ALWAYS);

        root.getChildren().addAll(leftCol, rightCol);

        // ── BARRA INFERIOR ────────────────────────────────────────────────────
        HBox bottom = buildBottomBar();

        VBox mainLayout = new VBox(0);
        mainLayout.setStyle("-fx-background-color: " + BG + ";");
        mainLayout.getChildren().addAll(root, bottom);
        VBox.setVgrow(root, Priority.ALWAYS);

        Scene scene = new Scene(mainLayout, 1280, 720);

        // Iniciar reloj
        iniciarReloj();

        return scene;
    }

    // ── ENCABEZADO ────────────────────────────────────────────────────────────
    private static HBox buildHeader() {
        HBox hb = new HBox(14);
        hb.setAlignment(Pos.CENTER_LEFT);

        StackPane iconCircle = makeIconCircle("👨‍🍳");

        VBox titleGroup = new VBox(2);
        Label title = new Label("ESTADO DE COCINA");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        Label sub = new Label("Monitorea los pedidos en preparación en tiempo real");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_G + ";");
        titleGroup.getChildren().addAll(title, sub);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        // Reloj
        VBox clockBox = new VBox(2);
        clockBox.setAlignment(Pos.CENTER_RIGHT);
        clockBox.setPadding(new Insets(8, 14, 8, 14));
        clockBox.setStyle("-fx-background-color: white; -fx-border-color: #EEEEEE;" +
            " -fx-border-radius: 10; -fx-background-radius: 10;");
        lblReloj = new Label(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lblReloj.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        Label lblFecha = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblFecha.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_G + ";");
        clockBox.getChildren().addAll(lblReloj, lblFecha);

        hb.getChildren().addAll(iconCircle, titleGroup, sp, clockBox);
        return hb;
    }

    // ── PANEL PEDIDOS ACTIVOS ─────────────────────────────────────────────────
    private static VBox buildActivosPanel() {
        VBox panel = cardPanel();

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label("👨‍🍳");
        ico.setStyle("-fx-font-size: 18px; -fx-text-fill: " + RED + ";");
        Label title = sectionTitle("PEDIDOS ACTIVOS (5 MÁX.)");
        titleRow.getChildren().addAll(ico, title);

        // Encabezado tabla
        HBox colHeader = buildColHeader(new String[]{"ID","PLATO","TIEMPO ESTIMADO","ESTADO"},
                                         new double[]{50, 300, 150, 150});

        VBox lista = new VBox(0);
        lista.setStyle("-fx-background-color: white;");

        // Construir filas desde AppState
        refreshActivosList(lista);

        // Escuchar cambios
        AppState.pedidosCocina.addListener((javafx.collections.ListChangeListener<PedidoCocina>) c -> {
            refreshActivosList(lista);
            actualizarResumen();
        });

        panel.getChildren().addAll(titleRow, colHeader, lista);
        return panel;
    }

    private static void refreshActivosList(VBox lista) {
        lista.getChildren().clear();
        int index = 0;
        for (PedidoCocina p : AppState.pedidosCocina) {
            lista.getChildren().add(buildPedidoRow(p, index % 2 == 0));
            index++;
        }
    }

    private static HBox buildPedidoRow(PedidoCocina p, boolean par) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 16, 10, 16));
        row.setStyle("-fx-background-color: " + (par ? "white" : "#FAFAFA") + ";" +
            "-fx-border-color: transparent transparent #F0F0F0 transparent; -fx-border-width: 1;");

        // ID badge
        StackPane idBadge = new StackPane();
        Circle bg = new Circle(14); bg.setFill(Color.web("#FFEAEA"));
        Label idLbl = new Label(String.valueOf(p.id));
        idLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");
        idBadge.getChildren().addAll(bg, idLbl);
        idBadge.setMinWidth(50);

        // Plato + info
        VBox platoInfo = new VBox(2);
        platoInfo.setMinWidth(300);
        Label platoLbl = new Label(p.plato);
        platoLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        Label infoLbl = new Label("Mesa: " + p.mesa + "  |  Cliente: " + p.cliente);
        infoLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_G + ";");
        platoInfo.getChildren().addAll(platoLbl, infoLbl);

        // Tiempo con countdown
        Label tiempoLbl = new Label(formatTiempo(p.tiempoSegundos));
        tiempoLbl.setMinWidth(150);
        tiempoLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + ORANGE + ";");

        // Estado badge
        Label estadoLbl = buildEstadoBadge(p.estado);
        estadoLbl.setMinWidth(150);

        // Botón marcar listo (solo si EN PROCESO)
        Button btnListo = new Button("✓ Marcar listo");
        btnListo.setStyle("-fx-background-color: " + GREEN_L + "; -fx-text-fill: white;" +
            " -fx-font-size: 11px; -fx-background-radius: 6; -fx-cursor: hand;" +
            " -fx-font-weight: bold; -fx-padding: 6 12 6 12;");
        btnListo.setVisible(p.estado.equals("EN PROCESO"));
        btnListo.setManaged(p.estado.equals("EN PROCESO"));
        btnListo.setOnAction(e -> marcarListo(p));

        row.getChildren().addAll(idBadge, platoInfo, tiempoLbl, estadoLbl, btnListo);

        // Countdown timer
        if (p.estado.equals("EN PROCESO")) {
            Timeline countdown = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
                if (p.tiempoSegundos > 0) {
                    p.tiempoSegundos--;
                    tiempoLbl.setText(formatTiempo(p.tiempoSegundos));
                }
            }));
            countdown.setCycleCount(Animation.INDEFINITE);
            countdown.play();
        }

        return row;
    }

    private static void marcarListo(PedidoCocina p) {
        p.estado = "LISTO PARA ENTREGAR";
        // Notificación
        AppState.agregarNotificacion("PEDIDO_LISTO",
            "✅ Pedido listo: " + p.plato + " para " + p.cliente);
        // Registrar en GestorArchivo
        new com.dhery.GestorArchivo.ArchivoManager().agregarLinea(
            "cocina_historial.txt",
            java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
            " | LISTO | " + p.plato + " | " + p.cliente + " | Mesa: " + p.mesa
        );
        // Historial reciente
        AppState.historialReciente.add(0, new PedidoFinalizado(p.plato, p.mesa, p.cliente));
        // Forzar refresh
        ObservableList<PedidoCocina> tmp = javafx.collections.FXCollections.observableArrayList(AppState.pedidosCocina);
        AppState.pedidosCocina.setAll(tmp);
        actualizarResumen();
    }

    // ── PANEL EN ESPERA ───────────────────────────────────────────────────────
    private static VBox buildEsperaPanel() {
        VBox panel = cardPanel();

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label("⏳");
        ico.setStyle("-fx-font-size: 16px; -fx-text-fill: " + RED + ";");
        Label title = sectionTitle("EN ESPERA (SIN PEDIDOS EN COLA)");
        titleRow.getChildren().addAll(ico, title);

        HBox msgBox = new HBox(12);
        msgBox.setAlignment(Pos.CENTER_LEFT);
        msgBox.setPadding(new Insets(16));
        msgBox.setStyle("-fx-background-color: #F0FFF0; -fx-background-radius: 10;" +
            "-fx-border-color: " + GREEN_L + "; -fx-border-radius: 10; -fx-border-width: 1;");
        Label checkIco = new Label("✅");
        checkIco.setStyle("-fx-font-size: 20px;");
        VBox msgText = new VBox(2);
        Label msg1 = new Label("No hay pedidos en cola en este momento.");
        msg1.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
        Label msg2 = new Label("¡Excelente! La cocina está al día.");
        msg2.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_G + ";");
        msgText.getChildren().addAll(msg1, msg2);
        msgBox.getChildren().addAll(checkIco, msgText);

        panel.getChildren().addAll(titleRow, msgBox);
        return panel;
    }

    // ── PANEL RESUMEN ─────────────────────────────────────────────────────────
    private static VBox buildResumenPanel() {
        VBox panel = cardPanel();

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label("📊");
        ico.setStyle("-fx-font-size: 18px; -fx-text-fill: " + RED + ";");
        Label title = sectionTitle("RESUMEN DE COCINA");
        titleRow.getChildren().addAll(ico, title);

        // Métricas 2x2
        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);

        lblActivos   = bigMetric("5", "🍛", "#FFEAEA", RED, "Pedidos Activos", "En preparación");
        lblTiempoProm = bigMetric("14:15", "🕐", "#FFF3E0", ORANGE, "Tiempo Promedio", "de preparación");
        lblListos    = bigMetric("2", "✅", "#E8F5E9", GREEN_L, "Listos para Entregar", "Pedidos listos");
        lblEnProceso = bigMetric("3", "👥", "#EDE7F6", "#7B1FA2", "En Proceso", "Preparándose");

        actualizarResumen();

        grid.add(lblActivos,    0, 0);
        grid.add(lblTiempoProm, 1, 0);
        grid.add(lblListos,     0, 1);
        grid.add(lblEnProceso,  1, 1);
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(50);
        grid.getColumnConstraints().addAll(cc, new ColumnConstraints(){{ setPercentWidth(50); }});

        panel.getChildren().addAll(titleRow, grid);
        return panel;
    }

    private static Label bigMetric(String val, String ico, String bgColor,
                                   String textColor, String label, String sub) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(14));
        box.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 12;");
        box.setAlignment(Pos.CENTER_LEFT);

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);
        Label icoLbl = new Label(ico);
        icoLbl.setStyle("-fx-font-size: 22px;");
        Label valLbl = new Label(val);
        valLbl.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
        valLbl.setId("val"); // para actualizar después
        top.getChildren().addAll(icoLbl, valLbl);

        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        Label subLbl = new Label(sub);
        subLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_G + ";");

        box.getChildren().addAll(top, labelLbl, subLbl);
        // Usar la Label contenedora como referencia retornando el VBox envuelto en Label dummy
        Label dummy = new Label();
        dummy.setGraphic(box);
        dummy.setId("metric_" + label.replace(" ", "_"));
        return dummy;
    }

    private static void actualizarResumen() {
        if (lblActivos == null) return;
        long activos = AppState.pedidosCocina.size();
        long listos = AppState.pedidosCocina.stream()
            .filter(p -> p.estado.equals("LISTO PARA ENTREGAR")).count();
        long enProceso = AppState.pedidosCocina.stream()
            .filter(p -> p.estado.equals("EN PROCESO")).count();
        double promSeg = AppState.pedidosCocina.stream()
            .mapToInt(p -> p.tiempoInicial).average().orElse(0);
        int promMin = (int)(promSeg / 60);
        int promSec = (int)(promSeg % 60);

        // Actualizar texto de las métricas buscando el VBox hijo
        updateMetricVal(lblActivos, String.valueOf(activos));
        updateMetricVal(lblTiempoProm, String.format("%02d:%02d", promMin, promSec));
        updateMetricVal(lblListos, String.valueOf(listos));
        updateMetricVal(lblEnProceso, String.valueOf(enProceso));
    }

    private static void updateMetricVal(Label metricLabel, String value) {
        if (metricLabel == null || metricLabel.getGraphic() == null) return;
        VBox box = (VBox) metricLabel.getGraphic();
        if (box.getChildren().isEmpty()) return;
        HBox top = (HBox) box.getChildren().get(0);
        if (top.getChildren().size() >= 2) {
            Label valLbl = (Label) top.getChildren().get(1);
            valLbl.setText(value);
        }
    }

    // ── HISTORIAL RECIENTE ────────────────────────────────────────────────────
    private static VBox buildHistorialPanel() {
        VBox panel = cardPanel();
        VBox.setVgrow(panel, Priority.ALWAYS);

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label("🔄");
        ico.setStyle("-fx-font-size: 16px; -fx-text-fill: " + RED + ";");
        Label title = sectionTitle("HISTORIAL RECIENTE");
        titleRow.getChildren().addAll(ico, title);

        VBox lista = new VBox(0);
        lista.setStyle("-fx-background-color: white;");

        ScrollPane sp = new ScrollPane(lista);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: white;");
        VBox.setVgrow(sp, Priority.ALWAYS);

        // Poblar
        refreshHistorial(lista);
        AppState.historialReciente.addListener((javafx.collections.ListChangeListener<PedidoFinalizado>) c ->
            refreshHistorial(lista));

        panel.getChildren().addAll(titleRow, sp);
        return panel;
    }

    private static void refreshHistorial(VBox lista) {
        lista.getChildren().clear();
        int max = Math.min(AppState.historialReciente.size(), 8);
        for (int i = 0; i < max; i++) {
            PedidoFinalizado pf = AppState.historialReciente.get(i);
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 14, 10, 14));
            row.setStyle("-fx-border-color: transparent transparent #F0F0F0 transparent;" +
                "-fx-border-width: 1; -fx-background-color: " + (i%2==0?"white":"#FAFAFA") + ";");

            Label checkIco = new Label("✅");
            checkIco.setStyle("-fx-font-size: 14px; -fx-text-fill: " + GREEN_L + ";");

            VBox info = new VBox(2);
            Label platoLbl = new Label(pf.plato);
            platoLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
            Label subLbl = new Label("Mesa: " + pf.mesa + "  |  " + pf.cliente);
            subLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_G + ";");
            info.getChildren().addAll(platoLbl, subLbl);

            Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

            Label horaLbl = new Label(pf.hora);
            horaLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GREEN_L + "; -fx-font-weight: bold;");

            row.getChildren().addAll(checkIco, info, spacer, horaLbl);
            lista.getChildren().add(row);
        }
    }

    // ── BARRA INFERIOR ────────────────────────────────────────────────────────
    private static HBox buildBottomBar() {
        HBox bar = new HBox(14);
        bar.setPadding(new Insets(12, 20, 12, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: white; -fx-border-color: #EEEEEE;" +
            " -fx-border-width: 1 0 0 0;");

        Button btnBack = redOutlineButton("← VOLVER");
        btnBack.setOnAction(e -> {
            if (relojTimeline != null) relojTimeline.stop();
            Router.goMenuCajeroView(currentUser);
        });

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        // Actualizar
        Button btnActualizar = new Button("🔄  ACTUALIZAR");
        btnActualizar.setPrefHeight(44);
        btnActualizar.setStyle("-fx-background-color: white; -fx-text-fill: " + TEXT_D + ";" +
            " -fx-border-color: #DDD; -fx-border-radius: 8; -fx-background-radius: 8;" +
            " -fx-font-weight: bold; -fx-cursor: hand;");
        Label lblUltimaAct = new Label("Última actualización: " +
            LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lblUltimaAct.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_G + ";");
        btnActualizar.setOnAction(e -> {
            lblUltimaAct.setText("Última actualización: " +
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });

        VBox actBox = new VBox(2, btnActualizar, lblUltimaAct);
        actBox.setAlignment(Pos.CENTER);

        // Notificar listos
        long listos = AppState.pedidosCocina.stream()
            .filter(p -> p.estado.equals("LISTO PARA ENTREGAR")).count();
        Button btnNotif = new Button("🔔  NOTIFICAR LISTOS");
        btnNotif.setPrefHeight(48);
        btnNotif.setPrefWidth(200);
        btnNotif.setStyle("-fx-background-color: " + RED + "; -fx-text-fill: white;" +
            " -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8; -fx-cursor: hand;");

        StackPane notifStack = new StackPane(btnNotif);
        if (listos > 0) {
            Label badge = new Label(String.valueOf(listos));
            badge.setStyle("-fx-background-color: #FF6B00; -fx-text-fill: white;" +
                " -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 2 5 2 5;" +
                " -fx-background-radius: 8;");
            badge.setTranslateX(85);
            badge.setTranslateY(-18);
            notifStack.getChildren().add(badge);
        }
        Label notifSub = new Label("Notificar pedidos listos para entrega");
        notifSub.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_G + ";");
        VBox notifBox = new VBox(3, notifStack, notifSub);
        notifBox.setAlignment(Pos.CENTER);

        btnNotif.setOnAction(e -> {
            AppState.pedidosCocina.stream()
                .filter(p -> p.estado.equals("LISTO PARA ENTREGAR"))
                .forEach(p -> AppState.agregarNotificacion("PEDIDO_LISTO",
                    "🔔 Pedido listo para entregar: " + p.plato + " - " + p.cliente));
        });

        bar.getChildren().addAll(btnBack, sp, actBox, notifBox);
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

    // ── DATOS DE MUESTRA ─────────────────────────────────────────────────────
    private static void poblarDatosMuestra() {
        AppState.pedidosCocina.addAll(
            new PedidoCocina(1,"Nachos Suprema","M-05","jhon alan",  45.50, 240, false,"","Efectivo",null),
            new PedidoCocina(2,"Pizza Especial", "M-03","marcelo",   50.00, 195, false,"","QR",null),
            new PedidoCocina(3,"Salchipapa",     "M-07","omar mirko",13.00, 165, false,"","Efectivo",null),
            new PedidoCocina(4,"Pollo Broaster", "M-02","dhery",     30.00,  80, true,"Av. América 123","Efectivo",null),
            new PedidoCocina(5,"Combo Familiar", "M-01","ale",      110.00,  45, true,"Calle 10 #45","QR",null)
        );
        AppState.historialReciente.addAll(
            new PedidoFinalizado("Hamburguesa Clásica","M-04","jhon alan"),
            new PedidoFinalizado("Papas Fritas","M-06","marcelo"),
            new PedidoFinalizado("Gaseosa 600ml","M-08","luis"),
            new PedidoFinalizado("Sandwich Mixto","M-09","sofia"),
            new PedidoFinalizado("Ensalada César","M-10","miguel")
        );
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private static String formatTiempo(int seg) {
        return String.format("%02d:%02d", seg / 60, seg % 60);
    }

    private static Label buildEstadoBadge(String estado) {
        Label lbl = new Label(estado);
        boolean listo = estado.equals("LISTO PARA ENTREGAR");
        lbl.setStyle("-fx-background-color: " + (listo ? "#E8F5E9" : "#FFF3E0") + ";" +
            "-fx-text-fill: " + (listo ? GREEN : ORANGE) + ";" +
            "-fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-padding: 4 10 4 10; -fx-background-radius: 12;");
        return lbl;
    }

    private static HBox buildColHeader(String[] cols, double[] widths) {
        HBox hb = new HBox(0);
        hb.setStyle("-fx-background-color: " + RED + "; -fx-padding: 10 16 10 16;");
        for (int i = 0; i < cols.length; i++) {
            Label lbl = new Label(cols[i]);
            lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
            lbl.setMinWidth(widths[i]);
            hb.getChildren().add(lbl);
        }
        return hb;
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

    private static Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");
        return l;
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
