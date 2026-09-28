# Explicación línea por línea — Facade (venta de tiquetes de bus)

> Basado en las respuestas del grupo durante la sesión de tutoría. Revisen y ajusten
> la redacción con sus propias palabras antes de usarlo en la diapositiva.

## InventarioSillas.java

- **L6**: `private final Set<Integer> ocupadas = new HashSet<>();` — se usa `Set` y no
  `List` porque una silla o está ocupada o no está: no tiene sentido que se repita, y
  `HashSet.contains()` es casi constante en tiempo. Con una `List`, bloquear la misma
  silla dos veces la agregaría dos veces, y peor: `List<Integer>.remove(silla)` con un
  `int` autoboxeado quita el elemento en esa **posición** (índice), no el valor con ese
  número — un bug muy difícil de detectar. Con `Set`, `remove(silla)` sí elimina el
  valor correcto.
- **L8-L11**: `estaDisponible(silla)` registra la consulta (`log(...)`) y devuelve si
  la silla **no** está en `ocupadas`. Este `log` se ejecuta siempre, incluso cuando la
  respuesta va a ser `false` — por eso el mensaje "Consultando disponibilidad..." aparece
  incluso en el caso de error.
- **L13-L16, L18-L21**: `bloquear`/`liberar` agregan o quitan la silla del conjunto. El
  estado vive en este atributo de instancia, por lo que persiste entre llamadas mientras
  el mismo objeto `InventarioSillas` siga vivo.

## ServicioPago.java

- **L4-L6**: `validarMedioPago` acepta cualquier texto no nulo ni vacío (`isBlank()`);
  no valida que sea un medio real (no hay lista de medios permitidos) porque el objetivo
  es simular, no implementar reglas de negocio reales.
- **L8-L10**: `cobrar` imprime el valor convertido a `long` (para no mostrar decimales
  en el mensaje) y genera un código `TRX-...` con `Math.abs((medio + valor).hashCode() %
  100000)`. Es determinista: `String.hashCode()` siempre da el mismo número para el
  mismo texto, así que el mismo par (medio, valor) produce siempre el mismo `TRX-...` —
  por eso la salida es reproducible y comparable con `salida.txt`. Es solo una
  simulación: dos pares distintos podrían colisionar en el mismo código.

## ServicioFacturacion.java

- **L3**: `private int consecutivo = 1000;` — atributo de instancia, no `static`. Cada
  objeto `ServicioFacturacion` lleva su propia numeración desde 1000. Por eso, en la
  salida completa, **`FE-1001` aparece dos veces**: una en la compra "sin fachada" (Ana
  Ruiz, con su propia instancia creada en `Main`) y otra en la primera compra "con
  fachada" (Carlos Gómez, con la instancia que vive dentro de `VentaTiquetesFacade`).
  No es un error del patrón, es una consecuencia de que son dos objetos distintos; en un
  sistema real el consecutivo sería único y compartido (por ejemplo, una secuencia en
  base de datos).

## ServicioNotificaciones.java

- Simula el envío de un correo con un solo `println`; no tiene estado ni lógica.

## VentaTiquetesFacade.java

- **L7-L11**: los cuatro subsistemas se crean como atributos `private final`
  inicializados directamente en la declaración, no en un constructor explícito. Es
  equivalente a crearlos dentro de un constructor `VentaTiquetesFacade()`: Java ejecuta
  esa asignación al construir el objeto de todas formas. Se prefiere así por ser más
  corto, ya que ninguno depende de un parámetro externo.
- **L15-L24**: el orden importa. Primero se validan las dos condiciones que pueden hacer
  fallar la compra (`estaDisponible`, `validarMedioPago`) con `return false` inmediato si
  fallan, **antes** de tocar `bloquear(silla)`. Si se bloqueara la silla antes de validar
  el pago y el pago fallara, la silla quedaría atascada como ocupada sin que se
  completara ninguna compra real — el mismo principio de "validar antes de actuar" que
  se usa en cualquier transacción.
- **Cuándo esta fachada dejaría de ser una fachada simple**: si empezara a acumular
  lógica de negocio propia (calcular descuentos, reglas de fidelidad, reintentos de
  pago, formateo de mensajes) en vez de solo coordinar llamadas en orden a los
  subsistemas. La señal de alerta es lógica condicional de negocio dentro de la fachada,
  no solo el número de líneas.

## Main.java

- **L12-L15**: se crean los 4 subsistemas directamente — este bloque (`L17-L22`)
  demuestra la "explosión de acoplamiento": el cliente conoce 4 clases y el orden exacto
  entre ellas.
- **L17**: `if (inventario.estaDisponible(12) && pago.validarMedioPago("Tarjeta"))` usa
  cortocircuito (`&&`): si la silla no estuviera disponible, `validarMedioPago` ni
  siquiera se ejecutaría, y todo el bloque (cobrar, facturar, notificar) se saltaría.
- **Riesgo real de este bloque manual**: si alguien en otro canal escribe la misma
  secuencia pero en distinto orden (por ejemplo cobra antes de bloquear), dos canales
  podrían vender la misma silla, o cobrarle a alguien por una silla que ya no está
  disponible.
- **L26-L29**: con la fachada, la misma operación se reduce a crear `VentaTiquetesFacade`
  y llamar `comprarTiquete(...)` — 2 líneas contra las 6 del bloque manual. El contraste
  de líneas es solo la consecuencia visible; la razón real es el bajo acoplamiento (el
  cliente no conoce los subsistemas ni su orden), el manejo de errores centralizado en un
  solo lugar, y que agregar un paso nuevo (ej. puntos de fidelidad) solo toca la fachada.
- **L28, L34**: ambas llamadas usan la silla **7**. La primera (Carlos Gómez) la bloquea;
  la segunda (Luisa Mora) falla porque `InventarioSillas` es la misma instancia dentro de
  la fachada durante toda la ejecución, así que recuerda que la silla 7 ya está ocupada.
  El mensaje `[Inventario] Consultando disponibilidad de la silla 7` sí se imprime antes
  de fallar, porque ese `log` está dentro de `estaDisponible()`, que se ejecuta siempre
  que se llama, independientemente del resultado.

## Verificación real (no inventada)

Compilado y ejecutado el 2026-09-27 desde `mi-trabajo/facade/` con
`javac -encoding UTF-8 -d out (Get-ChildItem src\*.java).FullName` y
`java "-Dstdout.encoding=UTF-8" -cp out Main`. La salida coincide, línea por línea, con
`facade/salida.txt` de la referencia: bloque "sin Facade" con la silla 12 (Ana Ruiz),
bloque "con Facade" con la silla 7 (Carlos Gómez, éxito), y el intento fallido sobre la
misma silla 7 (Luisa Mora, "Silla no disponible. Compra cancelada.").
