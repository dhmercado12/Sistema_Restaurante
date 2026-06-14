package com.dhery.views;

import com.dhery.app.AppState;
import com.dhery.app.Router;
import com.dhery.models.user;
import com.dhery.repositories.UserRepository;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Panel principal del Administrador — Tacabrón Restaurante.
 *
 * Integración:
 *  1. Colocar este archivo en: src/main/java/com/dhery/views/
 *  2. Agregar ADMINISTRADOR al enum Role en Router.java
 *  3. Agregar goAdminDashboard() en Router.java
 *  4. Actualizar ClientLoginView.java para redirigir al rol ADMINISTRADOR
 *
 * El panel reutiliza las vistas existentes (StockView, HistorialVentasView,
 * ClientesRegistradosView, EstadoCocinaView, ControlDeliveryView) accedidas
 * desde los botones del sidebar y de los módulos del dashboard.
 */
public class AdminDashboardView {

    // ── CONSTANTES DE COLOR ───────────────────────────────────────────────────
    private static final String ROJO       = "#CC0000";
    private static final String ROJO_HOVER = "#AA0000";
    private static final String ROJO_SUAVE = "#FFF5F5";
    private static final String ROJO_BG    = "#FFEAEA";
    private static final String GRIS_BORDE = "#EEEEEE";
    private static final String TEXTO_OSC  = "#1A1A1A";
    private static final String TEXTO_GRS  = "#888888";

    // ── RUTAS DE ARCHIVOS ─────────────────────────────────────────────────────
    private static final String RUTA_FACTURAS  = "src/main/java/com/dhery/GestorArchivo/facturas.txt";
    private static final String RUTA_PEDIDOS   = "src/main/java/com/dhery/GestorArchivo/pedidos.txt";
    private static final String RUTA_HISTORIAL = "src/main/java/com/dhery/GestorArchivo/historial.txt";

    private static user currentUser;

    // ── ENTRADA PÚBLICA ───────────────────────────────────────────────────────
    public static Scene getScene(user user) {
        currentUser = user;

        // Layout raíz: sidebar izquierdo + contenido central
        HBox root = new HBox();
        root.setStyle("-fx-background-color: #F5F5F5;");

        VBox sidebar  = buildSidebar();
        VBox mainArea = buildMainArea();
        HBox.setHgrow(mainArea, Priority.ALWAYS);

        root.getChildren().addAll(sidebar, mainArea);
        return new Scene(root, 1280, 720);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SIDEBAR
    // ══════════════════════════════════════════════════════════════════════════
    private static VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(320);
        sidebar.setMinWidth(320);
        sidebar.setMaxWidth(320);
        sidebar.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + GRIS_BORDE + ";" +
            "-fx-border-width: 0 1 0 0;"
        );

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: white;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox content = new VBox(0);
        content.setPadding(new Insets(20));

        // Sección: Gestión de Usuarios
        content.getChildren().add(buildSidebarSection("👥", "GESTIÓN DE USUARIOS"));
        content.getChildren().addAll(
            buildSidebarBtn("➕", "Crear Cajero",   () -> mostrarDialogoCrearUsuario("CAJERO")),
            buildSidebarBtn("🛵", "Crear Delivery",  () -> mostrarDialogoCrearUsuario("DELIVERY")),
            buildSidebarBtn("📝", "Editar Usuarios",  () -> mostrarDialogoEditarUsuario()),
            buildSidebarBtn("❌", "Eliminar Usuarios",() -> mostrarDialogoEliminarUsuario())
        );
        content.getChildren().add(buildDivider());

        // Sección: Inventario
        content.getChildren().add(buildSidebarSection("📦", "INVENTARIO"));
        content.getChildren().addAll(
            buildSidebarBtn("📋", "Gestionar Stock",    () -> Router.goStockView()),
            buildSidebarBtn("🔔", "Alertas de Escasez", () -> mostrarAlertasEscasez())
        );

        // Panel de escasez
        VBox escasezPanel = buildEscasezPanel();
        content.getChildren().add(escasezPanel);
        content.getChildren().add(buildDivider());

        // Sección: Monitor de Pedidos
        content.getChildren().add(buildSidebarSection("🛵", "MONITOR DE PEDIDOS"));
        content.getChildren().addAll(
            buildMonitorStatRow("🕐", "Pendientes",   contarPendientes(), ROJO),
            buildMonitorStatRow("🍳", "En preparación", contarEnPreparacion(), "#FF8C00"),
            buildMonitorStatRow("✅", "Entregados",   contarEntregados(), "#2E7D32")
        );

