package com.dhery.app;

import com.dhery.views.StartView;
import javafx.stage.Stage;

import com.dhery.views.ClientLoginView;
import com.dhery.utils.TakeOrderView;
import com.dhery.views.TakeOrderViewC;
import com.dhery.views.RegisterView;
import com.dhery.views.MenuCajeroView;
import com.dhery.views.MenuClienteView;
import com.dhery.views.StockView;
import com.dhery.views.SuggestionView;
import com.dhery.views.MostrarMenu;
import com.dhery.views.HistorialVentasView;
import com.dhery.views.ClientesRegistradosView;
import com.dhery.views.FacturasGuardadasView;


public class Router {

    public enum Role {
        CAJERO,
        CLIENTE
    }

    private static Role currentRole;

    public static void setRole(Role role) {
        currentRole = role;
    }

    public static Role getRole() {
        return currentRole;
    }

    private static Stage stage;

    public static void setStage(Stage s) {
        stage = s;
    }

    public static void goStart() {
        stage.setScene(StartView.getScene());
        stage.setTitle("Sistema - Inicio");
        stage.show();
    }


public static void goClientLogin() {

    stage.setScene(ClientLoginView.getScene());

    stage.setTitle("Tacabrón - Cliente");
}

public static void goRegisterView() {
        stage.setScene(RegisterView.getScene());
        stage.setTitle("Tacabrón - Registro");
    }
    
public static void goMenuCajeroView() {
    stage.setScene(MenuCajeroView.getScene());
    stage.setTitle("Tacabrón - Menú Cajero");
}

public static void goTakeOrderView() {
    stage.setScene(TakeOrderView.getScene());
    stage.setTitle("Tacabrón - Tomar Pedido");
}

public static void goTakeOrderViewC() {
    stage.setScene(TakeOrderViewC.getScene());
    stage.setTitle("Tacabrón - realizar Pedido");
}

public static void goStockView() {
    stage.setScene(StockView.getScene());
    stage.setTitle("Tacabrón - Stock");
}

public static void goOrderStatusView() {
        stage.setScene(MenuCajeroView.getScene());
        stage.setTitle("Tacabrón - Estado de Pedido");
    }

    public static void goMenuClienteView() {
        stage.setScene(MenuClienteView.getScene());
        stage.setTitle("Tacabrón - Menú Cliente");
    }

    public static void goSuggestionView() {
        stage.setScene(SuggestionView.getScene());
        stage.setTitle("Tacabrón - Sugerencias");
    }

    public static void goMostrarMenu() {
        stage.setScene(MostrarMenu.getScene());
        stage.setTitle("Tacabrón - Menú");
    }

    //----PANTALLA DE HISTORIAL DE VENTAS ----  
    public static void goHistorialVentasView() {
        stage.setScene(HistorialVentasView.getScene());
        stage.setTitle("Tacabrón - Historial de Ventas");
    }
    //-----PANTALLA DE CLIENTES REGISTRADOS-----
    public static void goClientesRegistradosView() {
        stage.setScene(ClientesRegistradosView.getScene());
        stage.setTitle("Tacabrón - Clientes Registrados");
    }
    //-----PANTALLA DE FACTURAS GUARDADAS-----
    public static void goFacturasGuardadasView() {
        stage.setScene(FacturasGuardadasView.getScene());
        stage.setTitle("Tacabrón - Facturas Guardadas");
    }

}