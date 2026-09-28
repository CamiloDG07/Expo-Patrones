import java.util.HashMap;
import java.util.Map;

/**
 * Proxy: combina tres usos clásicos del patrón sobre el mismo objeto real:
 *  - Proxy de protección: valida el rol antes de permitir el acceso.
 *  - Proxy virtual: crea el objeto real solo cuando se necesita (lazy).
 *  - Proxy de caché: evita repetir consultas costosas.
 * Además registra cada acceso (logging).
 */
public class ProxyReportes implements RepositorioReportes {
    private final String usuario;
    private final String rol;
    private RepositorioReportesReal real; // se crea al necesitarlo
    private final Map<String, String> cache = new HashMap<>();

    public ProxyReportes(String usuario, String rol) {
        this.usuario = usuario;
        this.rol = rol;
    }

    @Override
    public String obtenerReporte(String idReporte) {
        System.out.println("[Proxy] " + usuario + " (" + rol + ") solicita "
                + idReporte);

        // Protección
        if (!rol.equals("GERENTE") && !rol.equals("AUDITOR")) {
            System.out.println("[Proxy] ACCESO DENEGADO para el rol " + rol);
            return null;
        }
        // Caché
        if (cache.containsKey(idReporte)) {
            System.out.println(
                    "[Proxy] Respuesta desde caché (sin tocar la BD)");
            return cache.get(idReporte);
        }
        // Creación perezosa del objeto real
        if (real == null) {
            real = new RepositorioReportesReal();
        }
        String resultado = real.obtenerReporte(idReporte);
        cache.put(idReporte, resultado);
        return resultado;
    }
}
