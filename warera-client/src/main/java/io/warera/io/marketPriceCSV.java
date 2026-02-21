package io.warera.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.warera.model.Item;

public class marketPriceCSV {
    public static void writeResultsCsv(HashMap<String, Item> config, HashMap<String, Double> prices) throws IOException {

            Path path = Paths.get("warera-client\\src\\main\\resources\\market_results.csv");

            List<String> lines = new ArrayList<>();

            if (Files.exists(path)) {
                lines = Files.readAllLines(path);
            } else {
                // crear encabezado si no existe
                lines.add("nombre,marketPrice");
            }

            Map<String, String> updated = new LinkedHashMap<>();

            // saltar header
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty())
                    continue;

                String[] parts = line.split(",", -1);

                String name = parts[0];
                String oldPrice = parts.length > 1 ? parts[1] : "";

                Double newPrice = prices.get(name);

                String priceStr;
                if (newPrice == null) {
                    priceStr = oldPrice;
                } else {
                    priceStr = String.format(Locale.US, "%.3f", newPrice);
                }

                updated.put(name, name + "," + priceStr);
            }

            // si hay items nuevos en config que no estén en el CSV, los agregas
            for (String item : config.keySet()) {
                if (!updated.containsKey(item)) {
                    Double p = prices.get(item);
                    String priceStr = (p == null) ? "" : String.format(Locale.US, "%.3f", p);
                    updated.put(item, item + "," + priceStr);
                }
            }

            Files.createDirectories(path.getParent());

            try (BufferedWriter bw = Files.newBufferedWriter(path)) {
                bw.write("nombre,marketPrice");
                bw.newLine();

                for (String row : updated.values()) {
                    bw.write(row);
                    bw.newLine();
                }
            }
        }

    public static HashMap<String, Double> loadMarketPrices() {
        HashMap<String, Double> existingPrices = new HashMap<>();
        java.io.File file = new java.io.File("warera-client\\src\\main\\resources\\market_results.csv");
        if (!file.exists())
            return existingPrices;
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                if (row.length >= 2) {
                    String name = row[0].trim();
                    String priceVal = row[1].trim();
                    if (!priceVal.equals("null")) {
                        existingPrices.put(name, Double.valueOf(priceVal));
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("No previous results found or error reading them.");
        }
        return existingPrices;
    }
}