package io.warera.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import io.warera.model.Item;

public class csvConfigReader {
    public static HashMap<String, Item> readCsv() throws IOException {
        InputStream in = csvConfigReader.class.getClassLoader().getResourceAsStream("puntosProduccion.csv");
        if (in == null) {
            throw new IllegalStateException(
                    "No se pudo encontrar puntosProduccion.csv en src/main/resources"
            );
        }
        HashMap<String, Item> items = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            // saltar header
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] row = line.split(",");
                String nombre = row[0].trim();
                int pp = Integer.parseInt(row[1].trim());
                double bp = Double.parseDouble(row[2].trim());
                String mp = (row.length > 3 && !row[3].trim().isEmpty()) ? row[3].trim(): null;
                int qty = (row.length > 4 && !row[4].trim().isEmpty()) ? Integer.parseInt(row[4].trim()): 0;
                items.put(nombre, new Item(nombre, pp, bp, mp, qty));
            }
        }
        return items;
    }
}
