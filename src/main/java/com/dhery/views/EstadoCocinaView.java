package com.dhery.views;

import com.dhery.GestorArchivo.ArchivoManager;
import com.dhery.app.AppState;
import com.dhery.app.Router;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EstadoCocinaView {

    // ── rutas ────────────────────────────────────────────────────────────────
    private static final String PEDIDOS_TXT   = "src/main/java/com/dhery/GestorArchivo/pedidos.txt";
    private static final String HISTORIAL_TXT = "src/main/java/com/dhery/GestorArchivo/historial.txt";
    
    // ── paleta (imagen de referencia) ────────────────────────────────────────
    private static final String RED         = "#CC0000";
    private static final String RED_DARK    = "#AA0000";
    private static final String ORANGE      = "#E8890C";
    private static final String GREEN       = "#2E7D32";
    private static final String LIGHT_GREEN = "#4CAF50";
    private static final String BG          = "#FAFAFA";
    private static final String WHITE       = "#FFFFFF";
    private static final String GRAY        = "#888888";
    private static final String DARK        = "#1A1A1A";
    private static final String BORDER      = "#EEEEEE";

    // ── estado ───────────────────────────────────────────────────────────────
    private static VBox    activosContainer;
    private static VBox    historialContainer;
    private static Label   lblPedidosActivos;
    private static Label   lblListosParaEntregar;
    private static Label   lblEnProceso;
    private static Label   lblTiempoPromedio;
    private static Label   lblUltimaActualizacion;
    private static Label   lblNotifBadge;
    private static Timeline autoRefresh;
    private static VBox colaContainer;  

    // platos: platoId -> [segundosRestantes, 0=en_proceso / 1=listo]
    private static final Map<String, int[]>    timers        = new LinkedHashMap<>();
    private static final List<String[]>        platosActivos = new ArrayList<>();

    // ════════════════════════════════════════════════════════════════════════
    public static Scene getScene() {
        platosActivos.clear();
        timers.clear();
        if (autoRefresh != null) autoRefresh.stop();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        root.setTop(buildHeader());

        HBox content = new HBox(20);
        content.setPadding(new Insets(20));

        VBox left = buildLeftPanel();
        HBox.setHgrow(left, Priority.ALWAYS);

        VBox right = buildRightPanel();
        right.setPrefWidth(360);
        right.setMinWidth(360);

        content.getChildren().addAll(left, right);
        root.setCenter(content);
        root.setBottom(buildFooter());

        cargarPedidos();
        iniciarAutoRefresh();

        return new Scene(root, 1280, 720);
    }

    // ════════════════════════════════════════════════════════════════════════
    // HEADER — igual a la imagen: icono chef círculo rosa + línea roja + reloj
    // ════════════════════════════════════════════════════════════════════════
    private static HBox buildHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
            "-fx-background-color:" + WHITE + ";" +
            "-fx-border-color:" + BORDER + "; -fx-border-width:0 0 1.5 0;");

        // Icono chef en círculo rosa claro
        StackPane iconBox = new StackPane();
        Circle iconBg = new Circle(26);
        iconBg.setFill(Color.web("#FFEAEA"));
        Label iconLbl = new Label("👨\u200D🍳");
        iconLbl.setStyle("-fx-font-size:22px;");
        iconBox.getChildren().addAll(iconBg, iconLbl);

        // Línea roja vertical
        Region redLine = new Region();
        redLine.setPrefSize(3, 52);
        redLine.setStyle("-fx-background-color:" + RED + "; -fx-background-radius:2;");
        redLine.setTranslateX(8);

        VBox titleBox = new VBox(2);
        titleBox.setPadding(new Insets(0, 0, 0, 12));
        Label title = new Label("ESTADO DE COCINA");
        title.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:" + DARK + ";");
        Label subtitle = new Label("Monitorea los pedidos en preparación en tiempo real");
        subtitle.setStyle("-fx-font-size:12px; -fx-text-fill:" + GRAY + ";");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Reloj — cuadro gris claro
        VBox clockBox = new VBox(2);
        clockBox.setAlignment(Pos.CENTER_RIGHT);
        clockBox.setStyle(
            "-fx-background-color:#F5F5F5; -fx-background-radius:8;" +
            "-fx-padding:8 16 8 16;");
        Label clockLbl = new Label();
        clockLbl.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:" + DARK + ";");
        Label dateLbl  = new Label();
        dateLbl.setStyle("-fx-font-size:12px; -fx-text-fill:" + GRAY + ";");
        clockBox.getChildren().addAll(clockLbl, dateLbl);

        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            clockLbl.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            dateLbl.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
        clockLbl.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        dateLbl.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        header.getChildren().addAll(iconBox, redLine, titleBox, spacer, clockBox);
        return header;
    }

    // ════════════════════════════════════════════════════════════════════════
    // PANEL IZQUIERDO
    // ════════════════════════════════════════════════════════════════════════
    // ── PANEL IZQUIERDO — reemplazar buildLeftPanel() completo ──────────────────
