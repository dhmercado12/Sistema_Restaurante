package com.dhery.repositories;

import com.dhery.GestorArchivo.ArchivoManager;
import com.dhery.models.user;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final String RUTA_USUARIOS =
        "src/main/java/com/dhery/GestorArchivo/usuarios.txt";

    private final ArchivoManager archivoManager;

    public UserRepository() {
        this.archivoManager = new ArchivoManager();
    }

   public List<user> listarUsuarios() {

    List<user> usuarios = new ArrayList<>();

    List<String> lineas =
            archivoManager.leerLineas(
                    RUTA_USUARIOS
            );

    System.out.println("LINEAS LEIDAS: " + lineas.size());

    for (String linea : lineas) {

        System.out.println("LINEA -> " + linea);

        String[] datos = linea.split("\\|", -1);

        System.out.println(
                "CANTIDAD CAMPOS: "
                        + datos.length
        );

        user user = new user(
                Integer.parseInt(datos[0]),
                datos[1],
                datos[2],
                datos[3],
                datos[4],
                datos[5],
                datos[6]
        );

        usuarios.add(user);
    }

    return usuarios;
}

    public user login(
            String username,
            String password) {

        for (user user : listarUsuarios()) {

            if (user.getUsername()
                    .equalsIgnoreCase(username)
                    &&
                    user.getPassword()
                            .equals(password)) {

                return user;
            }
        }

        return null;
    }

    public boolean existeUsuario(
            String username) {

        for (user user : listarUsuarios()) {

            if (user.getUsername()
                    .equalsIgnoreCase(username)) {

                return true;
            }
        }

        return false;
    }

    public void guardarUsuario(
            user user) {

        archivoManager.agregarLinea(
                RUTA_USUARIOS,
                user.toString()
        );
    }

    public int generarNuevoId() {

    int maxId = 0;

    for (user user : listarUsuarios()) {

        if (user.getId() > maxId) {
            maxId = user.getId();
        }
    }

    return maxId + 1;
}
}