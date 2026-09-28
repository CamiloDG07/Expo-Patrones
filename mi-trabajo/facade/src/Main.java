/**
 * Demo del patrón Facade: comparar la compra "a mano" (el cliente conoce todos
 * los subsistemas) con la compra a través de la fachada (una sola llamada).
 */
public class Main {
    public static void main(String[] args) {
        Pausa.activar(args);
        System.out.println("=== Patrón FACADE: compra de un tiquete de bus ===\n");

        // ---- SIN fachada: el cliente coordina todo ----
        System.out.println("### Sin Facade (el cliente conoce 4 subsistemas)");
        InventarioSillas inventario = new InventarioSillas();
        ServicioPago pago = new ServicioPago();
        ServicioFacturacion facturacion = new ServicioFacturacion();
        ServicioNotificaciones notificaciones = new ServicioNotificaciones();

        if (inventario.estaDisponible(12) && pago.validarMedioPago("Tarjeta")) {
            inventario.bloquear(12);
            String trx = pago.cobrar("Tarjeta", 85_000);
            String factura = facturacion.generarFactura("Ana Ruiz", 85_000, trx);
            notificaciones.enviarConfirmacion("ana@correo.com", factura, 12);
        }
        Pausa.esperar();

        // ---- CON fachada: una sola llamada ----
        System.out.println("\n### Con Facade (el cliente solo conoce la fachada)");
        VentaTiquetesFacade ventas = new VentaTiquetesFacade();
        ventas.comprarTiquete("Carlos Gómez", "carlos@correo.com",
                7, 85_000, "PSE");
        Pausa.esperar();

        // Caso de error: la misma silla ya vendida
        System.out.println("### Intento de comprar una silla ya ocupada");
        ventas.comprarTiquete("Luisa Mora", "luisa@correo.com",
                7, 85_000, "Tarjeta");
    }
}
