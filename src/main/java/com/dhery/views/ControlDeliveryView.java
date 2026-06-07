package com.dhery.views;

import com.dhery.GestorArchivo.ArchivoManager;
import com.dhery.app.AppState;
import com.dhery.app.Router;
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
import java.util.*;

public class ControlDeliveryView {

    // ── rutas ────────────────────────────────────────────────────────────────
    private static final String PEDIDOS_TXT      = "src/main/java/com/dhery/GestorArchivo/pedidos.txt";
    private static final String REPARTIDORES_TXT = "src/main/java/com/dhery/GestorArchivo/repartidores.txt";
    private static final String HISTORIAL_TXT    = "src/main/java/com/dhery/GestorArchivo/historial.txt";

    // ── paleta ───────────────────────────────────────────────────────────────
    private static final String RED         = "#CC0000";
    private static final String RED_DARK    = "#AA0000";
    private static final String ORANGE      = "#E8890C";
    private static final String GREEN       = "#2E7D32";
    private static final String LIGHT_GREEN = "#4CAF50";
    private static final String BLUE        = "#1565C0";
    private static final String BG          = "#FAFAFA";
    private static final String WHITE       = "#FFFFFF";
    private static final String GRAY        = "#888888";
    private static final String DARK        = "#1A1A1A";
    private static final String BORDER      = "#EEEEEE";

    // ── estado ───────────────────────────────────────────────────────────────
    private static VBox    colaContainer;
    private static VBox    repartidoresContainer;
    private static VBox    detalleContainer;
    private static Label   lblTimerGrande;
    private static HBox    toastBox;
    private static Label   lblNotifToast;
    private static Timeline autoRefresh;
    private static Timeline selectedTimer;
    private static String[] pedidoSeleccionado = null;

    // Timers de la cola: idPedido -> [segundos restantes]
    private static final Map<String, int[]> colaTimers = new LinkedHashMap<>();

    // ════════════════════════════════════════════════════════════════════════
    public static Scene getScene() {
        pedidoSeleccionado = null;
        colaTimers.clear();
        if (autoRefresh   != null) autoRefresh.stop();
        if (selectedTimer != null) selectedTimer.stop();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        root.setTop(buildHeader());

        HBox content = new HBox(20);
        content.setPadding(new Insets(20));

        VBox left = buildLeftPanel();
        HBox.setHgrow(left, Priority.ALWAYS);

        VBox right = buildRightPanel();
        right.setPrefWidth(340);
        right.setMinWidth(340);

        content.getChildren().addAll(left, right);

        // Toast de confirmación sobre el contenido
        StackPane centerStack = new StackPane(content);
        toastBox = buildToast();
        StackPane.setAlignment(toastBox, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(toastBox, new Insets(0, 24, 80, 0));
        toastBox.setVisible(false);
        centerStack.getChildren().add(toastBox);

        root.setCenter(centerStack);
        root.setBottom(buildFooter());

        cargarTodo();
        iniciarAutoRefresh();

        return new Scene(root, 1280, 720);
    }

    // ════════════════════════════════════════════════════════════════════════
    // HEADER — igual a imagen 1: icono moto en círculo rosa + línea roja + reloj
    // ════════════════════════════════════════════════════════════════════════
    private static HBox buildHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
            "-fx-background-color:" + WHITE + ";" +
            "-fx-border-color:" + BORDER + "; -fx-border-width:0 0 1.5 0;");

        // Icono moto en círculo rosa
        StackPane iconBox = new StackPane();
        Circle iconBg = new Circle(26);
        iconBg.setFill(Color.web("#FFEAEA"));
        Label iconLbl = new Label("🛵");
        iconLbl.setStyle("-fx-font-size:22px;");
        iconBox.getChildren().addAll(iconBg, iconLbl);

        // Línea roja vertical
        Region redLine = new Region();
        redLine.setPrefSize(3, 52);
        redLine.setStyle("-fx-background-color:" + RED + "; -fx-background-radius:2;");
        redLine.setTranslateX(8);

