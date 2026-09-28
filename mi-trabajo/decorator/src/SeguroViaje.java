/**
 * Extra: seguro de viaje. Suma un valor fijo y agrega texto a la descripción.
 */
public class SeguroViaje extends TiqueteDecorator {

    private static final double COSTO_SEGURO = 3_500;

    public SeguroViaje(Tiquete envuelto) {
        super(envuelto);
    }

    @Override
    public String descripcion() {
        return super.descripcion() + " + Seguro de viaje";
    }

    @Override
    public double costo() {
        return super.costo() + COSTO_SEGURO;
    }
}
