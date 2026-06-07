package com.dhery.app;

import com.dhery.views.StartView;
import javafx.stage.Stage;

import com.dhery.views.ClientLoginView;
import com.dhery.models.user;
import com.dhery.utils.TakeOrderView;
import com.dhery.views.TakeOrderViewC;
import com.dhery.views.RegisterView;
import com.dhery.views.MenuCajeroView;
import com.dhery.views.MenuClienteView;
import com.dhery.views.MisDatosView;
import com.dhery.views.StockView;
import com.dhery.views.SuggestionView;
import com.dhery.views.MostrarMenu;
import com.dhery.views.HistorialVentasView;
import com.dhery.views.ClientesRegistradosView;
import com.dhery.views.FacturasGuardadasView;
import com.dhery.views.MisPedidosView;
import com.dhery.views.MostrarMenuCajera;
import com.dhery.views.EditarDatosView;
import com.dhery.views.EstadoCocinaView;
import com.dhery.views.ControlDeliveryView;


public class Router {
    private static com.dhery.models.user currentUser;
public static com.dhery.models.user getCurrentUser() { return currentUser; }

public static void setUser(com.dhery.models.user user) {
    currentUser = user;
}
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
    
public static void goMenuCajeroView(user user) {
    stage.setScene(MenuCajeroView.getScene(user));
    stage.setTitle("Tacabrón - Menú Cajero");
}

public static void goTakeOrderView(user user) {
    stage.setScene(TakeOrderView.getScene(user));
    stage.setTitle("Tacabrón - Tomar Pedido");
}

public static void goTakeOrderViewC(com.dhery.models.user user) {
    stage.setScene(TakeOrderViewC.getScene(user));
    stage.setTitle("Tacabrón - realizar Pedido");
}

public static void goStockView() {
    stage.setScene(StockView.getScene(currentUser));
    stage.setTitle("Tacabrón - Stock");
}



    public static void goMenuClienteView() {
        stage.setScene(MenuClienteView.getScene(currentUser));
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
        stage.setScene(HistorialVentasView.getScene(currentUser));
        stage.setTitle("Tacabrón - Historial de Ventas");
    }
    //-----PANTALLA DE CLIENTES REGISTRADOS-----
    public static void goClientesRegistradosView(user user) {
    stage.setScene(ClientesRegistradosView.getScene(user));
    stage.setTitle("Tacabrón - Clientes Registrados");
}
    //-----PANTALLA DE FACTURAS GUARDADAS-----
    public static void goFacturasGuardadasView() {
        stage.setScene(FacturasGuardadasView.getScene(currentUser));
        stage.setTitle("Tacabrón - Facturas Guardadas");
    }
    public static void goMisFacturas() {
        stage.setScene(MisPedidosView.getScene(currentUser));
        stage.setTitle("Tacabrón - Mis Pedidos");
    }
    public static void goMisDatos(user currentUser) {
    stage.setScene(MisDatosView.getScene(currentUser));
    stage.setTitle("Tacabrón - Mis Datos");
}

public static void goMisDatosEdit(user currentUser) {
    stage.setScene(EditarDatosView.getScene(currentUser));
    stage.setTitle("Tacabrón - Editar Mis Datos");
}
public static void goMostrarMenuCajera(){
        stage.setScene(MostrarMenuCajera.getScene(currentUser));
        stage.setTitle("Tacabrón - Menú");
    }
public static void goEstadoCocinaView() {
        stage.setScene(EstadoCocinaView.getScene());
        stage.setTitle("Tacabrón - Estado de Cocina");
    }
    public static void goControlDeliveryView() {
        stage.setScene(ControlDeliveryView.getScene());
        stage.setTitle("Tacabrón - Control de Delivery");
    }

}