/**
 * Contrato común para cualquier tiquete: básico o decorado con extras.
 * Main y el resto del sistema solo conocen esta interfaz, nunca las clases concretas.
 */
public interface Tiquete {

    /** Descripción acumulada de lo que incluye el tiquete (ruta + extras agregados). */
    String descripcion();

    /** Costo acumulado del tiquete (tarifa base + cada extra agregado). */
    double costo();
}
