# Explicación línea por línea — Decorator (tiquetes de bus)

> Basado en las respuestas del grupo durante la sesión de tutoría. Revisen y ajusten
> la redacción con sus propias palabras antes de usarlo en la diapositiva; lo importante
> es que cualquiera de los tres pueda decir esto en voz alta sin leerlo.

## Tiquete.java

- **L5**: `public interface Tiquete` — es una interfaz y no una clase abstracta porque
  solo define un contrato (`descripcion()`, `costo()`); no tiene estado ni código
  compartido. Con interfaz, `TiqueteBasico` y `TiqueteDecorator` pueden implementarla
  sin depender de una jerarquía común obligatoria.
- **L8**: `String descripcion();` — cada tiquete (básico o decorado) debe poder decir
  qué incluye. Main y los decoradores solo conocen esta firma, nunca una implementación
  concreta.
- **L11**: `double costo();` — el costo se representa en `double`. Es una simplificación:
  para dinero real en pesos (sin decimales) lo correcto sería `long`, o `BigDecimal` si
  hubiera decimales, porque `double` es punto flotante binario y puede acumular errores
  de redondeo. Aquí los valores (1.200, 3.500, 85.000) son enteros exactos en `double`,
  así que no falla, pero es una decisión consciente de simplicidad.

## TiqueteBasico.java

- **L6-L7**: `private final String ruta; private final double tarifa;` — `final` hace
  que el tiquete básico sea **inmutable**: se asignan una sola vez en el constructor y
  no hay setters. Eso lo hace predecible y permite que los decoradores lo envuelvan sin
  riesgo de que cambie por debajo.
- **L9-L12**: el constructor no valida (por ejemplo, `tarifa > 0`). No es un error de
  diseño: el objetivo del ejercicio es mostrar el patrón, no la validación de entradas.
  En un sistema real sí se validaría (`IllegalArgumentException` si la tarifa es negativa
  o la ruta es nula/vacía).
- **L15-L17**: `descripcion()` devuelve `"Tiquete " + ruta` (ajustado para que la salida
  coincida palabra por palabra con `decorator/salida.txt` de la referencia). Si `ruta`
  fuera `null`, la concatenación imprimiría `"Tiquete null"` sin lanzar excepción — otro
  caso borde que no se controla a propósito.
- **L19-L22**: `costo()` devuelve `tarifa` tal cual, sin extras.

## TiqueteDecorator.java

- **L6**: `public abstract class TiqueteDecorator implements Tiquete` — implementa la
  interfaz (no extiende `TiqueteBasico`). Si extendiera `TiqueteBasico`, heredaría
  atributos que un decorador no necesita (`ruta`, `tarifa`), quedaría atado a una clase
  concreta y, al apilar varios decoradores, cada uno arrastraría datos duplicados. Al
  implementar la interfaz, un decorador puede envolver **cualquier** `Tiquete`, incluido
  otro decorador, y por eso se pueden apilar en cualquier cantidad y orden.
- **L8**: `protected final Tiquete envuelto;` — `protected` porque las subclases
  (`SeguroViaje`, etc.) necesitan acceder a lo heredado; `private` lo restringiría a esta
  clase. `final` porque el objeto envuelto no cambia después de construido.
- **L10-L12**: el constructor es `protected`: nadie fuera de la jerarquía debe crear un
  `TiqueteDecorator` directamente (además la clase es `abstract`, así que tampoco se
  podría instanciar).
- **L15-L16, L20-L21**: `descripcion()` y `costo()` delegan en `envuelto`. Si esta clase
  no fuera abstracta, sería un envoltorio "transparente" que no agrega nada — por eso
  debe ser `abstract`: obliga a que cada decorador concreto sume algo propio.

## SeguroViaje.java

- **L6**: `private static final double COSTO_SEGURO = 3_500;` — `static` porque el valor
  es una constante de la clase (3.500 para todos los seguros), una sola copia compartida
  en vez de un atributo por instancia como `tarifa`. `final` evita que se reasigne. Le da
  nombre a un número que si no sería "mágico" en el código.
- **L13-L15**: `descripcion()` usa `super.descripcion()`, no `envuelto.descripcion()`
  directamente. Ambas formas dan el mismo resultado aquí, pero `super` respeta la
  jerarquía: si `TiqueteDecorator` agregara lógica extra en el futuro (por ejemplo un
  log), `SeguroViaje` la heredaría automáticamente.
- **L18-L19**: `costo()` suma `COSTO_SEGURO` al resultado del padre. Si se aplicara dos
  veces (`new SeguroViaje(new SeguroViaje(t))`), el seguro se cobraría dos veces — es un
  uso válido del patrón (cada decorador es independiente), no un error del patrón; una
  regla de negocio que lo impida iría en otra capa (por ejemplo, en la venta).