        VBox titleBox = new VBox(2);
        titleBox.setPadding(new Insets(0, 0, 0, 12));
        Label title    = new Label("CONTROL DE DELIVERY");
        title.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:" + DARK + ";");
        Label subtitle = new Label("Gestiona la cola de pedidos y el estado de entrega");
        subtitle.setStyle("-fx-font-size:12px; -fx-text-fill:" + GRAY + ";");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Reloj
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
    // PANEL IZQUIERDO — COLA + DETALLE
    // ════════════════════════════════════════════════════════════════════════
    private static VBox buildLeftPanel() {
        VBox panel = new VBox(16);

        // ── COLA DE PEDIDOS ───────────────────────────────────────────────
        VBox colaSection = new VBox(10);
        colaSection.setStyle(cardStyle());
        colaSection.setPadding(new Insets(16));

        HBox colaHeader = new HBox(8);
        colaHeader.setAlignment(Pos.CENTER_LEFT);
        Label listIco   = new Label("📋");
        listIco.setStyle("-fx-font-size:16px;");
        Label colaTitle = new Label("COLA DE PEDIDOS");
        colaTitle.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
        colaHeader.getChildren().addAll(listIco, colaTitle);

        HBox tableHead = buildColaTableHeader();
        colaContainer  = new VBox(0);
        ScrollPane colaScroll = new ScrollPane(colaContainer);
        colaScroll.setFitToWidth(true);
        colaScroll.setMaxHeight(220);
        colaScroll.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

        colaSection.getChildren().addAll(colaHeader, tableHead, colaScroll);

        // ── DETALLE DEL PEDIDO SELECCIONADO ──────────────────────────────
        VBox detalleSection = new VBox(10);
        detalleSection.setStyle(cardStyle());
        detalleSection.setPadding(new Insets(16));

        HBox detHeader = new HBox(8);
        detHeader.setAlignment(Pos.CENTER_LEFT);
        Label detIco   = new Label("📝");
        detIco.setStyle("-fx-font-size:16px;");
        Label detTitle = new Label("DETALLE DEL PEDIDO SELECCIONADO");
        detTitle.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
        detHeader.getChildren().addAll(detIco, detTitle);

        detalleContainer = new VBox(10);
        Label placeholder = new Label("Haz clic en una fila para ver el detalle del pedido.");
        placeholder.setStyle("-fx-font-size:12px; -fx-text-fill:" + GRAY + "; -fx-padding:10 0 10 0;");
        detalleContainer.getChildren().add(placeholder);

        detalleSection.getChildren().addAll(detHeader, detalleContainer);

        panel.getChildren().addAll(colaSection, detalleSection);
        VBox.setVgrow(detalleSection, Priority.ALWAYS);
        return panel;
    }

    // Encabezado tabla cola — idéntico a imagen 1
    private static HBox buildColaTableHeader() {
        HBox h = new HBox();
        h.setStyle("-fx-background-color:" + RED + "; -fx-background-radius:6;");
        h.setPadding(new Insets(8, 12, 8, 12));
        h.setAlignment(Pos.CENTER);

        Label nLbl  = hCell("N°",              50,  Pos.CENTER);
        Label cLbl  = hCell("CLIENTE",         110, Pos.CENTER_LEFT);
        Label pLbl  = hCell("PEDIDO",          -1,  Pos.CENTER_LEFT);
        Label tLbl  = hCell("TOTAL (Bs)",      90,  Pos.CENTER);
        Label trLbl = hCell("TIEMPO RESTANTE", 130, Pos.CENTER);
        Label eLbl  = hCell("ESTADO",          140, Pos.CENTER);

        HBox.setHgrow(pLbl, Priority.ALWAYS);
        h.getChildren().addAll(nLbl, cLbl, pLbl, tLbl, trLbl, eLbl);
        return h;
    }

    private static Label hCell(String t, double w, Pos align) {
        Label l = new Label(t);
        l.setStyle("-fx-text-fill:white; -fx-font-size:12px; -fx-font-weight:bold;");
        l.setAlignment(align);
        if (w > 0) { l.setPrefWidth(w); l.setMinWidth(w); }
        return l;
    }

