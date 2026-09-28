/**
 * Demo del patrón Proxy: el cliente usa la interfaz RepositorioReportes sin
 * saber si habla con el objeto real o con su proxy.
 */
public class Main {
    private static void medir(RepositorioReportes repo, String id) {
        long ini = System.currentTimeMillis();
        String r = repo.obtenerReporte(id);
        long ms = System.currentTimeMillis() - ini;
        System.out.println("   Resultado: " + r + " (" + ms + " ms)\n");
        Pausa.esperar();
    }

    public static void main(String[] args) {
        Pausa.activar(args);
        System.out.println("=== Patrón PROXY: acceso a reportes gerenciales ===\n");

        System.out.println("1) Usuario SIN permisos (proxy de protección)");
        // El proxy rechaza la petición antes de tocar el objeto real
        RepositorioReportes practicante = new ProxyReportes("Pedro", "PRACTICANTE");
        medir(practicante, "REP-2026-09");

        System.out.println("2) Con permisos, 1ª consulta (proxy virtual)");
        RepositorioReportes gerente = new ProxyReportes("Marta", "GERENTE");
        medir(gerente, "REP-2026-09");

        System.out.println("3) Misma consulta otra vez (proxy de caché)");
        medir(gerente, "REP-2026-09");

        System.out.println("4) Otro reporte (objeto real ya creado)");
        medir(gerente, "REP-2026-10");
    }
}
