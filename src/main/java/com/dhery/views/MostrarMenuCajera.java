package com.dhery.views;

import com.dhery.app.Router;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

public class MostrarMenuCajera{
private static com.dhery.models.user currentUser;
    // ═══════════════════════════════════════════════════════════
    //  PALETA  BOLIVIA × MÉXICO
    //  
    // ═══════════════════════════════════════════════════════════
    private static final String ROJO   = "#C8102E";
    private static final String VERDE  = "#1A6B3C";
    private static final String ORO    = "#D4A017";
    private static final String BG     = "#F5EDD8";
    private static final String CARD   = "#FFFFFF";
    private static final String ROW    = "#FBF5EC";
    private static final String DARK   = "#2C1A0E";

    private static boolean isGridMode = true;

    public static Scene getScene(com.dhery.models.user user) {

    currentUser = user;

        VBox container = new VBox(14);
        container.setPadding(new Insets(16));
        container.setStyle("-fx-background-color: " + BG + ";");

        // ─── TOP BAR ──────────────────────────────────────────
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← VOLVER");
        backBtn.setStyle(
            "-fx-background-color: " + ROJO + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 9 18 9 18;"
        );
        backBtn.setOnAction(e -> Router.goMenuCajeroView(currentUser));

        Label menuTitle = new Label("MENÚ TACABRON");
menuTitle.setStyle(
    "-fx-font-size: 28px;" +
    "-fx-font-weight: bold;" +
    "-fx-text-fill: " + ROJO + ";" +
    "-fx-letter-spacing: 3;"
);

Label subTitle = new Label(
    "Auténtica comida mexicana preparada con pasión y tradición"
);

subTitle.setStyle(
    "-fx-font-size: 13px;" +
    "-fx-text-fill: #6B5A4A;" +
    "-fx-font-style: italic;"
);

HBox flagStripe = buildBoliviaFlag();
flagStripe.setAlignment(Pos.CENTER);

VBox titleBox = new VBox(5);
titleBox.setAlignment(Pos.CENTER);
titleBox.getChildren().addAll(
    menuTitle,
    subTitle,
    flagStripe
);

        // Mini franja tricolor Bolivia–México centrada
        
        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Button gridBtn = new Button("⊞");
        Button listBtn = new Button("☰");
        styleToggle(gridBtn, true);
        styleToggle(listBtn, false);

        Region leftSpacer = new Region();
Region rightSpacer = new Region();

HBox.setHgrow(leftSpacer, Priority.ALWAYS);
HBox.setHgrow(rightSpacer, Priority.ALWAYS);

topBar.getChildren().addAll(
    backBtn,
    leftSpacer,
    titleBox,
    rightSpacer,
    gridBtn,
    listBtn
);
        // ─── CONTENIDO ────────────────────────────────────────
        HBox content = new HBox(14);
        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(
            buildPlatosColumn(),
            buildBebidasColumn(),
            buildPromosColumn()
        );

        // ─── TOGGLE GRID ──────────────────────────────────────
        gridBtn.setOnAction(e -> {
            if (isGridMode) return;
            isGridMode = true;
            styleToggle(gridBtn, true);
            styleToggle(listBtn, false);
            content.getChildren().clear();
            content.setSpacing(14);
            content.setAlignment(Pos.TOP_CENTER);
            content.getChildren().addAll(
                buildPlatosColumn(),
                buildBebidasColumn(),
                buildPromosColumn()
            );
        });

        // ─── TOGGLE LISTA ─────────────────────────────────────
        listBtn.setOnAction(e -> {
            if (!isGridMode) return;
            isGridMode = false;
            styleToggle(gridBtn, false);
            styleToggle(listBtn, true);
            content.getChildren().clear();
            content.setSpacing(0);
            content.setAlignment(Pos.TOP_CENTER);

            VBox mainList = new VBox(14);
            mainList.setPrefWidth(1000);
            mainList.setAlignment(Pos.TOP_CENTER);
            mainList.getChildren().addAll(
                buildListSection("🌮", "PLATOS",      ROJO,  buildPlatosListGrid()),
                buildListSection("🥤", "BEBIDAS",     VERDE, buildBebidasListGrid()),
                buildListSection("🔥", "PROMOCIONES", ORO,   buildPromosListGrid())
            );
            content.getChildren().add(mainList);
        });

        container.getChildren().addAll(topBar, content);
        return new Scene(container, 1280, 720);
    }

