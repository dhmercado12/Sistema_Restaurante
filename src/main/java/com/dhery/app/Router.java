package com.dhery.app;

import com.dhery.views.StartView;
import javafx.stage.Stage;
import com.dhery.views.LoginView;
import com.dhery.views.ClientLoginView;
import com.dhery.views.CashierLoginView;

public class Router {

    private static Stage stage;

    public static void setStage(Stage s) {
        stage = s;
    }

    public static void goStart() {
        stage.setScene(StartView.getScene());
        stage.setTitle("Sistema - Inicio");
        stage.show();
    }

    public static void goLogin() {

    stage.setScene(LoginView.getScene());
    stage.setTitle("Tacabrón - Login");
}

 public static void goClientLogin() {

    stage.setScene(ClientLoginView.getScene());

    stage.setTitle("Tacabrón - Cliente");
}

public static void goCashierLoginView() {

    stage.setScene(CashierLoginView.getScene());

    stage.setTitle("Tacabrón - Cajero");
}

}