        scroll.setContent(content);
        sidebar.getChildren().add(scroll);
        return sidebar;
    }

    private static Label buildSidebarSection(String icon, String title) {
        Label lbl = new Label(icon + "  " + title);
        lbl.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-text-fill: " + ROJO + ";" +
            "-fx-padding: 14 0 8 0;"
        );
        return lbl;
    }

    private static HBox buildSidebarBtn(String icon, String label, Runnable action) {
        HBox btn = new HBox(10);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 14, 10, 14));
        btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 15px;");
        Label textLbl = new Label(label);
        textLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXTO_OSC + ";");

        btn.getChildren().addAll(iconLbl, textLbl);
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + ROJO_SUAVE + ";" +
            "-fx-background-radius: 10; -fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: white; -fx-background-radius: 10; -fx-cursor: hand;"
        ));
        btn.setOnMouseClicked(e -> action.run());
        return btn;
    }

    private static Region buildDivider() {
        Region div = new Region();
        div.setPrefHeight(1);
        div.setStyle("-fx-background-color: " + GRIS_BORDE + ";");
        VBox.setMargin(div, new Insets(8, 0, 8, 0));
        return div;
    }

    private static VBox buildEscasezPanel() {
        VBox panel = new VBox(4);
        panel.setPadding(new Insets(12));
        panel.setStyle(
            "-fx-background-color: #FFF3F3;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #FFCCCC;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;"
        );

        Label titulo = new Label("PRODUCTOS EN ESCASEZ");
        titulo.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + ROJO + ";");

        VBox lista = new VBox(3);
        List<AppState.ItemStock> bajos = getItemsBajoStock();

        if (bajos.isEmpty()) {
            Label ok = new Label("✅  Sin alertas de escasez");
            ok.setStyle("-fx-font-size: 11px; -fx-text-fill: #2E7D32;");
            lista.getChildren().add(ok);
        } else {
            for (AppState.ItemStock item : bajos) {
                Label row = new Label("•  " + item.nombre);
                row.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");
                lista.getChildren().add(row);
            }
        }

        // Ícono triángulo de advertencia
        Label warn = new Label("⚠");
        warn.setStyle("-fx-font-size: 28px; -fx-text-fill: #FFAA00; -fx-opacity: 0.6;");
        StackPane.setAlignment(warn, Pos.CENTER_RIGHT);
        StackPane overlay = new StackPane(lista, warn);

        panel.getChildren().addAll(titulo, overlay);
        VBox.setMargin(panel, new Insets(6, 0, 6, 0));
        return panel;
    }

    private static HBox buildMonitorStatRow(String icon, String label, int count, String color) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 14, 8, 14));

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 16px;");
        Label textLbl = new Label(label);
        textLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXTO_OSC + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        StackPane badge = new StackPane();
        Circle circle = new Circle(14);
        circle.setFill(Color.web(color));
        Label num = new Label(String.valueOf(count));
        num.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white;");
        badge.getChildren().addAll(circle, num);

        row.getChildren().addAll(iconLbl, textLbl, spacer, badge);
        return row;
    }

    private static HBox buildRedFullButton(String text, Runnable action) {
        HBox btn = new HBox();
        btn.setAlignment(Pos.CENTER);
        btn.setPadding(new Insets(10));
        btn.setStyle(
            "-fx-background-color: " + ROJO + ";" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        VBox.setMargin(btn, new Insets(8, 14, 8, 14));

        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");
        btn.getChildren().add(lbl);

        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + ROJO_HOVER + ";" +
            "-fx-background-radius: 10; -fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + ROJO + ";" +
            "-fx-background-radius: 10; -fx-cursor: hand;"
        ));
        btn.setOnMouseClicked(e -> action.run());
        return btn;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ÁREA PRINCIPAL
    // ══════════════════════════════════════════════════════════════════════════
    private static VBox buildMainArea() {
        VBox area = new VBox(0);
        area.setStyle("-fx-background-color: #F5F5F5;");

        // Barra superior (header)
        HBox header = buildHeader();

        // Contenido scrolleable
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: #F5F5F5;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox scrollContent = new VBox(16);
        scrollContent.setPadding(new Insets(20));

        // Fila superior: Dashboard de ventas + Productos más vendidos
        HBox topRow = new HBox(16);
        VBox dashboardVentas = buildDashboardVentas();
        VBox productosVendidos = buildProductosMasVendidos();
        HBox.setHgrow(dashboardVentas, Priority.ALWAYS);
        topRow.getChildren().addAll(dashboardVentas, productosVendidos);

        // Fila inferior: Monitor de pedidos + Estadísticas
        HBox bottomRow = new HBox(16);
        VBox monitorPedidos = buildMonitorPedidos();
        VBox estadisticas = buildEstadisticasGenerales();
        HBox.setHgrow(monitorPedidos, Priority.ALWAYS);
        bottomRow.getChildren().addAll(monitorPedidos, estadisticas);

        // Botón cerrar sesión
        HBox logoutRow = new HBox();
        logoutRow.setAlignment(Pos.CENTER);
        Button btnLogout = buildLogoutButton();
        logoutRow.getChildren().add(btnLogout);

        // Tagline
        VBox tagline = buildTagline();

        scrollContent.getChildren().addAll(topRow, bottomRow, logoutRow, tagline);
        scroll.setContent(scrollContent);
        area.getChildren().addAll(header, scroll);
        return area;
    }

    // ── HEADER ────────────────────────────────────────────────────────────────
    private static HBox buildHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setAlignment(Pos.CENTER);
        header.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + GRIS_BORDE + ";" +
            "-fx-border-width: 0 0 1 0;"
        );

        // Logo + títulos centrados
        VBox titleBox = new VBox(2);
        titleBox.setAlignment(Pos.CENTER);

        HBox logoRow = new HBox(10);
        logoRow.setAlignment(Pos.CENTER);
        Label chefIcon = new Label("🍴🥄");
        chefIcon.setStyle("-fx-font-size: 26px; -fx-text-fill: " + ROJO + ";");
        Label mainTitle = new Label("ADMINISTRADOR");
        mainTitle.setStyle(
            "-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + TEXTO_OSC + ";"
        );
        logoRow.getChildren().addAll(chefIcon, mainTitle);

        HBox subtitleRow = new HBox(10);
        subtitleRow.setAlignment(Pos.CENTER);
        Region lLine = new Region(); lLine.setPrefSize(40, 2);
        lLine.setStyle("-fx-background-color: " + ROJO + ";");
        Label subtitle = new Label("TACABRÓN RESTAURANTE");
        subtitle.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold;" +
            "-fx-text-fill: " + ROJO + ";"
        );
        Region rLine = new Region(); rLine.setPrefSize(40, 2);
        rLine.setStyle("-fx-background-color: " + ROJO + ";");
        subtitleRow.getChildren().addAll(lLine, subtitle, rLine);

        titleBox.getChildren().addAll(logoRow, subtitleRow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Info de usuario (derecha)
        HBox userInfo = buildUserInfo();

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);

        header.getChildren().addAll(leftSpacer, titleBox, spacer, userInfo);
        return header;
    }

    private static HBox buildUserInfo() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);

        // Notif badge
        StackPane notifBtn = new StackPane();
        Circle circle = new Circle(22);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.web(GRIS_BORDE));
        circle.setStrokeWidth(1.5);
        Label bell = new Label("🔔");
        bell.setStyle("-fx-font-size: 15px;");

        StackPane badge = new StackPane();
        badge.setTranslateX(11); badge.setTranslateY(-11);
        Circle badgeC = new Circle(7); badgeC.setFill(Color.web(ROJO));
        Label badgeN = new Label("1");
        badgeN.setStyle("-fx-font-size: 8px; -fx-text-fill: white; -fx-font-weight: bold;");
        badge.getChildren().addAll(badgeC, badgeN);

        notifBtn.getChildren().addAll(circle, bell, badge);
        notifBtn.setMaxSize(44, 44);
        notifBtn.setStyle("-fx-cursor: hand;");

        // Nombre y correo
        VBox userText = new VBox(2);
        String nombre = currentUser != null ? currentUser.getUsername() : "Administrador";
        String email  = currentUser != null
            ? currentUser.getUsername().toLowerCase() + "@tacabron.com"
            : "admin@tacabron.com";
        Label nameL = new Label(nombre);
        nameL.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXTO_OSC + ";");
        Label emailL = new Label(email);
        emailL.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXTO_GRS + ";");
        userText.getChildren().addAll(nameL, emailL);

        box.getChildren().addAll(notifBtn, userText);
        return box;
    }

    // ── DASHBOARD DE VENTAS ───────────────────────────────────────────────────
    private static VBox buildDashboardVentas() {
        VBox card = buildCard();

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("📊");
        icon.setStyle("-fx-font-size: 18px;");
        Label title = new Label("DASHBOARD DE VENTAS");
        title.setStyle(
            "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + ROJO + ";"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Selector de rango de fechas (decorativo, muestra rango actual)
        HBox dateBox = new HBox(6);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setPadding(new Insets(4, 10, 4, 10));
        dateBox.setStyle(
            "-fx-background-color: white; -fx-border-color: " + GRIS_BORDE + ";" +
            "-fx-border-radius: 8; -fx-background-radius: 8;"
        );
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d MMM, yyyy",
            new Locale("es","ES"));
        Label dateLbl = new Label("📅  " + weekAgo.format(fmt) + " – " + today.format(fmt));
        dateLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXTO_OSC + ";");
        Label chevron = new Label("∨");
        chevron.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXTO_GRS + ";");
        dateBox.getChildren().addAll(dateLbl, chevron);

        titleRow.getChildren().addAll(icon, title, spacer, dateBox);

        // Tarjetas de resumen
        HBox statsRow = buildVentasStats();

        // Gráfico de área
        AreaChart<String, Number> chart = buildVentasChart();
        VBox.setVgrow(chart, Priority.ALWAYS);

        card.getChildren().addAll(titleRow, statsRow, chart);
        return card;
    }

    private static HBox buildVentasStats() {
        HBox row = new HBox(12);

        // Valores reales calculados desde facturas.txt
        double totalHoy    = calcularVentasHoy();
        double totalSemana = calcularVentasDias(7);
        double totalMes    = calcularVentasDias(30);

        row.getChildren().addAll(
            buildStatCard("VENTAS HOY",    totalHoy,    "+12.5%", "vs ayer",         "#1565C0"),
            buildStatCard("VENTAS SEMANA", totalSemana, "+8.3%",  "vs semana pasada", "#2E7D32"),
            buildStatCard("VENTAS MES",    totalMes,    "+15.7%", "vs mes pasado",    "#6A1B9A")
        );
        HBox.setHgrow(row.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(row.getChildren().get(1), Priority.ALWAYS);
        HBox.getHgrow(row.getChildren().get(2));
        return row;
    }

    private static VBox buildStatCard(String label, double valor, String pct, String sub, String color) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + GRIS_BORDE + ";" +
            "-fx-border-radius: 10; -fx-background-radius: 10;"
        );
        HBox.setHgrow(card, Priority.ALWAYS);

        Label labelLbl = new Label(label);
        labelLbl.setStyle(
            "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + color + ";"
        );
        Label valueLbl = new Label(String.format("Bs. %.0f", valor));
        valueLbl.setStyle(
            "-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXTO_OSC + ";"
        );
        Label pctLbl = new Label(pct);
        pctLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #2E7D32; -fx-font-weight: bold;");
        Label subLbl = new Label(sub);
        subLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXTO_GRS + ";");

        card.getChildren().addAll(labelLbl, valueLbl, pctLbl, subLbl);
        return card;
    }

    private static AreaChart<String, Number> buildVentasChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setStyle("-fx-font-size: 10px;");
        yAxis.setStyle("-fx-font-size: 10px;");
        yAxis.setLabel("Bs.");

        AreaChart<String, Number> chart = new AreaChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setPrefHeight(180);
        chart.setStyle(
            "-fx-background-color: transparent;" +
            ".chart-plot-background { -fx-background-color: transparent; }" +
            ".chart-series-area-fill { -fx-fill: rgba(204,0,0,0.12); }" +
            ".chart-series-area-line { -fx-stroke: #CC0000; -fx-stroke-width: 2.5; }"
        );

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("d MMM", new Locale("es","ES"));
        DateTimeFormatter fileFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Construir mapa de ventas por día desde facturas.txt
        Map<String, Double> ventasPorDia = new HashMap<>();
        for (String linea : leerLineas(RUTA_FACTURAS)) {
            String[] p = linea.split("\\|");
            if (p.length >= 4) {
                try {
                    LocalDate fecha = LocalDate.parse(p[2].trim(), fileFmt);
                    double total = Double.parseDouble(p[3].trim());
                    ventasPorDia.merge(fecha.format(fileFmt), total, Double::sum);
                } catch (Exception ignored) {}
            }
        }

        for (int i = 7; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            double v = ventasPorDia.getOrDefault(d.format(fileFmt), 0.0);
            series.getData().add(new XYChart.Data<>(d.format(dayFmt), v));
        }

        chart.getData().add(series);
        return chart;
    }

    // ── PRODUCTOS MÁS VENDIDOS ────────────────────────────────────────────────
    private static VBox buildProductosMasVendidos() {
        VBox card = buildCard();
        card.setPrefWidth(340);
        card.setMinWidth(300);

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("🏆");
        icon.setStyle("-fx-font-size: 18px;");
        Label title = new Label("PRODUCTOS MÁS VENDIDOS");
        title.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + ROJO + ";"
        );
        titleRow.getChildren().addAll(icon, title);

        // Cabecera de tabla
        HBox header = buildTablaHeader();

        // Filas: Top 5 de productos según historial AppState
        VBox tabla = new VBox(0);
        List<String[]> top5 = getTop5Productos();
        for (int i = 0; i < top5.size(); i++) {
            tabla.getChildren().add(buildTablaFila(i + 1, top5.get(i)));
        }

        // Botón ver reporte
        HBox reporteBtn = new HBox();
        reporteBtn.setAlignment(Pos.CENTER);
        reporteBtn.setPadding(new Insets(8, 12, 8, 12));
        reporteBtn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + ROJO + ";" +
            "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        Label btnLbl = new Label("📊  Ver reporte completo");
        btnLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + ROJO + ";");
        reporteBtn.getChildren().add(btnLbl);
        reporteBtn.setOnMouseEntered(e -> reporteBtn.setStyle(
            "-fx-background-color: " + ROJO_SUAVE + ";" +
            "-fx-border-color: " + ROJO + ";" +
            "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"
        ));
        reporteBtn.setOnMouseExited(e -> reporteBtn.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + ROJO + ";" +
            "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"
        ));
        reporteBtn.setOnMouseClicked(e -> Router.goHistorialVentasView());

        card.getChildren().addAll(titleRow, header, tabla, reporteBtn);
        return card;
    }

    private static HBox buildTablaHeader() {
        HBox row = new HBox();
        row.setPadding(new Insets(6, 0, 6, 0));
        row.setStyle("-fx-border-color: transparent transparent " + GRIS_BORDE + " transparent; -fx-border-width: 1;");

        Label h1 = headerLbl("#",        40);
        Label h2 = headerLbl("Producto", 130);
        Label h3 = headerLbl("Vendidos",  70);
        Label h4 = headerLbl("Ingresos",  90);
        row.getChildren().addAll(h1, h2, h3, h4);
        return row;
    }

    private static Label headerLbl(String text, double w) {
        Label l = new Label(text);
        l.setPrefWidth(w);
        l.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXTO_OSC + ";");
        return l;
    }

    private static HBox buildTablaFila(int num, String[] datos) {
        // datos: [nombre, vendidos, ingresos]
        HBox row = new HBox();
        row.setPadding(new Insets(8, 0, 8, 0));
        row.setAlignment(Pos.CENTER_LEFT);
        String bg = num % 2 == 0 ? "#FAFAFA" : "white";
        row.setStyle("-fx-background-color: " + bg + ";");

        Label n = cellLbl(String.valueOf(num), 40);
        Label p = cellLbl(datos[0], 130);
        Label v = cellLbl(datos[1], 70);
        Label i = cellLbl("Bs. " + datos[2], 90);
        row.getChildren().addAll(n, p, v, i);
        return row;
    }

    private static Label cellLbl(String text, double w) {
        Label l = new Label(text);
        l.setPrefWidth(w);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXTO_OSC + ";");
        return l;
    }

    // ── MONITOR DE PEDIDOS ────────────────────────────────────────────────────
    private static VBox buildMonitorPedidos() {
        VBox card = buildCard();

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("🛵");
        icon.setStyle("-fx-font-size: 18px;");
        Label title = new Label("MONITOR DE PEDIDOS EN TIEMPO REAL");
        title.setStyle(
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + ROJO + ";"
        );
        titleRow.getChildren().addAll(icon, title);

        // Cabecera tabla pedidos
        HBox header = buildPedidosHeader();

        // Filas de pedidos — primero AppState (tiempo real), luego pedidos.txt
        VBox tabla = new VBox(0);
        List<AppState.PedidoCocina> pedidosMemoria = new ArrayList<>(AppState.pedidosCocina);
        Collections.reverse(pedidosMemoria);

        if (!pedidosMemoria.isEmpty()) {
            int count = 0;
            for (AppState.PedidoCocina p : pedidosMemoria) {
                if (count++ >= 5) break;
                tabla.getChildren().add(buildPedidoFila(p));
            }
        } else {
            // Leer desde pedidos.txt
            List<String> lineasPedidos = leerLineas(RUTA_PEDIDOS);
            List<String> recientes = new ArrayList<>(lineasPedidos);
            Collections.reverse(recientes);

            if (recientes.isEmpty()) {
                Label empty = new Label("No hay pedidos registrados en este momento.");
                empty.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXTO_GRS + "; -fx-padding: 16;");
                tabla.getChildren().add(empty);
            } else {
                int count = 0;
                for (String linea : recientes) {
                    if (count++ >= 5) break;
                    HBox fila = buildPedidoFilaTxt(linea, count);
                    if (fila != null) tabla.getChildren().add(fila);
                }
            }
        }

        // Link ver todos
        HBox verTodos = new HBox(6);
        verTodos.setAlignment(Pos.CENTER);
        verTodos.setPadding(new Insets(8));
        verTodos.setStyle("-fx-cursor: hand;");
        Label eye = new Label("👁");
        Label verLbl = new Label("Ver todos los pedidos");
        verLbl.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + ROJO + ";"
        );
        verTodos.getChildren().addAll(eye, verLbl);
        verTodos.setOnMouseClicked(e -> Router.goEstadoCocinaView());

        card.getChildren().addAll(titleRow, header, tabla, verTodos);
        return card;
    }

    /**
     * Construye una fila de pedido leyendo desde una línea de pedidos.txt.
     * Formato: id|cliente|tel|dir|tipo|total|pago|estado|productos...
     */
    private static HBox buildPedidoFilaTxt(String linea, int rowNum) {
        String[] p = linea.split("\\|");
        if (p.length < 8) return null;

        HBox row = new HBox(8);
        row.setPadding(new Insets(9, 0, 9, 0));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
            "-fx-border-color: transparent transparent " + GRIS_BORDE + " transparent;" +
            "-fx-border-width: 1;"
        );

        String idStr     = p[0].trim();
        String cliente   = p[1].trim();
        String tipo      = p[4].trim();      // DELIVERY o LOCAL
        String estadoTxt = p[7].trim();      // LISTO, ENTREGADO, etc.
        String totalStr  = p[5].trim();

        Label numLbl     = cellLbl("#" + String.format("%05d", Integer.parseInt(idStr.isEmpty() ? "0" : idStr)), 70);
        Label clienteLbl = cellLbl(cliente.length() > 14 ? cliente.substring(0, 14) + "…" : cliente, 120);

        boolean esDelivery = "DELIVERY".equalsIgnoreCase(tipo);
        Label tipoBadge  = buildBadge(esDelivery ? "Delivery" : "Mesa", ROJO, "white");

        String estadoColor, estadoDisplay;
        switch (estadoTxt.toUpperCase()) {
            case "LISTO":
                // Mesa: listo en cocina = se sirve directo → Entregado
                // Delivery: listo = pendiente de reparto → Pendiente
                if (!esDelivery) {
                    estadoColor = "#2E7D32"; estadoDisplay = "Entregado";
                } else {
                    estadoColor = "#FF8C00"; estadoDisplay = "Pendiente";
                }
                break;
            case "LISTO_PARA_ENVIO":
                estadoColor = "#FF8C00"; estadoDisplay = "Pendiente";     break;
            case "ENTREGADO":
                estadoColor = "#2E7D32"; estadoDisplay = "Entregado";     break;
            case "EN_PROCESO":
                estadoColor = "#1565C0"; estadoDisplay = "En preparación"; break;
            default:
                estadoColor = "#1565C0"; estadoDisplay = "En preparación"; break;
        }
        Label estadoBadge = buildBadge(estadoDisplay, estadoColor, "white");

        double total = 0;
        try { total = Double.parseDouble(totalStr); } catch (NumberFormatException ignored) {}

        Label fechaLbl = cellLbl(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 130);
        Label totalLbl = cellLbl("Bs. " + String.format("%.0f", total), 80);
        Label dots     = new Label("⋮");
        dots.setStyle("-fx-font-size: 18px; -fx-text-fill: " + TEXTO_GRS + "; -fx-cursor: hand;");

        HBox tipoBox   = new HBox(tipoBadge);   tipoBox.setPrefWidth(80);
        HBox estadoBox = new HBox(estadoBadge); estadoBox.setPrefWidth(110);

        row.getChildren().addAll(numLbl, clienteLbl, tipoBox, estadoBox, fechaLbl, totalLbl, dots);
        return row;
    }

    private static HBox buildPedidosHeader() {
        HBox row = new HBox(8);
        row.setPadding(new Insets(6, 0, 6, 0));
        row.setStyle("-fx-border-color: transparent transparent " + GRIS_BORDE + " transparent; -fx-border-width: 1;");
        row.getChildren().addAll(
            headerLbl("Pedido",  70),
            headerLbl("Cliente", 120),
            headerLbl("Tipo",    80),
            headerLbl("Estado",  110),
            headerLbl("Fecha",   130),
            headerLbl("Total",   80)
        );
        return row;
    }

    private static HBox buildPedidoFila(AppState.PedidoCocina p) {
        HBox row = new HBox(8);
        row.setPadding(new Insets(9, 0, 9, 0));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
            "-fx-border-color: transparent transparent " + GRIS_BORDE + " transparent;" +
            "-fx-border-width: 1;"
        );

        Label numLbl    = cellLbl("#" + String.format("%05d", p.id), 70);
        Label clienteLbl= cellLbl(p.cliente.length() > 14 ? p.cliente.substring(0,14)+"…" : p.cliente, 120);

        // Badge tipo
        Label tipoBadge = buildBadge(p.esDelivery ? "Delivery" : "Mesa", ROJO, "white");

        // Badge estado — para Mesa: LISTO = Entregado; para Delivery: LISTO = Pendiente (pendiente de reparto)
        String estadoColor, estadoText;
        if (p.estado.contains("PROCESO")) {
            estadoColor = "#1565C0"; estadoText = "En preparación";
        } else if (p.estado.contains("ENTREGADO")) {
            estadoColor = "#2E7D32"; estadoText = "Entregado";
        } else if (p.estado.contains("LISTO")) {
            if (!p.esDelivery) {
                estadoColor = "#2E7D32"; estadoText = "Entregado";
            } else {
                estadoColor = "#FF8C00"; estadoText = "Pendiente";
            }
        } else {
            estadoColor = "#1565C0"; estadoText = "Pendiente";
        }
        Label estadoBadge  = buildBadge(estadoText, estadoColor, "white");

        Label fechaLbl  = cellLbl(LocalDate.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " --:--", 130);
        Label totalLbl  = cellLbl("Bs. " + String.format("%.0f", p.total), 80);

        // Menú de opciones (tres puntos)
        Label dots = new Label("⋮");
        dots.setStyle("-fx-font-size: 18px; -fx-text-fill: " + TEXTO_GRS + "; -fx-cursor: hand;");

        HBox tipoBox = new HBox(tipoBadge);  tipoBox.setPrefWidth(80);
        HBox estadoBox = new HBox(estadoBadge); estadoBox.setPrefWidth(110);

        row.getChildren().addAll(numLbl, clienteLbl, tipoBox, estadoBox, fechaLbl, totalLbl, dots);
        return row;
    }

    private static Label buildBadge(String text, String bgColor, String textColor) {
        Label badge = new Label(text);
        badge.setPadding(new Insets(3, 8, 3, 8));
        badge.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-text-fill: " + textColor + ";" +
            "-fx-font-size: 10px; -fx-font-weight: bold;" +
            "-fx-background-radius: 12;"
        );
        return badge;
    }

    // ── ESTADÍSTICAS GENERALES ────────────────────────────────────────────────
    private static VBox buildEstadisticasGenerales() {
        VBox card = buildCard();
        card.setPrefWidth(280);
        card.setMinWidth(260);

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("📈");
        icon.setStyle("-fx-font-size: 18px;");
        Label title = new Label("ESTADÍSTICAS GENERALES");
        title.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + ROJO + ";"
        );
        titleRow.getChildren().addAll(icon, title);

        UserRepository repo = new UserRepository();
        List<user> allUsers = repo.listarUsuarios();

        // Clientes: rol CLIENTE
        long clientes   = allUsers.stream()
            .filter(u -> "CLIENTE".equalsIgnoreCase(u.getRol())).count();

        // Cajeros: rol ADMIN y username contiene "cajera" o "cajero"
        long cajeros    = allUsers.stream()
            .filter(u -> "ADMIN".equalsIgnoreCase(u.getRol()) &&
                (u.getUsername().toLowerCase().contains("cajera") ||
                 u.getUsername().toLowerCase().contains("cajero"))).count();

        // Delivery: contar desde repartidores.txt (fuente de verdad)
        com.dhery.GestorArchivo.ArchivoManager arch2 =
            new com.dhery.GestorArchivo.ArchivoManager();
        long deliveries = arch2.leerLineas(REPARTIDORES_TXT).stream()
            .filter(l -> !l.trim().isEmpty()).count();

        // Productos en catálogo: contar desde AppState
        long productos  = AppState.inventario.size();

        // Pedidos hoy: leer desde facturas.txt
        int pedidosHoy  = contarPedidosHoy();

        VBox stats = new VBox(8);
        stats.getChildren().addAll(
            buildStatRow("👥", "Clientes registrados", (int) clientes),
            buildStatRow("👤", "Cajeros activos",       (int) Math.max(cajeros, 1)),
            buildStatRow("🛵", "Delivery activos",      (int) deliveries),
            buildStatRow("📦", "Productos en catálogo", (int) productos),
            buildStatRow("📋", "Pedidos hoy",           pedidosHoy)
        );

        card.getChildren().addAll(titleRow, stats);
        return card;
    }

    private static HBox buildStatRow(String icon, String label, int value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 8, 10, 8));
        row.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + GRIS_BORDE + ";" +
            "-fx-border-radius: 8; -fx-background-radius: 8;"
        );

        // Ícono con fondo circular rojo
        StackPane iconBox = new StackPane();
        Circle bg = new Circle(18);
        bg.setFill(Color.web(ROJO_BG));
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 14px;");
        iconBox.getChildren().addAll(bg, iconLbl);

        Label textLbl = new Label(label);
        textLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXTO_OSC + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valueLbl = new Label(String.valueOf(value));
        valueLbl.setStyle(
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXTO_OSC + ";"
        );

        row.getChildren().addAll(iconBox, textLbl, spacer, valueLbl);
        return row;
    }

    // ── LOGOUT + TAGLINE ──────────────────────────────────────────────────────
    private static Button buildLogoutButton() {
        Button btn = new Button("🚪  CERRAR SESIÓN");
        btn.setPrefSize(280, 46);
        String normal =
            "-fx-background-color: " + ROJO + ";" +
            "-fx-text-fill: white; -fx-font-size: 14px;" +
            "-fx-font-weight: bold; -fx-background-radius: 28; -fx-cursor: hand;";
        String hover =
            "-fx-background-color: " + ROJO_HOVER + ";" +
            "-fx-text-fill: white; -fx-font-size: 14px;" +
            "-fx-font-weight: bold; -fx-background-radius: 28; -fx-cursor: hand;";
        btn.setStyle(normal);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
        btn.setOnAction(e -> Router.goClientLogin());
        return btn;
    }

    private static VBox buildTagline() {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);

        HBox lineRow = new HBox(12);
        lineRow.setAlignment(Pos.CENTER);
        Region lLine = new Region(); lLine.setPrefSize(80, 1);
        lLine.setStyle("-fx-background-color: #DDDDDD;");
        Label chefMini = new Label("🍴");
        chefMini.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ROJO + ";");
        Region rLine = new Region(); rLine.setPrefSize(80, 1);
        rLine.setStyle("-fx-background-color: #DDDDDD;");
        lineRow.getChildren().addAll(lLine, chefMini, rLine);

        Label tagline = new Label("Sabor que enamora");
        tagline.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #777777;");

        box.getChildren().addAll(lineRow, tagline);
        return box;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DIÁLOGOS DE GESTIÓN DE USUARIOS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Diálogo para crear un nuevo usuario (Cajero o Delivery).
     * Usa el mismo formato del archivo usuarios.txt:
     *   id|username|password|rol|apellidos|telefono|direccion
     * El rol guardado es "ADMIN" para mantener compatibilidad con el login existente,
     * y el username distingue cajera/delivery (como en ClientLoginView).
     */
    private static final String REPARTIDORES_TXT =
        "src/main/java/com/dhery/GestorArchivo/repartidores.txt";

    private static void mostrarDialogoCrearUsuario(String tipo) {
        boolean esDelivery = "DELIVERY".equalsIgnoreCase(tipo);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Crear " + tipo);
        dialog.setHeaderText("Nuevo usuario — " + tipo);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField tfUsername  = new TextField();
        tfUsername.setPromptText("Nombre de usuario");
        TextField tfApellidos = new TextField();
        tfApellidos.setPromptText("Apellidos");
        TextField tfTelefono  = new TextField();
        tfTelefono.setPromptText("Teléfono");

        int row = 0;
        grid.add(new Label("Usuario:"),   0, row); grid.add(tfUsername,  1, row++);

        // Contraseña solo para CAJERO, no para DELIVERY. Dirección eliminada para CAJERO.
        PasswordField tfPass  = new PasswordField();
        TextField tfDireccion = new TextField();
        if (!esDelivery) {
            tfPass.setPromptText("Contraseña (dejar vacío = 1234)");
            grid.add(new Label("Contraseña:"), 0, row); grid.add(tfPass, 1, row++);
        }

        grid.add(new Label("Apellidos:"), 0, row); grid.add(tfApellidos, 1, row++);
        grid.add(new Label("Teléfono:"),  0, row); grid.add(tfTelefono,  1, row++);

        Label hint;
        if (esDelivery) {
            hint = new Label("💡 El repartidor se vinculará automáticamente al panel\n" +
                "de Control de Delivery con estado LIBRE.");
        } else {
            hint = new Label("💡 Para " + tipo + ", el sistema usará el rol ADMIN " +
                "y redirigirá por username al panel correspondiente.");
        }
        hint.setWrapText(true);
        hint.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
        grid.add(hint, 0, row, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                String username  = tfUsername.getText().trim();
                String apellidos = tfApellidos.getText().trim();
                String telefono  = tfTelefono.getText().trim();

                if (username.isEmpty()) {
                    new Alert(Alert.AlertType.WARNING,
                        "El nombre de usuario es obligatorio.").showAndWait();
                    return;
                }

                String passInput = tfPass.getText();
                String pass      = esDelivery ? "delivery1234"
                                 : (passInput.isEmpty() ? "1234" : passInput);
                String direccion = "";   // Dirección eliminada para CAJERO

                UserRepository repo = new UserRepository();
                int nuevoId = repo.generarNuevoId();
                user nuevoUser = new user(nuevoId, username, pass, "ADMIN",
                    apellidos, telefono, direccion);
                repo.guardarUsuario(nuevoUser);

                // Si es DELIVERY, registrar también en repartidores.txt
                if (esDelivery) {
                    com.dhery.GestorArchivo.ArchivoManager arch =
                        new com.dhery.GestorArchivo.ArchivoManager();
                    List<String> reps = arch.leerLineas(REPARTIDORES_TXT);
                    int nextRepId = reps.size() + 1;
                    arch.agregarLinea(REPARTIDORES_TXT,
                        nextRepId + "|" + username + "|LIBRE");
                }

                new Alert(Alert.AlertType.INFORMATION,
                    "✅ " + tipo + " creado correctamente.\n" +
                    "Usuario: " + username +
                    (esDelivery ? "\n🛵 Repartidor registrado en el panel de Delivery." : ""))
                    .showAndWait();
            }
        });
    }

    /** Determina el rol visible según username y rol interno. */
    private static String resolverRolVisible(user u) {
        String username = u.getUsername().toLowerCase();
        String rol      = u.getRol().toUpperCase();
        if ("CLIENTE".equals(rol))  return "CLIENTE";
        // Rol ADMIN: distinguir por username o por presencia en repartidores
        com.dhery.GestorArchivo.ArchivoManager archTmp =
            new com.dhery.GestorArchivo.ArchivoManager();
        boolean esRepartidor = archTmp.leerLineas(REPARTIDORES_TXT).stream()
            .anyMatch(l -> { String[] p = l.split("\\|"); return p.length > 1 &&
                p[1].trim().equalsIgnoreCase(u.getUsername()); });
        if (esRepartidor)                           return "DELIVERY";
        if (username.contains("cajera") || username.contains("cajero")) return "CAJERO";
        if (username.contains("cocina"))            return "COCINA";
        if (username.equals("admin"))               return "ADMINISTRADOR";
        return "ADMIN";
    }

    /** Color de badge según rol visible. */
    private static String colorRol(String rolVisible) {
        switch (rolVisible) {
            case "ADMINISTRADOR": return "#1565C0";
            case "CAJERO":        return "#6A1B9A";
            case "DELIVERY":      return "#E8890C";
            case "COCINA":        return "#2E7D32";
            case "CLIENTE":       return "#888888";
            default:              return "#555555";
        }
    }

    private static void mostrarDialogoEditarUsuario() {
        UserRepository repo = new UserRepository();
        List<user> todos = repo.listarUsuarios();

        if (todos.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No hay usuarios registrados.").showAndWait();
            return;
        }

        // ── Ventana principal tipo tabla ─────────────────────────────────────
        Stage stage = new Stage();
        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        stage.setTitle("Editar Usuarios");
        stage.setMinWidth(860);
        stage.setMinHeight(520);

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color:#F5F5F5;");

        // Encabezado
        HBox header = new HBox(10);
        header.setPadding(new Insets(20, 28, 16, 28));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color:white;" +
            "-fx-border-color:#EEEEEE; -fx-border-width:0 0 1 0;");
        Circle dot = new Circle(7, Color.web(ROJO));
        Label titulo = new Label("EDITAR USUARIO");
        titulo.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:" + ROJO + ";");
        header.getChildren().addAll(dot, titulo);

        // Cabecera de tabla
        String[] cols   = {"ID", "Usuario", "Nombre", "Rol", "Estado", "Acciones"};
        double[] widths = { 55,   130,        200,      140,   110,       130 };

        HBox colHeader = new HBox(0);
        colHeader.setPadding(new Insets(10, 28, 10, 28));
        colHeader.setStyle("-fx-background-color:white;" +
            "-fx-border-color:#EEEEEE; -fx-border-width:0 0 1 0;");
        for (int i = 0; i < cols.length; i++) {
            Label lbl = new Label(cols[i]);
            lbl.setPrefWidth(widths[i]);
            lbl.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#1A1A1A;");
            colHeader.getChildren().add(lbl);
        }

        // Filas scrolleables
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:#F5F5F5;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox tabla = new VBox(0);
        tabla.setStyle("-fx-background-color:#F5F5F5;");

        for (user u : todos) {
            String rolVisible = resolverRolVisible(u);
            String nombre     = (u.getUsername() + " " + u.getApellidos()).trim();

            HBox fila = new HBox(0);
            fila.setPadding(new Insets(12, 28, 12, 28));
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.setStyle("-fx-background-color:white;" +
                "-fx-border-color:#EEEEEE; -fx-border-width:0 0 1 0;");

            // ID
            Label lblId = new Label(String.valueOf(u.getId()));
            lblId.setPrefWidth(widths[0]);
            lblId.setStyle("-fx-font-size:13px; -fx-text-fill:#1A1A1A;");

            // Usuario
            Label lblUser = new Label(u.getUsername());
            lblUser.setPrefWidth(widths[1]);
            lblUser.setStyle("-fx-font-size:13px; -fx-text-fill:#1A1A1A;");

            // Nombre completo
            Label lblNombre = new Label(nombre);
            lblNombre.setPrefWidth(widths[2]);
            lblNombre.setStyle("-fx-font-size:13px; -fx-text-fill:#1A1A1A;");

            // Badge rol
            Label badgeRol = new Label(rolVisible);
            badgeRol.setPadding(new Insets(4, 12, 4, 12));
            String cRol = colorRol(rolVisible);
            badgeRol.setStyle(
                "-fx-background-color:" + cRol + "22;" +
                "-fx-text-fill:" + cRol + ";" +
                "-fx-font-size:11px; -fx-font-weight:bold;" +
                "-fx-background-radius:20;");
            HBox boxRol = new HBox(badgeRol);
            boxRol.setPrefWidth(widths[3]);
            boxRol.setAlignment(Pos.CENTER_LEFT);

            // Badge estado
            Label badgeEst = new Label("Activo");
            badgeEst.setPadding(new Insets(4, 12, 4, 12));
            badgeEst.setStyle(
                "-fx-background-color:#2E7D32;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:11px; -fx-font-weight:bold;" +
                "-fx-background-radius:20;");
            HBox boxEst = new HBox(badgeEst);
            boxEst.setPrefWidth(widths[4]);
            boxEst.setAlignment(Pos.CENTER_LEFT);

            // Botón editar
            Button btnEditar = new Button("✏  Editar");
            btnEditar.setPrefWidth(110);
            String estNormal =
                "-fx-background-color:white;" +
                "-fx-border-color:#1565C0; -fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-text-fill:#1565C0; -fx-font-size:12px;" +
                "-fx-font-weight:bold; -fx-cursor:hand;";
            String estHover =
                "-fx-background-color:#E3F2FD;" +
                "-fx-border-color:#1565C0; -fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-text-fill:#1565C0; -fx-font-size:12px;" +
                "-fx-font-weight:bold; -fx-cursor:hand;";
            btnEditar.setStyle(estNormal);
            btnEditar.setOnMouseEntered(e -> btnEditar.setStyle(estHover));
            btnEditar.setOnMouseExited(e  -> btnEditar.setStyle(estNormal));

            // Acción editar → abre sub-diálogo
            final user target = u;
            final List<user> todosRef = todos;
            btnEditar.setOnAction(e ->
                abrirFormEdicion(target, todosRef, rolVisible, tabla, stage));

            HBox boxAccion = new HBox(btnEditar);
            boxAccion.setPrefWidth(widths[5]);
            boxAccion.setAlignment(Pos.CENTER_LEFT);

            fila.getChildren().addAll(
                lblId, lblUser, lblNombre, boxRol, boxEst, boxAccion);

            // Hover fila
            fila.setOnMouseEntered(e ->
                fila.setStyle("-fx-background-color:#F9F9F9;" +
                    "-fx-border-color:#EEEEEE; -fx-border-width:0 0 1 0;"));
            fila.setOnMouseExited(e ->
                fila.setStyle("-fx-background-color:white;" +
                    "-fx-border-color:#EEEEEE; -fx-border-width:0 0 1 0;"));

            tabla.getChildren().add(fila);
        }

        scroll.setContent(tabla);

        // Footer con botón cerrar
        HBox footer = new HBox();
        footer.setPadding(new Insets(14, 28, 14, 28));
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setStyle("-fx-background-color:white;" +
            "-fx-border-color:#EEEEEE; -fx-border-width:1 0 0 0;");
        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setPrefSize(110, 36);
        btnCerrar.setStyle(
            "-fx-background-color:#EEEEEE; -fx-text-fill:#333;" +
            "-fx-font-size:13px; -fx-background-radius:8; -fx-cursor:hand;");
        btnCerrar.setOnAction(e -> stage.close());
        footer.getChildren().add(btnCerrar);

        root.getChildren().addAll(header, colHeader, scroll, footer);
        stage.setScene(new Scene(root, 860, 540));
        stage.show();
    }

    /** Sub-diálogo de edición de un usuario individual. */
    private static void abrirFormEdicion(user target, List<user> todos,
                                          String rolVisible, VBox tabla, Stage parentStage) {
        boolean esDelivery = "DELIVERY".equals(rolVisible);
        boolean esCliente  = "CLIENTE".equals(rolVisible);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editando: " + target.getUsername());
        dialog.setHeaderText("Modificar datos de " + target.getUsername()
            + "  [" + rolVisible + "]");
        dialog.initOwner(parentStage);

        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(12);
        grid.setPadding(new Insets(24));

        TextField     tfUsername  = new TextField(target.getUsername());
        PasswordField tfPass      = new PasswordField();
        tfPass.setPromptText("Dejar vacío para no cambiar");
        TextField     tfApellidos = new TextField(target.getApellidos());
        TextField     tfTelefono  = new TextField(target.getTelefono());
        TextField     tfDireccion = new TextField(target.getDireccion());

        // Ancho mínimo de campos
        tfUsername.setPrefWidth(240);
        tfApellidos.setPrefWidth(240);
        tfTelefono.setPrefWidth(240);
        tfDireccion.setPrefWidth(240);

        int r = 0;
        grid.add(new Label("Usuario:"),   0, r); grid.add(tfUsername,  1, r++);
        grid.add(new Label("Apellidos:"), 0, r); grid.add(tfApellidos, 1, r++);
        grid.add(new Label("Teléfono:"),  0, r); grid.add(tfTelefono,  1, r++);

        // Dirección solo si no es DELIVERY
        if (!esDelivery) {
            grid.add(new Label("Dirección:"), 0, r); grid.add(tfDireccion, 1, r++);
        }
        // Contraseña: no para DELIVERY
        if (!esDelivery) {
            grid.add(new Label("Contraseña:"), 0, r); grid.add(tfPass, 1, r++);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefWidth(440);

        dialog.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                target.setUsername(tfUsername.getText().trim());
                target.setApellidos(tfApellidos.getText().trim());
                target.setTelefono(tfTelefono.getText().trim());
                if (!esDelivery) {
                    target.setDireccion(tfDireccion.getText().trim());
                    if (!tfPass.getText().isEmpty())
                        target.setPassword(tfPass.getText());
                }
                ArchivoManagerHelper.actualizarUsuario(todos, target);

                // Si es DELIVERY, actualizar también nombre en repartidores.txt
                if (esDelivery) {
                    com.dhery.GestorArchivo.ArchivoManager arch =
                        new com.dhery.GestorArchivo.ArchivoManager();
                    List<String> reps = arch.leerLineas(REPARTIDORES_TXT);
                    List<String> nuevos = new java.util.ArrayList<>();
                    for (String l : reps) {
                        String[] p = l.split("\\|");
                        if (p.length > 1 && p[1].trim()
                                .equalsIgnoreCase(target.getUsername())) {
                            nuevos.add(p[0] + "|" + target.getUsername()
                                + "|" + (p.length > 2 ? p[2] : "LIBRE"));
                        } else {
                            nuevos.add(l);
                        }
                    }
                    arch.reescribirLineas(REPARTIDORES_TXT, nuevos);
                }

                new Alert(Alert.AlertType.INFORMATION,
                    "✅ Usuario \"" + target.getUsername() + "\" actualizado correctamente.")
                    .showAndWait();

                // Refrescar la tabla recargando la ventana
                parentStage.close();
                mostrarDialogoEditarUsuario();
            }
        });
    }

    private static void mostrarDialogoEliminarUsuario() {
        UserRepository repo = new UserRepository();
    List<user> todos = repo.listarUsuarios();

    // No permitir eliminar al administrador actual
    todos.removeIf(u -> currentUser != null &&
        u.getUsername().equalsIgnoreCase(currentUser.getUsername()));

    if (todos.isEmpty()) {
        new Alert(Alert.AlertType.INFORMATION, "No hay otros usuarios para eliminar.").showAndWait();
        return;
    }

    // ── Ventana principal tipo tabla ─────────────────────────────────────
    Stage stage = new Stage();
    stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
    stage.setTitle("Eliminar Usuarios");
    stage.setMinWidth(860);
    stage.setMinHeight(520);

    VBox root = new VBox(0);
    root.setStyle("-fx-background-color:#F5F5F5;");

    // Encabezado
    HBox header = new HBox(10);
    header.setPadding(new Insets(20, 28, 16, 28));
    header.setAlignment(Pos.CENTER_LEFT);
    header.setStyle("-fx-background-color:white;" +
        "-fx-border-color:#EEEEEE; -fx-border-width:0 0 1 0;");
    Circle dot = new Circle(7, Color.web(ROJO));
    Label titulo = new Label("ELIMINAR USUARIO");
    titulo.setStyle("-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:" + ROJO + ";");
    header.getChildren().addAll(dot, titulo);

    // Cabecera de tabla
    String[] cols   = {"ID", "Usuario", "Nombre", "Rol", "Estado", "Acciones"};
    double[] widths = { 55,   130,        200,      140,   110,       130};

    HBox colHeader = new HBox(0);
    colHeader.setPadding(new Insets(10, 28, 10, 28));
    colHeader.setStyle("-fx-background-color:white;" +
        "-fx-border-color:#EEEEEE; -fx-border-width:0 0 1 0;");
    for (int i = 0; i < cols.length; i++) {
        Label lbl = new Label(cols[i]);
        lbl.setPrefWidth(widths[i]);
        lbl.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#1A1A1A;");
        colHeader.getChildren().add(lbl);
    }

    // Filas scrolleables
    ScrollPane scroll = new ScrollPane();
    scroll.setFitToWidth(true);
    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scroll.setStyle("-fx-background-color:transparent; -fx-background:#F5F5F5;");
    VBox.setVgrow(scroll, Priority.ALWAYS);

    VBox tabla = new VBox(0);
    tabla.setStyle("-fx-background-color:#F5F5F5;");

    for (user u : todos) {
        String rolVisible = resolverRolVisible(u);
        String nombre     = (u.getUsername() + " " + u.getApellidos()).trim();

        HBox fila = new HBox(0);
        fila.setPadding(new Insets(12, 28, 12, 28));
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setStyle("-fx-background-color:white;" +
            "-fx-border-color:#EEEEEE; -fx-border-width:0 0 1 0;");

        // ID
        Label lblId = new Label(String.valueOf(u.getId()));
        lblId.setPrefWidth(widths[0]);
        lblId.setStyle("-fx-font-size:13px; -fx-text-fill:#1A1A1A;");

        // Usuario
        Label lblUser = new Label(u.getUsername());
        lblUser.setPrefWidth(widths[1]);
        lblUser.setStyle("-fx-font-size:13px; -fx-text-fill:#1A1A1A;");

        // Nombre completo
        Label lblNombre = new Label(nombre);
        lblNombre.setPrefWidth(widths[2]);
        lblNombre.setStyle("-fx-font-size:13px; -fx-text-fill:#1A1A1A;");

        // Badge rol
        Label badgeRol = new Label(rolVisible);
        badgeRol.setPadding(new Insets(4, 12, 4, 12));
        String cRol = colorRol(rolVisible);
        badgeRol.setStyle(
            "-fx-background-color:" + cRol + "22;" +
            "-fx-text-fill:" + cRol + ";" +
            "-fx-font-size:11px; -fx-font-weight:bold;" +
            "-fx-background-radius:20;");
        HBox boxRol = new HBox(badgeRol);
        boxRol.setPrefWidth(widths[3]);
        boxRol.setAlignment(Pos.CENTER_LEFT);

        // Badge estado
        Label badgeEst = new Label("Activo");
        badgeEst.setPadding(new Insets(4, 12, 4, 12));
        badgeEst.setStyle(
            "-fx-background-color:#2E7D32;" +
            "-fx-text-fill:white;" +
            "-fx-font-size:11px; -fx-font-weight:bold;" +
            "-fx-background-radius:20;");
        HBox boxEst = new HBox(badgeEst);
        boxEst.setPrefWidth(widths[4]);
        boxEst.setAlignment(Pos.CENTER_LEFT);

        // Botón eliminar
        Button btnElim = new Button("🗑  Eliminar");
        btnElim.setPrefWidth(110);
        String estNormal =
            "-fx-background-color:white;" +
            "-fx-border-color:#CC0000; -fx-border-radius:6;" +
            "-fx-background-radius:6;" +
            "-fx-text-fill:#CC0000; -fx-font-size:12px;" +
            "-fx-font-weight:bold; -fx-cursor:hand;";
        String estHover =
            "-fx-background-color:#FFF5F5;" +
            "-fx-border-color:#CC0000; -fx-border-radius:6;" +
            "-fx-background-radius:6;" +
            "-fx-text-fill:#CC0000; -fx-font-size:12px;" +
            "-fx-font-weight:bold; -fx-cursor:hand;";
        btnElim.setStyle(estNormal);
        btnElim.setOnMouseEntered(e -> btnElim.setStyle(estHover));
        btnElim.setOnMouseExited(e  -> btnElim.setStyle(estNormal));

        final user target = u;
        btnElim.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar eliminación");
            confirm.setHeaderText("¿Eliminar a \"" + target.getUsername() + "\"?");
            confirm.setContentText("Esta acción no se puede deshacer.");
            confirm.initOwner(stage);
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    ArchivoManagerHelper.eliminarUsuario(target.getId());

                    // Si es DELIVERY, eliminar también de repartidores.txt
                    if ("DELIVERY".equals(resolverRolVisible(target))) {
                        com.dhery.GestorArchivo.ArchivoManager arch =
                            new com.dhery.GestorArchivo.ArchivoManager();
                        List<String> reps = arch.leerLineas(REPARTIDORES_TXT);
                        reps.removeIf(l -> {
                            String[] p = l.split("\\|");
                            return p.length > 1 &&
                                p[1].trim().equalsIgnoreCase(target.getUsername());
                        });
                        arch.reescribirLineas(REPARTIDORES_TXT, reps);
                    }

                    new Alert(Alert.AlertType.INFORMATION,
                        "✅ Usuario \"" + target.getUsername() + "\" eliminado correctamente.")
                        .showAndWait();

                    // Refrescar tabla
                    stage.close();
                    mostrarDialogoEliminarUsuario();
                }
            });
        });

        HBox boxAccion = new HBox(btnElim);
        boxAccion.setPrefWidth(widths[5]);
        boxAccion.setAlignment(Pos.CENTER_LEFT);

        fila.getChildren().addAll(lblId, lblUser, lblNombre, boxRol, boxEst, boxAccion);

        // Hover fila
        fila.setOnMouseEntered(ev ->
            fila.setStyle("-fx-background-color:#FFF5F5;" +
                "-fx-border-color:#EEEEEE; -fx-border-width:0 0 1 0;"));
        fila.setOnMouseExited(ev ->
            fila.setStyle("-fx-background-color:white;" +
                "-fx-border-color:#EEEEEE; -fx-border-width:0 0 1 0;"));

        tabla.getChildren().add(fila);
    }

    scroll.setContent(tabla);

    // Footer con botón cerrar
    HBox footer = new HBox();
    footer.setPadding(new Insets(14, 28, 14, 28));
    footer.setAlignment(Pos.CENTER_RIGHT);
    footer.setStyle("-fx-background-color:white;" +
        "-fx-border-color:#EEEEEE; -fx-border-width:1 0 0 0;");
    Button btnCerrar = new Button("Cerrar");
    btnCerrar.setPrefSize(110, 36);
    btnCerrar.setStyle(
        "-fx-background-color:#EEEEEE; -fx-text-fill:#333;" +
        "-fx-font-size:13px; -fx-background-radius:8; -fx-cursor:hand;");
    btnCerrar.setOnAction(e -> stage.close());
    footer.getChildren().add(btnCerrar);

    root.getChildren().addAll(header, colHeader, scroll, footer);
    stage.setScene(new Scene(root, 860, 540));
    stage.show();
}

    private static void mostrarAlertasEscasez() {
        List<AppState.ItemStock> bajos = getItemsBajoStock();
        if (bajos.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION,
                "✅ Todos los productos tienen stock suficiente.").showAndWait();
        } else {
            StringBuilder sb = new StringBuilder("⚠️ Productos con stock bajo:\n\n");
            for (AppState.ItemStock item : bajos) {
                sb.append("• ").append(item.nombre)
                  .append(": ").append(String.format("%.0f", item.cantidad))
                  .append(" ").append(item.unidad).append("\n");
            }
            Alert alert = new Alert(Alert.AlertType.WARNING, sb.toString());
            alert.setHeaderText("Alertas de Escasez");
            alert.setTitle("Inventario — Tacabrón");
            alert.showAndWait();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPERS DE DATOS — leen desde los archivos .txt
    // ══════════════════════════════════════════════════════════════════════════

    /** Lee todas las líneas de un archivo .txt, ignorando líneas vacías. */
    private static List<String> leerLineas(String ruta) {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String l;
            while ((l = br.readLine()) != null) {
                l = l.trim();
                if (!l.isEmpty()) lineas.add(l);
            }
        } catch (IOException ignored) {}
        return lineas;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPERS DE DATOS — leen desde los archivos .txt
    // ══════════════════════════════════════════════════════════════════════════

    private static List<AppState.ItemStock> getItemsBajoStock() {
        List<AppState.ItemStock> bajos = new ArrayList<>();
        for (AppState.ItemStock item : AppState.inventario) {
            if (item.cantidad < AppState.STOCK_BAJO_UMBRAL) bajos.add(item);
        }
        return bajos;
    }

    /**
     * Lee pedidos.txt y cuenta los que tienen estado "LISTO" (en espera de entrega).
     * Formato: id|cliente|tel|dir|tipo|total|pago|estado|productos...
     */
    private static int contarPendientes() {
        // También revisar AppState por pedidos en tiempo real
        int desdeTxt = (int) leerLineas(RUTA_PEDIDOS).stream()
            .filter(l -> {
                String[] p = l.split("\\|");
                return p.length >= 8 && p[7].trim().equalsIgnoreCase("LISTO");
            }).count();
        int desdeMemoria = (int) AppState.pedidosCocina.stream()
            .filter(p -> p.estado.contains("PROCESO")).count();
        return Math.max(desdeTxt, desdeMemoria);
    }

    private static int contarEnPreparacion() {
        return (int) AppState.pedidosCocina.stream()
            .filter(p -> !p.estado.contains("PROCESO") && !p.estado.contains("LISTO")).count();
    }

    /**
     * Cuenta pedidos entregados desde pedidos.txt.
     */
    private static int contarEntregados() {
        int desdeTxt = (int) leerLineas(RUTA_PEDIDOS).stream()
            .filter(l -> {
                String[] p = l.split("\\|");
                return p.length >= 8 && p[7].trim().equalsIgnoreCase("ENTREGADO");
            }).count();
        return Math.max(desdeTxt, AppState.historialReciente.size());
    }

    /**
     * Calcula ventas del día de hoy leyendo facturas.txt.
     * Formato: id|clienteId|fecha|total|tipo|productos
     * Solo suma las facturas cuya fecha sea hoy.
     */
    private static double calcularVentasHoy() {
        String hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        double totalTxt = leerLineas(RUTA_FACTURAS).stream()
            .mapToDouble(l -> {
                String[] p = l.split("\\|");
                if (p.length >= 4 && p[2].trim().equals(hoy)) {
                    try { return Double.parseDouble(p[3].trim()); } catch (NumberFormatException e) { return 0; }
                }
                return 0;
            }).sum();
        // Sumar también pedidos en memoria (sesión actual)
        double desdeMemoria = AppState.pedidosCocina.stream().mapToDouble(p -> p.total).sum();
        return totalTxt + desdeMemoria;
    }

    /**
     * Calcula ventas de los últimos N días desde facturas.txt.
     */
    private static double calcularVentasDias(int dias) {
        LocalDate desde = LocalDate.now().minusDays(dias - 1);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return leerLineas(RUTA_FACTURAS).stream()
            .mapToDouble(l -> {
                String[] p = l.split("\\|");
                if (p.length >= 4) {
                    try {
                        LocalDate fecha = LocalDate.parse(p[2].trim(), fmt);
                        if (!fecha.isBefore(desde)) {
                            return Double.parseDouble(p[3].trim());
                        }
                    } catch (Exception ignored) {}
                }
                return 0;
            }).sum();
    }

    /**
     * Cuenta total de pedidos registrados hoy en facturas.txt.
     */
    private static int contarPedidosHoy() {
        String hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int desdeTxt = (int) leerLineas(RUTA_FACTURAS).stream()
            .filter(l -> {
                String[] p = l.split("\\|");
                return p.length >= 3 && p[2].trim().equals(hoy);
            }).count();
        return Math.max(desdeTxt, AppState.pedidosCocina.size());
    }

    /**
     * Top 5 productos más vendidos calculado desde facturas.txt.
     * Formato de productos en factura: PRODUCTO x1, PRODUCTO2 x2, ...
     * Devuelve: [nombre, cantidad_vendida, ingresos_estimados]
     */
    private static List<String[]> getTop5Productos() {
        Map<String, int[]> conteo = new LinkedHashMap<>(); // nombre -> [cantidad, ingresos]

        // Leer precios aproximados por producto desde las facturas (total / items)
        // Usamos un mapa de precios conocidos del menú
        Map<String, Double> precios = new HashMap<>();
        precios.put("RAMEN BIRRIA",          55.0);
        precios.put("BIRRIA",                45.0);
        precios.put("QUESABIRRIA",           50.0);
        precios.put("SUADERO",               45.0);
        precios.put("PASTOR",                45.0);
        precios.put("NACHOS SUPREMOS",       40.0);
        precios.put("NACHOS SUPREMOS COMBO 2", 55.0);
        precios.put("MEGABURRITO",           65.0);
        precios.put("MEGABURRITO COMBO 2",   65.0);
        precios.put("HORCHATA",              10.0);
        precios.put("JAMAICA",                8.0);
        precios.put("TORTILLA EXTRA",         5.0);

        for (String linea : leerLineas(RUTA_FACTURAS)) {
            String[] partes = linea.split("\\|");
            if (partes.length < 6) continue;
            // Los productos están en partes[5] (y posibles partes[6], [7]…)
            // Ejemplo: "HORCHATA x1, RAMEN BIRRIA x1, BIRRIA x1"
            // También pueden estar en columnas separadas por | después de partes[5]
            StringBuilder productosCrudo = new StringBuilder();
            for (int i = 5; i < partes.length; i++) {
                if (productosCrudo.length() > 0) productosCrudo.append(", ");
                productosCrudo.append(partes[i].trim());
            }
            // Separar por coma
            String[] items = productosCrudo.toString().split(",");
            for (String item : items) {
                item = item.trim();
                if (item.isEmpty()) continue;
                // Parsear "NOMBRE xN"
                int xIdx = item.lastIndexOf(" x");
                if (xIdx < 0) continue;
                String nombre = item.substring(0, xIdx).trim().toUpperCase();
                int cant = 1;
                try { cant = Integer.parseInt(item.substring(xIdx + 2).trim()); } catch (NumberFormatException ignored) {}
                if (nombre.isEmpty()) continue;
                int[] arr = conteo.computeIfAbsent(nombre, k -> new int[]{0, 0});
                arr[0] += cant;
                double precio = precios.getOrDefault(nombre, 18.0);
                arr[1] += (int)(cant * precio);
            }
        }

        // También incluir pedidos en memoria
        for (AppState.PedidoCocina pc : AppState.pedidosCocina) {
            if (pc.productos != null) {
                for (String prod : pc.productos) {
                    String nombre = prod.trim().toUpperCase();
                    int[] arr = conteo.computeIfAbsent(nombre, k -> new int[]{0, 0});
                    arr[0] += 1;
                    arr[1] += precios.getOrDefault(nombre, 18.0).intValue();
                }
            }
        }

        List<String[]> lista = new ArrayList<>();
        conteo.entrySet().stream()
            .sorted((a, b) -> b.getValue()[0] - a.getValue()[0])
            .limit(5)
            .forEach(e -> {
                // Capitalizar nombre para display
                String nombre = capitalize(e.getKey());
                lista.add(new String[]{
                    nombre,
                    String.valueOf(e.getValue()[0]),
                    String.valueOf(e.getValue()[1])
                });
            });

        // Si no hay datos en archivos, mostrar mensaje vacío
        if (lista.isEmpty()) {
            lista.add(new String[]{"Sin datos aún", "0", "0"});
        }
        return lista;
    }

    /** Capitaliza la primera letra de cada palabra. */
    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        String[] words = s.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
            }
        }
        return sb.toString();
    }

    // ── HELPER LAYOUT ─────────────────────────────────────────────────────────
    private static VBox buildCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + GRIS_BORDE + ";" +
            "-fx-border-radius: 12; -fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.06),10,0,0,3);"
        );
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPER INTERNO: escritura de usuarios (delega a ArchivoManager)
    // ══════════════════════════════════════════════════════════════════════════
    private static class ArchivoManagerHelper {
        private static final String RUTA =
            "src/main/java/com/dhery/GestorArchivo/usuarios.txt";

        static void actualizarUsuario(List<user> todos, user actualizado) {
            com.dhery.GestorArchivo.ArchivoManager mgr =
                new com.dhery.GestorArchivo.ArchivoManager();
            mgr.actualizarLineaPorId(RUTA,
                String.valueOf(actualizado.getId()),
                actualizado.toString());
        }

        static void eliminarUsuario(int id) {
            com.dhery.GestorArchivo.ArchivoManager mgr =
                new com.dhery.GestorArchivo.ArchivoManager();
            List<String> lineas = mgr.leerLineas(RUTA);
            lineas.removeIf(l -> {
                String[] p = l.split("\\|");
                return p.length > 0 && p[0].trim().equals(String.valueOf(id));
            });
            mgr.reescribirLineas(RUTA, lineas);
        }
    }
}