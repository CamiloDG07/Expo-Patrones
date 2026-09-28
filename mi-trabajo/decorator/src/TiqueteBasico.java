/**
 * Tiquete sin extras: ruta y tarifa base.
 */
public class TiqueteBasico implements Tiquete {

    private final String ruta;
    private final double tarifa;

    public TiqueteBasico(String ruta, double tarifa) {
        this.ruta = ruta;
        this.tarifa = tarifa;
    }

    @Override
    public String descripcion() {
        return "Tiquete " + ruta;
    }

    @Override
    public double costo() {
        return tarifa;
    }
}
