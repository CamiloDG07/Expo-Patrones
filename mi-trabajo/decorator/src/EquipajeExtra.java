/**
 * Extra: equipaje adicional, cobrado por kilo.
 */
public class EquipajeExtra extends TiqueteDecorator {

    private static final double COSTO_POR_KILO = 1_200;

    private final int kilos;

    public EquipajeExtra(Tiquete envuelto, int kilos) {
        super(envuelto);
        this.kilos = kilos;
    }

    @Override
    public String descripcion() {
        return super.descripcion() + " + Equipaje extra (" + kilos + " kg)";
    }

    @Override
    public double costo() {
        return super.costo() + (kilos * COSTO_POR_KILO);
    }
}
