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

    private static final Path FILE = Paths.get("market_results.csv");

    public static void writeResultsCsv(HashMap<String, Item> config,
                                       HashMap<String, Double> prices) throws IOException {

        System.out.println("CSV PATH = " + FILE.toAbsolutePath());

        List<String> lines = new ArrayList<>();

        if (Files.exists(FILE)) {
            lines = Files.readAllLines(FILE);
        } else {
            lines.add("nombre,marketPrice");
        }

        Map<String, String> updated = new LinkedHashMap<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

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

        for (String item : config.keySet()) {
            if (!updated.containsKey(item)) {
                Double p = prices.get(item);
                String priceStr = (p == null) ? "" : String.format(Locale.US, "%.3f", p);
                updated.put(item, item + "," + priceStr);
            }
        }

        if (FILE.getParent() != null) {
            Files.createDirectories(FILE.getParent());
        }

        try (BufferedWriter bw = Files.newBufferedWriter(FILE)) {
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

        if (!Files.exists(FILE)) {
            return existingPrices;
        }

        try (BufferedReader br = Files.newBufferedReader(FILE)) {

            br.readLine(); // header

            String line;
            while ((line = br.readLine()) != null) {

                String[] row = line.split(",");

                if (row.length >= 2) {
                    String name = row[0].trim();
                    String priceVal = row[1].trim();

                    if (!priceVal.isEmpty()) {
                        existingPrices.put(name, Double.parseDouble(priceVal));
                    }
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading market_results.csv");
        }

        return existingPrices;
    }
}
