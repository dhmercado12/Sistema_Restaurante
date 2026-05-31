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

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class EditarDatosView {

    // ─────────────────────────────────────────────
    // PALETA (igual que el resto de pantallas)
    // ─────────────────────────────────────────────
    private static final String RED      = "#CC0000";
    private static final String RED_DARK = "#AA0000";
    private static final String RED_BG   = "#FFEAEA";
    private static final String TEXT_D   = "#1A1A1A";
    private static final String TEXT_G   = "#888888";
    private static final String BG       = "#F0F2F5";
    private static final String CARD_BG  = "white";
    private static final String BORDER   = "#E8E8E8";
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

        Button btnBack = ghostButton("← Volver a mis datos");
        btnBack.setOnAction(e -> Router.goMisDatos(currentUser));
        topBar.getChildren().addAll(brand, topSpacer, btnBack);

        // ── CARD ─────────────────────────────────
        VBox card = new VBox(0);
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 16; -fx-background-radius: 16; -fx-border-width: 1;"
        );
        VBox.setVgrow(card, Priority.ALWAYS);

        // ── HEADER ROJO ──────────────────────────
        HBox header = new HBox(16);
        header.setPadding(new Insets(24, 32, 24, 32));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
            "-fx-background-color: " + RED + ";" +
            "-fx-background-radius: 16 16 0 0;"
        );

        StackPane headerIcon = new StackPane();
        Rectangle iconBg = new Rectangle(48, 48);
        iconBg.setArcWidth(10); iconBg.setArcHeight(10);
        iconBg.setFill(Color.web("#FFFFFF2E"));
        Label iconLbl = new Label("✏️");
        iconLbl.setStyle("-fx-font-size: 20px;");
        headerIcon.getChildren().addAll(iconBg, iconLbl);

        VBox headerText = new VBox(4);
        Label hTitle = new Label("Editar mis datos");
        hTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label hSub = new Label("Actualiza tu información personal");
        hSub.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.75);");
        headerText.getChildren().addAll(hTitle, hSub);

        header.getChildren().addAll(headerIcon, headerText);

        // ── BODY ─────────────────────────────────
        VBox body = new VBox(0);
        body.setPadding(new Insets(28, 32, 28, 32));

        // Toast (oculto al inicio)
        HBox toast = new HBox(10);
        toast.setAlignment(Pos.CENTER_LEFT);
        toast.setPadding(new Insets(12, 16, 12, 16));
        toast.setMaxWidth(Double.MAX_VALUE);
        toast.setVisible(false);
        toast.setManaged(false);
        VBox.setMargin(toast, new Insets(0, 0, 16, 0));
        Label toastLbl = new Label();
        toastLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        Circle toastDot = new Circle(5);
        toast.getChildren().addAll(toastDot, toastLbl);

        // ── SECCIÓN: Información personal ────────
        HBox sec1 = sectionDivider("Información personal");

        GridPane grid1 = buildGrid();

        TextField fNombre    = formField(nvl(currentUser.getUsername()));
        TextField fApellidos = formField(nvl(currentUser.getApellidos()));
        TextField fTelefono  = formField(nvl(currentUser.getTelefono()));
        TextField fDireccion = formField(nvl(currentUser.getDireccion()));

        VBox gNombre    = formGroup("👤  Nombre",    fNombre);
        VBox gApellidos = formGroup("👤  Apellidos", fApellidos);
        VBox gTelefono  = formGroup("📞  Teléfono",  fTelefono);
        VBox gDireccion = formGroup("📍  Dirección", fDireccion);

        grid1.add(gNombre,    0, 0);
        grid1.add(gApellidos, 1, 0);
        grid1.add(gTelefono,  0, 1);
        grid1.add(gDireccion, 1, 1);

        // ── SECCIÓN: Contraseña ──────────────────
        HBox sec2 = sectionDivider("Cambiar contraseña");
        VBox.setMargin(sec2, new Insets(20, 0, 0, 0));

        GridPane grid2 = buildGrid();

        PasswordField fPwdActual = pwdField("Ingresa tu contraseña actual");
        PasswordField fPwdNueva  = pwdField("Mínimo 4 caracteres");

        grid2.add(formGroup("🔑  Contraseña actual", fPwdActual), 0, 0);
        grid2.add(formGroup("🔐  Nueva contraseña",  fPwdNueva),  1, 0);

        // Nota discreta
        Label pwdNote = new Label(
            "⚠  Solo completa estos campos si deseas cambiar tu contraseña."
        );
        pwdNote.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_G + "; -fx-padding: 6 0 0 0;");

        // ── ACCIONES ────────────────────────────
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(24, 0, 0, 0));
        actions.setStyle("-fx-border-color: " + BORDER + "; -fx-border-width: 1 0 0 0;");

        Button btnCancel = ghostButton("Cancelar");
        btnCancel.setOnAction(e -> Router.goMisDatos(currentUser));

        Button btnSave = redButton("💾  Guardar cambios");
        btnSave.setOnAction(e -> {
            String resultado = guardarCambios(
                fNombre.getText().trim(),
                fApellidos.getText().trim(),
                fTelefono.getText().trim(),
                fDireccion.getText().trim(),
                fPwdActual.getText(),
                fPwdNueva.getText()
            );

            // Estilo del toast según resultado
            boolean ok = resultado.startsWith("✅");
            toast.setStyle(
                "-fx-background-color: " + (ok ? "#E8F5E9" : "#FFF3E0") + ";" +
                "-fx-border-color: " + (ok ? "#A5D6A7" : "#FFCC80") + ";" +
                "-fx-border-radius: 8; -fx-background-radius: 8;"
            );
            toastDot.setFill(Color.web(ok ? "#2E7D32" : "#E65100"));
            toastLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;" +
                "-fx-text-fill: " + (ok ? "#2E7D32" : "#E65100") + ";");
            toastLbl.setText(resultado);

            toast.setVisible(true);
            toast.setManaged(true);

            // Auto-ocultar a los 3 s
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(ev -> {
                toast.setVisible(false);
                toast.setManaged(false);
            });
            pause.play();

            // Si OK, limpiar campos de contraseña
            if (ok) {
                fPwdActual.clear();
                fPwdNueva.clear();
            }
        });

        actions.getChildren().addAll(btnCancel, btnSave);

        body.getChildren().addAll(toast, sec1, grid1, sec2, grid2, pwdNote, actions);
        card.getChildren().addAll(header, body);
        root.getChildren().addAll(topBar, card);

        return new Scene(root, 1280, 720);
    }

    // ─────────────────────────────────────────────
    // GUARDAR EN usuarios.txt
    // Formato: id|nombre|contraseña|ROL|apellidos|telefono|direccion
    // ─────────────────────────────────────────────
    private static String guardarCambios(String nombre, String apellidos,
                                          String telefono, String direccion,
                                          String pwdActual, String pwdNueva) {

        if (nombre.isEmpty())    return "❌ El nombre no puede estar vacío.";
        if (apellidos.isEmpty()) return "❌ Los apellidos no pueden estar vacíos.";

        String pwdFinal = currentUser.getPassword();

        // Cambio de contraseña solo si se llenaron ambos campos
        boolean cambiaPwd = !pwdActual.isEmpty() || !pwdNueva.isEmpty();
        if (cambiaPwd) {
            if (!pwdActual.equals(currentUser.getPassword()))
                return "❌ La contraseña actual es incorrecta.";
            if (pwdNueva.length() < 4)
                return "❌ La nueva contraseña debe tener al menos 4 caracteres.";
            pwdFinal = pwdNueva;
        }

        ArchivoManager archivo = new ArchivoManager();
        List<String> lineas = archivo.leerLineas(
            "src/main/java/com/dhery/GestorArchivo/usuarios.txt"
        );

        List<String> nuevas = new ArrayList<>();
        boolean encontrado = false;

        for (String linea : lineas) {
            String[] p = linea.split("\\|");
            if (p.length >= 1) {
                try {
                    if (Integer.parseInt(p[0].trim()) == currentUser.getId()) {
                        String rol = p.length > 3 ? p[3] : "CLIENTE";
                        nuevas.add(currentUser.getId() + "|" + nombre + "|" + pwdFinal
                            + "|" + rol + "|" + apellidos + "|" + telefono + "|" + direccion);
                        encontrado = true;
                        continue;
                    }
                } catch (NumberFormatException ignored) {}
            }
            nuevas.add(linea);
        }

        if (!encontrado) return "❌ No se encontró el usuario en el archivo.";

        // Reescribir archivo completo
        try {
            FileWriter fw = new FileWriter(
                "src/main/java/com/dhery/GestorArchivo/usuarios.txt", false
            );
            for (int i = 0; i < nuevas.size(); i++) {
                fw.write(nuevas.get(i));
                if (i < nuevas.size() - 1) fw.write("\n");
            }
            fw.close();
        } catch (Exception e) {
            return "❌ Error al guardar: " + e.getMessage();
        }

        // Actualizar objeto en memoria
        currentUser.setUsername(nombre);
        currentUser.setApellidos(apellidos);
        currentUser.setTelefono(telefono);
        currentUser.setDireccion(direccion);
        currentUser.setPassword(pwdFinal);

        return "✅ Datos actualizados correctamente.";
    }

    // ─────────────────────────────────────────────
    // HELPERS UI
    // ─────────────────────────────────────────────

    private static GridPane buildGrid() {
        GridPane g = new GridPane();
        g.setHgap(16); g.setVgap(14);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50);
        g.getColumnConstraints().addAll(c1, c2);
        return g;
    }

    private static VBox formGroup(String label, Control field) {
        VBox g = new VBox(6);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_G + ";");
        g.getChildren().addAll(lbl, field);
        return g;
    }

    private static TextField formField(String value) {
        TextField tf = new TextField(value);
        tf.setPrefHeight(40);
        String base = "-fx-background-color: " + FIELD_BG + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-padding: 0 12; -fx-font-size: 13px; -fx-text-fill: " + TEXT_D + ";";
        String focus = "-fx-background-color: white;" +
            "-fx-border-color: " + RED + "; -fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-padding: 0 12; -fx-font-size: 13px; -fx-text-fill: " + TEXT_D + ";";
        tf.setStyle(base);
        tf.focusedProperty().addListener((obs, o, f) -> tf.setStyle(f ? focus : base));
        return tf;
    }

    private static PasswordField pwdField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setPrefHeight(40);
        String base = "-fx-background-color: " + FIELD_BG + ";" +
            "-fx-border-color: " + BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-padding: 0 12; -fx-font-size: 13px;";
        String focus = "-fx-background-color: white;" +
            "-fx-border-color: " + RED + "; -fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-padding: 0 12; -fx-font-size: 13px;";
        pf.setStyle(base);
        pf.focusedProperty().addListener((obs, o, f) -> pf.setStyle(f ? focus : base));
        return pf;
    }

    private static HBox sectionDivider(String text) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 12, 0));
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_G + ";");
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
        String h = "-fx-background-color: " + RED_DARK + "; -fx-text-fill: white;" +
            "-fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 20;";
        btn.setStyle(n);
        btn.setOnMouseEntered(e -> btn.setStyle(h));
        btn.setOnMouseExited(e  -> btn.setStyle(n));
        return btn;
    }

    private static String nvl(String s) { return s != null ? s : ""; }
}