private static VBox buildLeftPanel() {
    VBox panel = new VBox(16);

    // ── PEDIDOS ACTIVOS (EN_PROCESO) ──────────────────────────────────────
    VBox activosSection = new VBox(10);
    activosSection.setStyle(cardStyle());
    activosSection.setPadding(new Insets(16));

    HBox actHeader = new HBox(8);
    actHeader.setAlignment(Pos.CENTER_LEFT);
    Label chefIco  = new Label("👨‍🍳");
    chefIco.setStyle("-fx-font-size:16px;");
    Label actTitle = new Label("PEDIDOS ACTIVOS (EN PREPARACIÓN)");
    actTitle.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
    actHeader.getChildren().addAll(chefIco, actTitle);

    HBox tableHeader = buildTableHeader();
    activosContainer = new VBox(0);

    ScrollPane activosScroll = new ScrollPane(activosContainer);
    activosScroll.setFitToWidth(true);
    activosScroll.setMaxHeight(220);
    activosScroll.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

    activosSection.getChildren().addAll(actHeader, tableHeader, activosScroll);

    // ── EN COLA (EN_COLA) ─────────────────────────────────────────────────
    // ── EN COLA ───────────────────────────────────────────────────────────
VBox colaSection = new VBox(10);
colaSection.setStyle(cardStyle());
colaSection.setPadding(new Insets(16));

HBox colaHeader = new HBox(8);
colaHeader.setAlignment(Pos.CENTER_LEFT);
Label sandIco  = new Label("⏳");
sandIco.setStyle("-fx-font-size:16px;");
Label colaTitle = new Label("EN ESPERA — COLA DE PEDIDOS");
colaTitle.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + ORANGE + ";");
colaHeader.getChildren().addAll(sandIco, colaTitle);

colaContainer = new VBox(8);
colaContainer.setPadding(new Insets(4, 0, 0, 0));

// ScrollPane SIN altura máxima fija → crece con el contenido
ScrollPane colaScroll = new ScrollPane(colaContainer);
colaScroll.setFitToWidth(true);
colaScroll.setFitToHeight(false);   // deja que el contenido dicte la altura
colaScroll.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

colaSection.getChildren().addAll(colaHeader, colaScroll);

// Esto hace que colaSection se expanda y empuje hacia abajo
VBox.setVgrow(colaSection, Priority.ALWAYS);
VBox.setVgrow(colaScroll,  Priority.ALWAYS);

