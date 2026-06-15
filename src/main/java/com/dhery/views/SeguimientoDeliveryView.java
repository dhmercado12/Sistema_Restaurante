package com.dhery.views;

import com.dhery.GestorArchivo.ArchivoManager;
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
import java.util.*;

public class SeguimientoDeliveryView {

    // ── rutas ────────────────────────────────────────────────────────────────
    private static final String PEDIDOS_TXT      = "src/main/java/com/dhery/GestorArchivo/pedidos.txt";
    private static final String REPARTIDORES_TXT = "src/main/java/com/dhery/GestorArchivo/repartidores.txt";

    // ── paleta ───────────────────────────────────────────────────────────────
    private static final String RED         = "#CC0000";
    private static final String RED_DARK    = "#AA0000";
    private static final String RED_BG      = "#FFEAEA";
    private static final String GREEN       = "#2E7D32";
    private static final String LIGHT_GREEN = "#4CAF50";
    private static final String BLUE        = "#1565C0";
    private static final String BLUE_BG     = "#E3F2FD";
    private static final String ORANGE      = "#E65100";
    private static final String ORANGE_BG   = "#FFF3E0";
    private static final String WHITE       = "#FFFFFF";
    private static final String BG          = "#F5F5F5";
    private static final String GRAY        = "#888888";
    private static final String DARK        = "#1A1A1A";
    private static final String BORDER      = "#EEEEEE";

    // ── precios por producto (igual que TakeOrderViewC) ──────────────────────
    private static final Map<String, Integer> PRECIOS = new HashMap<>();
    static {
        PRECIOS.put("BIRRIA",                35);
        PRECIOS.put("QUESABIRRIA",           40);
        PRECIOS.put("SUADERO",               30);
        PRECIOS.put("PASTOR",                30);
        PRECIOS.put("RAMEN BIRRIA",          45);
        PRECIOS.put("NACHOS SUPREMOS",       40);
        PRECIOS.put("MEGABURRITO",           45);
        PRECIOS.put("TORTILLA EXTRA",        10);
        PRECIOS.put("HORCHATA",              10);
        PRECIOS.put("JAMAICA",                8);
        PRECIOS.put("NACHOS SUPREMOS COMBO 2", 45);
        PRECIOS.put("MEGABURRITO COMBO 2",   55);
    }

    // ── estado de la vista ───────────────────────────────────────────────────
    private static user        currentUser;
    private static Timeline    countdownTimer;
    private static Timeline    autoRefresh;

    // referencias a nodos que se actualizan
    private static Label       lblEtaDisplay;
    private static Label       lblStatusBadge;
    private static Label       lblPedidoNum;
    private static Label       lblPedidoFecha;
    private static Label       lblRepNombre;
    private static Label       lblRepTel;
    private static Button      btnLlamar;
    private static VBox        productosContainer;
    private static Label       lblSubtotal;
    private static Label       lblTotal;
    private static ScrollPane  mainScroll;

    // nodos del stepper
    private static StackPane   circCamino;
    private static StackPane   circCerca;
    private static StackPane   circEntregado;
    private static Label       lblLblCamino;
    private static Label       lblLblCerca;
    private static Label       lblLblEntregado;
    private static Label       lblTRecibido;
    private static Label       lblTCamino;
    private static Label       lblTCerca;
    private static Label       lblTEntregado;
    private static Region      connCaminoDone;
    private static Region      connCercaDone;
    private static Region      connEntregadoDone;

    // ════════════════════════════════════════════════════════════════════════
    public static Scene getScene(user u) {
        currentUser = u;
        if (countdownTimer != null) countdownTimer.stop();
        if (autoRefresh    != null) autoRefresh.stop();

        ScrollPane root = new ScrollPane(buildContent());
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color:" + BG + "; -fx-background:" + BG + ";");

        cargarPedido();
        iniciarAutoRefresh();

        return new Scene(root, 1280, 720);
    }

    // ════════════════════════════════════════════════════════════════════════
    // ESTRUCTURA PRINCIPAL
    // ════════════════════════════════════════════════════════════════════════
    private static VBox buildContent() {
        VBox page = new VBox(20);
        page.setPadding(new Insets(28, 60, 32, 60));
        page.setStyle("-fx-background-color:" + BG + ";");

        page.getChildren().addAll(
            buildBackButton(),
            buildPageHeader(),
            buildPedidoCard(),
            buildRepartidorCard(),
            buildDetalleSection(),
            buildFooterNote()
        );
        return page;
    }

