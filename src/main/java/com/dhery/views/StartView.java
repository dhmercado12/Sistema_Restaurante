package com.dhery.views;

import com.dhery.app.Router;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StartView {

    public static Scene getScene() {

        // =====================================================
        // NAVBAR
        // =====================================================
        Label logo = new Label("🌮 TACABRÓN");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Button loginBtn = new Button("Iniciar Sesión");
        loginBtn.setOnAction(e -> {
              Router.goLogin();
           });
        loginBtn.setStyle("""
            -fx-background-color: rgba(138,92,255,0.18);
            -fx-border-color: #6FA8FF;
            -fx-text-fill: white;
            -fx-background-radius: 12;
            -fx-border-radius: 12;
            -fx-padding: 12 24;
            -fx-font-size: 14px;
            -fx-cursor: hand;
        """);

        BorderPane navbar = new BorderPane();
        navbar.setLeft(logo);
        navbar.setRight(loginBtn);

        navbar.setPadding(new Insets(25, 40, 25, 40));

        // =====================================================
        // HERO LEFT CONTENT
        // =====================================================
        Label badge = new Label("Sistema Inteligente de Gestión");
        badge.setStyle("""
            -fx-background-color: rgba(138,92,255,0.15);
            -fx-text-fill: #9B7BFF;
            -fx-padding: 8 18;
            -fx-background-radius: 20;
            -fx-font-size: 13px;
        """);

        Label title = new Label(
                "Transformando la gestión\n"
                        + "de la Taquería Tacabrón"
        );

        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 52));
        title.setTextFill(Color.WHITE);

        Label desc = new Label("""
                Plataforma desarrollada para optimizar la administración
                de pedidos, ventas, inventario, delivery y reportes
                administrativos dentro de la taquería Tacabrón.

                El sistema permite reemplazar procesos manuales por
                herramientas digitales modernas, mejorando la eficiencia,
                reduciendo errores y ofreciendo una atención más rápida
                y organizada para clientes y trabajadores.
                """);

        desc.setTextFill(Color.web("#B8B8C7"));
        desc.setFont(Font.font(17));
        desc.setWrapText(true);
        desc.setMaxWidth(550);

        Button primaryBtn = new Button("Explorar Sistema");
        primaryBtn.setOnAction(e -> {
              Router.goLogin();
           });
        primaryBtn.setStyle("""
            -fx-background-color: linear-gradient(to right, #8A5CFF, #00C2FF);
            -fx-text-fill: white;
            -fx-font-size: 15px;
            -fx-font-weight: bold;
            -fx-padding: 14 34;
            -fx-background-radius: 14;
            -fx-cursor: hand;
        """);

        Button secondaryBtn = new Button("Ver Características");

        secondaryBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-border-color: rgba(255,255,255,0.3);
            -fx-text-fill: white;
            -fx-font-size: 15px;
            -fx-padding: 14 34;
            -fx-border-radius: 14;
            -fx-background-radius: 14;
            -fx-cursor: hand;
        """);

        HBox buttons = new HBox(15, primaryBtn, secondaryBtn);

        VBox leftContent = new VBox(
                22,
                badge,
                title,
                desc,
                buttons
        );

        leftContent.setAlignment(Pos.CENTER_LEFT);

        // =====================================================
        // DASHBOARD DERECHO
        // =====================================================
        VBox dashboard = new VBox(18);

        dashboard.setPrefSize(430, 340);

        dashboard.setStyle("""
            -fx-background-color: rgba(255,255,255,0.05);
            -fx-background-radius: 28;
            -fx-border-radius: 28;
            -fx-border-color: rgba(138,92,255,0.35);
            -fx-padding: 24;
        """);

        Label dashTitle = new Label("Dashboard Tacabrón");
        dashTitle.setTextFill(Color.WHITE);
        dashTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        VBox salesCard = createCard(
                "Gestión de Ventas",
                "Pedidos automatizados",
                "#00FF99"
        );

        VBox inventoryCard = createCard(
                "Control de Inventario",
                "Stock en tiempo real",
                "#8A5CFF"
        );

        VBox reportCard = createCard(
                "Reportes Inteligentes",
                "Análisis de ingresos",
                "#00C2FF"
        );

        dashboard.getChildren().addAll(
                dashTitle,
                salesCard,
                inventoryCard,
                reportCard
        );

        // =====================================================
        // HERO SECTION
        // =====================================================
        HBox hero = new HBox(80, leftContent, dashboard);

        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(60));

        // =====================================================
        // SECCIÓN SOBRE EL SISTEMA
        // =====================================================
        Label sectionTitle = new Label(
                "Todo lo que Tacabrón necesita\n"
                        + "para administrar su negocio"
        );

        sectionTitle.setTextFill(Color.WHITE);

        sectionTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 40)
        );

        VBox feature1 = featureCard(
                "🧾",
                "Gestión de Pedidos",
                """
                Permite registrar órdenes de clientes
                de manera rápida y organizada,
                reduciendo errores y optimizando
                la atención durante horarios pico.
                """
        );

        VBox feature2 = featureCard(
                "📦",
                "Control de Inventario",
                """
                Monitorea ingredientes e insumos
                en tiempo real, evitando pérdidas,
                desabastecimientos y desperdicio
                de materia prima.
                """
        );

        VBox feature3 = featureCard(
                "📊",
                "Reportes Administrativos",
                """
                Genera reportes de ventas,
                ingresos y productos más vendidos
                para facilitar la toma
                de decisiones estratégicas.
                """
        );

        HBox features = new HBox(
                25,
                feature1,
                feature2,
                feature3
        );

        features.setAlignment(Pos.CENTER);

        VBox section = new VBox(
                40,
                sectionTitle,
                features
        );

        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(70));

       

       
        // =====================================================
        // SOLUCIÓN DEL SISTEMA
        // =====================================================
        Label solutionTitle = new Label(
                "Nuestra solución tecnológica"
        );

        solutionTitle.setTextFill(Color.WHITE);

        solutionTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 38)
        );

        Label solutionText = new Label("""
                El sistema desarrollado para Tacabrón centraliza toda
                la información del negocio en una única plataforma.

                La aplicación permite gestionar ventas, pedidos,
                inventario, productos, delivery y reportes financieros
                de forma organizada, moderna y eficiente.

                Gracias al uso de una arquitectura MVC y herramientas
                modernas de desarrollo, el sistema garantiza una
                estructura escalable, mantenible y fácil de utilizar.
                """);

        solutionText.setTextFill(Color.web("#B8B8C7"));
        solutionText.setFont(Font.font(18));
        solutionText.setWrapText(true);
        solutionText.setMaxWidth(900);

        VBox solutionSection = new VBox(
                30,
                solutionTitle,
                solutionText
        );

        solutionSection.setAlignment(Pos.CENTER);
        solutionSection.setPadding(new Insets(70));

        // =====================================================
        // BENEFICIOS
        // =====================================================
        Label benefitsTitle = new Label(
                "Beneficios esperados"
        );

        benefitsTitle.setTextFill(Color.WHITE);

        benefitsTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 38)
        );

        VBox benefit1 = featureCard(
                "⚡",
                "Mayor rapidez",
                "Atención más ágil durante horarios de alta demanda."
        );

        VBox benefit2 = featureCard(
                "💰",
                "Reducción de pérdidas",
                "Mejor control del stock y disminución de errores operativos."
        );

        VBox benefit3 = featureCard(
                "📈",
                "Mejor toma de decisiones",
                "Acceso a información organizada y reportes inteligentes."
        );

        HBox benefits = new HBox(
                25,
                benefit1,
                benefit2,
                benefit3
        );

        benefits.setAlignment(Pos.CENTER);

        VBox benefitsSection = new VBox(
                35,
                benefitsTitle,
                benefits
        );

        benefitsSection.setAlignment(Pos.CENTER);
        benefitsSection.setPadding(new Insets(70));

        // =====================================================
        // IMPACTO
        // =====================================================
        Label impactTitle = new Label(
                "Impacto esperado"
        );

        impactTitle.setTextFill(Color.WHITE);

        impactTitle.setFont(
                Font.font("Arial", FontWeight.BOLD, 38)
        );

        Label impactDesc = new Label("""
                La implementación del sistema permitirá transformar
                el modelo de gestión manual de Tacabrón en una plataforma
                digital moderna y eficiente.

                Esto incrementará la competitividad del negocio dentro
                del mercado gastronómico de Cochabamba, mejorando
                la organización interna, la experiencia del cliente
                y el control administrativo del restaurante.
                """);

        impactDesc.setTextFill(Color.web("#B8B8C7"));

        impactDesc.setFont(Font.font(18));

        impactDesc.setWrapText(true);

        impactDesc.setMaxWidth(900);

        VBox impactSection = new VBox(
                30,
                impactTitle,
                impactDesc
        );

        impactSection.setAlignment(Pos.CENTER);

        impactSection.setPadding(new Insets(70));

        // =====================================================
        // FOOTER
        // =====================================================
        Label footer = new Label(
                "Binary Bricks © 2026 • Universidad Mayor de San Simón"
        );

        footer.setTextFill(Color.web("#6F6F8F"));
        footer.setPadding(new Insets(40));

        // =====================================================
        // MAIN CONTENT
        // =====================================================
        VBox content = new VBox(
                navbar,
                hero,
                section,
                solutionSection,
                benefitsSection,
                impactSection,
                footer
        );

        // =====================================================
        // ROOT
        // =====================================================
        StackPane root = new StackPane(content);

        root.setStyle("""
            -fx-background-color:
                radial-gradient(
                    center 50% 30%,
                    radius 100%,
                    #1A1A40 0%,
                    #0B0B14 45%,
                    #070710 100%
                );
        """);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        scroll.setStyle("""
            -fx-background: #0B0B14;
            -fx-background-color: #0B0B14;
        """);

        return new Scene(scroll, 1280, 720);
    }

    // =====================================================
    // FEATURE CARD
    // =====================================================
    private static VBox featureCard(
            String icon,
            String title,
            String desc
    ) {

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 34px;");

        Label titleLabel = new Label(title);

        titleLabel.setTextFill(Color.WHITE);

        titleLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 20)
        );

        Label descLabel = new Label(desc);

        descLabel.setWrapText(true);

        descLabel.setTextFill(Color.web("#B8B8C7"));

        VBox card = new VBox(
                18,
                iconLabel,
                titleLabel,
                descLabel
        );

        card.setPrefWidth(320);

        card.setPadding(new Insets(28));

        card.setStyle("""
            -fx-background-color: rgba(255,255,255,0.04);
            -fx-background-radius: 24;
            -fx-border-radius: 24;
            -fx-border-color: rgba(138,92,255,0.15);
        """);

        return card;
    }

    // =====================================================
    // DASHBOARD CARD
    // =====================================================
    private static VBox createCard(
            String title,
            String value,
            String color
    ) {

        Label t = new Label(title);
        t.setTextFill(Color.web("#B8B8C7"));

        Label v = new Label(value);

        v.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: bold;
        """);

        v.setTextFill(Color.web(color));

        VBox card = new VBox(10, t, v);

        card.setPadding(new Insets(18));

        card.setStyle("""
            -fx-background-color: rgba(255,255,255,0.04);
            -fx-background-radius: 18;
            -fx-border-radius: 18;
            -fx-border-color: rgba(255,255,255,0.06);
        """);

        return card;
    }

    // =====================================================
    // INFO CARD
    // =====================================================
    private static VBox infoCard(
            String title,
            String desc
    ) {

        Label titleLabel = new Label(title);

        titleLabel.setTextFill(Color.WHITE);

        titleLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 20)
        );

        Label descLabel = new Label(desc);

        descLabel.setWrapText(true);

        descLabel.setTextFill(Color.web("#B8B8C7"));

        VBox card = new VBox(
                12,
                titleLabel,
                descLabel
        );

        card.setPadding(new Insets(25));

        card.setStyle("""
            -fx-background-color: rgba(255,255,255,0.04);
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-border-color: rgba(255,255,255,0.05);
        """);

        return card;
    }
}