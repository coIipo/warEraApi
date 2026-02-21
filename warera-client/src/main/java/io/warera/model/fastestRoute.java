package io.warera.model;

import java.io.IOException;
import java.util.HashMap;

import io.warera.io.csvConfigReader;
import io.warera.io.marketPriceCSV;
import io.warera.service.Profit;
//Item objetivo T
//Cantidad Q a alcanzar
//Ruta A: Fabricar T desde 0
//Ruta B: Fabricar lo mas profitable, vender y comprar Q*T

public class fastestRoute {
    public String fastest(HashMap<String, Double> marketPrices, HashMap<String, Item> items, String targetItem, int targetQuantity) {
    double mayorProfit = 0.0;
    double horasNecesariasA = 0.0;
    Item itemTarget = items.get(targetItem);
    double itemsPorHora = itemTarget.getProduccionPorHora() / itemTarget.getPuntoProduccion();
    
    // Ruta A
    double produccionReal = itemsPorHora;
    horasNecesariasA = targetQuantity / produccionReal;
    
    // Ruta B
    String itemMasProfit = null;

    for (String itemName : items.keySet()) {
        Profit.ProfitResult r =
                Profit.calculate(items.get(itemName), items, marketPrices);

        if (r.getGananciasPorHora() > mayorProfit) {
            mayorProfit = r.getGananciasPorHora();
            itemMasProfit = itemName;
        }
    }
    // monedas necesarias para comprar el target
    double monedasTotales = marketPrices.get(targetItem) * targetQuantity;

    // usar directamente la ganancia por hora del mejor item
    double horasNecesariasB = monedasTotales / mayorProfit;

    System.out.println("Ruta A: " + Math.round(horasNecesariasA)+" horas" +" | Ruta B: " + Math.round(horasNecesariasB)+" horas");
    System.out.println();
    if (horasNecesariasA < horasNecesariasB) {
        return "Ruta A: " + Math.round(horasNecesariasA) +" horas fabricando " + targetItem;
    } else {
        return "Ruta B: " + Math.round(horasNecesariasB) +" horas fabricando " + itemMasProfit +" y vendiendo para comprar " + targetItem;
    }
}
    public static void main(String[] args) throws IOException {
        fastestRoute fr = new fastestRoute();
        HashMap<String, Double> marketPrices = marketPriceCSV.loadMarketPrices();
        HashMap<String, Item> items = csvConfigReader.readCsv();
        System.out.println(fr.fastest(marketPrices, items, "concrete", 400));
    }
}