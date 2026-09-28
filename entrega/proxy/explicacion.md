# Explicación línea por línea — Proxy (acceso a reportes gerenciales)

> Basado en las respuestas del grupo (Nicolás) durante la sesión de tutoría. Revisen y
> ajusten la redacción con sus propias palabras antes de usarlo en la diapositiva.

## RepositorioReportes.java

- **L2-L3**: `obtenerReporte(String idReporte): String` — el único método del contrato,
  compartido por el objeto real y el proxy. Esto es lo que permite que `Main` use
  cualquiera de los dos exactamente igual (polimorfismo): si `ProxyReportes` no
  implementara esta interfaz, el cliente tendría que conocer un tipo distinto y con su
  propia forma de llamarlo, perdiendo la transparencia del patrón.

## RepositorioReportesReal.java

- **L3-L6**: el constructor simula "abrir la conexión con la BD" con una espera de
  ~800 ms. Este costo va en el **constructor**, no en `obtenerReporte`, porque conectar
  es un paso previo y separado de consultar; esto es lo que hace posible el proxy
  virtual: mientras el proxy no cree ningún `RepositorioReportesReal`, ese costo nunca se
  paga.
- **L10-L14**: `obtenerReporte` simula la consulta costosa (~1000 ms) y devuelve el
  contenido. Esta clase **no** sabe nada de roles ni de caché — esa separación de
  responsabilidades es lo que permite que el proxy exista como una capa aparte: el real
  solo sabe "consultar de verdad".
- **L17-L23**: `dormir(ms)` envuelve `Thread.sleep(ms)` en un `try/catch`. El `catch` no
  se deja vacío: `Thread.sleep` borra el estado de interrupción del hilo al lanzar
  `InterruptedException`, así que `Thread.currentThread().interrupt()` lo restaura, para
  no "tragarse" en silencio una señal de cancelación que código más arriba podría
  necesitar revisar. Es una práctica estándar de Java concurrente.

## ProxyReportes.java

- **L11**: `implements RepositorioReportes` — misma interfaz que el objeto real, para
  que el cliente no distinga entre ambos.
- **L12-L15**: atributos — `usuario`/`rol` (para la validación de protección), `real`
  (empieza en `null`, creación perezosa) y `cache` (`Map` de instancia, para no repetir
  consultas). El constructor (**L17-L20**) solo guarda `usuario` y `rol`: **no** recibe
  ni crea el objeto real, a diferencia del `envuelto` de un decorador que sí llega por
  constructor — aquí es el propio proxy quien decide cuándo crear su objeto real.
- **L27-L31** (protección, primero): si el rol no es `GERENTE` ni `AUDITOR`, se rechaza
  de inmediato con `return null`. Va **antes** que la caché a propósito: la seguridad no
  puede depender de si algo ya fue consultado antes. Si un practicante pidiera un reporte
  que un gerente ya consultó y está en caché, no debería poder verlo solo por eso — la
  caché nunca debe ser una puerta trasera del control de acceso.
- **L32-L37** (caché): si `idReporte` ya está en `cache`, se devuelve directamente, sin
  tocar `real`. Como `cache` es un atributo de **instancia**, cada `ProxyReportes` tiene
  su propio mapa: el proxy del practicante nunca vería lo que cacheó el proxy del
  gerente, aunque ambos pidieran el mismo reporte (y de todas formas el practicante nunca
  llega a esta línea, por la validación anterior).
- **L38-L41** (creación perezosa): `if (real == null)` — el objeto real se crea en la
  línea 40, y solo la primera vez que se necesita de verdad. Llamadas posteriores
  reutilizan la misma instancia sin repetir el costo de conexión.
- **L30**: el método devuelve `null` cuando el acceso es denegado. Riesgo: quien llama a
  `obtenerReporte` y olvida comprobar `null` antes de usarlo puede toparse con un
  `NullPointerException` lejos de la causa real. En un sistema real sería preferible
  lanzar una excepción específica (`SecurityException`); aquí se deja como `null` con el
  mensaje impreso como simplificación aceptable para la demo.

## Pausa.java

- Idéntica a los otros tres patrones: clase utilitaria (`final`, constructor privado,
  métodos `static`, como `Math`/`Collections`). No forma parte del patrón Proxy.

## Main.java

- **L6-L12**: `medir(repo, id)` recibe el parámetro con tipo `RepositorioReportes` (la
  interfaz, no `ProxyReportes`) y mide el tiempo real de la llamada con
  `System.currentTimeMillis()`.
- **L18-L21** (caso 1): un `ProxyReportes` con rol `PRACTICANTE` — rechazado de
  inmediato (≈0 ms), sin tocar el objeto real.
- **L23-L25** (caso 2): un `ProxyReportes` con rol `GERENTE`, primera consulta —
  se crea el objeto real (≈800 ms) y se hace la consulta (≈1000 ms): ≈1.800 ms en total.
- **L27-L28** (caso 3): se reutiliza la **misma variable** `gerente` (misma instancia)
  para la misma consulta — imprescindible, porque tanto `cache` como `real` son
  atributos de instancia: un `ProxyReportes` nuevo no tendría nada cacheado. Resultado
  casi inmediato (≈0 ms), servido desde caché.
- **L30-L31** (caso 4): mismo `gerente`, reporte **distinto** (`REP-2026-10`) — no
  repite el costo de conexión (`real` ya no es `null`, desde el caso 2), pero sí paga la
  consulta completa (≈1000 ms) porque ese id no estaba en caché.

## Verificación real (no inventada)

Compilado y ejecutado el 2026-09-27 desde `mi-trabajo/proxy/` con
`javac -encoding UTF-8 -d out (Get-ChildItem src\*.java).FullName` y
`java "-Dstdout.encoding=UTF-8" -cp out Main`. El texto de la salida coincide línea por
línea con `proxy/salida.txt` (denegado / conexión+consulta / caché / consulta nueva). Los
milisegundos exactos varían levemente entre ejecuciones (8/1805/6/1001 ms aquí vs.
26/1805/0/1000 ms en la referencia) porque son medidas de tiempo real de `Thread.sleep`
y la JVM, no deterministas — el patrón relativo (≈0 / ≈1.800 / ≈0 / ≈1.000 ms) es el
mismo y es lo que hay que poder explicar, no el número exacto.