    // ═══════════════════════════════════════════════════════════
    //  FRANJA TRICOLOR DECORATIVA
    // ═══════════════════════════════════════════════════════════
    private static HBox buildBoliviaFlag() {

    HBox stripe = new HBox();
    stripe.setPrefWidth(120);
    stripe.setPrefHeight(8);

    Region red = new Region();
    Region yellow = new Region();
    Region green = new Region();

    HBox.setHgrow(red, Priority.ALWAYS);
    HBox.setHgrow(yellow, Priority.ALWAYS);
    HBox.setHgrow(green, Priority.ALWAYS);

    red.setStyle(
        "-fx-background-color: #D52B1E;" +
        "-fx-background-radius: 4 0 0 4;"
    );

    yellow.setStyle(
        "-fx-background-color: #F9E300;"
    );

    green.setStyle(
        "-fx-background-color: #007934;" +
        "-fx-background-radius: 0 4 4 0;"
    );

    stripe.getChildren().addAll(red, yellow, green);

    return stripe;
}

    // ═══════════════════════════════════════════════════════════
    //  GRID — COLUMNAS
    // ═══════════════════════════════════════════════════════════

    private static VBox buildPlatosColumn() {
        VBox col = buildColumnBase(ROJO);
        col.getChildren().add(buildHeader("🌮", "PLATOS", ROJO, false));
        col.getChildren().add(buildColorStripe(VERDE, "white", ROJO));

        VBox items = new VBox(8);
        items.setPadding(new Insets(14));
        // Precios premium en rojo, intermedios en oro, básicos en verde
        items.getChildren().addAll(

    buildFoodRow(
        "1",
        "Nachos Supremos",
        "Bs 45",
        ROJO,
        "/images/nachos-supremos.jpg"
    ),

    buildFoodRow(
        "2",
        "MegaBurrito",
        "Bs 45",
        ROJO,
        "/images/megaburrito.jpg"
    ),

    buildFoodRow(
        "3",
        "RamenBirria",
        "Bs 35",
        ROJO,
        "/images/ramenbirria.jpg"
    ),

    buildFoodRow(
        "4",
        "Tacobirria",
        "Bs 15",
        ORO,
        "/images/tacobirria.jpg"
    ),

    buildFoodRow(
        "5",
        "Quesabirria",
        "Bs 15",
        ORO,
        "/images/quesabirria.jpg"
    ),

    buildFoodRow(
        "6",
        "Suadero",
        "Bs 15",
        ORO,
        "/images/suadero.jpg"
    ),

    buildFoodRow(
        "7",
        "Pastor",
        "Bs 15",
        VERDE,
        "/images/pastor.jpg"
    ),

    buildFoodRow(
        "8",
        "Lengua",
        "Bs 15",
        VERDE,
        "/images/lengua.jpg"
    )
);
        col.getChildren().add(items);
        return col;
    }

    private static VBox buildBebidasColumn() {
        VBox col = buildColumnBase(VERDE);
        col.getChildren().add(buildHeader("🥤", "BEBIDAS", VERDE, false));
        col.getChildren().add(buildColorStripe(ROJO, "white", VERDE));

        VBox items = new VBox(14);
        items.setAlignment(Pos.TOP_CENTER);
        items.setPadding(new Insets(16));
        items.getChildren().addAll(

    buildDrinkCard(
        "11",
        "Horchata",
        "Bs 8",
        "/images/horchata.jpg"
    ),

    buildDrinkCard(
        "12",
        "Jamaica",
        "Bs 8",
        "/images/jamaica.jpg"
    )

);
        col.getChildren().add(items);
        return col;
    }

