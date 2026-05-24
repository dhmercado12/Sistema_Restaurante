package com.dhery;

import com.dhery.app.*;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        Router.setStage(stage);
        Router.goStart(); // 👈 primera pantalla

    }

    public static void main(String[] args) {
        launch();
    }
}