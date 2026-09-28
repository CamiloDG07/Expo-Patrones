/**
 * Contexto: guarda el estado EXTRÍNSECO (único de cada bus) y una referencia
 * al flyweight compartido.
 */
public class BusEnRuta {
    private final String placa;
    private double latitud;
    private double longitud;
    private final TipoBus tipo; // compartido

    public BusEnRuta(String placa, double latitud, double longitud,
                     TipoBus tipo) {
        this.placa = placa;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tipo = tipo;
    }

    public void mover(double lat, double lon) {
        this.latitud = lat;
        this.longitud = lon;
    }

    public String dibujar() {
        return tipo.dibujar(placa, latitud, longitud);
    }
}