    private static VBox buildPromosColumn() {
        VBox col = buildColumnBase(ORO);
        col.getChildren().add(buildHeader("🔥", "PROMOCIONES", ORO, true)); // texto oscuro sobre oro
        col.getChildren().add(buildColorStripe(VERDE, ROJO, ORO));

        VBox items = new VBox(14);
        items.setPadding(new Insets(16));
        items.getChildren().addAll(

    buildPromoCard(
        "13",
        "Nachos Supremos",
        "COMBO 2",
        "Bs 70",
        "/images/nachos-supremos.jpg"
    ),

    buildPromoCard(
        "14",
        "MegaBurrito",
        "COMBO 2",
        "Bs 70",
        "/images/megaburrito.jpg"
    )

);
        col.getChildren().add(items);
        return col;
    }

    // ─── Base de columna ──────────────────────────────────────
    private static VBox buildColumnBase(String accentColor) {
        VBox col = new VBox();
        col.setPrefWidth(360);
        col.setMinWidth(360);
        col.setMaxWidth(360);
        col.setStyle(
            "-fx-background-color: " + CARD + ";" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: " + accentColor + ";" +
            "-fx-border-width: 0 0 0 4;" +   // acento izquierdo
            "-fx-border-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(44,26,14,0.12), 14, 0, 0, 5);"
        );
        return col;
    }

    // ─── Header ───────────────────────────────────────────────
    private static HBox buildHeader(String icon, String title, String bg, boolean darkText) {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 18, 14, 18));
        header.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 19 19 0 0;"
        );

        StackPane iconBox = new StackPane();
        Circle circle = new Circle(20);
        circle.setFill(Color.web("rgba(255,255,255,0.22)"));
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 17px;");
        iconBox.getChildren().addAll(circle, iconLbl);

        String textColor = darkText ? DARK : "white";
        Label titleLbl = new Label(title);
        titleLbl.setStyle(
            "-fx-font-size: 17px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + textColor + ";" +
            "-fx-letter-spacing: 1.5;"
        );

        header.getChildren().addAll(iconBox, titleLbl);
        return header;
    }

    // ─── Franja tricolor bajo el header ───────────────────────
    private static HBox buildColorStripe(String c1, String c2, String c3) {
        HBox stripe = new HBox();
        stripe.setPrefHeight(5);
        Region r1 = new Region(); r1.setStyle("-fx-background-color: " + c1 + ";");
        Region r2 = new Region(); r2.setStyle("-fx-background-color: " + c2 + ";");
        Region r3 = new Region(); r3.setStyle("-fx-background-color: " + c3 + ";");
        HBox.setHgrow(r1, Priority.ALWAYS);
        HBox.setHgrow(r2, Priority.ALWAYS);
        HBox.setHgrow(r3, Priority.ALWAYS);
        stripe.getChildren().addAll(r1, r2, r3);
        return stripe;
    }

    // ─── Fila plato ───────────────────────────────────────────
   private static HBox buildFoodRow(
    String num,
    String title,
    String price,
    String accent,
    String imagePath
) {

    HBox item = new HBox(10);
item.setAlignment(Pos.CENTER_LEFT);
item.setPadding(new Insets(8, 10, 8, 10));
item.setStyle(
    "-fx-background-color: " + ROW + ";" +
    "-fx-background-radius: 12;" +
    "-fx-cursor: hand;"
);

    Image image = new Image(
    MostrarMenu.class.getResourceAsStream(imagePath)
);
ImageView img = new ImageView(image);

img.setFitWidth(40);
img.setFitHeight(40);

Circle clip = new Circle(20, 20, 20);
img.setClip(clip);

    Label numLbl = new Label(num);
    numLbl.setMinWidth(22);
    numLbl.setMinHeight(22);
    numLbl.setAlignment(Pos.CENTER);
    numLbl.setStyle(
        "-fx-background-color: " + accent + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 10px;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 50;" +
        "-fx-padding: 4 5 4 5;"
    );

    Label titleLbl = new Label(title);
    titleLbl.setStyle(
        "-fx-font-size: 13px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + DARK + ";"
    );

    // Hace que el nombre ocupe todo el espacio disponible
    HBox.setHgrow(titleLbl, Priority.ALWAYS);
    titleLbl.setMaxWidth(Double.MAX_VALUE);

    Label priceLbl = new Label(price);
    priceLbl.setPrefWidth(70);      // ancho fijo
    priceLbl.setAlignment(Pos.CENTER);

    priceLbl.setStyle(
        "-fx-background-color: " + accent + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 11px;" +
        "-fx-font-weight: bold;" +
        "-fx-padding: 6 11 6 11;" +
        "-fx-background-radius: 8;"
    );

    item.getChildren().addAll(
        img,
        numLbl,
        titleLbl,
        priceLbl
    );
    item.setOnMouseClicked(e -> {

    item.setScaleX(1.05);
    item.setScaleY(1.05);

    item.setStyle(
        "-fx-background-color: #FFE5E5;" +
        "-fx-background-radius: 12;" +
        "-fx-border-color: " + accent + ";" +
        "-fx-border-width: 2;" +
        "-fx-border-radius: 12;" +
        "-fx-cursor: hand;"
    );

    mostrarIngredientes(
        title,
        imagePath,
        price
    );

    item.setScaleX(1);
    item.setScaleY(1);

    item.setStyle(
        "-fx-background-color: " + ROW + ";" +
        "-fx-background-radius: 12;" +
        "-fx-cursor: hand;"
    );
});
    return item;
}

    // ─── Bebida card ──────────────────────────────────────────
   private static VBox buildDrinkCard(
    String num,
    String title,
    String price,
    String imagePath
) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + ROW + "; -fx-background-radius: 16;");

        Image image = new Image(
    MostrarMenu.class.getResourceAsStream(imagePath)
);

