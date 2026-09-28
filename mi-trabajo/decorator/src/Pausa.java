import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Utilidad para la demostración en vivo: con el argumento --pausas el programa
 * se detiene entre secciones hasta que se presione Enter. Sin el argumento no
 * cambia nada en la ejecución.
 */
public final class Pausa {
    private static boolean activa = false;

    private Pausa() {
    }

    public static void activar(String[] args) {
        for (String a : args) {
            if (a.equals("--pausas")) {
                activa = true;
            }
        }
    }

    public static void esperar() {
        if (!activa) {
            return;
        }
        System.out.print("   [Enter para continuar] ");
        try {
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            // se ignora: la demo continúa
        }
    }
}
