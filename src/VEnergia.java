/**
* this class represents the Plant types, with their prices, costs and losses
*/



public class VEnergia {
    private static final double[][] precios = new double[][]{{400.0D, 300.0D, 50.0D}, {500.0D, 400.0D, 50.0D}, {600.0D, 500.0D, 50.0D}};
    private static final double[][] costes = new double[][]{{50.0D, 20000.0D, 15000.0D}, {80.0D, 10000.0D, 5000.0D}, {150.0D, 5000.0D, 1500.0D}};
    private static final double[][] perdida = new double[][]{{10.0D, 0.0D}, {25.0D, 0.1D}, {50.0D, 0.2D}, {75.0D, 0.4D}, {1000.0D, 0.6D}};

    public VEnergia() {
    }

    public static double getTarifaClienteGarantizada(int tipo) throws Exception {
        if (tipo >= 0 && tipo <= 2) {
            return precios[tipo][0];
        } else {
            throw new Exception("Tipo fuera de rango");
        }
    }

    public static double getTarifaClienteNoGarantizada(int tipo) throws Exception {
        if (tipo >= 0 && tipo <= 2) {
            return precios[tipo][1];
        } else {
            throw new Exception("Tipo fuera de rango");
        }
    }

    public static double getTarifaClientePenalizacion(int tipo) throws Exception {
        if (tipo >= 0 && tipo <= 2) {
            return precios[tipo][2];
        } else {
            throw new Exception("Tipo fuera de rango");
        }
    }

    public static double getCosteProduccionMW(int tipo) throws Exception {
        if (tipo >= 0 && tipo <= 2) {
            return costes[tipo][0];
        } else {
            throw new Exception("Tipo fuera de rango");
        }
    }

    public static double getCosteMarcha(int tipo) throws Exception {
        if (tipo >= 0 && tipo <= 2) {
            return costes[tipo][1];
        } else {
            throw new Exception("Tipo fuera de rango");
        }
    }

    public static double getCosteParada(int tipo) throws Exception {
        if (tipo >= 0 && tipo <= 2) {
            return costes[tipo][2];
        } else {
            throw new Exception("Tipo fuera de rango");
        }
    }

    public static double getPerdida(double dist) {
        int i;
        for(i = 0; dist > perdida[i][0]; ++i) {
        }

        return perdida[i][1];
    }







}
