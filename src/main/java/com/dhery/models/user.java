package com.dhery.models;

public class user {

    private int id;
    private String username;
    private String password;
    private String rol;
    private String apellidos;
    private String telefono;
    private String direccion;

    public user(
            int id,
            String username,
            String password,
            String rol,
            String apellidos,
            String telefono,
            String direccion) {

        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRol() {
        return rol;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }
     // SETTERS

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }


    @Override
    public String toString() {

        return id + "|" +
                username + "|" +
                password + "|" +
                rol + "|" +
                apellidos + "|" +
                telefono + "|" +
                direccion;
    }
}