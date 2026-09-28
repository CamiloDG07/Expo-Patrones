/**
 * Fachada: ofrece UN método simple (comprarTiquete) que orquesta los cuatro
 * subsistemas en el orden correcto y maneja el caso de error. El cliente
 * no necesita conocer a InventarioSillas, ServicioPago, etc.
 */
public class VentaTiquetesFacade {
    private final InventarioSillas inventario = new InventarioSillas();
    private final ServicioPago pago = new ServicioPago();
    private final ServicioFacturacion facturacion = new ServicioFacturacion();
    private final ServicioNotificaciones notificaciones =
            new ServicioNotificaciones();

    public boolean comprarTiquete(String pasajero, String correo, int silla,
                                  double valor, String medioPago) {
        System.out.println("-> Fachada: compra de " + pasajero);

        if (!inventario.estaDisponible(silla)) {
            System.out.println("  Silla no disponible. Compra cancelada.");
            return false;
        }
        if (!pago.validarMedioPago(medioPago)) {
            System.out.println("  Medio de pago inválido. Compra cancelada.");
            return false;
        }
        inventario.bloquear(silla);
        String transaccion = pago.cobrar(medioPago, valor);
        String factura = facturacion.generarFactura(
                pasajero, valor, transaccion);
        notificaciones.enviarConfirmacion(correo, factura, silla);
        System.out.println("-> Fachada: compra exitosa\n");
        return true;
    }
}
