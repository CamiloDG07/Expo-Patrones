import java.util.HashMap;
import java.util.Map;

/** Fábrica de flyweights: reutiliza instancias ya creadas mediante un caché. */
public class TipoBusFactory {
    private static final Map<String, TipoBus> CACHE = new HashMap<>();

    public static TipoBus obtener(String marca, String modelo,
                                  int capacidad) {
        String clave = TipoBus.clave(marca, modelo, capacidad);
        return CACHE.computeIfAbsent(clave,
                k -> new TipoBus(marca, modelo, capacidad));
    }

    public static int totalTiposCreados() {
        return CACHE.size();
    }
}
