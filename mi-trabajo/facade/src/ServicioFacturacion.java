/** Subsistema 3: genera la factura electrónica (simulada). */
public class ServicioFacturacion {
    private int consecutivo = 1000;

    public String generarFactura(String pasajero, double valor, String transaccion) {
        consecutivo++;
        System.out.println("  [Facturación] Factura FE-" + consecutivo + " para " + pasajero
                + " (ref. " + transaccion + ")");
        return "FE-" + consecutivo;
    }
}