## EquipajeExtra.java

- **L6, L8**: `COSTO_POR_KILO` es `static final` (regla de negocio fija, 1.200 por kilo);
  `kilos` es un atributo de instancia porque varía por tiquete (10 kg, 5 kg, ...). Fijo
  por clase, variable por objeto.
- **L8** (corrección aplicada): `kilos` es `int`, no `double`. Con `double`, la
  descripción imprimiría `"10.0 kg"` en vez de `"10 kg"` (Java concatena `Double.toString`
  al hacer `+` con un `String`). Los kilos son una cantidad entera del mundo real; el
  costo, en cambio, sigue en `double` porque es dinero.
- **L21-L22**: `costo()` suma `kilos * COSTO_POR_KILO` al costo heredado. Con 10 kg:
  10 × 1.200 = 12.000.

## RefrigerioABordo.java

- Misma estructura que `SeguroViaje.java`, con `COSTO_REFRIGERIO = 8_000`. El grupo
  decidió **no** generalizar las tres clases hoja en una sola clase parametrizable
  (`ExtraDeCostoFijo`) porque: (a) cada extra se lee de un vistazo, con nombre de dominio,
  bueno para la explicación línea por línea; (b) `EquipajeExtra` no encaja en "costo
  fijo" por los kilos, así que una clase genérica necesitaría condicionales; (c) agregar
  un extra nuevo sigue siendo "una clase pequeña más", que es justamente lo que
  demuestra el patrón (Abierto/Cerrado) frente a las 8 subclases que harían falta con
  herencia pura.

## Pausa.java

- **L10, L13**: `public final class Pausa` con constructor `private Pausa() {}` — es una
  clase utilitaria (todos sus métodos son `static`, como `Math` o `Collections`): nunca
  se instancia, y el constructor privado lo impide en tiempo de compilación. `final`
  porque no tiene sentido heredar de una clase que solo agrupa utilidades estáticas.
  **No es parte del patrón Decorator**, es solo una ayuda para la demo en vivo.
- **L25-L27**: `if (!activa) { return; }` — aquí está el único punto donde cambia el
  comportamiento: sin `--pausas`, `activa` queda en `false` y el método termina de
  inmediato; con `--pausas`, sigue hasta bloquear en `readLine()` esperando Enter.

## Main.java

- **L9**: `NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"))` — formatea
  los montos como pesos colombianos (`$ 108.500,00`).
- **L12**: `t.descripcion().split(" \\+ ")` — la responsabilidad de **formatear la
  salida** está en `Main` (la clase «control»/cliente), no en el dominio. `Tiquete` y
  los decoradores solo saben decir qué incluyen y cuánto cuestan; qué se hace con esa
  información (imprimirla bonita, guardarla, enviarla) lo decide quien la usa —
  responsabilidad única. Matiz honesto: este `split` depende de que todos los
  decoradores usen exactamente `" + "` como separador; es un acuerdo implícito de texto,
  suficiente para esta demo, pero en un sistema real sería más robusto que `Tiquete`
  devolviera una lista de componentes en vez de un `String`.
- **L27-L28**: caso 1, tiquete sin decorar. Resultado: $85.000,00.
- **L31-L32**: caso 2, un solo decorador. Resultado: $88.500,00.
- **L35-L38**: caso 3, tres decoradores apilados en una sola expresión (seguro + equipaje
  10 kg + refrigerio). Resultado: 85.000 + 3.500 + 12.000 + 8.000 = **$108.500,00**. La
  suma es conmutativa: el total no cambia según el orden de apilado, solo el orden en
  que aparecen los extras en la descripción.
- **L41-L44**: caso 4, `dinamico` (tipo `Tiquete`, la interfaz) se **reasigna** varias
  veces, envolviendo cada vez el objeto anterior sin modificarlo. Esto ilustra mejor que
  los casos 2 y 3 la idea de "decoración en tiempo de ejecución": los extras se agregan
  paso a paso, como si el cliente los fuera eligiendo después de emitido el tiquete
  básico. Resultado: 120.000 + 8.000 + 3.500 = **$131.500,00**.

## Verificación real (no inventada)

Compilado y ejecutado el 2026-09-27 desde `mi-trabajo/decorator/`:
`javac -encoding UTF-8 -d out (Get-ChildItem src\*.java).FullName` seguido de
`java "-Dstdout.encoding=UTF-8" -cp out Main`. Tras agregar el prefijo `"Tiquete "` en
`TiqueteBasico.descripcion()` (L16), la salida coincide **palabra por palabra** con
`decorator/salida.txt` de la referencia, incluidos los cuatro totales: $85.000,00 /
$88.500,00 / **$108.500,00** / **$131.500,00**.
