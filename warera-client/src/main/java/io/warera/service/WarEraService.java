package io.warera.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.warera.api.ApiClient;
import io.warera.io.csvConfigReader;
import io.warera.io.marketPriceCSV;
import io.warera.model.Item;

@Service
public class WarEraService {

    public Map<String, Double> profitPerHour() throws IOException, InterruptedException {
        return calculate().profitPerHour;
    }

    public Map<String, Double> profitPerPoint() throws IOException, InterruptedException {
        return calculate().profitPerPoint;
    }

    private Result calculate() throws IOException, InterruptedException {
        HashMap<String, Item> items = csvConfigReader.readCsv();
        HashMap<String, Double> prices = ApiClient.getMarketPrices(items.keySet());
        HashMap<String, Double> finalPrices = new HashMap<>();
        marketPriceCSV.writeResultsCsv(items, prices);
        finalPrices.putAll(marketPriceCSV.loadMarketPrices());

        for (var e : prices.entrySet()) {
            if (e.getValue() != null) {
                finalPrices.put(e.getKey(), e.getValue());
            }
        }
        Map<String, Double> perHour = new HashMap<>();
        Map<String, Double> perPoint = new HashMap<>();

        for (Item item : items.values()) {
            Profit.ProfitResult r = Profit.calculate(item, items, finalPrices);
            perHour.put(item.getName(), r.getGananciasPorHora());
            perPoint.put(item.getName(), r.getGananciasPorPunto());
        }
        return new Result(sort(perHour), sort(perPoint));
    }

    private Map<String, Double> sort(Map<String, Double> in) {
        Map<String, Double> out = new LinkedHashMap<>();
        in.entrySet().stream()
        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
        .forEach(e -> out.put(e.getKey(), e.getValue()));
        return out;
    }

    private static class Result {
        final Map<String, Double> profitPerHour;
        final Map<String, Double> profitPerPoint;

        Result(Map<String, Double> h, Map<String, Double> p) {
            this.profitPerHour = h;
            this.profitPerPoint = p;
        }
    }
}