ImageView img = new ImageView(image);

img.setFitWidth(88);
img.setFitHeight(88);

Circle clip = new Circle(44, 44, 44);
img.setClip(clip);

        Label numLbl = new Label(num);
        numLbl.setStyle(
            "-fx-background-color: " + VERDE + ";" +
            "-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-padding: 5 9 5 9; -fx-background-radius: 50;"
        );

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + DARK + ";");

        Label priceLbl = new Label(price);
        priceLbl.setStyle(
            "-fx-background-color: " + VERDE + ";" +
            "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;" +
            "-fx-padding: 8 16 8 16; -fx-background-radius: 10;"
        );

        card.getChildren().addAll(img, numLbl, titleLbl, priceLbl);
        card.setOnMouseClicked(e -> {

    card.setScaleX(1.05);
    card.setScaleY(1.05);

    card.setStyle(
        "-fx-background-color: #EAF8EF;" +
        "-fx-background-radius: 16;" +
        "-fx-border-color: " + VERDE + ";" +
        "-fx-border-width: 2;" +
        "-fx-border-radius: 16;"
    );

    mostrarIngredientes(
        title,
        imagePath,
        price
    );

    card.setScaleX(1);
    card.setScaleY(1);

    card.setStyle(
        "-fx-background-color: " + ROW + ";" +
        "-fx-background-radius: 16;"
    );
});
        return card;
    }

    // ─── Promo card ───────────────────────────────────────────
    private static VBox buildPromoCard(
    String num,
    String title,
    String combo,
    String price,
    String imagePath
) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: " + ROW + "; -fx-background-radius: 16;");

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        Label numLbl = new Label(num);
        numLbl.setStyle(
            "-fx-background-color: " + VERDE + ";" +
            "-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-padding: 5 9 5 9; -fx-background-radius: 50;"
        );

        VBox texts = new VBox(2);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + DARK + ";");
        Label comboLbl = new Label(combo);
        comboLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + VERDE + ";");
        texts.getChildren().addAll(titleLbl, comboLbl);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Image image = new Image(
    MostrarMenu.class.getResourceAsStream(imagePath)
);

