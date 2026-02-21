package io.warera.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import io.warera.WarEraUpdated;
import io.warera.model.Item;

public class csvConfigReader {
    public static HashMap<String, Item> readCsv() throws IOException {
        InputStream in = WarEraUpdated.class.getClassLoader().getResourceAsStream("puntosProduccion.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line;
        br.readLine(); // header
        HashMap<String, Item> Items = new HashMap<>();
        line = br.readLine();
        while (line != null) {
            String[] row = line.split(",");
            String nombre = row[0].trim();
            int pp = Integer.parseInt(row[1].trim());
            double bp = Double.parseDouble(row[2].trim());
            String mp = (row.length > 3 && !row[3].trim().isEmpty()) ? row[3].trim() : null;
            int qty = (row.length > 4 && !row[4].trim().isEmpty()) ? Integer.parseInt(row[4].trim()) : 0;
            Items.put(nombre, new Item(nombre, pp, bp, mp, qty));
            line = br.readLine();
        }
        br.close();
        return Items;
    }
}