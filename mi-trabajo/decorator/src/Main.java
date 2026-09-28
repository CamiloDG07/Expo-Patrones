import java.text.NumberFormat;
import java.util.Locale;

/**
 * Demo del patrón Decorator: los servicios adicionales se apilan en tiempo
 * de ejecución sin crear una subclase por cada combinación posible.
 */
public class Main {
    private static final NumberFormat COP = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

    private static void imprimir(Tiquete t) {
        String[] partes = t.descripcion().split(" \\+ ");
        System.out.println(partes[0]);
        for (int i = 1; i < partes.length; i++) {
            System.out.println("   + " + partes[i]);
        }
        System.out.println("   Total: " + COP.format(t.costo()));
        System.out.println();
        Pausa.esperar();
    }

    public static void main(String[] args) {
        Pausa.activar(args);
        System.out.println("=== Patrón DECORATOR: tiquetes de bus con servicios adicionales ===\n");

        // 1) Tiquete sin decorar
        Tiquete basico = new TiqueteBasico("Bogotá - Bucaramanga", 85_000);
        imprimir(basico);

        // 2) Un decorador
        Tiquete conSeguro = new SeguroViaje(new TiqueteBasico("Bogotá - Bucaramanga", 85_000));
        imprimir(conSeguro);

        // 3) Varios decoradores apilados (el orden de envoltura no altera el total)
        Tiquete base = new TiqueteBasico("Bogotá - Bucaramanga", 85_000);
        Tiquete completo = new RefrigerioABordo(
                new EquipajeExtra(new SeguroViaje(base), 10));
        imprimir(completo);

        // 4) Decoración dinámica: el cliente agrega un servicio "después"
        Tiquete dinamico = new TiqueteBasico("Bogotá - Cúcuta", 120_000);
        dinamico = new RefrigerioABordo(dinamico);
        dinamico = new SeguroViaje(dinamico);
        imprimir(dinamico);

        System.out.println("Sin Decorator: 2^3 = 8 subclases para cubrir todo.");
        System.out.println("Con Decorator: solo 3 clases decoradoras.");
    }
}
