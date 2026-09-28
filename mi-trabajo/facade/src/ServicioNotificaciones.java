/** Subsistema 4: envía la confirmación al pasajero (simulada). */
public class ServicioNotificaciones {
    public void enviarConfirmacion(String correo, String factura, int silla) {
        System.out.println("  [Notificaciones] Correo a " + correo
                + ": " + factura + ", silla " + silla);
    }
}
