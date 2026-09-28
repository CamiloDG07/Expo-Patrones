/**
 * Flyweight: guarda el estado INTRÍNSECO (compartido e inmutable) de un
 * modelo de bus. El arreglo "modelo3D" simula un recurso pesado (textura,
 * ícono, ficha técnica) que no debe duplicarse por cada bus.
 */
public final class TipoBus {
    private final String marca;
    private final String modelo;
    private final int capacidad;
    private final byte[] modelo3D;

    public TipoBus(String marca, String modelo, int capacidad) {
        this.marca = marca;
        this.modelo = modelo;
        this.capacidad = capacidad;
        this.modelo3D = new byte[20 * 1024]; // ~20 KB de recurso
    }

    /** Recibe el estado EXTRÍNSECO (placa, posición) como parámetro. */
    public String dibujar(String placa, double lat, double lon) {
        return String.format(java.util.Locale.US,
                "Bus %s [%s %s, %d sillas] en (%.4f, %.4f)",
                placa, marca, modelo, capacidad, lat, lon);
    }

    /** Clave de identidad de un modelo, calculable SIN tener un TipoBus creado. */
    public static String clave(String marca, String modelo, int capacidad) {
        return marca + "|" + modelo + "|" + capacidad;
    }

    public String clave() {
        return clave(this.marca, this.modelo, this.capacidad);
    }
}