    // ── Botón atrás ──────────────────────────────────────────────────────────
    private static HBox buildBackButton() {
        Button btn = new Button("← Volver al menú");
        btn.setStyle(
            "-fx-background-color:" + WHITE + ";" +
            "-fx-text-fill:" + GRAY + ";" +
            "-fx-border-color:" + BORDER + "; -fx-border-width:1.5;" +
            "-fx-border-radius:8; -fx-background-radius:8;" +
            "-fx-font-size:12px; -fx-font-weight:bold; -fx-cursor:hand;" +
            "-fx-padding:6 14 6 14;");
        btn.setOnAction(e -> {
            if (countdownTimer != null) countdownTimer.stop();
            if (autoRefresh    != null) autoRefresh.stop();
            Router.goMenuClienteView();
        });
        HBox box = new HBox(btn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    // ── Encabezado de página ─────────────────────────────────────────────────
    private static VBox buildPageHeader() {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);

        Label icons = new Label("🍴 🥄");
        icons.setStyle("-fx-font-size:22px; -fx-text-fill:" + RED + ";");

        Label title = new Label("MI PEDIDO DE DELIVERY");
        title.setStyle("-fx-font-size:30px; -fx-font-weight:bold; -fx-text-fill:" + DARK + ";");

        // Línea decorativa con badge central
        HBox brandRow = new HBox(8);
        brandRow.setAlignment(Pos.CENTER);
        Region lineL = new Region(); lineL.setPrefSize(50, 2);
        lineL.setStyle("-fx-background-color:" + RED + ";");
        Label badge = new Label("TACABRÓN RESTAURANTE");
        badge.setStyle(
            "-fx-background-color:" + RED + "; -fx-text-fill:white;" +
            "-fx-font-size:11px; -fx-font-weight:bold; -fx-padding:3 12 3 12;" +
            "-fx-background-radius:4;");
        Region lineR = new Region(); lineR.setPrefSize(50, 2);
        lineR.setStyle("-fx-background-color:" + RED + ";");
        brandRow.getChildren().addAll(lineL, badge, lineR);

        Label sub = new Label("Consulta el estado de tu pedido en tiempo real y los datos de tu repartidor.");
        sub.setStyle("-fx-font-size:13px; -fx-text-fill:" + GRAY + ";");

        box.getChildren().addAll(icons, title, brandRow, sub);
        return box;
    }

    // ════════════════════════════════════════════════════════════════════════
    // CARD: INFORMACIÓN DEL PEDIDO + STEPPER
    // ════════════════════════════════════════════════════════════════════════
    private static VBox buildPedidoCard() {
        VBox card = card();

        // ── Fila superior ────────────────────────────────────────────────────
        HBox topRow = new HBox(14);
        topRow.setAlignment(Pos.TOP_LEFT);

        // Icono
        StackPane iconBox = new StackPane();
        Circle iconBg = new Circle(24);
        iconBg.setFill(Color.web(RED_BG));
        Label iconLbl = new Label("📦");
        iconLbl.setStyle("-fx-font-size:20px;");
        iconBox.getChildren().addAll(iconBg, iconLbl);

        // Info izquierda
        VBox infoLeft = new VBox(4);
        lblPedidoNum = new Label("PEDIDO #—");
        lblPedidoNum.setStyle("-fx-font-size:17px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
        lblPedidoFecha = new Label("—");
        lblPedidoFecha.setStyle("-fx-font-size:12px; -fx-text-fill:" + GRAY + ";");
        infoLeft.getChildren().addAll(lblPedidoNum, lblPedidoFecha);

        Region hSpacer = new Region(); HBox.setHgrow(hSpacer, Priority.ALWAYS);

        // Info derecha: badge + ETA
        VBox infoRight = new VBox(4);
        infoRight.setAlignment(Pos.TOP_RIGHT);

        lblStatusBadge = new Label("—");
        lblStatusBadge.setStyle(
            "-fx-background-color:" + BLUE_BG + "; -fx-text-fill:" + BLUE + ";" +
            "-fx-font-size:11px; -fx-font-weight:bold;" +
            "-fx-padding:4 12 4 12; -fx-background-radius:20;");

        Label etaLabel = new Label("Tiempo estimado de entrega");
        etaLabel.setStyle("-fx-font-size:11px; -fx-text-fill:" + GRAY + ";");

        lblEtaDisplay = new Label("00:10");
        lblEtaDisplay.setStyle(
            "-fx-font-size:32px; -fx-font-weight:bold; -fx-text-fill:" + LIGHT_GREEN + ";");

        HBox etaUnits = new HBox(20);
        etaUnits.setAlignment(Pos.CENTER_RIGHT);
        Label uHoras   = new Label("HORAS");
        Label uMinutos = new Label("MINUTOS");
        uHoras.setStyle("-fx-font-size:9px; -fx-text-fill:" + GRAY + ";");
        uMinutos.setStyle("-fx-font-size:9px; -fx-text-fill:" + GRAY + ";");
        etaUnits.getChildren().addAll(uHoras, uMinutos);

        infoRight.getChildren().addAll(lblStatusBadge, etaLabel, lblEtaDisplay, etaUnits);
        topRow.getChildren().addAll(iconBox, infoLeft, hSpacer, infoRight);

        // ── Stepper ──────────────────────────────────────────────────────────
        HBox stepper = buildStepper();

        card.getChildren().addAll(topRow, stepper);
        return card;
    }

    private static HBox buildStepper() {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(20, 0, 4, 0));

        // PASO 1 — PEDIDO RECIBIDO (siempre activo)
        VBox step1 = buildStepNode("✔", true, false);
        Label lbl1 = (Label) ((VBox) step1.getChildren().get(1)).getChildren().get(0);
        lbl1.setText("PEDIDO RECIBIDO");
        lbl1.setStyle("-fx-font-size:10px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
        lblTRecibido = (Label) ((VBox) step1.getChildren().get(1)).getChildren().get(1);

        connCaminoDone = buildConnector(true);

        // PASO 2 — EN CAMINO
        VBox step2 = buildStepNode("🛵", false, false);
        circCamino  = (StackPane) step2.getChildren().get(0);
        VBox lblBox2 = (VBox) step2.getChildren().get(1);
        lblLblCamino = (Label) lblBox2.getChildren().get(0);
        lblLblCamino.setText("EN CAMINO");
        lblTCamino = (Label) lblBox2.getChildren().get(1);

        connCercaDone = buildConnector(false);

        // PASO 3 — CERCA DE TI
        VBox step3 = buildStepNode("📍", false, false);
        circCerca   = (StackPane) step3.getChildren().get(0);
        VBox lblBox3 = (VBox) step3.getChildren().get(1);
        lblLblCerca = (Label) lblBox3.getChildren().get(0);
        lblLblCerca.setText("CERCA DE TI");
        lblTCerca = (Label) lblBox3.getChildren().get(1);

        connEntregadoDone = buildConnector(false);

        // PASO 4 — ENTREGADO
        VBox step4 = buildStepNode("✔", false, false);
        circEntregado = (StackPane) step4.getChildren().get(0);
        VBox lblBox4  = (VBox) step4.getChildren().get(1);
        lblLblEntregado = (Label) lblBox4.getChildren().get(0);
        lblLblEntregado.setText("ENTREGADO");
        lblTEntregado = (Label) lblBox4.getChildren().get(1);

        row.getChildren().addAll(step1, connCaminoDone, step2, connCercaDone, step3, connEntregadoDone, step4);
        HBox.setHgrow(connCaminoDone,   Priority.ALWAYS);
        HBox.setHgrow(connCercaDone,    Priority.ALWAYS);
        HBox.setHgrow(connEntregadoDone, Priority.ALWAYS);
        return row;
    }

    /** Construye un nodo del stepper: círculo + (etiqueta, hora) */
    private static VBox buildStepNode(String icon, boolean done, boolean active) {
        VBox node = new VBox(6);
        node.setAlignment(Pos.TOP_CENTER);
        node.setMinWidth(100);

        StackPane circle = new StackPane();
        circle.setMinSize(40, 40);
        circle.setMaxSize(40, 40);
        Circle bg = new Circle(20);

        if (done) {
            bg.setFill(Color.web(RED));
            bg.setStroke(Color.web(RED));
        } else {
            bg.setFill(Color.web("#F0F0F0"));
            bg.setStroke(Color.web("#DDDDDD"));
        }
        bg.setStrokeWidth(2.5);

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size:15px; -fx-text-fill:" + (done ? "white" : GRAY) + ";");
        circle.getChildren().addAll(bg, iconLbl);

        Label lblText = new Label("—");
        lblText.setStyle("-fx-font-size:10px; -fx-font-weight:bold; -fx-text-fill:" + (done ? RED : GRAY) + ";");
        lblText.setAlignment(Pos.CENTER);
        Label lblTime = new Label("—");
        lblTime.setStyle("-fx-font-size:10px; -fx-text-fill:" + GRAY + ";");
        lblTime.setAlignment(Pos.CENTER);

        VBox labels = new VBox(2, lblText, lblTime);
        labels.setAlignment(Pos.CENTER);
        node.getChildren().addAll(circle, labels);
        return node;
    }

    private static Region buildConnector(boolean done) {
        Region r = new Region();
        r.setPrefHeight(3);
        r.setMaxHeight(3);
        r.setMinHeight(3);
        r.setStyle("-fx-background-color:" + (done ? RED : "#E0E0E0") + ";");
        VBox.setMargin(r, new Insets(0, 0, 20, 0));
        return r;
    }

    // ════════════════════════════════════════════════════════════════════════
    // CARD: REPARTIDOR
    // ════════════════════════════════════════════════════════════════════════
    private static VBox buildRepartidorCard() {
        VBox card = card();

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label personIco = new Label("👤");
        personIco.setStyle("-fx-font-size:16px;");
        Label title = new Label("TU REPARTIDOR");
        title.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
        titleRow.getChildren().addAll(personIco, title);

        HBox body = new HBox(24);
        body.setAlignment(Pos.CENTER_LEFT);

        // Avatar
        StackPane avatar = new StackPane();
        Circle avatarBg = new Circle(36);
        avatarBg.setFill(Color.web(RED_BG));
        avatarBg.setStroke(Color.web(RED));
        avatarBg.setStrokeWidth(3);
        Label avatarIco = new Label("🛵");
        avatarIco.setStyle("-fx-font-size:28px;");
        avatar.getChildren().addAll(avatarBg, avatarIco);

        // Info
        VBox info = new VBox(8);
        info.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox rowNombre = new HBox(8); rowNombre.setAlignment(Pos.CENTER_LEFT);
        Label lblNombreTxt = new Label("Nombre:");
        lblNombreTxt.setStyle("-fx-font-size:13px; -fx-text-fill:" + GRAY + "; -fx-min-width:80;");
        lblRepNombre = new Label("—");
        lblRepNombre.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + DARK + ";");
        rowNombre.getChildren().addAll(lblNombreTxt, lblRepNombre);

        HBox rowTel = new HBox(8); rowTel.setAlignment(Pos.CENTER_LEFT);
        Label lblTelTxt = new Label("Teléfono:");
        lblTelTxt.setStyle("-fx-font-size:13px; -fx-text-fill:" + GRAY + "; -fx-min-width:80;");
        lblRepTel = new Label("—");
        lblRepTel.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
        rowTel.getChildren().addAll(lblTelTxt, lblRepTel);

        info.getChildren().addAll(rowNombre, rowTel);

        // Sección contacto
        HBox contactBox = new HBox(14);
        contactBox.setAlignment(Pos.CENTER_LEFT);
        contactBox.setPadding(new Insets(14, 20, 14, 20));
        contactBox.setStyle(
            "-fx-background-color:" + RED_BG + ";" +
            "-fx-background-radius:12;");

        StackPane phoneCircle = new StackPane();
        Circle phoneBg = new Circle(22);
        phoneBg.setFill(Color.web(RED));
        Label phoneIco = new Label("📞");
        phoneIco.setStyle("-fx-font-size:16px;");
        phoneCircle.getChildren().addAll(phoneBg, phoneIco);

        VBox ctaBox = new VBox(6);
        Label ctaTxt = new Label("¿Necesitas contactarlo?");
        ctaTxt.setStyle("-fx-font-size:12px; -fx-text-fill:" + GRAY + ";");
        btnLlamar = new Button("LLAMAR REPARTIDOR");
        btnLlamar.setStyle(
            "-fx-background-color:" + RED + "; -fx-text-fill:white;" +
            "-fx-font-size:12px; -fx-font-weight:bold;" +
            "-fx-background-radius:8; -fx-cursor:hand; -fx-padding:7 16 7 16;");
        btnLlamar.setOnMouseEntered(ev -> btnLlamar.setStyle(
            "-fx-background-color:" + RED_DARK + "; -fx-text-fill:white;" +
            "-fx-font-size:12px; -fx-font-weight:bold;" +
            "-fx-background-radius:8; -fx-cursor:hand; -fx-padding:7 16 7 16;"));
        btnLlamar.setOnMouseExited(ev -> btnLlamar.setStyle(
            "-fx-background-color:" + RED + "; -fx-text-fill:white;" +
            "-fx-font-size:12px; -fx-font-weight:bold;" +
            "-fx-background-radius:8; -fx-cursor:hand; -fx-padding:7 16 7 16;"));
        ctaBox.getChildren().addAll(ctaTxt, btnLlamar);
        contactBox.getChildren().addAll(phoneCircle, ctaBox);

        body.getChildren().addAll(avatar, info, contactBox);
        card.getChildren().addAll(titleRow, body);
        return card;
    }

    // ════════════════════════════════════════════════════════════════════════
    // SECCIÓN: DETALLE DEL PEDIDO + RESUMEN
    // ════════════════════════════════════════════════════════════════════════
    private static HBox buildDetalleSection() {
    HBox row = new HBox(16);
    row.setAlignment(Pos.TOP_LEFT);

    VBox cardDetalle = card();
    HBox.setHgrow(cardDetalle, Priority.ALWAYS);

    HBox titleRow = new HBox(8);
    titleRow.setAlignment(Pos.CENTER_LEFT);
    Label listIco = new Label("≡");
    listIco.setStyle("-fx-font-size:16px; -fx-text-fill:" + RED + ";");
    Label titleLbl = new Label("DETALLE DE TU PEDIDO");
    titleLbl.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
    titleRow.getChildren().addAll(listIco, titleLbl);

    HBox tableHeader = new HBox(0);
    tableHeader.setStyle("-fx-background-color:" + RED + "; -fx-background-radius:6;");
    tableHeader.setPadding(new Insets(10, 12, 10, 12));
    Label hProd = tblHeaderCell("Producto", -1);
    HBox.setHgrow(hProd, Priority.ALWAYS);
    tableHeader.getChildren().addAll(hProd,
        tblHeaderCell("Cantidad", 100),
        tblHeaderCell("Precio Unit.", 120),
        tblHeaderCell("Total", 100));

    productosContainer = new VBox(0);
    cardDetalle.getChildren().addAll(titleRow, tableHeader, productosContainer);

    // ── Card resumen ──────────────────────────────────────────────────────
    VBox cardResumen = card();
    cardResumen.setPrefWidth(230);
    cardResumen.setMinWidth(230);

    Label resTitle = new Label("RESUMEN");
    resTitle.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");

    // Subtotal
    HBox rowSub = new HBox();
    rowSub.setAlignment(Pos.CENTER_LEFT);
    Label subKey = new Label("Subtotal:");
    subKey.setStyle("-fx-font-size:13px; -fx-text-fill:" + GRAY + ";");
    Region subSp = new Region(); HBox.setHgrow(subSp, Priority.ALWAYS);
    lblSubtotal = new Label("—");
    lblSubtotal.setStyle("-fx-font-size:13px; -fx-text-fill:" + DARK + ";");
    rowSub.getChildren().addAll(subKey, subSp, lblSubtotal);

    // Envío
    HBox rowEnvio = new HBox();
    rowEnvio.setAlignment(Pos.CENTER_LEFT);
    Label envioKey = new Label("Costo de envío:");
    envioKey.setStyle("-fx-font-size:13px; -fx-text-fill:" + GRAY + ";");
    Region envioSp = new Region(); HBox.setHgrow(envioSp, Priority.ALWAYS);
    Label envioVal = new Label("Bs 10");
    envioVal.setStyle("-fx-font-size:13px; -fx-text-fill:" + DARK + ";");
    rowEnvio.getChildren().addAll(envioKey, envioSp, envioVal);

    Separator sep = new Separator();

    // Total
    HBox rowTotal = new HBox();
    rowTotal.setPadding(new Insets(8, 0, 0, 0));
    Label totalKey = new Label("TOTAL:");
    totalKey.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
    Region totalSp = new Region(); HBox.setHgrow(totalSp, Priority.ALWAYS);
    lblTotal = new Label("—");
    lblTotal.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
    rowTotal.getChildren().addAll(totalKey, totalSp, lblTotal);

    cardResumen.getChildren().addAll(resTitle, rowSub, rowEnvio, sep, rowTotal);
    row.getChildren().addAll(cardDetalle, cardResumen);
    return row;
}

    // ── Nota al pie ──────────────────────────────────────────────────────────
    private static HBox buildFooterNote() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(10, 16, 10, 16));
        box.setStyle(
            "-fx-background-color:" + ORANGE_BG + ";" +
            "-fx-border-color:#FFD59950; -fx-border-width:1;" +
            "-fx-background-radius:8; -fx-border-radius:8;");
        Label ico = new Label("ℹ");
        ico.setStyle("-fx-font-size:15px; -fx-text-fill:" + ORANGE + ";");
        Label txt = new Label("Los tiempos pueden variar según condiciones del tráfico y la ubicación.");
        txt.setStyle("-fx-font-size:12px; -fx-text-fill:#7A4F00;");
        box.getChildren().addAll(ico, txt);
        return box;
    }

    // ════════════════════════════════════════════════════════════════════════
    // LÓGICA DE CARGA DESDE ARCHIVOS
    // ════════════════════════════════════════════════════════════════════════
    private static void cargarPedido() {
        ArchivoManager arch = new ArchivoManager();
        List<String> lineas = arch.leerLineas(PEDIDOS_TXT);

        String[] pedidoEncontrado = null;

        for (String linea : lineas) {
            if (linea == null || linea.isBlank()) continue;
            String[] p = linea.split("\\|");
            if (p.length < 9) continue;

            // Debe ser DELIVERY
            if (!p[4].trim().equalsIgnoreCase("DELIVERY")) continue;

            // El nombre del pedido debe coincidir con el usuario actual
            // Buscamos por nombre O por id de factura en facturas.txt
            String estado = p[7].trim();

            // Mostrar si está EN cualquier estado activo delivery
            boolean esActivo = estado.equals("EN_PROCESO")
    || estado.equals("LISTO_PARA_ENVIO")
    || estado.equals("EN_CAMINO");

            if (!esActivo) continue;

            // Verificar que pertenece al usuario: comparar nombre
            String nombrePedido = p[1].trim();
            String nombreUser   = (currentUser.getUsername() + " " + currentUser.getApellidos()).trim();
            if (nombrePedido.equalsIgnoreCase(nombreUser)) {
                pedidoEncontrado = p;
                break;
            }
        }

        if (pedidoEncontrado == null) {
            mostrarSinPedido();
            return;
        }

        renderPedido(pedidoEncontrado, arch);
    }

    private static void renderPedido(String[] p, ArchivoManager arch) {
        String idPedido = p[0].trim();
        String estado   = p[7].trim();

        // Número de pedido
        lblPedidoNum.setText("PEDIDO #" + String.format("%04d", parseIntSafe(idPedido)));

        // Fecha — usamos la fecha del día (pedidos.txt no guarda fecha/hora de creación)
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String horaAhora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        lblPedidoFecha.setText("Realizado el " + fechaHoy + " - " + horaAhora);
      
        // Badge de estado
        actualizarBadge(estado);

        // Stepper
        actualizarStepper(estado);

        // Productos
        List<String[]> productos = parsearProductos(p);
        renderProductos(productos);

        // Totales
        int subtotal = calcularSubtotal(productos);
        lblSubtotal.setText("Bs " + subtotal);
        lblTotal.setText("Bs " + (subtotal + 10));

        // Repartidor
        String repId = getRepartidorId(p);
        cargarRepartidor(repId, arch);

        // Countdown — 44 minutos
        iniciarCountdown(10);
    }

    private static void actualizarBadge(String estado) {
        switch (estado) {
            case "EN_PROCESO":
                lblStatusBadge.setText("EN PREPARACIÓN");
                lblStatusBadge.setStyle(badgeStyle(ORANGE_BG, ORANGE));
                break;
            case "LISTO_PARA_ENVIO":
                lblStatusBadge.setText("LISTO — ASIGNANDO REPARTIDOR");
                lblStatusBadge.setStyle(badgeStyle("#FFF9E0", "#8A6B00"));
                break;
            case "EN_CAMINO":
                lblStatusBadge.setText("EN CAMINO");
                lblStatusBadge.setStyle(badgeStyle(BLUE_BG, BLUE));
                break;
            case "ENTREGADO":
                lblStatusBadge.setText("ENTREGADO");
                lblStatusBadge.setStyle(badgeStyle("#E8F5E9", GREEN));
                lblEtaDisplay.setText("00:00");
                lblEtaDisplay.setStyle("-fx-font-size:32px; -fx-font-weight:bold; -fx-text-fill:" + LIGHT_GREEN + ";");
                break;
            default:
                lblStatusBadge.setText(estado);
                lblStatusBadge.setStyle(badgeStyle(BLUE_BG, BLUE));
        }
    }

   private static void actualizarStepper(String estado) {

    String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    lblTRecibido.setText(hora);

    switch (estado) {

        case "EN_PROCESO":
            break;

        case "LISTO_PARA_ENVIO":
        case "EN_CAMINO":

            activarStep(circCamino, lblLblCamino, lblTCamino, hora);
            connCaminoDone.setStyle("-fx-background-color:" + RED + ";");
            break;

        case "EN_CAMINO_AVANZADO": // si lo usas luego
            activarStep(circCerca, lblLblCerca, lblTCerca, hora);
            connCercaDone.setStyle("-fx-background-color:" + RED + ";");
            break;

        case "ENTREGADO":

            activarStep(circCamino, lblLblCamino, lblTCamino, hora);
            activarStep(circCerca, lblLblCerca, lblTCerca, hora);
            activarStep(circEntregado, lblLblEntregado, lblTEntregado, hora);

            connCaminoDone.setStyle("-fx-background-color:" + RED + ";");
            connCercaDone.setStyle("-fx-background-color:" + RED + ";");
            connEntregadoDone.setStyle("-fx-background-color:" + RED + ";");
            break;
    }
}

    private static void activarStep(StackPane circ, Label lblTexto, Label lblHora, String hora) {
        if (circ != null) {
            Circle bg = (Circle) circ.getChildren().get(0);
            bg.setFill(Color.web(RED));
            bg.setStroke(Color.web(RED));
            Label ico = (Label) circ.getChildren().get(1);
            ico.setStyle("-fx-font-size:15px; -fx-text-fill:white;");
        }
        if (lblTexto != null)
            lblTexto.setStyle("-fx-font-size:10px; -fx-font-weight:bold; -fx-text-fill:" + RED + ";");
        if (lblHora != null)
            lblHora.setText(hora);
    }

    private static void renderProductos(List<String[]> productos) {
        productosContainer.getChildren().clear();
        for (int i = 0; i < productos.size(); i++) {
            String[] pr  = productos.get(i);
            String nombre = capitalize(pr[0]);
            int    cant   = parseIntSafe(pr[1]);
            int    precio = PRECIOS.getOrDefault(pr[0].toUpperCase(), 0);
            int    linea  = precio * cant;

            HBox row = new HBox(0);
            row.setPadding(new Insets(10, 12, 10, 12));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color:" + (i % 2 == 0 ? WHITE : "#FAFAFA") + ";" +
                "-fx-border-color:" + BORDER + "; -fx-border-width:0 0 1 0;");

            Label lblNombre = new Label("• " + nombre);
            lblNombre.setStyle("-fx-font-size:13px; -fx-text-fill:" + DARK + ";");
            HBox.setHgrow(lblNombre, Priority.ALWAYS);

            Label lblCant   = tblCell(String.valueOf(cant),  100);
            Label lblPrecio = tblCell("Bs " + precio,        120);
            Label lblTotal2 = tblCell("Bs " + linea,         100);

            row.getChildren().addAll(lblNombre, lblCant, lblPrecio, lblTotal2);
            productosContainer.getChildren().add(row);
        }
    }

    private static void cargarRepartidor(String repId, ArchivoManager arch) {
    List<String> reps = arch.leerLineas(REPARTIDORES_TXT);

    String nombreTemp = "—";
    String telefonoTemp = "Sin teléfono registrado";

    if (repId != null && !repId.isBlank()) {

        for (String r : reps) {
            if (r == null || r.isBlank()) continue;

            String[] p = r.split("\\|");
            if (p.length < 4) continue;

            String id = p[0].trim();

            if (id.equals(repId)) {
                nombreTemp = p[1].trim();
                telefonoTemp = p[3].trim(); //formato: id|nombre|estado|telefono
                break;
            }
        }
    }

    // variables finales para usar en el botón
    final String nombre = nombreTemp;
    final String telefono = telefonoTemp;

    lblRepNombre.setText(nombre);
    lblRepTel.setText(telefono);

    btnLlamar.setOnAction(e -> {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Llamar repartidor");
        a.setHeaderText(null);
        a.setContentText(
            "Repartidor: " + nombre +
            "\nTeléfono: " + telefono
        );
        a.showAndWait();
    });
}

    // ── Countdown ─────────────────────────────────────────────────────────────
    private static void iniciarCountdown(int segundos) {
    if (countdownTimer != null) countdownTimer.stop();

    int[] segs = {segundos};

    countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {

        if (segs[0] <= 0) {

    countdownTimer.stop();

    // Obtener el ID actual del pedido
    String idPedido = lblPedidoNum.getText()
            .replace("PEDIDO #", "")
            .trim();

    marcarComoEntregado(String.valueOf(parseIntSafe(idPedido)));

    cargarPedido();

    return;
}


        segs[0]--;

        int m = segs[0] / 60;
        int s = segs[0] % 60;

        lblEtaDisplay.setText(String.format("%02d:%02d", m, s));

        // 🔥 activar "cerca de ti"
        if (segs[0] == 5) {
            activarStep(
                circCerca,
                lblLblCerca,
                lblTCerca,
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            );
            connCercaDone.setStyle("-fx-background-color:" + RED + ";");
        }
    }));

    countdownTimer.setCycleCount(Timeline.INDEFINITE);
    countdownTimer.play();
}

    // ── Auto-refresh cada 15 s ────────────────────────────────────────────────
    private static void iniciarAutoRefresh() {
      //  autoRefresh = new Timeline(new KeyFrame(Duration.seconds(15), e -> cargarPedido()));
        //autoRefresh.setCycleCount(Timeline.INDEFINITE);
    //    autoRefresh.play();
    }

    // ── Sin pedido activo ─────────────────────────────────────────────────────
    private static void mostrarSinPedido() {
    if (productosContainer != null) productosContainer.getChildren().clear();

    lblPedidoNum.setText("Sin pedidos activos");
    lblPedidoFecha.setText("No tienes un pedido delivery en proceso.");
    lblStatusBadge.setText("SIN PEDIDO");

    lblEtaDisplay.setText("--:--");

    lblRepNombre.setText("—");
    lblRepTel.setText("—");

    lblSubtotal.setText("—");
    lblTotal.setText("—");
}

    // ════════════════════════════════════════════════════════════════════════
    // HELPERS UTILITARIOS
    // ════════════════════════════════════════════════════════════════════════
    private static List<String[]> parsearProductos(String[] p) {
        List<String[]> lista = new ArrayList<>();
        for (int i = 8; i < p.length; i++) {
            String seg = p[i].trim();
            if (seg.isEmpty() || seg.startsWith("REP:")) continue;
            int xIdx = seg.lastIndexOf(" x");
            if (xIdx != -1) {
                String nombre = seg.substring(0, xIdx).trim();
                String cant   = seg.substring(xIdx + 2).trim();
                lista.add(new String[]{nombre, cant});
            } else {
                lista.add(new String[]{seg, "1"});
            }
        }
        return lista;
    }

    private static int calcularSubtotal(List<String[]> prods) {
        int sub = 0;
        for (String[] pr : prods) {
            int precio = PRECIOS.getOrDefault(pr[0].toUpperCase(), 0);
            int cant   = parseIntSafe(pr[1]);
            sub += precio * cant;
        }
        return sub;
    }

    private static String getRepartidorId(String[] p) {
        for (int i = 8; i < p.length; i++) {
            if (p[i].trim().startsWith("REP:"))
                return p[i].trim().replace("REP:", "").trim();
        }
        return null;
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private static String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        String[] words = s.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words)
            if (!w.isEmpty()) sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        return sb.toString().trim();
    }

    // ════════════════════════════════════════════════════════════════════════
    // HELPERS DE ESTILO
    // ════════════════════════════════════════════════════════════════════════
    private static VBox card() {
        VBox c = new VBox(14);
        c.setPadding(new Insets(20, 24, 20, 24));
        c.setStyle(
            "-fx-background-color:" + WHITE + ";" +
            "-fx-border-color:" + BORDER + "; -fx-border-width:1;" +
            "-fx-border-radius:14; -fx-background-radius:14;");
        return c;
    }

    private static String badgeStyle(String bg, String fg) {
        return "-fx-background-color:" + bg + "; -fx-text-fill:" + fg + ";" +
            "-fx-font-size:11px; -fx-font-weight:bold;" +
            "-fx-padding:4 12 4 12; -fx-background-radius:20;";
    }

    private static Label tblHeaderCell(String text, double width) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:white;");
        l.setAlignment(Pos.CENTER_LEFT);
        if (width > 0) { l.setPrefWidth(width); l.setMinWidth(width); }
        return l;
    }

    private static Label tblCell(String text, double width) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:13px; -fx-text-fill:" + DARK + ";");
        l.setPrefWidth(width);
        l.setAlignment(Pos.CENTER_LEFT);
        return l;
    }

    private static HBox resumenRow(String label) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label key = new Label(label);
        key.setStyle("-fx-font-size:13px; -fx-text-fill:" + GRAY + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label val = new Label("—");
        val.setStyle("-fx-font-size:13px; -fx-text-fill:" + DARK + ";");
        row.getChildren().addAll(key, sp, val);
        return row;
    }
    private static void marcarComoEntregado(String idPedido) {

    ArchivoManager arch = new ArchivoManager();
    List<String> lineas = arch.leerLineas(PEDIDOS_TXT);

    for (int i = 0; i < lineas.size(); i++) {

        String[] p = lineas.get(i).split("\\|");

        if (p.length < 8) continue;

        if (p[0].trim().equals(idPedido)) {

            p[7] = "ENTREGADO";

            lineas.set(i, String.join("|", p));
            break;
        }
    }

    arch.reescribirLineas(PEDIDOS_TXT, lineas);
}
}
