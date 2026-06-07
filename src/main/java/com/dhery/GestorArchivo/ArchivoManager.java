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

    
    /** Reescribe todo el archivo con las lineas dadas */
    public void reescribirLineas(String ruta, List<String> lineas) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, false))) {
            for (String l : lineas) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la primera linea cuyo primer campo (separado por |) coincide con id.
     * Reemplaza esa linea completa por nuevaLinea.
     */
    public void actualizarLineaPorId(String ruta, String id, String nuevaLinea) {
        List<String> lineas = leerLineas(ruta);
        for (int i = 0; i < lineas.size(); i++) {
            String[] partes = lineas.get(i).split("\\|");
            if (partes.length > 0 && partes[0].trim().equals(id)) {
                lineas.set(i, nuevaLinea);
                break;
            }
        }
        reescribirLineas(ruta, lineas);
    }


}