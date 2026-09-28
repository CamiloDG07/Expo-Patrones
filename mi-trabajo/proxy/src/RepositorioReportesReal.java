/** Sujeto real: consulta costosa (simulada con una espera de 1 segundo). */
public class RepositorioReportesReal implements RepositorioReportes {
    public RepositorioReportesReal() {
        System.out.println(
                "  [Real] Abriendo conexión con la BD (costoso)...");
        dormir(800);
    }

    @Override
    public String obtenerReporte(String idReporte) {
        System.out.println(
                "  [Real] Consultando " + idReporte + " en la BD...");
        dormir(1000);
        return "Contenido confidencial de " + idReporte;
    }

    private static void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