ImageView img = new ImageView(image);

img.setFitWidth(76);
img.setFitHeight(76);

Circle clip = new Circle(38, 38, 38);
img.setClip(clip);

        top.getChildren().addAll(numLbl, texts, sp, img);

        Label priceLbl = new Label(price);
        priceLbl.setMaxWidth(Double.MAX_VALUE);
        priceLbl.setAlignment(Pos.CENTER);
        priceLbl.setStyle(
            "-fx-background-color: " + VERDE + ";" +
            "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;" +
            "-fx-padding: 8 14 8 14; -fx-background-radius: 10;"
        );

        card.getChildren().addAll(top, priceLbl);
        card.setOnMouseClicked(e -> {

    card.setScaleX(1.05);
    card.setScaleY(1.05);

    card.setStyle(
        "-fx-background-color: #FFF7E0;" +
        "-fx-background-radius: 16;" +
        "-fx-border-color: " + ORO + ";" +
        "-fx-border-width: 2;" +
        "-fx-border-radius: 16;"
    );

    mostrarIngredientes(
        title,
        imagePath,
        price
    );

    card.setScaleX(1);
    card.setScaleY(1);

    card.setStyle(
        "-fx-background-color: " + ROW + ";" +
        "-fx-background-radius: 16;"
    );
});
        return card;
    }

    // ═══════════════════════════════════════════════════════════
    //  LIST MODE
    // ═══════════════════════════════════════════════════════════

    private static VBox buildListSection(String icon, String title, String color, GridPane grid) {
        VBox section = new VBox();
        section.setStyle(
            "-fx-background-color: " + CARD + ";" +
            "-fx-background-radius: 20;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 0 0 0 4;" +
            "-fx-border-radius: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(44,26,14,0.12), 14, 0, 0, 5);"
        );

        boolean darkText = color.equals(ORO);

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(13, 18, 13, 18));
        header.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 19 19 0 0;"
        );

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 19px;");

        String txtColor = darkText ? DARK : "white";
        Label titleLbl = new Label(title);
        titleLbl.setStyle(
            "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + txtColor + "; -fx-letter-spacing: 1.5;"
        );

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        int count = grid.getChildren().size();
        String countBg = darkText ? "rgba(0,0,0,0.15)" : "rgba(255,255,255,0.22)";
        Label countLbl = new Label(count + " items");
        countLbl.setStyle(
            "-fx-background-color: " + countBg + ";" +
            "-fx-text-fill: " + txtColor + ";" +
            "-fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-padding: 4 11 4 11; -fx-background-radius: 20;"
        );

        header.getChildren().addAll(iconLbl, titleLbl, sp, countLbl);

        // Franja tricolor bajo el header
        HBox stripe = buildColorStripe(VERDE, "white", ROJO);

        VBox body = new VBox();
        body.setPadding(new Insets(12));
        body.getChildren().add(grid);

        section.getChildren().addAll(header, stripe, body);
        return section;
    }

    private static GridPane buildPlatosListGrid() {
        GridPane g = makeListGrid();
        String[][] data = {
    {"Nachos Supremos","Bs 45","/images/nachos-supremos.jpg",ROJO},
    {"MegaBurrito","Bs 45","/images/megaburrito.jpg",ROJO},
    {"RamenBirria","Bs 35","/images/ramenbirria.jpg",ROJO},
    {"Tacobirria","Bs 15","/images/tacobirria.jpg",ORO},
    {"Quesabirria","Bs 15","/images/quesabirria.jpg",ORO},
    {"Suadero","Bs 15","/images/suadero.jpg",ORO},
    {"Pastor","Bs 15","/images/pastor.jpg",VERDE},
    {"Lengua","Bs 15","/images/lengua.jpg",VERDE}
};
        for (int i = 0; i < data.length; i++) {
            g.add(
    buildListItem(
        data[i][0],
        data[i][1],
        data[i][2],
        data[i][3]
    ),
    i % 2,
    i / 2
);
        }
        return g;
    }

    private static GridPane buildBebidasListGrid() {
        GridPane g = makeListGrid();
        g.add(
    buildListItem(
        "Horchata",
        "Bs 8",
        "/images/horchata.jpg",
        VERDE
    ),
    0,
    0
);

g.add(
    buildListItem(
        "Jamaica",
        "Bs 8",
        "/images/jamaica.jpg",
        VERDE
    ),
    1,
    0
);
        return g;
    }

    private static GridPane buildPromosListGrid() {
        GridPane g = makeListGrid();
        g.add(
    buildListItemPromo(
        "Combo Nachos",
        "COMBO 2",
        "Bs 70",
        "/images/nachos-supremos.jpg",
        VERDE
    ),
    0,
    0
);

g.add(
    buildListItemPromo(
        "Combo Burrito",
        "COMBO 2",
        "Bs 70",
        "/images/megaburrito.jpg",
        VERDE
    ),
    1,
    0
);
        return g;
    }

    private static GridPane makeListGrid() {
        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(10);
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(50);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(50);
        g.getColumnConstraints().addAll(cc, cc2);
        return g;
    }

    private static HBox buildListItem(
    String title,
    String price,
    String imagePath,
    String accent
) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 12, 10, 12));
        item.setStyle(
            "-fx-background-color: " + ROW + ";" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(44,26,14,0.06), 6, 0, 0, 2);"
        );

        Image image = new Image(
    MostrarMenu.class.getResourceAsStream(imagePath)
);

