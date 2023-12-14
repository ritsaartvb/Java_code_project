public class Cliente {
    private int Tipo;
    private int CoordX;
    private int CoordY;
    private double Consumo;
    private int Contrato;

    public Cliente(int tipo, double consumo, int contrato, int x, int y) {
        this.Tipo = tipo;
        this.Consumo = consumo;
        this.Contrato = contrato;
        this.CoordX = x;
        this.CoordY = y;
    }

    public int getTipo() {
        return this.Tipo;
    }

    public int getCoordX() {
        return this.CoordX;
    }

    public int getCoordY() {
        return this.CoordY;
    }

    public double getConsumo() {
        return this.Consumo;
    }

    public int getContrato() {
        return this.Contrato;
    }

    public void setTipo(int tipo) {
        this.Tipo = tipo;
    }

    public void setCoordX(int x) {
        this.CoordX = x;
    }

    public void setCoordY(int y) {
        this.CoordY = y;
    }

    public void setConsumo(double consumo) {
        this.Consumo = consumo;
    }

    public void setContrato(int contrato) {
        this.Contrato = contrato;
    }
}
