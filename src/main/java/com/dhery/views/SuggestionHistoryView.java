package com.dhery.views;

import com.dhery.app.Router;
import com.dhery.models.Suggestion;
import com.dhery.repositories.SuggestionRepository;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class SuggestionHistoryView {

    public static Scene getScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color:white;"
        );

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(25)
        );

        content.setAlignment(
                Pos.TOP_CENTER
        );

        Label title =
                new Label(
                        "💬 HISTORIAL DE SUGERENCIAS"
                );

        title.setStyle(
                "-fx-font-size:32;" +
                "-fx-font-weight:bold;" +
                "-fx-text-fill:#CC0000;"
        );

        VBox listContainer =
                new VBox(15);

        for(Suggestion s :
                SuggestionRepository.obtenerTodas()){

            VBox card =
                    new VBox(8);

            card.setPadding(
                    new Insets(20)
            );

            card.setStyle(
                    "-fx-background-color:white;" +
                    "-fx-border-color:#EEEEEE;" +
                    "-fx-border-radius:15;" +
                    "-fx-background-radius:15;"
            );

            Label user =
                    new Label(
                            "👤 " +
                            s.getNombre()
                    );

            Label cat =
                    new Label(
                            "📂 " +
                            s.getCategoria()
                    );

            Label msg =
                    new Label(
                            s.getMensaje()
                    );

            msg.setWrapText(true);

            Label date =
                    new Label(
                            "📅 " +
                            s.getFecha()
                    );

            card.getChildren().addAll(
                    user,
                    cat,
                    msg,
                    date
            );

            listContainer
                    .getChildren()
                    .add(card);
        }

        ScrollPane scroll =
                new ScrollPane(
                        listContainer
                );

        scroll.setFitToWidth(true);

        Button back =
                new Button(
                        "← VOLVER"
                );

        back.setOnAction(
                e -> Router.goMenuCajeroView(
                        Router.getCurrentUser()
                )
        );

        content.getChildren().addAll(
                title,
                scroll,
                back
        );

        root.setCenter(content);

        return new Scene(
                root,
                1280,
                720
        );
    }
}
