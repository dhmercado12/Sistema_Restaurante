package com.dhery.GestorArchivo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoManager {

    public List<String> leerLineas(String ruta) {

        List<String> lineas = new ArrayList<>();

        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(ruta))) {

            String linea;

            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lineas;
    }

    public void agregarLinea(
            String ruta,
            String contenido) {

        try (BufferedWriter bw =
                     new BufferedWriter(
                             new FileWriter(ruta, true))) {

            bw.write(contenido);
            bw.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}