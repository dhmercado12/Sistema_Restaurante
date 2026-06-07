package com.dhery.models;

public class Suggestion {

    private String nombre;
    private String categoria;
    private String mensaje;
    private String fecha;

    public Suggestion(
            String nombre,
            String categoria,
            String mensaje,
            String fecha
    ) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getFecha() {
        return fecha;
    }
}
