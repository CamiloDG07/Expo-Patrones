/**
 * Extra: refrigerio a bordo. Suma un valor fijo.
 */
public class RefrigerioABordo extends TiqueteDecorator {

    private static final double COSTO_REFRIGERIO = 8_000;

    public RefrigerioABordo(Tiquete envuelto) {
        super(envuelto);
    }

    @Override
    public String descripcion() {
        return super.descripcion() + " + Refrigerio a bordo";
    }

    @Override
    public double costo() {
        return super.costo() + COSTO_REFRIGERIO;
    }
}
