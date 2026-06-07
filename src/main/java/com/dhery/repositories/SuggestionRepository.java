package com.dhery.repositories;

import com.dhery.models.Suggestion;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SuggestionRepository {

    private static final String FILE =
            "sugerencias.txt";

    public static void guardar(Suggestion s) {

        try (BufferedWriter bw =
                     new BufferedWriter(
                             new FileWriter(FILE, true))) {

            bw.write(
                    s.getNombre() + ";" +
                    s.getCategoria() + ";" +
                    s.getMensaje() + ";" +
                    s.getFecha()
            );

            bw.newLine();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Suggestion> obtenerTodas() {

        List<Suggestion> lista =
                new ArrayList<>();

        File file = new File(FILE);

        if (!file.exists()) {
            return lista;
        }

        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(FILE))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data =
                        line.split(";");

                if (data.length >= 4) {

                    lista.add(
                            new Suggestion(
                                    data[0],
                                    data[1],
                                    data[2],
                                    data[3]
                            )
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}