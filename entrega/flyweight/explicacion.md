# Explicación línea por línea — Flyweight (buses en el mapa)

> Basado en las respuestas del grupo (Santiago) durante la sesión de tutoría. Revisen y
> ajusten la redacción con sus propias palabras antes de usarlo en la diapositiva.

## TipoBus.java

- **L6**: `public final class TipoBus` — clase `final` (no se puede heredar) y todos sus
  atributos `final`: la inmutabilidad total es necesaria porque este objeto es
  **compartido** (referenciado por miles de `BusEnRuta` al mismo tiempo). Si un atributo
  no fuera `final` y cambiara a través de un bus, ese cambio se vería reflejado
  instantáneamente en todos los demás buses que comparten ese `TipoBus`, sin que les
  correspondiera.
- **L16**: `this.modelo3D = new byte[20 * 1024];` — simula un recurso pesado (icono,
  modelo 3D, ficha técnica) con ~20 KB de bytes vacíos. Para el experimento de memoria
  solo importa el **tamaño** del arreglo, no su contenido: basta con reservar el bloque
  para demostrar cuánta memoria se ahorraría si esto fuera un recurso real.
- **L20-L24**: `dibujar(placa, lat, lon)` recibe el estado **extrínseco** (placa,
  posición) como parámetros del método, en vez de guardarlo como atributo. Eso es lo que
  permite que un mismo objeto `TipoBus` "dibuje" cualquier bus, sin importar cuál —
  quien llama trae esos datos en el momento. Se usa `Locale.US` en el `String.format`
  para que el separador decimal sea siempre punto (`4.7207`), sin importar la
  configuración regional del computador donde se ejecute — importante porque el día de
  la exposición puede ser una máquina distinta a la que usaron para desarrollar, y sin
  esto la salida podría mostrar coma decimal y no coincidir con `salida.txt`.
- **L27-L29, L31-L33**: `clave(marca, modelo, capacidad)` es `static` — se puede calcular
  **sin tener un objeto `TipoBus` ya construido**, que es justo lo que necesita la
  fábrica para decidir si crea uno nuevo o reutiliza uno existente. La versión de
  instancia `clave()` (L31-L33) delega en la `static`, para poder llamarla también sobre
  un `TipoBus` ya creado sin repetir la fórmula. Evita que la fórmula de identidad de un
  modelo (qué campos lo definen) viva en dos lugares distintos (`TipoBus` y
  `TipoBusFactory`): si mañana cambia qué campos identifican un modelo único, solo se
  toca aquí.

## BusEnRuta.java

- **L6-L9**: `placa` y `tipo` son `final` (no cambian durante la vida del bus en la
  demo); `latitud`/`longitud` **no** son `final` porque un bus real se mueve
  constantemente.
- **L19-L22**: `mover(lat, lon)` reasigna la posición. No se usa en la demo actual, pero
  se deja en la clase porque `BusEnRuta` representa un bus real, que debería poder
  actualizar su posición (por ejemplo, al recibir una lectura de GPS) — es coherente con
  que la posición no sea `final`.
- **L24-L26**: `dibujar()` (sin parámetros) reúne los datos propios (`placa`, `latitud`,
  `longitud`) y se los pasa al flyweight compartido (`tipo.dibujar(...)`). Encapsula el
  detalle de cómo se arma esa llamada: quien usa `BusEnRuta` no necesita saber que existe
  un `TipoBus` detrás ni qué parámetros recibe.
- Con 5.000 `BusEnRuta` del mismo modelo: **1 solo** objeto `TipoBus` en memoria,
  compartido por los 5.000 (agregación, no composición: `BusEnRuta` no es dueño del
  ciclo de vida de `tipo`), y **5.000** objetos `BusEnRuta` distintos, uno por bus físico.

## TipoBusFactory.java

- **L6**: `private static final Map<String, TipoBus> CACHE` — `static` porque debe
  existir **un único caché compartido por toda la aplicación**. Si la fábrica no fuera
  estática y cada parte del código tuviera su propia instancia, cada una mantendría su
  propio mapa y se duplicarían `TipoBus` para el mismo modelo, rompiendo el ahorro de
  memoria que es el objetivo del patrón.
