package io.warera.service;

import java.util.Map;

import io.warera.model.Item;

public class Profit{

    public static class ProfitResult{

        private final double gananciasPorHora;
        private final double gananciasPorPunto;

        public ProfitResult(double gananciasPorHora, double gananciasPorPunto) {
            this.gananciasPorHora = gananciasPorHora;
            this.gananciasPorPunto = gananciasPorPunto;
        }

        public double getGananciasPorHora() {
            return gananciasPorHora;
        }

        public double getGananciasPorPunto() {
            return gananciasPorPunto;
        }
    }

    public static ProfitResult calculate(Item item,Map<String, Item> items,Map<String, Double> finalPrices) {

        Double marketPrice = finalPrices.get(item.getName());
        double precioMercado = marketPrice != null ? marketPrice : 0.0;

        double produccionHora = item.getProduccionPorHora();
        double puntosProduccion = item.getPuntoProduccion();

        int materiaPrimaNecesaria = 0;
        double precioMateriaPrima = 0.0;

        if (item.getMateriaPrima() != null) {
            precioMateriaPrima = finalPrices.getOrDefault(
                    item.getMateriaPrima(), 0.0);
            materiaPrimaNecesaria = item.getCantidadMP();
        }

        double costoMateriaPrima = materiaPrimaNecesaria * precioMateriaPrima;
        double gananciaPorItem = precioMercado - costoMateriaPrima;
        double itemsPorHora = produccionHora / puntosProduccion;

        double gananciasPorHora = gananciaPorItem * itemsPorHora;
        double gananciasPorPunto = gananciaPorItem / puntosProduccion;

        return new ProfitResult(gananciasPorHora, gananciasPorPunto);
    }
}