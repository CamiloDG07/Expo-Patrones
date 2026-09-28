/** Subsistema 2: procesa el cobro (simulado). */
public class ServicioPago {
    public boolean validarMedioPago(String medio) {
        System.out.println("  [Pago] Validando medio de pago: " + medio);
        return medio != null && !medio.isBlank();
    }

    public String cobrar(String medio, double valor) {
        System.out.println("  [Pago] Cobrando $" + (long) valor + " con " + medio);
        return "TRX-" + Math.abs((medio + valor).hashCode() % 100000);
    }
}