    // ════════════════════════════════════════════════════════════════════════
    // PANEL DERECHO — REPARTIDORES + PEDIDO EN PROCESO
    // ════════════════════════════════════════════════════════════════════════
    private static VBox buildRightPanel() {
        VBox panel = new VBox(16);

        // ── ESTADO DE REPARTIDORES ────────────────────────────────────────
        VBox repSection = new VBox(10);
        repSection.setStyle(cardStyle());
        repSection.setPadding(new Insets(16));

        HBox repHeader = new HBox(8);
        repHeader.setAlignment(Pos.CENTER_LEFT);
        Label repIco   = new Label("👤");
        repIco.setStyle("-fx-font-size:16px;");
        Label repTitle = new Label("ESTADO DE REPARTIDORES");
        repTitle.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
        repHeader.getChildren().addAll(repIco, repTitle);

        repartidoresContainer = new VBox(8);
        repSection.getChildren().addAll(repHeader, repartidoresContainer);

        // ── PEDIDO EN PROCESO ─────────────────────────────────────────────
        VBox timerSection = new VBox(12);
        timerSection.setStyle(cardStyle());
        timerSection.setPadding(new Insets(16));
        timerSection.setAlignment(Pos.CENTER);

        HBox timerHeader = new HBox(8);
        timerHeader.setAlignment(Pos.CENTER_LEFT);
        Label timerIco   = new Label("📦");
        timerIco.setStyle("-fx-font-size:16px;");
        Label timerTitle = new Label("PEDIDO EN PROCESO");
        timerTitle.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
        timerHeader.getChildren().addAll(timerIco, timerTitle);

        lblTimerGrande = new Label("--:--");
        lblTimerGrande.setStyle(
            "-fx-font-size:52px; -fx-font-weight:bold; -fx-text-fill:" + ORANGE + ";");

        Label timerSub = new Label("Tiempo restante\ndel pedido");
        timerSub.setStyle("-fx-font-size:13px; -fx-text-fill:" + GRAY + "; -fx-text-alignment:center;");
        timerSub.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // MARCAR COMO ENTREGADO — borde rojo (outline), hover rojo sólido
        Button btnEntregado = outlineRedButton("✔  MARCAR COMO ENTREGADO");
        btnEntregado.setMaxWidth(Double.MAX_VALUE);
        btnEntregado.setOnAction(e -> marcarEntregado());

        timerSection.getChildren().addAll(timerHeader, lblTimerGrande, timerSub, btnEntregado);

        panel.getChildren().addAll(repSection, timerSection);
        VBox.setVgrow(timerSection, Priority.ALWAYS);
        return panel;
    }

    // ════════════════════════════════════════════════════════════════════════
    // FOOTER — ATRÁS (borde rojo)
    // ════════════════════════════════════════════════════════════════════════
    private static HBox buildFooter() {
        HBox footer = new HBox();
        footer.setPadding(new Insets(14, 24, 14, 24));
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle(
            "-fx-background-color:" + WHITE + ";" +
            "-fx-border-color:" + BORDER + "; -fx-border-width:1.5 0 0 0;");

        Button btnBack = outlineRedButton("← ATRÁS");
        btnBack.setOnAction(e -> {
            if (autoRefresh   != null) autoRefresh.stop();
            if (selectedTimer != null) selectedTimer.stop();
            Router.goMenuCajeroView(Router.getCurrentUser());
        });

        footer.getChildren().add(btnBack);
        return footer;
    }