ImageView img = new ImageView(image);

img.setFitWidth(44);
img.setFitHeight(44);

Circle clip = new Circle(22,22,22);
img.setClip(clip);

        VBox texts = new VBox(3);
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + DARK + ";");
        Label s = new Label("★★★★★");
        s.setStyle("-fx-text-fill: #D4A017; -fx-font-size: 11px;");
        texts.getChildren().addAll(t, s);
        HBox.setHgrow(texts, Priority.ALWAYS);

        Label priceLbl = new Label(price);
        priceLbl.setStyle(
            "-fx-background-color: " + accent + ";" +
            "-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-padding: 6 11 6 11; -fx-background-radius: 8;"
        );

        item.getChildren().addAll(img, texts, priceLbl);
        return item;
    }

    private static HBox buildListItemPromo(
    String title,
    String badge,
    String price,
    String imagePath,
    String accent
){
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 12, 10, 12));
        item.setStyle(
            "-fx-background-color: " + ROW + ";" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(44,26,14,0.06), 6, 0, 0, 2);"
        );

        Image image = new Image(
    MostrarMenu.class.getResourceAsStream(imagePath)
);

ImageView img = new ImageView(image);

img.setFitWidth(44);
img.setFitHeight(44);

Circle clip = new Circle(22,22,22);
img.setClip(clip);

        VBox texts = new VBox(3);
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + DARK + ";");
        Label b = new Label(badge);
        b.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + accent + ";");
        texts.getChildren().addAll(t, b);
        HBox.setHgrow(texts, Priority.ALWAYS);

        Label priceLbl = new Label(price);
        priceLbl.setStyle(
            "-fx-background-color: " + accent + ";" +
            "-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-padding: 6 11 6 11; -fx-background-radius: 8;"
        );

        item.getChildren().addAll(img, texts, priceLbl);
        return item;
    }

    // ─── Botones de toggle ────────────────────────────────────
    private static void styleToggle(Button btn, boolean active) {
        if (active) {
            btn.setStyle(
                "-fx-background-color: " + ROJO + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px; -fx-font-weight: bold;" +
                "-fx-background-radius: 10; -fx-cursor: hand;" +
                "-fx-padding: 8 14 8 14;"
            );
        } else {
            btn.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #7A5C3A;" +
                "-fx-font-size: 18px; -fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #D5C4A8;" +
                "-fx-border-radius: 10; -fx-border-width: 1.5;" +
                "-fx-cursor: hand; -fx-padding: 8 14 8 14;"
            );
        }
    }
    private static void mostrarIngredientes(
        String plato,
        String imagen,
        String precio
) {

    Stage ventana = new Stage();
    ventana.initModality(Modality.APPLICATION_MODAL);
    ventana.setTitle(plato);

    String ingredientes = "";
    String descripcion = "";

    switch (plato) {

             case "Pastor":
            descripcion =
                "Auténtico taco al pastor preparado con carne marinada al estilo mexicano, acompañado de piña fresca, cebolla y cilantro que aportan un equilibrio perfecto entre dulzura y sabor.";

            ingredientes =
                "• Tortilla de maíz......1 und\n" +
                "• Birria de res........60 gr\n" +
                "• Piña picada..........10 gr\n" +
                "• Cilantro.............10 gr\n" +
                "• Cebolla..............10 gr";
            break;
             case "quesabirria":
            descripcion =
                 "Crujiente tortilla dorada rellena de jugosa birria y abundante queso mozzarella fundido, una combinación irresistible para los amantes del auténtico sabor mexicano.";

            ingredientes =
                "• Tortilla de maíz......1 und\n" +
                "• Carne de birria.......60 gr\n" +
                "• Queso mozzarella.....40 gr\n" +
                "• Cilantro.............10 gr\n" +
                "• Aceite...............10 ml\n" +
                "• Cebolla..............10 gr";
            break;
            case "RamenBirria":
            descripcion =
                 "Fusión única entre la tradición japonesa y mexicana: fideos ramen bañados en un aromático caldo de birria, acompañados de tierna carne de res y un toque picante.";

            ingredientes =
                "• Fideo ramen (cocido).150 gr\n" +
                "• Clado de res.........350 ml\n" +
                "• Carne birria.........80 gr\n" +
                "• Chili................15 ml";
            break;
            case "Suadero":
            descripcion =
                "Taco tradicional de suadero cocinado lentamente para lograr una textura suave y jugosa, servido con cebolla y cilantro frescos.";
            ingredientes =
                "• Tortilla de maíz......1 und\n" +
                "• Carne de birria.......60 gr\n" +
                "• Queso mozzarella.....40 gr\n" +
                "• Cilantro.............10 gr\n" +
                "• Aceite...............10 ml\n" +
                "• Cebolla..............10 gr";
            break;

        case "Tacobirria":
            descripcion =
                "Tortilla de maíz rellena de tierna carne de birria cocida a fuego lento, servida con cebolla fresca para resaltar todo su sabor.";

            ingredientes =
                "• Tortilla de maíz......1 und\n" +
                "• Carne de birria.......60 gr\n" +
                "• Aceite...............10 ml\n" +
                "• Cebolla..............10 gr";
            break;

        case "Quesabirria":
            descripcion =
                "Deliciosa tortilla dorada con queso fundido y carne de birria sazonada, acompañada de cilantro y cebolla para una experiencia auténticamente mexicana.";

            ingredientes =
                "• Tortilla de maíz......1 und\n" +
                "• Carne de birria.......60 gr\n" +
                "• Queso mozzarella.....40 gr\n" +
                "• Cilantro.............10 gr\n" +
                "• Aceite...............10 ml\n" +
                "• Cebolla..............10 gr";
            break;
            case "MegaBurrito":
            descripcion =
                "Un burrito gigante repleto de carne chili, arroz, guacamole y una mezcla de quesos fundidos, ideal para quienes buscan una comida abundante y llena de sabor.";

            ingredientes =
                "• Tortilla de arina xl..120 gr\n" +
                "• Carne chili..........80 gr\n" +
                "• Arroz cocido.........60 gr\n" +
                "• Guacamole............40 ml\n" +
                "• Queso cheddar........40 gr\n" +
                "• Queso mozzarella.....40 gr";
            break;
            case "Lengua":
            descripcion =
                "Taco de lengua de res cocinada lentamente hasta alcanzar una textura suave y delicada, acompañado de cebolla y cilantro frescos.";

            ingredientes =
                "• Tortilla\n" +
                "• Birria\n" +
                "• Queso\n" +
                "• Cilantro\n" +
                "• Cebolla";
            break;
            case "Jamaica":
            descripcion =
                 "Refrescante bebida tradicional mexicana elaborada con flor de jamaica, de sabor dulce y ligeramente ácido, perfecta para acompañar cualquier platillo.";


            ingredientes =
            
                "• Jamaica.................500 ml";
            break;
            case "Horchata":
            descripcion =
                "Bebida tradicional mexicana preparada a base de arroz y canela, cremosa, dulce y sumamente refrescante.";

            ingredientes =
            
                "• Horchata.................500 ml";
            break;
case "Nachos Supremos":
    descripcion =
       "Crujientes nachos cubiertos con carne chili, queso cheddar derretido y guacamole fresco, ideales para compartir y disfrutar en cualquier ocasión.";
    ingredientes =
                "• Nachos..............120 gr\n" +
                "• Carne chili..........80 gr\n" +
                "• Guacamole............50 gr\n" +
                "• Queso cheddar........60 gr";
    break;


case "Mega Burrito":
    descripcion =
        "Nuestra especialidad de gran tamaño: tortilla XL rellena con carne chili, arroz, guacamole y una generosa porción de quesos fundidos.";
    ingredientes =
                "• Tortilla de arina xl..120 gr\n" +
                "• Carne chili..........80 gr\n" +
                "• Arroz cocido.........60 gr\n" +
                "• Guacamole............40 ml\n" +
                "• Queso cheddar........40 gr\n" +
                "• Queso mozzarella.....40 gr";
    break;
    }
    

    VBox root = new VBox(15);
    root.setPadding(new Insets(20));
    root.setAlignment(Pos.TOP_CENTER);

    root.setStyle(
        "-fx-background-color: #F5EDD8;"
    );

    Label titulo = new Label(plato);

    titulo.setStyle(
        "-fx-font-size: 24px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: #C8102E;"
    );

    ImageView img = new ImageView(
        new Image(
            MostrarMenu.class.getResourceAsStream(imagen)
        )
    );

    img.setFitWidth(250);
    img.setFitHeight(180);
    img.setPreserveRatio(true);

    Label desc = new Label(descripcion);

    desc.setWrapText(true);
    desc.setMaxWidth(300);

    Label ing = new Label(ingredientes);

    ing.setStyle(
        "-fx-font-size: 14px;"
    );

    Label price = new Label(
        "Precio: " + precio
    );

    price.setStyle(
        "-fx-font-size: 18px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: #1A6B3C;"
    );

    Button cerrar = new Button("Cerrar");

    cerrar.setStyle(
        "-fx-background-color: #C8102E;" +
        "-fx-text-fill: white;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 10;"
    );

    cerrar.setOnAction(e -> ventana.close());

   Label descTitulo = new Label("Descripción");
descTitulo.setStyle(
    "-fx-font-size: 16px;" +
    "-fx-font-weight: bold;" +
    "-fx-text-fill: #C8102E;"
);

Label ingTitulo = new Label("Ingredientes");
ingTitulo.setStyle(
    "-fx-font-size: 16px;" +
    "-fx-font-weight: bold;" +
    "-fx-text-fill: #C8102E;"
);

desc.setStyle(
    "-fx-font-size: 14px;" +
    "-fx-text-fill: #333333;"
);

ing.setStyle(
    "-fx-font-size: 14px;" +
    "-fx-text-fill: #333333;"
);

root.getChildren().addAll(
    titulo,
    img,
    descTitulo,
    desc,
    ingTitulo,
    ing,
    price,
    cerrar
);

    Scene scene = new Scene(root, 500, 650);

    ventana.setScene(scene);
    ventana.showAndWait();
}
}