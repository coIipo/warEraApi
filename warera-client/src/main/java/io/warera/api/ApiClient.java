package io.warera.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ApiClient {

    public static HashMap<String, Double> getMarketPrices(Set<String> wantedItems) throws IOException {
        String apiKey = System.getenv("WARERA_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("WARERA_API_KEY not defined");
        }
        HashMap<String, Double> prices = new HashMap<>();
        for (String k : wantedItems) prices.put(k, null);
        String cursor = null;
        int pagesProcessed = 0;
        int maxPages = 30;
        while (true) {
            boolean allFound = true;
            for (Double p : prices.values()) {
                if (p == null) {
                    allFound = false;
                    break;
                }
            }
            if (allFound || pagesProcessed >= maxPages) break;
            String input = (cursor == null) ? "{}" : "{\"cursor\":\"" + cursor + "\"}";
            String urlStr = "https://api2.warera.io/trpc/transaction.getPaginatedTransactions?input=" + URLEncoder.encode(input, "UTF-8");
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("x-api-key", apiKey);
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
            }
            JsonObject root = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject data = root.getAsJsonObject("result").getAsJsonObject("data");
            JsonArray items = data.getAsJsonArray("items");
            for (JsonElement e : items) {
                JsonObject t = e.getAsJsonObject();
                if (!t.has("transactionType")) continue;
                if (!"trading".equals(t.get("transactionType").getAsString())) continue;
                if (!t.has("itemCode")) continue;
                String item = t.get("itemCode").getAsString();
                if (!prices.containsKey(item)) continue;
                if (prices.get(item) != null) continue;
                int quantity = t.get("quantity").getAsInt();
                if (quantity <= 0) continue;
                double money = t.get("money").getAsDouble();
                double unitPrice = money / quantity;
                prices.put(item, unitPrice);
            }
            if (!data.has("nextCursor") || data.get("nextCursor").isJsonNull()) break;
            cursor = data.get("nextCursor").getAsString();
            pagesProcessed++;
        }
        return prices;
    }
}