    // ════════════════════════════════════════════════════════════════════════
    // TOAST — notificación "¡PEDIDO REALIZADO!" inferior derecha
    // ════════════════════════════════════════════════════════════════════════
    private static HBox buildToast() {
        HBox toast = new HBox(14);
        toast.setAlignment(Pos.CENTER_LEFT);
        toast.setPadding(new Insets(16, 20, 16, 16));
        toast.setStyle(
            "-fx-background-color:" + WHITE + ";" +
            "-fx-border-color:#E0E0E0; -fx-border-width:1.5;" +
            "-fx-border-radius:12; -fx-background-radius:12;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.22),18,0,0,6);");
        toast.setMaxWidth(400);
        toast.setMinWidth(360);

        // Círculo verde con check
        StackPane checkBox = new StackPane();
        Circle c = new Circle(22);
        c.setFill(Color.web(LIGHT_GREEN));
        Label chk = new Label("✔");
        chk.setStyle("-fx-font-size:15px; -fx-text-fill:white; -fx-font-weight:bold;");
        checkBox.getChildren().addAll(c, chk);

        VBox msgBox = new VBox(4);
        HBox.setHgrow(msgBox, Priority.ALWAYS);
        Label toastTitle = new Label("¡PEDIDO REALIZADO!");
        toastTitle.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + GREEN + ";");
        lblNotifToast = new Label("");
        lblNotifToast.setStyle("-fx-font-size:12px; -fx-text-fill:" + GRAY + ";");
        lblNotifToast.setWrapText(true);
        msgBox.getChildren().addAll(toastTitle, lblNotifToast);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
            "-fx-background-color:transparent; -fx-text-fill:" + GRAY + ";" +
            "-fx-cursor:hand; -fx-font-size:14px; -fx-padding:0 4 0 4;");
        closeBtn.setOnAction(e -> toastBox.setVisible(false));

        toast.getChildren().addAll(checkBox, msgBox, closeBtn);
        return toast;
    }

    private static void mostrarToast(String cliente) {
        if (lblNotifToast != null)
            lblNotifToast.setText("El pedido de " + cliente + " ha sido entregado correctamente.");
        if (toastBox != null) {
            toastBox.setVisible(true);
            new Timeline(new KeyFrame(Duration.seconds(5), e -> toastBox.setVisible(false))).play();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // LÓGICA DE CARGA
    // ════════════════════════════════════════════════════════════════════════
    private static void cargarTodo() {
        cargarCola();
        cargarRepartidores();
    }

    private static void cargarCola() {
        if (colaContainer == null) return;
        colaContainer.getChildren().clear();

        ArchivoManager arch  = new ArchivoManager();
        List<String>   lines = arch.leerLineas(PEDIDOS_TXT);

        int rowNum = 1;
        for (String linea : lines) {
            String[] p = linea.split("\\|");
            if (p.length < 8) continue;
            String tipo         = p[4].trim();
            String estadoCocina = p[7].trim();
            if (!tipo.equals("DELIVERY")) continue;
            if (estadoCocina.equals("EN_PROCESO") || estadoCocina.equals("ENTREGADO")) continue;

            // EN CAMINO o PEDIDO ENTREGADO según estado
            String estadoDisplay = estadoCocina.equals("LISTO_PARA_ENVIO") ? "EN_CAMINO" : estadoCocina;
            colaContainer.getChildren().add(buildColaRow(rowNum, p, estadoDisplay));
            rowNum++;
        }

        if (colaContainer.getChildren().isEmpty()) {
            Label empty = new Label("No hay pedidos delivery activos.");
            empty.setStyle("-fx-font-size:12px; -fx-text-fill:" + GRAY + "; -fx-padding:10 0 10 0;");
            colaContainer.getChildren().add(empty);
        }
    }

    // ── Fila de la cola — idéntica a imagen 1 ───────────────────────────────
    private static HBox buildColaRow(int num, String[] p, String estado) {
        boolean entregado = estado.equals("PEDIDO_ENTREGADO") || estado.equals("ENTREGADO");
        String  idPedido  = p[0].trim();
        String  rowBg     = num % 2 == 0 ? "#F9F9F9" : WHITE;

        HBox row = new HBox();
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setAlignment(Pos.CENTER);
        row.setStyle(
            "-fx-background-color:" + rowBg + ";" +
            "-fx-border-color:" + BORDER + "; -fx-border-width:0 0 1 0; -fx-cursor:hand;");

        // N°
        Label nLbl = new Label(String.valueOf(num));
        nLbl.setStyle("-fx-font-size:13px; -fx-text-fill:" + DARK + ";");
        nLbl.setPrefWidth(50);
        nLbl.setAlignment(Pos.CENTER);

        // Cliente
        Label cLbl = new Label(p[1]);
        cLbl.setStyle("-fx-font-size:13px; -fx-text-fill:" + DARK + ";");
        cLbl.setPrefWidth(110);
        cLbl.setAlignment(Pos.CENTER_LEFT);

        // Productos
        StringBuilder prod = new StringBuilder();
        for (int i = 8; i < p.length; i++) {
            if (!p[i].trim().isEmpty() && !p[i].trim().startsWith("REP:")) {
                if (prod.length() > 0) prod.append(" + ");
                prod.append(p[i].trim());
            }
        }
        Label pLbl = new Label(prod.length() > 0 ? prod.toString() : "—");
        pLbl.setStyle("-fx-font-size:12px; -fx-text-fill:" + DARK + ";");
        pLbl.setWrapText(false);
        pLbl.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(pLbl, Priority.ALWAYS);

        // Total
        Label tLbl = new Label(p[5]);
        tLbl.setStyle("-fx-font-size:13px; -fx-text-fill:" + DARK + ";");
        tLbl.setPrefWidth(90);
        tLbl.setAlignment(Pos.CENTER);

        // Tiempo restante (timer coordinado)
        if (!colaTimers.containsKey(idPedido) && !entregado) {
            colaTimers.put(idPedido, new int[]{44 * 60});
        }
        int[] secsArr = colaTimers.getOrDefault(idPedido, new int[]{0});

        Label tiempoLbl = new Label(entregado ? "00:00"
            : String.format("%02d:%02d", secsArr[0] / 60, secsArr[0] % 60));
        tiempoLbl.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" +
            (entregado ? LIGHT_GREEN : ORANGE) + ";");
        tiempoLbl.setPrefWidth(130);
        tiempoLbl.setMinWidth(130);
        tiempoLbl.setAlignment(Pos.CENTER);

        if (!entregado) {
            Timeline t = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                if (secsArr[0] > 0) secsArr[0]--;
                int s = secsArr[0];
                tiempoLbl.setText(String.format("%02d:%02d", s / 60, s % 60));
                tiempoLbl.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" +
                    (s < 300 ? LIGHT_GREEN : ORANGE) + ";");
            }));
            t.setCycleCount(Timeline.INDEFINITE);
            t.play();
        }

        // Chip de estado
        Label estadoLbl;
        if (entregado) {
            estadoLbl = new Label("PEDIDO ENTREGADO");
            estadoLbl.setStyle(
                "-fx-background-color:#E8F5E9; -fx-text-fill:" + LIGHT_GREEN + ";" +
                "-fx-font-size:11px; -fx-font-weight:bold;" +
                "-fx-padding:4 10 4 10; -fx-background-radius:20;");
        } else {
            estadoLbl = new Label("EN CAMINO");
            estadoLbl.setStyle(
                "-fx-background-color:#E3F2FD; -fx-text-fill:" + BLUE + ";" +
                "-fx-font-size:11px; -fx-font-weight:bold;" +
                "-fx-padding:4 10 4 10; -fx-background-radius:20;");
        }
        estadoLbl.setPrefWidth(140);
        estadoLbl.setAlignment(Pos.CENTER);

        row.getChildren().addAll(nLbl, cLbl, pLbl, tLbl, tiempoLbl, estadoLbl);

        // Hover
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color:#FFF5F5;" +
            "-fx-border-color:" + BORDER + "; -fx-border-width:0 0 1 0; -fx-cursor:hand;"));
        row.setOnMouseExited(e -> row.setStyle(
            "-fx-background-color:" + rowBg + ";" +
            "-fx-border-color:" + BORDER + "; -fx-border-width:0 0 1 0; -fx-cursor:hand;"));

        // Clic → mostrar detalle
        row.setOnMouseClicked(e -> {
            pedidoSeleccionado = p;
            cargarDetalle(p, secsArr);
        });

        return row;
    }

    // ── Estado de repartidores — igual a imagen 1 ───────────────────────────
    private static void cargarRepartidores() {
        if (repartidoresContainer == null) return;
        repartidoresContainer.getChildren().clear();

        ArchivoManager arch  = new ArchivoManager();
        List<String>   lines = arch.leerLineas(REPARTIDORES_TXT);

        if (lines.isEmpty()) {
            Label empty = new Label("No hay repartidores registrados.");
            empty.setStyle("-fx-font-size:12px; -fx-text-fill:" + GRAY + ";");
            repartidoresContainer.getChildren().add(empty);
            return;
        }

        for (String linea : lines) {
            String[] p = linea.split("\\|");
            if (p.length < 3) continue;
            boolean enCamino = p[2].trim().equals("OCUPADO");

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 0, 6, 0));

            Label motoIco = new Label("🏍️");
            motoIco.setStyle("-fx-font-size:18px;");

            Label nombreLbl = new Label(p[1]);
            nombreLbl.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + DARK + ";");
            HBox.setHgrow(nombreLbl, Priority.ALWAYS);

            Label estadoChip;
            if (enCamino) {
                estadoChip = new Label("EN CAMINO");
                estadoChip.setStyle(
                    "-fx-background-color:#E3F2FD; -fx-text-fill:" + BLUE + ";" +
                    "-fx-font-size:11px; -fx-font-weight:bold;" +
                    "-fx-padding:3 10 3 10; -fx-background-radius:20;");
            } else {
                estadoChip = new Label("LIBRE");
                estadoChip.setStyle(
                    "-fx-background-color:#E8F5E9; -fx-text-fill:" + LIGHT_GREEN + ";" +
                    "-fx-font-size:11px; -fx-font-weight:bold;" +
                    "-fx-padding:3 10 3 10; -fx-background-radius:20;");
            }

            Label tiempoRep = new Label(
                enCamino && p.length > 3 ? "Entrega en " + p[3].trim() : "");
            tiempoRep.setStyle("-fx-font-size:11px; -fx-text-fill:" + GRAY + ";");

            row.getChildren().addAll(motoIco, nombreLbl, estadoChip, tiempoRep);

            Separator sep = new Separator();
            sep.setStyle("-fx-background-color:" + BORDER + ";");

            repartidoresContainer.getChildren().addAll(row, sep);
        }
    }

    // ── Detalle del pedido seleccionado ─────────────────────────────────────
    private static void cargarDetalle(String[] p, int[] secsArr) {
        if (detalleContainer == null) return;
        detalleContainer.getChildren().clear();

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(8);

        addDetRow(grid, "Cliente:",       p[1],                                  0, 0);
        addDetRow(grid, "Teléfono:",      p[2],                                  1, 0);
        addDetRow(grid, "Dirección:",     p[3],                                  2, 0);

        // Pedido (lista de productos)
        Label pedLabel = detLabel("Pedido:");
        grid.add(pedLabel, 0, 3);
        VBox prodList = new VBox(3);
        for (int i = 8; i < p.length; i++) {
            if (!p[i].trim().isEmpty() && !p[i].trim().startsWith("REP:")) {
                Label prod = new Label("• " + p[i].trim());
                prod.setStyle("-fx-font-size:12px; -fx-text-fill:" + DARK + ";");
                prodList.getChildren().add(prod);
            }
        }
        grid.add(prodList, 1, 3);

        // Total en rojo
        grid.add(detLabel("Total:"), 0, 4);
        Label totalVal = new Label("Bs " + p[5]);
        totalVal.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
        grid.add(totalVal, 1, 4);

        addDetRow(grid, "Método de pago:", p.length > 6 ? p[6].trim() : "Efectivo", 5, 0);

        // Tiempo estimado (columna derecha)
        grid.add(detLabel("Tiempo estimado:"), 2, 0);
        Label teVal = new Label("44 minutos");
        teVal.setStyle("-fx-font-size:12px; -fx-text-fill:" + DARK + ";");
        grid.add(teVal, 3, 0);

        // Tiempo restante — sincronizado con el timer de la cola
        grid.add(detLabel("Tiempo restante:"), 2, 1);
        Label trVal = new Label(String.format("%02d:%02d", secsArr[0] / 60, secsArr[0] % 60));
        trVal.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:" + ORANGE + ";");
        grid.add(trVal, 3, 1);

        if (lblTimerGrande != null)
            lblTimerGrande.setText(String.format("%02d:%02d", secsArr[0] / 60, secsArr[0] % 60));

        // Timer del detalle — lee el array de la cola (ya decrementado)
        if (selectedTimer != null) selectedTimer.stop();
        selectedTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int s = secsArr[0];
            trVal.setText(String.format("%02d:%02d", s / 60, s % 60));
            if (lblTimerGrande != null)
                lblTimerGrande.setText(String.format("%02d:%02d", s / 60, s % 60));
        }));
        selectedTimer.setCycleCount(Timeline.INDEFINITE);
        selectedTimer.play();

        ColumnConstraints c0 = new ColumnConstraints(130);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setHgrow(Priority.ALWAYS);
        ColumnConstraints c2 = new ColumnConstraints(130);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c0, c1, c2, c3);

        detalleContainer.getChildren().add(grid);
    }

    private static void addDetRow(GridPane g, String label, String value, int row, int colOffset) {
        g.add(detLabel(label), colOffset, row);
        Label val = new Label(value);
        val.setStyle("-fx-font-size:12px; -fx-text-fill:" + DARK + ";");
        val.setWrapText(true);
        g.add(val, colOffset + 1, row);
    }

    private static Label detLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:" + GRAY + ";");
        return l;
    }

    // ════════════════════════════════════════════════════════════════════════
    // MARCAR COMO ENTREGADO
    // ════════════════════════════════════════════════════════════════════════
    private static void marcarEntregado() {
        if (pedidoSeleccionado == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Sin selección");
            a.setHeaderText(null);
            a.setContentText("Selecciona un pedido de la cola primero.");
            a.showAndWait();
            return;
        }

        String idPedido = pedidoSeleccionado[0].trim();
        String cliente  = pedidoSeleccionado[1].trim();

        ArchivoManager arch    = new ArchivoManager();
        List<String>   pedidos = arch.leerLineas(PEDIDOS_TXT);
        List<String>   nuevos  = new ArrayList<>();
        String repartidorAsignado = null;

        for (String linea : pedidos) {
            String[] p = linea.split("\\|");
            if (p.length > 0 && p[0].trim().equals(idPedido)) {
                p[7] = "ENTREGADO";
                for (int i = 8; i < p.length; i++) {
                    if (p[i].trim().startsWith("REP:")) {
                        repartidorAsignado = p[i].trim().replace("REP:", "");
                        break;
                    }
                }
                nuevos.add(String.join("|", p));
            } else {
                nuevos.add(linea);
            }
        }
        arch.reescribirLineas(PEDIDOS_TXT, nuevos);

        // Liberar repartidor
        if (repartidorAsignado != null) liberarRepartidor(repartidorAsignado, arch);
        else                            liberarPrimerOcupado(arch);

        colaTimers.remove(idPedido);

        // Historial
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        arch.agregarLinea(HISTORIAL_TXT, "Pedido #" + idPedido + "|" + cliente + "|" + hora + "|ENTREGADO");

        AppState.agregarNotificacion("DELIVERY_OK",
            "📦 Pedido de " + cliente + " entregado correctamente · " + hora);

        pedidoSeleccionado = null;
        if (selectedTimer   != null) selectedTimer.stop();
        if (lblTimerGrande  != null) lblTimerGrande.setText("--:--");
        if (detalleContainer != null) {
            detalleContainer.getChildren().clear();
            Label ph = new Label("Pedido entregado. Selecciona otro pedido.");
            ph.setStyle("-fx-font-size:12px; -fx-text-fill:" + LIGHT_GREEN + ";");
            detalleContainer.getChildren().add(ph);
        }

        mostrarToast(cliente);
        cargarTodo();
    }

    // ── Gestión de repartidores ──────────────────────────────────────────────
    private static void liberarRepartidor(String idRep, ArchivoManager arch) {
        List<String> reps   = arch.leerLineas(REPARTIDORES_TXT);
        List<String> nuevos = new ArrayList<>();
        for (String r : reps) {
            String[] p = r.split("\\|");
            if (p.length >= 2 && p[0].trim().equals(idRep))
                nuevos.add(p[0] + "|" + p[1] + "|LIBRE");
            else
                nuevos.add(r);
        }
        arch.reescribirLineas(REPARTIDORES_TXT, nuevos);
    }

    private static void liberarPrimerOcupado(ArchivoManager arch) {
        List<String> reps    = arch.leerLineas(REPARTIDORES_TXT);
        List<String> nuevos  = new ArrayList<>();
        boolean liberado = false;
        for (String r : reps) {
            String[] p = r.split("\\|");
            if (!liberado && p.length >= 3 && p[2].trim().equals("OCUPADO")) {
                nuevos.add(p[0] + "|" + p[1] + "|LIBRE");
                liberado = true;
            } else {
                nuevos.add(r);
            }
        }
        arch.reescribirLineas(REPARTIDORES_TXT, nuevos);
    }

    private static void asignarRepartidorAlPedido(String idPedido, ArchivoManager arch) {
        List<String> reps = arch.leerLineas(REPARTIDORES_TXT);
        String repIdLibre = null, repNomLibre = null;
        for (String r : reps) {
            String[] p = r.split("\\|");
            if (p.length >= 3 && p[2].trim().equals("LIBRE")) {
                repIdLibre  = p[0].trim();
                repNomLibre = p[1].trim();
                break;
            }
        }
        if (repIdLibre == null) return;

        final String fRepId = repIdLibre;
        List<String> nuevosReps = new ArrayList<>();
        for (String r : reps) {
            String[] p = r.split("\\|");
            if (p[0].trim().equals(fRepId)) nuevosReps.add(p[0] + "|" + p[1] + "|OCUPADO|00:44");
            else                            nuevosReps.add(r);
        }
        arch.reescribirLineas(REPARTIDORES_TXT, nuevosReps);

        List<String> pedidos   = arch.leerLineas(PEDIDOS_TXT);
        List<String> nuevosPed = new ArrayList<>();
        for (String linea : pedidos) {
            String[] p = linea.split("\\|");
            if (p.length > 0 && p[0].trim().equals(idPedido) && p[7].trim().equals("LISTO_PARA_ENVIO")) {
                p[7] = "EN_CAMINO";
                boolean tieneRep = false;
                for (String campo : p) if (campo.startsWith("REP:")) { tieneRep = true; break; }
                if (!tieneRep) {
                    String[] ext = Arrays.copyOf(p, p.length + 1);
                    ext[p.length] = "REP:" + fRepId;
                    nuevosPed.add(String.join("|", ext));
                } else {
                    nuevosPed.add(String.join("|", p));
                }
            } else {
                nuevosPed.add(linea);
            }
        }
        arch.reescribirLineas(PEDIDOS_TXT, nuevosPed);
    }

    // ── Auto-refresh cada 8 s ────────────────────────────────────────────────
    private static void iniciarAutoRefresh() {
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(8), e -> {
            ArchivoManager arch = new ArchivoManager();
            List<String> pedidos = arch.leerLineas(PEDIDOS_TXT);
            for (String linea : pedidos) {
                String[] p = linea.split("\\|");
                if (p.length >= 8 && p[7].trim().equals("LISTO_PARA_ENVIO"))
                    asignarRepartidorAlPedido(p[0].trim(), arch);
            }
            cargarTodo();
        }));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
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

    private static String cardStyle() {
        return
            "-fx-background-color:" + WHITE + ";" +
            "-fx-border-color:" + BORDER + "; -fx-border-width:1.5;" +
            "-fx-border-radius:12; -fx-background-radius:12;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.06),8,0,0,2);";
    }
}
