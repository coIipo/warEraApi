package io.warera.model;
public class Item{
        String name;
        int puntoProduccion;
        double bonusProduccion;
        String materiaPrima;
        int cantidadMP;

        public Item(String n, int pP, double bP, String mP, int cMP) {
            this.name = n;
            this.puntoProduccion = pP;
            this.bonusProduccion = bP;
            this.materiaPrima = mP;
            this.cantidadMP = cMP;
        }

        public String getName() {
            return name;
        }

        public int getPuntoProduccion() {
            return puntoProduccion;
        }
        public Item getItemMateriaPrima(){
            if(materiaPrima==null){
                return null;
            }
            return new Item(materiaPrima, 0, 0, null, 0);
        }
        public double getBonusProduccion() {
            return bonusProduccion;
        }

        public String getMateriaPrima() {
            return materiaPrima;
        }

        public int getCantidadMP() {
            return cantidadMP;
        }

        public double getProduccionPorHora() {
            double bonusProduccionDecimal = bonusProduccion / 100.0;
            bonusProduccionDecimal = bonusProduccionDecimal + 1;
            return 4 * bonusProduccionDecimal;
        }
    }