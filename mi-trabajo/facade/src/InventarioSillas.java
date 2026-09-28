import java.util.HashSet;
import java.util.Set;

/** Subsistema 1: controla qué sillas están libres en un viaje. */
public class InventarioSillas {
    private final Set<Integer> ocupadas = new HashSet<>();

    public boolean estaDisponible(int silla) {
        log("Consultando disponibilidad de la silla " + silla);
        return !ocupadas.contains(silla);
    }

    public void bloquear(int silla) {
        log("Bloqueando silla " + silla);
        ocupadas.add(silla);
    }

    public void liberar(int silla) {
        log("Liberando silla " + silla);
        ocupadas.remove(silla);
    }

    private void log(String mensaje) {
        System.out.println("  [Inventario] " + mensaje);
    }
}
