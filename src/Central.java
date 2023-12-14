public class Central {
    private int Tipo;
    private double Produccion;
    private int CoordX;
    private int CoordY;

    public Central(int tipo, double prod, int y, int x) {
        this.Tipo = tipo;
        this.Produccion = prod;
        this.CoordX = y;
        this.CoordY = x;
    }

    public int getCoordX() {
        return this.CoordX;
    }

    public void setCoordX(int x) {
        this.CoordX = x;
    }

    public int getCoordY() {
        return this.CoordY;
    }

    public void setCoordY(int y) {
        this.CoordY = y;
    }

    public int getTipo() {
        return this.Tipo;
    }

    public void setTipo(int tipo) {
        this.Tipo = tipo;
    }

    public double getProduccion() {
        return this.Produccion;
    }

    public void setProduccion(double prod) {
        this.Produccion = prod;
    }
}
