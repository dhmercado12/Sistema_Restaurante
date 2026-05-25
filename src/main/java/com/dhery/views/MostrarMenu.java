package com.dhery.views;

import com.dhery.app.Router;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MostrarMenu {

    private static boolean isGridMode = true;

    public static Scene getScene() {

        // =====================================================
        // ROOT GENERAL
        // =====================================================

        VBox container = new VBox(15);

        container.setPadding(new Insets(15));

        container.setStyle(
                "-fx-background-color: #F5E8D7;"
        );

        // =====================================================
        // TOP BAR
        // =====================================================

        HBox topBar = new HBox();

        topBar.setAlignment(Pos.CENTER_LEFT);

        Region topSpacer = new Region();

        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Button gridBtn = new Button("☷");
        Button listBtn = new Button("☰");
        Button backBtn = new Button("← VOLVER");

        String btnStyle =
                "-fx-background-color: white;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 8 14 8 14;";

        gridBtn.setStyle(btnStyle);
        listBtn.setStyle(btnStyle);

         backBtn.setStyle(
        "-fx-background-color: #CC0000;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 14px;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 12;" +
        "-fx-cursor: hand;" +
        "-fx-padding: 8 16 8 16;"
         );
         backBtn.setOnAction(e -> {
         Router.goMenuClienteView();
         });

        topBar.getChildren().addAll(
        backBtn,
        topSpacer,
        gridBtn,
        listBtn
        );

        // =====================================================
        // CONTENIDO
        // =====================================================

        HBox content = new HBox(15);

        content.setAlignment(Pos.TOP_CENTER);

        VBox platos = buildPlatosColumn();
        VBox bebidas = buildBebidasColumn();
        VBox promos = buildPromosColumn();

        content.getChildren().addAll(
                platos,
                bebidas,
                promos
        );

        // =====================================================
        // BOTON GRID
        // =====================================================

        gridBtn.setOnAction(e -> {

            if (isGridMode) return;

            isGridMode = true;

            content.getChildren().clear();

            content.setSpacing(15);

            content.getChildren().addAll(
                    buildPlatosColumn(),
                    buildBebidasColumn(),
                    buildPromosColumn()
            );
        });

        // =====================================================
        // BOTON LISTA
        // =====================================================

        listBtn.setOnAction(e -> {

    if (!isGridMode) return;

    isGridMode = false;

    content.getChildren().clear();

    VBox mainList = new VBox(16);

mainList.setPrefWidth(950);

mainList.setAlignment(Pos.CENTER);

HBox wrapper = new HBox(mainList);

wrapper.setAlignment(Pos.TOP_CENTER);

wrapper.setPrefWidth(1200);

    // =====================================================
    // PLATOS
    // =====================================================

    VBox platosSection = buildCompactListSection(
            "🌮",
            "PLATOS",
            "#FF5A00"
    );

    GridPane platosGrid = new GridPane();

    platosGrid.setHgap(10);
    platosGrid.setVgap(10);

    platosGrid.add(buildCompactListItem(
            "Nachos Supremos",
            "Bs 45"
    ), 0, 0);

    platosGrid.add(buildCompactListItem(
            "MegaBurrito",
            "Bs 45"
    ), 1, 0);

    platosGrid.add(buildCompactListItem(
            "RamenBirria",
            "Bs 35"
    ), 0, 1);

    platosGrid.add(buildCompactListItem(
            "Tacobirria",
            "Bs 15"
    ), 1, 1);

    platosGrid.add(buildCompactListItem(
            "Quesabirria",
            "Bs 15"
    ), 0, 2);

    platosGrid.add(buildCompactListItem(
            "Suadero",
            "Bs 15"
    ), 1, 2);

    platosGrid.add(buildCompactListItem(
            "Pastor",
            "Bs 15"
    ), 0, 3);

    platosGrid.add(buildCompactListItem(
            "Lengua",
            "Bs 15"
    ), 1, 3);

    platosSection.getChildren().add(platosGrid);

    // =====================================================
    // BEBIDAS
    // =====================================================

    VBox bebidasSection = buildCompactListSection(
            "🥤",
            "BEBIDAS",
            "#6E2EBB"
    );

    GridPane bebidasGrid = new GridPane();

    bebidasGrid.setHgap(10);
    bebidasGrid.setVgap(10);

    bebidasGrid.add(buildCompactListItem(
            "Horchata",
            "Bs 8"
    ), 0, 0);

    bebidasGrid.add(buildCompactListItem(
            "Jamaica",
            "Bs 8"
    ), 1, 0);

    bebidasSection.getChildren().add(bebidasGrid);

    // =====================================================
    // PROMOCIONES
    // =====================================================

    VBox promoSection = buildCompactListSection(
            "🔥",
            "PROMOCIONES",
            "#248A1D"
    );

    GridPane promoGrid = new GridPane();

    promoGrid.setHgap(10);
    promoGrid.setVgap(10);

    promoGrid.add(buildCompactListItem(
            "Combo Nachos",
            "Bs 70"
    ), 0, 0);

    promoGrid.add(buildCompactListItem(
            "Combo Burrito",
            "Bs 70"
    ), 1, 0);

    promoSection.getChildren().add(promoGrid);

    // =====================================================

    mainList.getChildren().addAll(
            platosSection,
            bebidasSection,
            promoSection
    );

    content.getChildren().add(wrapper);
});

        container.getChildren().addAll(
                topBar,
                content
        );

        return new Scene(container, 1280, 720);
    }

    // =========================================================
    // PLATOS
    // =========================================================

    private static VBox buildPlatosColumn() {

        VBox column = buildColumnBase();

        HBox header = buildHeader(
                "🌮",
                "PLATOS",
                "#FF5A00"
        );

        VBox items = new VBox(10);

        items.setPadding(new Insets(16));

        items.getChildren().addAll(

                buildFoodItem("1", "Nachos Supremos", "Bs 45"),
                buildFoodItem("2", "MegaBurrito", "Bs 45"),
                buildFoodItem("3", "RamenBirria", "Bs 35"),
                buildFoodItem("4", "Tacobirria", "Bs 15"),
                buildFoodItem("5", "Quesabirria", "Bs 15"),
                buildFoodItem("6", "Suadero", "Bs 15"),
                buildFoodItem("7", "Pastor", "Bs 15"),
                buildFoodItem("8", "Lengua", "Bs 15")
        );

        column.getChildren().addAll(
                header,
                items
        );

        return column;
    }

    // =========================================================
    // BEBIDAS
    // =========================================================

    private static VBox buildBebidasColumn() {

        VBox column = buildColumnBase();

        HBox header = buildHeader(
                "🥤",
                "BEBIDAS",
                "#6E2EBB"
        );

        VBox items = new VBox(18);

        items.setAlignment(Pos.TOP_CENTER);

        items.setPadding(new Insets(16));

        items.getChildren().addAll(

                buildDrinkCard(
                        "11",
                        "Horchata",
                        "Bs 8"
                ),

                buildDrinkCard(
                        "12",
                        "Jamaica",
                        "Bs 8"
                )
        );

        column.getChildren().addAll(
                header,
                items
        );

        return column;
    }

    // =========================================================
    // PROMOS
    // =========================================================

    private static VBox buildPromosColumn() {

        VBox column = buildColumnBase();

        HBox header = buildHeader(
                "🔥",
                "PROMOCIONES",
                "#248A1D"
        );

        VBox items = new VBox(16);

        items.setPadding(new Insets(16));

        items.getChildren().addAll(

                buildPromoCard(
                        "13",
                        "Nachos Supremos",
                        "COMBO 2",
                        "Bs 70"
                ),

                buildPromoCard(
                        "14",
                        "MegaBurrito",
                        "COMBO 2",
                        "Bs 70"
                )
        );

        column.getChildren().addAll(
                header,
                items
        );

        return column;
    }

    // =========================================================
    // BASE COLUMNA
    // =========================================================

    private static VBox buildColumnBase() {

        VBox column = new VBox();

        // TODAS IGUALES
        column.setPrefWidth(350);
        column.setMinWidth(350);
        column.setMaxWidth(350);

        column.setStyle(
                "-fx-background-color: #FFF9F2;" +
                "-fx-background-radius: 22;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08),10,0,0,3);"
        );

        return column;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private static HBox buildHeader(
            String icon,
            String title,
            String color
    ) {

        HBox header = new HBox(10);

        header.setAlignment(Pos.CENTER_LEFT);

        header.setPadding(new Insets(15));

        header.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-background-radius: 22 22 0 0;"
        );

        Circle circle = new Circle(18);

        circle.setFill(Color.WHITE);

        Label iconLbl = new Label(icon);

        iconLbl.setStyle(
                "-fx-font-size: 16px;"
        );

        StackPane iconBox = new StackPane(
                circle,
                iconLbl
        );

        Label titleLbl = new Label(title);

        titleLbl.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        header.getChildren().addAll(
                iconBox,
                titleLbl
        );

        return header;
    }

    // =========================================================
    // ITEM COMIDA
    // =========================================================

    private static HBox buildFoodItem(
            String number,
            String title,
            String price
    ) {

        HBox item = new HBox(10);

        item.setAlignment(Pos.CENTER_LEFT);

        item.setPadding(new Insets(8));

        item.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;"
        );

        Circle image = new Circle(18);

        image.setFill(Color.web("#D9D9D9"));

        Label num = new Label(number);

        num.setStyle(
                "-fx-background-color: #FF6B00;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 4 7 4 7;" +
                "-fx-background-radius: 50;"
        );

        Label titleLbl = new Label(title);

        titleLbl.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1A1A1A;"
        );

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label priceLbl = new Label(price);

        priceLbl.setStyle(
                "-fx-background-color: #FF6B00;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 6 10 6 10;" +
                "-fx-background-radius: 8;"
        );

        item.getChildren().addAll(
                image,
                num,
                titleLbl,
                spacer,
                priceLbl
        );

        return item;
    }

    // =========================================================
    // BEBIDA
    // =========================================================

    private static VBox buildDrinkCard(
            String number,
            String title,
            String price
    ) {

        VBox card = new VBox(12);

        card.setAlignment(Pos.CENTER);

        card.setPadding(new Insets(15));

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;"
        );

        Circle image = new Circle(38);

        image.setFill(Color.web("#DCC2FF"));

        Label num = new Label(number);

        num.setStyle(
                "-fx-background-color: #6E2EBB;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 5 8 5 8;" +
                "-fx-background-radius: 50;"
        );

        Label titleLbl = new Label(title);

        titleLbl.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        Label priceLbl = new Label(price);

        priceLbl.setStyle(
                "-fx-background-color: #6E2EBB;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 14 8 14;" +
                "-fx-background-radius: 8;"
        );

        card.getChildren().addAll(
                image,
                num,
                titleLbl,
                priceLbl
        );

        return card;
    }

    // =========================================================
    // PROMOS
    // =========================================================

    private static VBox buildPromoCard(
            String number,
            String title,
            String combo,
            String price
    ) {

        VBox card = new VBox(12);

        card.setPadding(new Insets(15));

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;"
        );

        HBox top = new HBox(8);

        Label num = new Label(number);

        num.setStyle(
                "-fx-background-color: #248A1D;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 5 8 5 8;" +
                "-fx-background-radius: 50;"
        );

        VBox texts = new VBox(1);

        Label titleLbl = new Label(title);

        titleLbl.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );

        Label comboLbl = new Label(combo);

        comboLbl.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #248A1D;"
        );

        texts.getChildren().addAll(
                titleLbl,
                comboLbl
        );

        top.getChildren().addAll(
                num,
                texts
        );

        Label priceLbl = new Label(price);

        priceLbl.setStyle(
                "-fx-background-color: #248A1D;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 14 8 14;" +
                "-fx-background-radius: 8;"
        );

        Circle image = new Circle(38);

        image.setFill(Color.web("#FFD76A"));

        VBox.setMargin(image, new Insets(0,0,0,35));

        card.getChildren().addAll(
                top,
                priceLbl,
                image
        );

        return card;
    }

    private static VBox buildCompactListSection(
        String icon,
        String title,
        String color
) {

    VBox section = new VBox(10);

    HBox header = new HBox(10);

    header.setAlignment(Pos.CENTER_LEFT);

    Label iconLbl = new Label(icon);

    iconLbl.setStyle(
            "-fx-font-size: 18px;"
    );

    Label titleLbl = new Label(title);

    titleLbl.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + color + ";"
    );

    Region line = new Region();

    HBox.setHgrow(line, Priority.ALWAYS);

    line.setPrefHeight(2);

    line.setStyle(
            "-fx-background-color: " + color + ";"
    );

    header.getChildren().addAll(
            iconLbl,
            titleLbl,
            line
    );

    section.getChildren().add(header);

    return section;
}
private static HBox buildCompactListItem(
        String title,
        String price
) {

    HBox item = new HBox(10);

    item.setAlignment(Pos.CENTER_LEFT);

    item.setPadding(new Insets(10));

    // MÁS PEQUEÑO
    item.setPrefWidth(420);

    item.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 14;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05),6,0,0,2);"
    );

    // Imagen
    Circle image = new Circle(18);

    image.setFill(Color.web("#D9D9D9"));

    // Textos
    VBox texts = new VBox(2);

    Label titleLbl = new Label(title);

    titleLbl.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #1A1A1A;"
    );

    Label stars = new Label("★★★★★");

    stars.setStyle(
            "-fx-text-fill: #FFB400;" +
            "-fx-font-size: 10px;"
    );

    texts.getChildren().addAll(
            titleLbl,
            stars
    );

    Region spacer = new Region();

    HBox.setHgrow(spacer, Priority.ALWAYS);

    // Precio
    Label priceLbl = new Label(price);

    priceLbl.setStyle(
            "-fx-background-color: #FF6B00;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 6 10 6 10;" +
            "-fx-background-radius: 8;"
    );

    item.getChildren().addAll(
            image,
            texts,
            spacer,
            priceLbl
    );

    return item;
}
}