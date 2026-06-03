package com.dhery.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Estado global compartido entre todas las vistas.
 * Inventario, pedidos, notificaciones y repartidores.
 */
public class AppState {

    // ── UMBRAL DE STOCK BAJO ──────────────────────────────────────────────────
    public static final double STOCK_BAJO_UMBRAL = 500.0;

    // ── INVENTARIO GLOBAL ─────────────────────────────────────────────────────
    public static class ItemStock {
        public final String nombre;
        public double cantidad;
        public final String unidad;

        public ItemStock(String nombre, double cantidad, String unidad) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.unidad = unidad;
        }
    }

    public static final List<ItemStock> inventario = new ArrayList<>();

    static {
        inventario.add(new ItemStock("Nachos",                  99400, "gr"));
        inventario.add(new ItemStock("Carne chilli",           199000, "gr"));
        inventario.add(new ItemStock("Carne de birria",        299740, "gr"));
        inventario.add(new ItemStock("Carne de cerdo al pastor",139520,"gr"));
        inventario.add(new ItemStock("Lengua de res",          149640, "gr"));
        inventario.add(new ItemStock("Guacamole",               99590, "gr"));
        inventario.add(new ItemStock("Cebolla y cilantro",      89830, "gr"));
        inventario.add(new ItemStock("Piña picada",             89920, "gr"));
        inventario.add(new ItemStock("Queso cheddar líquido",   99700, "ml"));
        inventario.add(new ItemStock("Queso cheddar mozzarella",149840,"gr"));
        inventario.add(new ItemStock("Queso mozzarella",       150000, "gr"));
        inventario.add(new ItemStock("Tortilla de harina XL",    9996, "und"));
        inventario.add(new ItemStock("Tortilla de maíz",        48983, "und"));
        inventario.add(new ItemStock("Tortilla de harina",      30000, "und"));
        inventario.add(new ItemStock("Masa de pizza",          200000, "gr"));
        inventario.add(new ItemStock("Arroz cocido",           199760, "gr"));
        inventario.add(new ItemStock("Fideo ramen (cocido)",   199850, "gr"));
        inventario.add(new ItemStock("Caldo de res",           299650, "ml"));
        inventario.add(new ItemStock("Caldo de cocción",       199940, "ml"));
        inventario.add(new ItemStock("Horchata",               499500, "ml"));
        inventario.add(new ItemStock("Jamaica",                   510, "ml"));
    }

    // ── NOTIFICACIONES GLOBALES ───────────────────────────────────────────────
    public static class Notificacion {
        public final String tipo;   // "STOCK_BAJO", "PEDIDO_LISTO", "DELIVERY_OK"
        public final String mensaje;
        public final String hora;
        public boolean leida = false;

        public Notificacion(String tipo, String mensaje) {
            this.tipo = tipo;
            this.mensaje = mensaje;
            this.hora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }

    public static final ObservableList<Notificacion> notificaciones =
        FXCollections.observableArrayList();

    public static void agregarNotificacion(String tipo, String mensaje) {
        notificaciones.add(0, new Notificacion(tipo, mensaje));
    }

    // ── PEDIDOS EN COLA (compartido Cocina ↔ Delivery) ────────────────────────
    public static class PedidoCocina {
        public int id;
        public String plato;
        public String mesa;
        public String cliente;
        public double total;
        public int tiempoSegundos;      // countdown
        public final int tiempoInicial;
        public String estado;           // "EN PROCESO", "LISTO PARA ENTREGAR"
        public String metodoPago;
        public String direccion;
        public boolean esDelivery;
        public List<String> productos;

        public PedidoCocina(int id, String plato, String mesa, String cliente,
                            double total, int tiempoSeg, boolean esDelivery,
                            String direccion, String metodoPago, List<String> productos) {
            this.id = id; this.plato = plato; this.mesa = mesa;
            this.cliente = cliente; this.total = total;
            this.tiempoSegundos = tiempoSeg; this.tiempoInicial = tiempoSeg;
            this.estado = "EN PROCESO"; this.esDelivery = esDelivery;
            this.direccion = direccion; this.metodoPago = metodoPago;
            this.productos = productos != null ? productos : new ArrayList<>();
        }
    }

    public static final ObservableList<PedidoCocina> pedidosCocina =
        FXCollections.observableArrayList();

    // ── REPARTIDORES ─────────────────────────────────────────────────────────
    public static class Repartidor {
        public final String nombre;
        public boolean ocupado = false;
        public int tiempoRestanteSeg = 0;
        public String pedidoAsignado = "";

        public Repartidor(String nombre) { this.nombre = nombre; }
    }

    public static final List<Repartidor> repartidores = new ArrayList<>();
    static {
        repartidores.add(new Repartidor("Carlos"));
        repartidores.add(new Repartidor("Ana"));
        repartidores.add(new Repartidor("Luis"));
        repartidores.add(new Repartidor("Sofia"));
        repartidores.add(new Repartidor("Miguel"));
    }

    // ── HISTORIAL DE PEDIDOS FINALIZADOS ──────────────────────────────────────
    public static class PedidoFinalizado {
        public final String plato;
        public final String mesa;
        public final String cliente;
        public final String hora;

        public PedidoFinalizado(String plato, String mesa, String cliente) {
            this.plato = plato; this.mesa = mesa; this.cliente = cliente;
            this.hora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }

    public static final ObservableList<PedidoFinalizado> historialReciente =
        FXCollections.observableArrayList();

    // ── REDUCE INVENTARIO ─────────────────────────────────────────────────────
    /** Reduce stock de un ingrediente y dispara notificación si queda bajo */
    public static void reducirStock(String ingrediente, double cantidad) {
        for (ItemStock item : inventario) {
            if (item.nombre.trim().equalsIgnoreCase(ingrediente.trim())) {
                item.cantidad = Math.max(0, item.cantidad - cantidad);
                if (item.cantidad < STOCK_BAJO_UMBRAL) {
                    agregarNotificacion("STOCK_BAJO",
                        "⚠ Stock bajo: " + item.nombre +
                        " → " + String.format("%.0f", item.cantidad) + " " + item.unidad);
                    // Registrar en GestorArchivo
                    new com.dhery.GestorArchivo.ArchivoManager().agregarLinea(
                        "stock_alertas.txt",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                        " | STOCK BAJO | " + item.nombre + " | " + item.cantidad + " " + item.unidad
                    );
                }
                break;
            }
        }
    }

    // ── MAPA INGREDIENTES POR PRODUCTO ───────────────────────────────────────
    public static void descontarIngredientesPorProducto(String producto, int cantidad) {
        switch (producto.toUpperCase()) {
            case "BIRRIA":
                reducirStock("Tortilla de maíz",     cantidad * 2);
                reducirStock("Carne de birria",       cantidad * 150);
                reducirStock("Cebolla y cilantro",    cantidad * 30);
                break;
            case "QUESABIRRIA":
                reducirStock("Tortilla de harina",    cantidad * 2);
                reducirStock("Carne de birria",       cantidad * 130);
                reducirStock("Queso cheddar mozzarella", cantidad * 80);
                break;
            case "SUADERO":
                reducirStock("Tortilla de maíz",      cantidad * 2);
                reducirStock("Lengua de res",         cantidad * 120);
                reducirStock("Cebolla y cilantro",    cantidad * 20);
                break;
            case "PASTOR":
                reducirStock("Tortilla de maíz",      cantidad * 2);
                reducirStock("Carne de cerdo al pastor", cantidad * 120);
                reducirStock("Piña picada",            cantidad * 40);
                break;
            case "RAMEN BIRRIA":
                reducirStock("Fideo ramen (cocido)",   cantidad * 200);
                reducirStock("Caldo de res",           cantidad * 250);
                reducirStock("Carne de birria",        cantidad * 100);
                break;
            case "NACHOS SUPREMOS":
                reducirStock("Nachos",                 cantidad * 150);
                reducirStock("Queso cheddar líquido",  cantidad * 80);
                reducirStock("Guacamole",              cantidad * 50);
                break;
            case "MEGABURRITO":
                reducirStock("Tortilla de harina XL",  cantidad);
                reducirStock("Arroz cocido",            cantidad * 120);
                reducirStock("Carne chilli",            cantidad * 100);
                break;
            case "TORTILLA EXTRA":
                reducirStock("Tortilla de harina",      cantidad * 2);
                break;
            case "HORCHATA":
                reducirStock("Horchata",                cantidad * 300);
                break;
            case "JAMAICA":
                reducirStock("Jamaica",                 cantidad * 250);
                break;
            default:
                break;
        }
    }
}
