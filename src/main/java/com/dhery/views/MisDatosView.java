package com.dhery.views;

import com.dhery.GestorArchivo.ArchivoManager;
import com.dhery.app.Router;
import com.dhery.models.user;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.List;

public class MisDatosView {

    // ─────────────────────────────────────────────
    // PALETA
    // ─────────────────────────────────────────────
    private static final String RED     = "#CC0000";
    private static final String RED_BG  = "#FFEAEA";
    private static final String TEXT_D  = "#1A1A1A";
    private static final String TEXT_G  = "#888888";
    private static final String BG      = "#F0F2F5";
    private static final String CARD_BG = "white";
    private static final String BORDER  = "#E8E8E8";
    private static final String FIELD_BG = "#F7F8FA";

    private static user currentUser;

    // ─────────────────────────────────────────────
    // SCENE
    // ─────────────────────────────────────────────
    public static Scene getScene(user user) {
        currentUser = user;

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: " + BG + ";");
        root.setPadding(new Insets(24));

        // ── TOP BAR ─────────────────────────────
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 20, 0));

        HBox brand = new HBox(8);
        brand.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(5, Color.web(RED));
        Label brandName = new Label("TACABRÓN");
        brandName.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";"
        );
        brand.getChildren().addAll(dot, brandName);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Button btnBack = ghostButton("← Volver al menú");
        btnBack.setOnAction(e -> Router.goMenuClienteView());
        topBar.getChildren().addAll(brand, topSpacer, btnBack);

        // ── CARD ────────────────────────────────
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16; -fx-background-radius: 16; -fx-border-width: 1;"
        );
        VBox.setVgrow(card, Priority.ALWAYS);

        // ── CARD HEADER ROJO ─────────────────────
        StackPane cardHeader = new StackPane();
        cardHeader.setStyle(
            "-fx-background-color: " + RED + ";" +
            "-fx-background-radius: 16 16 0 0; -fx-padding: 28 32 64 32;"
        );
        cardHeader.setAlignment(Pos.TOP_LEFT);

        VBox headerText = new VBox(4);
        headerText.setAlignment(Pos.TOP_LEFT);
        Label hTitle = new Label("Mi perfil");
        hTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label hSub = new Label("Información de tu cuenta en Tacabrón");
        hSub.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.75);");
        headerText.getChildren().addAll(hTitle, hSub);
        cardHeader.getChildren().add(headerText);

        // ── AVATAR flotante sobre el header ──────
        String inicial = (currentUser.getUsername() != null && !currentUser.getUsername().isEmpty())
            ? currentUser.getUsername().substring(0, 1).toUpperCase() : "U";

        StackPane avatar = new StackPane();
        avatar.setMaxSize(88, 88);
        avatar.setMinSize(88, 88);
        Circle avatarBorder = new Circle(44);
        avatarBorder.setFill(Color.web(CARD_BG));
        Circle avatarBg = new Circle(40);
        avatarBg.setFill(Color.web(RED_BG));
        Label avatarLbl = new Label(inicial);
        avatarLbl.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");
        avatar.getChildren().addAll(avatarBorder, avatarBg, avatarLbl);

        // Wrapper que da el efecto de "flotar" sobre el header
        HBox avatarRow = new HBox();
        avatarRow.setPadding(new Insets(0, 0, 0, 32));
        avatarRow.getChildren().add(avatar);

        // ── CARD BODY ────────────────────────────
        VBox cardBody = new VBox(0);
        cardBody.setPadding(new Insets(16, 32, 32, 32));

        // Nombre + rol + botón editar
        HBox nameRow = new HBox();
        nameRow.setAlignment(Pos.CENTER_LEFT);
        nameRow.setPadding(new Insets(0, 0, 24, 0));

        VBox nameGroup = new VBox(6);
        String nombreCompleto = nvl(currentUser.getUsername())
            + (currentUser.getApellidos() != null ? " " + currentUser.getApellidos() : "");
        Label nameLbl = new Label(nombreCompleto.trim());
        nameLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");

        HBox rolPill = new HBox(5);
        rolPill.setAlignment(Pos.CENTER_LEFT);
        rolPill.setPadding(new Insets(3, 10, 3, 10));
        rolPill.setStyle("-fx-background-color: " + RED_BG + "; -fx-background-radius: 20;");
        Label rolIco = new Label("👤"); rolIco.setStyle("-fx-font-size: 10px;");
        Label rolLbl = new Label("Cliente");
        rolLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #AA0000;");
        rolPill.getChildren().addAll(rolIco, rolLbl);
        nameGroup.getChildren().addAll(nameLbl, rolPill);

        Region nameSpacer = new Region();
        HBox.setHgrow(nameSpacer, Priority.ALWAYS);

        Button btnEdit = redButton("✏  Editar datos");
        btnEdit.setOnAction(e -> Router.goMisDatosEdit(currentUser));

        nameRow.getChildren().addAll(nameGroup, nameSpacer, btnEdit);

        // ── SECCIÓN: Datos personales ────────────
        HBox sec1 = sectionDivider("Datos personales");

        GridPane gridDatos = new GridPane();
        gridDatos.setHgap(14); gridDatos.setVgap(14);
        gridDatos.setPadding(new Insets(0, 0, 24, 0));
        for (int i = 0; i < 3; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(33.33);
            gridDatos.getColumnConstraints().add(cc);
        }

        gridDatos.add(fieldCard("👤  Nombre",      nvl(currentUser.getUsername())),    0, 0);
        gridDatos.add(fieldCard("👤  Apellidos",    nvl(currentUser.getApellidos())), 1, 0);
        gridDatos.add(fieldCard("🪪  ID de cuenta", "#" + String.format("%05d", currentUser.getId())), 2, 0);
        gridDatos.add(fieldCard("📞  Teléfono",     nvl(currentUser.getTelefono())),  0, 1);

        // Dirección ocupa 2 columnas
        VBox dirCard = fieldCard("📍  Dirección", nvl(currentUser.getDireccion()));
        GridPane.setColumnSpan(dirCard, 2);
        gridDatos.add(dirCard, 1, 1);

        // ── SECCIÓN: Actividad ───────────────────
        HBox sec2 = sectionDivider("Actividad de cuenta");

        HBox statsRow = new HBox(14);
        statsRow.setPadding(new Insets(0, 0, 0, 0));

        int totalPedidos = countPedidos();
        int totalGasto   = sumGasto();

        statsRow.getChildren().addAll(
            statCard("🛍", String.valueOf(totalPedidos), "Pedidos realizados"),
            statCard("🧾", "Bs " + totalGasto,          "Total gastado"),
            statCard("✅", "Activo",                     "Estado de cuenta")
        );
        for (javafx.scene.Node n : statsRow.getChildren())
            HBox.setHgrow(n, Priority.ALWAYS);

        cardBody.getChildren().addAll(nameRow, sec1, gridDatos, sec2, statsRow);

        // Ensamblar card con offset visual del avatar
        // Usamos un StackPane para superponer el avatar sobre el borde header/body
        StackPane headerStack = new StackPane();
        headerStack.setAlignment(Pos.BOTTOM_LEFT);
        headerStack.getChildren().addAll(cardHeader, avatarRow);
        // Empujar avatar hacia abajo para que quede mitad en header, mitad fuera
        StackPane.setMargin(avatarRow, new Insets(0, 0, -44, 0));

        card.getChildren().addAll(headerStack, cardBody);

        root.getChildren().addAll(topBar, card);
        return new Scene(root, 1280, 720);
    }

    // ─────────────────────────────────────────────
    // ESTADÍSTICAS desde facturas.txt
    // ─────────────────────────────────────────────
    private static int countPedidos() {
        try {
            ArchivoManager a = new ArchivoManager();
            List<String> lineas = a.leerLineas(
                "src/main/java/com/dhery/GestorArchivo/facturas.txt");
            int c = 0;
            for (String l : lineas) {
                String[] p = l.split("\\|");
                if (p.length > 1 && Integer.parseInt(p[1].trim()) == currentUser.getId()) c++;
            }
            return c;
        } catch (Exception e) { return 0; }
    }

    private static int sumGasto() {
        try {
            ArchivoManager a = new ArchivoManager();
            List<String> lineas = a.leerLineas(
                "src/main/java/com/dhery/GestorArchivo/facturas.txt");
            int total = 0;
            for (String l : lineas) {
                String[] p = l.split("\\|");
                if (p.length > 3 && Integer.parseInt(p[1].trim()) == currentUser.getId())
                    total += Integer.parseInt(p[3].trim());
            }
            return total;
        } catch (Exception e) { return 0; }
    }

    // ─────────────────────────────────────────────
    // COMPONENTES UI
    // ─────────────────────────────────────────────

    /** Tarjeta de campo solo lectura */
    private static VBox fieldCard(String label, String value) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(14, 16, 14, 16));
        box.setStyle(
            "-fx-background-color: " + FIELD_BG + ";" +
            "-fx-background-radius: 8;"
        );
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_G + ";");
        Label val = new Label(value.isEmpty() ? "—" : value);
        val.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        val.setWrapText(true);
        box.getChildren().addAll(lbl, val);
        return box;
    }

    /** Tarjeta de estadística */
    private static HBox statCard(String ico, String value, String label) {
        HBox box = new HBox(12);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(14, 16, 14, 16));
        box.setStyle(
            "-fx-background-color: " + FIELD_BG + ";" +
            "-fx-background-radius: 8;"
        );

        StackPane icoBox = new StackPane();
        Rectangle icoRect = new Rectangle(36, 36);
        icoRect.setArcWidth(8); icoRect.setArcHeight(8);
        icoRect.setFill(Color.web(RED_BG));
        Label icoLbl = new Label(ico);
        icoLbl.setStyle("-fx-font-size: 16px;");
        icoBox.getChildren().addAll(icoRect, icoLbl);

        VBox texts = new VBox(2);
        Label valLbl = new Label(value);
        valLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_D + ";");
        Label lblLbl = new Label(label);
        lblLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_G + ";");
        texts.getChildren().addAll(valLbl, lblLbl);

        box.getChildren().addAll(icoBox, texts);
        return box;
    }

    /** Línea divisora con etiqueta */
    private static HBox sectionDivider(String text) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 12, 0));

        Label lbl = new Label(text);
        lbl.setStyle(
            "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_G + ";"
        );
        Region line = new Region();
        line.setPrefHeight(1);
        line.setStyle("-fx-background-color: " + BORDER + ";");
        HBox.setHgrow(line, Priority.ALWAYS);

        box.getChildren().addAll(lbl, line);
        return box;
    }

    private static Button ghostButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(34);
        String n = "-fx-background-color: white; -fx-text-fill: " + TEXT_G + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-width: 1;" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 14;";
        String h = "-fx-background-color: " + FIELD_BG + "; -fx-text-fill: " + TEXT_D + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-width: 1;" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 14;";
        btn.setStyle(n);
        btn.setOnMouseEntered(e -> btn.setStyle(h));
        btn.setOnMouseExited(e  -> btn.setStyle(n));
        return btn;
    }

    private static Button redButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(38);
        String n = "-fx-background-color: " + RED + "; -fx-text-fill: white;" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 20;";
        String h = "-fx-background-color: #AA0000; -fx-text-fill: white;" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 20;";
        btn.setStyle(n);
        btn.setOnMouseEntered(e -> btn.setStyle(h));
        btn.setOnMouseExited(e  -> btn.setStyle(n));
        return btn;
    }

    private static String nvl(String s) {
        return s != null ? s : "";
    }
    
}
