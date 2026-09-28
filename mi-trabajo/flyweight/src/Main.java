import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Demo del patrón Flyweight: 5.000 buses en un mapa de monitoreo GPS.
 * Se compara la memoria usada creando un TipoBus por bus (sin Flyweight)
 * contra reutilizar solo 3 instancias compartidas (con Flyweight).
 */
public class Main {
    private static final int TOTAL_BUSES = 5_000;
    private static final String[][] MODELOS = {
            {"Mercedes-Benz", "O500", "42"},
            {"Volvo", "B8R", "46"},
            {"Scania", "K360", "40"}
    };

    /** Memoria usada en KB (tras forzar el recolector de basura). */
    private static long memoriaUsadaKB() {
        Runtime rt = Runtime.getRuntime();
        for (int i = 0; i < 3; i++) {
            System.gc();
        }
        return (rt.totalMemory() - rt.freeMemory()) / 1024;
    }

    public static void main(String[] args) {
        Pausa.activar(args);
        System.out.println("=== Patrón FLYWEIGHT: " + TOTAL_BUSES + " buses en el mapa de monitoreo ===\n");
        Random azar = new Random(42);

        // ---------- SIN Flyweight ----------
        long antes = memoriaUsadaKB();
        List<BusEnRuta> sinFlyweight = new ArrayList<>();
        for (int i = 0; i < TOTAL_BUSES; i++) {
            String[] m = MODELOS[i % 3];
            // una copia de TipoBus por cada bus
            TipoBus propio = new TipoBus(m[0], m[1], Integer.parseInt(m[2]));
            double lat = 4.6 + azar.nextDouble();
            double lon = -74.1 + azar.nextDouble();
            sinFlyweight.add(new BusEnRuta("BUS" + i, lat, lon, propio));
        }
        long memSin = memoriaUsadaKB() - antes;
        System.out.println("SIN Flyweight -> objetos TipoBus: " + TOTAL_BUSES
                + " | memoria aprox.: " + memSin / 1024 + " MB");
        Pausa.esperar();

        // liberar antes de la segunda medición
        sinFlyweight = null;
        long base = memoriaUsadaKB();

        // ---------- CON Flyweight ----------
        List<BusEnRuta> conFlyweight = new ArrayList<>();
        for (int i = 0; i < TOTAL_BUSES; i++) {
            String[] m = MODELOS[i % 3];
            // se reutiliza el TipoBus compartido
            TipoBus compartido = TipoBusFactory.obtener(
                    m[0], m[1], Integer.parseInt(m[2]));
            double lat = 4.6 + azar.nextDouble();
            double lon = -74.1 + azar.nextDouble();
            conFlyweight.add(new BusEnRuta("BUS" + i, lat, lon, compartido));
        }
        long memCon = memoriaUsadaKB() - base;
        System.out.println("CON Flyweight -> objetos TipoBus: " + TipoBusFactory.totalTiposCreados()
                + " | memoria aprox.: " + memCon + " KB");
        Pausa.esperar();

        System.out.println("\nEjemplos dibujados con Flyweight:");
        for (int i = 0; i < 3; i++) {
            System.out.println("  " + conFlyweight.get(i).dibujar());
        }
        System.out.println("\nConclusión: 5000 buses comparten solo "
                + TipoBusFactory.totalTiposCreados() + " objetos TipoBus.");
    }
}