- **L10-L12**: `obtener(...)` calcula la clave con `TipoBus.clave(...)` (sin repetir la
  fórmula) y usa `CACHE.computeIfAbsent(clave, k -> new TipoBus(...))`: si la clave ya
  existe, **no** ejecuta el lambda y devuelve el objeto ya guardado; si no existe, sí lo
  ejecuta, crea el `TipoBus`, lo guarda y lo devuelve. Con 5.000 llamadas y solo 3 claves
  distintas, `new TipoBus(...)` se ejecuta exactamente **3 veces**.
- **L15-L17**: `totalTiposCreados()` devuelve `CACHE.size()` — el número real de modelos
  creados hasta el momento, medido del estado del programa. Se prefiere sobre escribir un
  `3` fijo porque ese número deja de ser una suposición: si cambiara la cantidad de
  modelos en `MODELOS`, este valor seguiría siendo correcto sin tocar el código.

## Pausa.java

- Idéntica a la de Decorator y Facade: clase utilitaria (`final`, constructor privado,
  todos los métodos `static`, como `Math` o `Collections`). No forma parte del patrón
  Flyweight — solo controla las pausas de la demo en vivo.

## Main.java

- **L11-L16**: `TOTAL_BUSES = 5_000` y `MODELOS` (solo 3 modelos reales) — la base del
  experimento: muchos buses, pocos modelos.
- **L19-L25**: `memoriaUsadaKB()` llama `System.gc()` tres veces antes de medir, para
  partir de un estado lo más "limpio" posible (sin basura de pasos anteriores todavía
  sin recolectar). `System.gc()` es solo una **sugerencia** a la JVM, no una garantía —
  por eso la medición sigue siendo aproximada, como advierte la guía.
- **L30**: `Random azar = new Random(42)` — semilla fija, no `new Random()`. Con la misma
  semilla, la secuencia de números pseudoaleatorios es siempre idéntica en cualquier
  ejecución, lo que hace que las coordenadas de los buses sean reproducibles y
  comparables contra `salida.txt`.
- **L35-L42** (bloque "sin Flyweight"): `new TipoBus(...)` dentro del bucle — una copia
  por cada uno de los 5.000 buses.
- **L49-L50**: `sinFlyweight = null;` antes de medir `base` — rompe la referencia a los
  5.000 objetos anteriores para que sean alcanzables por el recolector de basura; sin
  esto, la segunda medición arrancaría con la memoria del primer experimento todavía
  ocupada, contaminando la comparación.
- **L54-L61** (bloque "con Flyweight"): se sigue usando el **mismo objeto** `azar` (no
  uno nuevo), para continuar la secuencia pseudoaleatoria donde quedó tras el primer
  bucle — así las coordenadas de este bloque también son reproducibles. Se llama
  `TipoBusFactory.obtener(...)` en cada vuelta (5.000 veces), pero eso produce solo 3
  creaciones reales de `TipoBus` (ver `TipoBusFactory.java`, L10-L12).
- **L64-L73**: se imprime `TipoBusFactory.totalTiposCreados()` (medido, no un `3` fijo) y
  se dibujan los 3 primeros buses con `conFlyweight.get(i).dibujar()`, mostrando marca,
  modelo, capacidad y posición combinadas desde dos objetos distintos (`TipoBus` +
  `BusEnRuta`).

## Verificación real (no inventada)

Compilado y ejecutado el 2026-09-27 desde `mi-trabajo/flyweight/` con
`javac -encoding UTF-8 -d out (Get-ChildItem src\*.java).FullName` y
`java "-Dstdout.encoding=UTF-8" -cp out Main`. Coincide con `flyweight/salida.txt`:
`objetos TipoBus: 5000` / `3`, las 3 líneas dibujadas con las mismas coordenadas
(tras forzar `Locale.US` en `TipoBus.dibujar()` para el punto decimal), y la conclusión
final. La memoria en KB del bloque "con Flyweight" difiere del valor de referencia
(530 KB aquí vs. 567 KB en la referencia) — es la variación esperada y documentada por
la guía; lo que sí es idéntico y es el argumento válido es la relación 5.000 objetos
(sin el patrón) contra 3 (con el patrón).