panel.getChildren().addAll(activosSection, colaSection);
VBox.setVgrow(activosSection, Priority.NEVER); // activos toma solo lo que necesita
    return panel;
}

    // Encabezado de tabla — fondo rojo, texto blanco, 4 columnas como en imagen
    private static HBox buildTableHeader() {
        HBox h = new HBox();
        h.setStyle("-fx-background-color:" + RED + "; -fx-background-radius:6;");
        h.setPadding(new Insets(8, 12, 8, 12));
        h.setAlignment(Pos.CENTER);

        Label id     = headerCell("ID",              60,  Pos.CENTER);
        Label plato  = headerCell("PLATO",           -1,  Pos.CENTER_LEFT);
        Label tiempo = headerCell("TIEMPO ESTIMADO", 160, Pos.CENTER);
        Label estado = headerCell("ESTADO",          160, Pos.CENTER);

        HBox.setHgrow(plato, Priority.ALWAYS);
        h.getChildren().addAll(id, plato, tiempo, estado);
        return h;
    }

    private static Label headerCell(String text, double w, Pos align) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:white; -fx-font-size:12px; -fx-font-weight:bold;");
        l.setAlignment(align);
        if (w > 0) { l.setPrefWidth(w); l.setMinWidth(w); }
        return l;
    }

    // ════════════════════════════════════════════════════════════════════════
    // PANEL DERECHO
    // ════════════════════════════════════════════════════════════════════════
    private static VBox buildRightPanel() {
        VBox panel = new VBox(16);

        // ── RESUMEN ───────────────────────────────────────────────────────
        VBox resumen = new VBox(12);
        resumen.setStyle(cardStyle());
        resumen.setPadding(new Insets(16));

        HBox resHeader = new HBox(8);
        resHeader.setAlignment(Pos.CENTER_LEFT);
        Label barIco   = new Label("📊");
        barIco.setStyle("-fx-font-size:16px;");
        Label resTitle = new Label("RESUMEN DE COCINA");
        resTitle.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
        resHeader.getChildren().addAll(barIco, resTitle);

        GridPane stats = new GridPane();
        stats.setHgap(12);
        stats.setVgap(12);

        lblPedidosActivos     = new Label("0");
        lblTiempoPromedio     = new Label("00:00");
        lblListosParaEntregar = new Label("0");
        lblEnProceso          = new Label("0");

        stats.add(statCard("🍽️", lblPedidosActivos,     "Pedidos Activos",      "En preparación", "#FFF5F5"), 0, 0);
        stats.add(statCard("⏱️", lblTiempoPromedio,     "Tiempo Promedio",      "de preparación", "#FFF8F0"), 1, 0);
        stats.add(statCard("✅", lblListosParaEntregar, "Listos para Entregar", "Pedidos listos",  "#F0FFF4"), 0, 1);
        stats.add(statCard("👨\u200D🍳", lblEnProceso,  "En Proceso",           "Preparándose",    "#F5F0FF"), 1, 1);

        ColumnConstraints cc1 = new ColumnConstraints(); cc1.setPercentWidth(50);
        ColumnConstraints cc2 = new ColumnConstraints(); cc2.setPercentWidth(50);
        stats.getColumnConstraints().addAll(cc1, cc2);

        resumen.getChildren().addAll(resHeader, stats);

        // ── HISTORIAL ─────────────────────────────────────────────────────
        VBox histSection = new VBox(10);
        histSection.setStyle(cardStyle());
        histSection.setPadding(new Insets(16));

        HBox histHeader = new HBox(8);
        histHeader.setAlignment(Pos.CENTER_LEFT);
        Label histIco   = new Label("🔄");
        histIco.setStyle("-fx-font-size:16px;");
        Label histTitle = new Label("HISTORIAL RECIENTE");
        histTitle.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
        histHeader.getChildren().addAll(histIco, histTitle);

        historialContainer = new VBox(6);
        ScrollPane histScroll = new ScrollPane(historialContainer);
        histScroll.setFitToWidth(true);
        histScroll.setMaxHeight(250);
        histScroll.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

        histSection.getChildren().addAll(histHeader, histScroll);

        panel.getChildren().addAll(resumen, histSection);
        return panel;
    }

    private static VBox statCard(String icon, Label valueLbl,
                                  String title, String subtitle, String bg) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color:" + bg + "; -fx-background-radius:10;");

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label(icon);
        ico.setStyle("-fx-font-size:22px;");
        valueLbl.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:" + DARK + ";");
        top.getChildren().addAll(ico, valueLbl);

        Label t = new Label(title);
        t.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + DARK + ";");
        Label s = new Label(subtitle);
        s.setStyle("-fx-font-size:10px; -fx-text-fill:" + GRAY + ";");

        card.getChildren().addAll(top, t, s);
        return card;
    }

    // ════════════════════════════════════════════════════════════════════════
    // FOOTER — VOLVER (borde rojo) + ACTUALIZAR + NOTIFICAR LISTOS (rojo sólido)
    // ════════════════════════════════════════════════════════════════════════
    private static HBox buildFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(14, 24, 14, 24));
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle(
            "-fx-background-color:" + WHITE + ";" +
            "-fx-border-color:" + BORDER + "; -fx-border-width:1.5 0 0 0;");

        // VOLVER con borde rojo
        Button btnBack = outlineRedButton("← VOLVER");
        btnBack.setOnAction(e -> {
            if (autoRefresh != null) autoRefresh.stop();
            Router.goMenuCajeroView(Router.getCurrentUser());
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblUltimaActualizacion = new Label("Última actualización: --:--:--");
        lblUltimaActualizacion.setStyle("-fx-font-size:11px; -fx-text-fill:" + GRAY + ";");

        // ACTUALIZAR — fondo gris claro
        Button btnActualizar = new Button("🔄 ACTUALIZAR");
        btnActualizar.setStyle(
            "-fx-background-color:#F5F5F5; -fx-text-fill:" + DARK + ";" +
            "-fx-font-size:12px; -fx-font-weight:bold;" +
            "-fx-background-radius:8; -fx-cursor:hand; -fx-padding:10 18 10 18;");
        btnActualizar.setOnAction(e -> {
            limpiarPedidosTerminados();
            cargarPedidos();
        });

        Region sp2 = new Region();
        sp2.setPrefWidth(16);

        // NOTIFICAR LISTOS — rojo sólido con badge naranja
        StackPane notifStack = new StackPane();
        Button btnNotificar = solidRedButton("🔔  NOTIFICAR LISTOS");
        btnNotificar.setOnAction(e -> notificarListos());

        lblNotifBadge = new Label("0");
        lblNotifBadge.setStyle(
            "-fx-font-size:9px; -fx-text-fill:white; -fx-font-weight:bold;" +
            "-fx-background-color:#E8890C; -fx-background-radius:8;" +
            "-fx-padding:1 5 1 5;");
        lblNotifBadge.setTranslateX(50);
        lblNotifBadge.setTranslateY(-14);
        lblNotifBadge.setVisible(false);
        notifStack.getChildren().addAll(btnNotificar, lblNotifBadge);

        Label notifSub = new Label("Notificar pedidos listos para entrega");
        notifSub.setStyle("-fx-font-size:10px; -fx-text-fill:" + GRAY + ";");
        VBox notifBox = new VBox(2, notifStack, notifSub);
        notifBox.setAlignment(Pos.CENTER);

        footer.getChildren().addAll(btnBack, spacer, lblUltimaActualizacion, sp2, btnActualizar, notifBox);
        return footer;
    }

    // ════════════════════════════════════════════════════════════════════════
    // LÓGICA DE CARGA
    // ════════════════════════════════════════════════════════════════════════
    private static void cargarPedidos() {
    ArchivoManager arch = new ArchivoManager();
    List<String> lineas = arch.leerLineas(PEDIDOS_TXT);

    platosActivos.clear();
    timers.clear();
    activosContainer.getChildren().clear();
    if (colaContainer != null) colaContainer.getChildren().clear();

    List<String[]> pedidosCola = new ArrayList<>();

    for (String linea : lineas) {
        String[] p = linea.split("\\|");
        if (p.length < 9) continue;
        String estadoCocina = p[7].trim();

        // ── COLA: mostrar en sección de espera ──────────────────────────
        if (estadoCocina.equals("EN_COLA")) {
            pedidosCola.add(p);
            continue;
        }

        // ── Ignorar terminados ──────────────────────────────────────────
        if (estadoCocina.equals("ENTREGADO")
         || estadoCocina.equals("LISTO_PARA_ENVIO")
         || estadoCocina.equals("LISTO")) continue;

        // ── EN_PROCESO: construir platos activos ────────────────────────
        for (int i = 8; i < p.length; i++) {
            if (p[i].trim().isEmpty() || p[i].trim().startsWith("REP:")) continue;
            String prod = p[i].trim();
            String nombrePlato;
            int qty = 1;
            if (prod.contains(" x")) {
                String[] parts = prod.split(" x");
                nombrePlato = parts[0].trim();
                try { qty = Integer.parseInt(parts[1].trim()); } catch (Exception ex) { qty = 1; }
            } else {
                nombrePlato = prod;
            }
            for (int q = 0; q < qty; q++) {
                String platoId = p[0] + "-" + (i - 7) + "-" + q;
                int secs = tiempoPorPlato(nombrePlato);
                if (!timers.containsKey(platoId)) {
                    timers.put(platoId, new int[]{secs, 0});
                }
                String mesa = p.length > 3 ? p[3].trim() : "—";
                platosActivos.add(new String[]{platoId, nombrePlato, p[1], p[0], "EN_PROCESO", mesa});
            }
        }
    }

    // ── Renderizar cola ─────────────────────────────────────────────────
    if (colaContainer != null) {
        if (pedidosCola.isEmpty()) {
            HBox emptyBox = new HBox(12);
            emptyBox.setAlignment(Pos.CENTER_LEFT);
            emptyBox.setPadding(new Insets(14));
            emptyBox.setStyle("-fx-background-color:#F0FFF0; -fx-background-radius:8;");
            Label okIco = new Label("✅");
            okIco.setStyle("-fx-font-size:22px;");
            VBox msgBox = new VBox(2);
            Label msg1 = new Label("No hay pedidos en cola en este momento.");
            msg1.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + GREEN + ";");
            Label msg2 = new Label("¡Excelente! La cocina está al día.");
            msg2.setStyle("-fx-font-size:11px; -fx-text-fill:" + GRAY + ";");
            msgBox.getChildren().addAll(msg1, msg2);
            emptyBox.getChildren().addAll(okIco, msgBox);
            colaContainer.getChildren().add(emptyBox);
        } else {
            for (String[] p : pedidosCola) {
                colaContainer.getChildren().add(buildColaRow(p));
            }
        }
    }

    // ── Renderizar activos (máx 5) ──────────────────────────────────────
    int listosCount  = 0;
    int procesoCount = 0;
    int totalSecs    = 0;
    int idx = 0;

    for (String[] plato : platosActivos) {
        if (idx >= 5) break;
        int[] td     = timers.get(plato[0]);
        boolean listo = td != null && td[1] == 1;
        if (listo) listosCount++;
        else { procesoCount++; totalSecs += td != null ? td[0] : 0; }
        activosContainer.getChildren().add(buildPlatoRow(plato, idx, listo));
        idx++;
    }

    lblPedidosActivos.setText(String.valueOf(platosActivos.size()));
    lblListosParaEntregar.setText(String.valueOf(listosCount));
    lblEnProceso.setText(String.valueOf(procesoCount));
    int avg = procesoCount > 0 ? totalSecs / procesoCount : 0;
    lblTiempoPromedio.setText(String.format("%02d:%02d", avg / 60, avg % 60));

    if (lblNotifBadge != null) {
        lblNotifBadge.setText(String.valueOf(listosCount));
        lblNotifBadge.setVisible(listosCount > 0);
    }

    if (listosCount > 0) {
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        AppState.agregarNotificacion("PEDIDO_LISTO",
            "✅ " + listosCount + " pedido(s) LISTO(S) para entregar · " + hora);
    }

    cargarHistorial();

    if (lblUltimaActualizacion != null) {
        lblUltimaActualizacion.setText("Última actualización: " +
            LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
}

    // ── Fila de plato activo ────────────────────────────────────────────────
    private static HBox buildPlatoRow(String[] plato, int idx, boolean listo) {
        HBox row = new HBox();
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setAlignment(Pos.CENTER);
        String rowBg = idx % 2 == 0 ? WHITE : "#F9F9F9";
        row.setStyle(
            "-fx-background-color:" + rowBg + ";" +
            "-fx-border-color:" + BORDER + "; -fx-border-width:0 0 1 0;");

        // ID — círculo de color
        StackPane idBox = new StackPane();
        idBox.setPrefSize(60, 44);
        idBox.setMinSize(60, 44);
        idBox.setAlignment(Pos.CENTER);
        Circle idCircle = new Circle(18);
        idCircle.setFill(Color.web(listo ? "#E8F5E9" : "#FFF3E0"));
        Label idLbl = new Label(plato[3]);
        idLbl.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" +
            (listo ? GREEN : ORANGE) + ";");
        idBox.getChildren().addAll(idCircle, idLbl);

        // Nombre + subtítulo mesa/cliente
        VBox platoInfo = new VBox(2);
        HBox.setHgrow(platoInfo, Priority.ALWAYS);
        platoInfo.setAlignment(Pos.CENTER_LEFT);
        Label platoNombre = new Label(plato[1]);
        platoNombre.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + DARK + ";");
        String mesaTxt = plato[5] != null && !plato[5].isEmpty() ? "Mesa: " + plato[5] + "  |  " : "";
        Label clienteInfo = new Label(mesaTxt + "Cliente: " + plato[2]);
        clienteInfo.setStyle("-fx-font-size:11px; -fx-text-fill:" + GRAY + ";");
        platoInfo.getChildren().addAll(platoNombre, clienteInfo);

        // Tiempo + contador
        int[] td   = timers.get(plato[0]);
        int   secs = td != null ? td[0] : 0;
        Label tiempoLbl = new Label(String.format("%02d:%02d", secs / 60, secs % 60));
        tiempoLbl.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:" +
            (listo ? LIGHT_GREEN : ORANGE) + ";");
        Label restante = new Label("restante");
        restante.setStyle("-fx-font-size:10px; -fx-text-fill:" + GRAY + ";");
        HBox tiempoIcon = new HBox(4, new Label("⏱️"), tiempoLbl);
        tiempoIcon.setAlignment(Pos.CENTER);
        VBox tiempoBox = new VBox(2, tiempoIcon, restante);
        tiempoBox.setAlignment(Pos.CENTER);
        tiempoBox.setPrefWidth(160);
        tiempoBox.setMinWidth(160);

        // Timer en vivo
        if (!listo && td != null) {
            Timeline t = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                if (td[0] > 0) td[0]--;
                tiempoLbl.setText(String.format("%02d:%02d", td[0] / 60, td[0] % 60));
                if (td[0] == 0 && td[1] == 0) {
                    td[1] = 1;
                    notificarPlatoListoAuto(plato[1], plato[2]);
                    // FIX: persistir en archivo y refrescar vista
                    marcarPedidoListoEnArchivo(plato[3]);
                    Platform.runLater(() -> cargarPedidos());
                }
            }));
            t.setCycleCount(Timeline.INDEFINITE);
            t.play();
        }

        // Chip de estado
        Label estadoLbl;
        if (listo) {
            estadoLbl = new Label("LISTO PARA ENTREGAR");
            estadoLbl.setStyle(
                "-fx-background-color:#E8F5E9; -fx-text-fill:" + GREEN + ";" +
                "-fx-font-size:11px; -fx-font-weight:bold;" +
                "-fx-padding:5 10 5 10; -fx-background-radius:20;");
        } else {
            estadoLbl = new Label("EN PROCESO ●");
            estadoLbl.setStyle(
                "-fx-background-color:#FFF3E0; -fx-text-fill:" + ORANGE + ";" +
                "-fx-font-size:11px; -fx-font-weight:bold;" +
                "-fx-padding:5 10 5 10; -fx-background-radius:20;");
        }
        estadoLbl.setPrefWidth(160);
        estadoLbl.setMinWidth(160);
        estadoLbl.setAlignment(Pos.CENTER);

        row.getChildren().addAll(idBox, platoInfo, tiempoBox, estadoLbl);
        return row;
    }

    // ── Historial ───────────────────────────────────────────────────────────
    private static void cargarHistorial() {
        if (historialContainer == null) return;
        historialContainer.getChildren().clear();
        ArchivoManager arch  = new ArchivoManager();
        List<String>   lines = arch.leerLineas(HISTORIAL_TXT);
        int start = Math.max(0, lines.size() - 5);
        for (int i = lines.size() - 1; i >= start; i--) {
            String[] p = lines.get(i).split("\\|");
            if (p.length < 3) continue;
            historialContainer.getChildren().add(buildHistorialRow(p));
        }
        if (historialContainer.getChildren().isEmpty()) {
            Label empty = new Label("No hay historial aún.");
            empty.setStyle("-fx-font-size:12px; -fx-text-fill:" + GRAY + ";");
            historialContainer.getChildren().add(empty);
        }
    }

    private static HBox buildHistorialRow(String[] p) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 8, 6, 8));
        row.setStyle("-fx-background-color:#F9F9F9; -fx-background-radius:6;");

        Label chk = new Label("✅");
        chk.setStyle("-fx-font-size:14px;");

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label platoLbl = new Label(p.length > 0 ? p[0] : "—");
        platoLbl.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:" + DARK + ";");
        // Mostrar "Mesa: M-xx  |  cliente" si el campo 1 parece una mesa
        String subTxt = p.length > 1 ? "Cliente: " + p[1] : "";
        Label subLbl = new Label(subTxt);
        subLbl.setStyle("-fx-font-size:10px; -fx-text-fill:" + GRAY + ";");
        info.getChildren().addAll(platoLbl, subLbl);

        Label horaLbl = new Label(p.length > 2 ? p[2] : "");
        horaLbl.setStyle("-fx-font-size:11px; -fx-text-fill:" + LIGHT_GREEN + "; -fx-font-weight:bold;");

        row.getChildren().addAll(chk, info, horaLbl);
        return row;
    }

    // ── Limpia pedidos ENTREGADO del archivo ────────────────────────────────
    private static void limpiarPedidosTerminados() {
        ArchivoManager arch = new ArchivoManager();
        List<String>   lines = arch.leerLineas(PEDIDOS_TXT);
        List<String>   keep  = new ArrayList<>();
        for (String l : lines) {
            String[] p = l.split("\\|");
            if (p.length >= 8 && p[7].trim().equals("ENTREGADO")) continue;
            keep.add(l);
        }
        arch.reescribirLineas(PEDIDOS_TXT, keep);
    }

    // ── Notificar listos manualmente ────────────────────────────────────────
    private static void notificarListos() {
        ArchivoManager arch = new ArchivoManager();
        List<String>   lines = arch.leerLineas(PEDIDOS_TXT);
        List<String>   nuevas = new ArrayList<>();
        int notificados = 0;

        for (String linea : lines) {
            String[] p = linea.split("\\|");
            if (p.length < 8) { nuevas.add(linea); continue; }
            if (p[7].trim().equals("EN_PROCESO")) {
                p[7] = "LISTO";
                String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                for (int i = 8; i < p.length; i++) {
                    if (!p[i].trim().isEmpty() && !p[i].trim().startsWith("REP:")) {
                        arch.agregarLinea(HISTORIAL_TXT,
                            p[i].trim() + "|" + p[1] + "|" + hora + "|LISTO");
                    }
                }
                notificados++;
                if (p.length > 4 && p[4].trim().equals("DELIVERY")) p[7] = "LISTO_PARA_ENVIO";
                nuevas.add(String.join("|", p));
            } else {
                nuevas.add(linea);
            }
        }
        arch.reescribirLineas(PEDIDOS_TXT, nuevas);

        if (notificados > 0) {
            String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            AppState.agregarNotificacion("PEDIDO_LISTO",
                "🔔 " + notificados + " pedido(s) notificado(s) LISTOS · " + hora);
        }

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Notificación enviada");
        a.setHeaderText(null);
        a.setContentText(notificados > 0
            ? "✅ " + notificados + " pedido(s) marcado(s) como LISTOS."
            : "No hay pedidos EN PROCESO para notificar.");
        a.showAndWait();
        cargarPedidos();
    }

    // ── Auto-notificación cuando un timer llega a 0 ─────────────────────────
    private static void notificarPlatoListoAuto(String plato, String cliente) {
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        AppState.agregarNotificacion("PEDIDO_LISTO",
            "✅ " + plato + " de " + cliente + " — LISTO PARA ENTREGAR · " + hora);
    }

    /**
     * FIX: escribe LISTO en pedidos.txt para pedidos LOCAL cuando el timer llega a 0.
     * Sin esto cargarPedidos() leía EN_PROCESO y recreaba la fila indefinidamente.
     */
    private static void marcarPedidoListoEnArchivo(String idPedido) {
        ArchivoManager arch  = new ArchivoManager();
        List<String>   lines = arch.leerLineas(PEDIDOS_TXT);
        List<String>   nuevas = new ArrayList<>();
        for (String linea : lines) {
            String[] p = linea.split("\\|");
            if (p.length >= 8 && p[0].trim().equals(idPedido.trim())) {
                String tipo = p.length > 4 ? p[4].trim() : "";
                if (!tipo.equals("DELIVERY") && p[7].trim().equals("EN_PROCESO")) {
                    p[7] = "LISTO";
                    String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                    for (int i = 8; i < p.length; i++) {
                        if (!p[i].trim().isEmpty() && !p[i].trim().startsWith("REP:")) {
                            arch.agregarLinea(HISTORIAL_TXT,
                                p[i].trim() + "|" + p[1].trim() + "|" + hora + "|LISTO");
                        }
                    }
                }
                nuevas.add(String.join("|", p));
            } else {
                nuevas.add(linea);
            }
        }
        arch.reescribirLineas(PEDIDOS_TXT, nuevas);
    }

    // ── Auto-refresh cada 10 s ──────────────────────────────────────────────
    private static void iniciarAutoRefresh() {
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(10), e -> cargarPedidos()));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }

    // ── Tiempo estimado por plato ────────────────────────────────────────────
    private static int tiempoPorPlato(String nombre) {
        nombre = nombre.toUpperCase();
        if (nombre.contains("PIZZA"))      return 10 * 60;
        if (nombre.contains("HAMBURGUE")) return  5 * 60;
        if (nombre.contains("SALCHIPAPA") || nombre.contains("PAPAS")) return 4 * 60;
        if (nombre.contains("COMBO"))      return  8 * 60;
        if (nombre.contains("TACOS") || nombre.contains("BIRRIA") || nombre.contains("QUESABIRRIA")) return 6 * 60;
        if (nombre.contains("NACHOS"))     return  4 * 60;
        if (nombre.contains("RAMEN"))      return  7 * 60;
        return 5 * 60;
    }

    // ── Helpers de botones ───────────────────────────────────────────────────
    private static Button outlineRedButton(String text) {
        String normal =
            "-fx-background-color:transparent;" +
            "-fx-border-color:" + RED + "; -fx-border-width:1.5;" +
            "-fx-border-radius:8; -fx-background-radius:8;" +
            "-fx-text-fill:" + RED + "; -fx-font-size:13px; -fx-font-weight:bold;" +
            "-fx-cursor:hand; -fx-padding:10 22 10 22;";
        String hover =
            "-fx-background-color:" + RED + ";" +
            "-fx-border-color:" + RED + "; -fx-border-width:1.5;" +
            "-fx-border-radius:8; -fx-background-radius:8;" +
            "-fx-text-fill:white; -fx-font-size:13px; -fx-font-weight:bold;" +
            "-fx-cursor:hand; -fx-padding:10 22 10 22;";
        Button b = new Button(text);
        b.setStyle(normal);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e  -> b.setStyle(normal));
        return b;
    }

    private static Button solidRedButton(String text) {
        String normal =
            "-fx-background-color:" + RED + "; -fx-text-fill:white;" +
            "-fx-font-size:13px; -fx-font-weight:bold;" +
            "-fx-background-radius:8; -fx-cursor:hand; -fx-padding:10 22 10 22;";
        String hover =
            "-fx-background-color:" + RED_DARK + "; -fx-text-fill:white;" +
            "-fx-font-size:13px; -fx-font-weight:bold;" +
            "-fx-background-radius:8; -fx-cursor:hand; -fx-padding:10 22 10 22;";
        Button b = new Button(text);
        b.setStyle(normal);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e  -> b.setStyle(normal));
        return b;
    }

    private static String cardStyle() {
        return
            "-fx-background-color:" + WHITE + ";" +
            "-fx-border-color:" + BORDER + "; -fx-border-width:1.5;" +
            "-fx-border-radius:12; -fx-background-radius:12;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.06),8,0,0,2);";
    }
    private static HBox buildColaRow(String[] p) {
    HBox row = new HBox(10);
    row.setPadding(new Insets(10, 12, 10, 12));
    row.setAlignment(Pos.CENTER);
    row.setStyle(
        "-fx-background-color:#FFF8E7;" +
        "-fx-background-radius:8;" +
        "-fx-border-color:" + ORANGE + "; -fx-border-width:1;" +
        "-fx-border-radius:8;");

    // Badge ID
    StackPane idBox = new StackPane();
    idBox.setPrefSize(50, 40);
    idBox.setMinSize(50, 40);
    Circle idCircle = new Circle(18);
    idCircle.setFill(Color.web("#FFF3E0"));
    Label idLbl = new Label("#" + p[0]);
    idLbl.setStyle("-fx-font-size:11px; -fx-font-weight:bold; -fx-text-fill:" + ORANGE + ";");
    idBox.getChildren().addAll(idCircle, idLbl);

    // Info pedido
    VBox info = new VBox(3);
    HBox.setHgrow(info, Priority.ALWAYS);

    // Construir resumen de productos
    StringBuilder productos = new StringBuilder();
    for (int i = 8; i < p.length; i++) {
        if (!p[i].trim().isEmpty() && !p[i].trim().startsWith("REP:")) {
            if (productos.length() > 0) productos.append("  ·  ");
            productos.append(p[i].trim());
        }
    }
    Label prodLbl = new Label(productos.toString());
    prodLbl.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:" + DARK + ";");
    prodLbl.setWrapText(true);

    String tipo = p.length > 4 ? p[4].trim() : "";
    Label metaLbl = new Label("Cliente: " + p[1] + "   |   " + tipo + "   |   Total: " + (p.length > 5 ? p[5] : "?") + " Bs");
    metaLbl.setStyle("-fx-font-size:11px; -fx-text-fill:" + GRAY + ";");

    info.getChildren().addAll(prodLbl, metaLbl);

    // Botón "▶ Mandar a Preparar"
    Button btnPreparar = new Button("▶  MANDAR A PREPARAR");
    btnPreparar.setStyle(
        "-fx-background-color:" + GREEN + "; -fx-text-fill:white;" +
        "-fx-font-size:11px; -fx-font-weight:bold;" +
        "-fx-background-radius:8; -fx-cursor:hand; -fx-padding:8 14 8 14;");
    btnPreparar.setOnAction(e -> mandarAPreparar(p[0]));

    row.getChildren().addAll(idBox, info, btnPreparar);
    return row;
}
private static void mandarAPreparar(String idPedido) {
    ArchivoManager arch  = new ArchivoManager();
    List<String>   lines = arch.leerLineas(PEDIDOS_TXT);
    List<String>   nuevas = new ArrayList<>();

    for (String linea : lines) {
        String[] p = linea.split("\\|");
        if (p.length >= 8 && p[0].trim().equals(idPedido.trim())
                && p[7].trim().equals("EN_COLA")) {
            p[7] = "EN_PROCESO";
            nuevas.add(String.join("|", p));
        } else {
            nuevas.add(linea);
        }
    }
    arch.reescribirLineas(PEDIDOS_TXT, nuevas);
    cargarPedidos();  // refrescar vista inmediatamente
}
}
