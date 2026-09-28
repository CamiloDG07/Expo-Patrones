/**
 * Decorador base: envuelve un Tiquete y delega en él.
 * Las clases concretas (SeguroViaje, EquipajeExtra, RefrigerioABordo) heredan de aquí
 * y solo agregan su parte al costo y a la descripción.
 */
public abstract class TiqueteDecorator implements Tiquete {

    protected final Tiquete envuelto;

    protected TiqueteDecorator(Tiquete envuelto) {
        this.envuelto = envuelto;
    }

    @Override
    public String descripcion() {
        return envuelto.descripcion();
    }

    @Override
    public double costo() {
        return envuelto.costo();
    }
}
