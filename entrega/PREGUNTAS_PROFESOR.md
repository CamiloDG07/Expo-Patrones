# Preguntas probables del profesor — Grupo 3

10 preguntas por patrón con respuesta, más una hoja de repaso de 1 página por patrón.
Todo sale de las respuestas que el grupo ya dio y defendió durante el desarrollo — no
hay nada inventado aquí, son las mismas explicaciones, condensadas para estudiar.

---

## DECORATOR — 10 preguntas

1. **¿Por qué `Tiquete` es una interfaz y no una clase abstracta?**
   Solo define un contrato (`descripcion()`, `costo()`), sin estado ni código
   compartido; así `TiqueteBasico` y `TiqueteDecorator` pueden implementarla sin
   depender de una jerarquía común obligatoria.

2. **¿Por qué `TiqueteDecorator` implementa `Tiquete` en vez de extender `TiqueteBasico`?**
   Si extendiera `TiqueteBasico`, heredaría atributos que no necesita, quedaría atado a
   una clase concreta, y al apilar varios decoradores se duplicarían datos. Al
   implementar la interfaz puede envolver cualquier `Tiquete`, incluido otro decorador.

3. **¿Cuántas subclases harían falta sin Decorator, con 3 extras opcionales?**
   2³ = 8 subclases, y cada extra nuevo duplicaría esa cantidad.

4. **¿Qué es estado intrínseco/extrínseco aquí?** (pregunta cruzada con Flyweight)
   No aplica igual que en Flyweight — en Decorator no hay estado compartido; cada
   decorador es un objeto independiente que envuelve a otro.

5. **¿Por qué `envuelto` es `protected` y no `private`?**
   Para que las subclases concretas (`SeguroViaje`, etc.) puedan acceder a él
   directamente si lo necesitan; con `private` solo `TiqueteDecorator` lo vería.

6. **¿Qué pasa si se aplica el mismo decorador dos veces (`new SeguroViaje(new SeguroViaje(t))`)?**
   Es válido: el seguro se cobra dos veces. El patrón no lo prohíbe; sería una regla de
   negocio a controlar en otra capa si no tiene sentido.

7. **¿Por qué `COSTO_SEGURO` es `static final` y no un atributo de instancia?**
   Es una constante de la clase, igual para todos los seguros (3.500 siempre); con
   `static` hay una sola copia compartida y no se gasta memoria por instancia.

8. **¿Qué principio de diseño ilustra el RNF de "agregar un extra sin modificar lo existente"?**
   El principio Abierto/Cerrado (Open/Closed): abierto a extensión (nuevas clases),
   cerrado a modificación (no se toca el código existente).

9. **¿Por qué no se generalizaron los 3 decoradores en una sola clase parametrizable?**
   Por claridad (cada clase se lee de un vistazo), porque `EquipajeExtra` no encaja en
   "costo fijo" (depende de los kilos), y porque el patrón se demuestra mejor con clases
   pequeñas y explícitas — la repetición es el precio de la claridad en un ejemplo
   didáctico.

10. **Decorator vs. Proxy: misma estructura, ¿qué cambia?**
    Ambos implementan la misma interfaz que el objeto que envuelven. Decorator agrega
    funcionalidad y el cliente arma la cadena de envoltorios; Proxy controla el acceso y
    es el propio proxy quien crea/administra el objeto real.

### Hoja de repaso — Decorator (1 página, 10 min)
- **Problema:** combinar extras (seguro, equipaje, refrigerio) sin explosión de subclases.
- **Piezas:** `Tiquete` (interfaz) → `TiqueteBasico` / `TiqueteDecorator` (implementan) →
  `SeguroViaje`, `EquipajeExtra`, `RefrigerioABordo` (extienden `TiqueteDecorator`).
- **Truco:** cada decorador delega en `envuelto` (`super.costo()`) y suma su parte.
- **Números:** básico 85.000 → +seguro 88.500 → +equipaje(10kg)+refrigerio 108.500 →
  otro tiquete con 2 extras 131.500.
- **Frase clave:** "composición en vez de herencia para extender comportamiento en
  tiempo de ejecución".

---

## FACADE — 10 preguntas

1. **¿Qué problema resuelve Facade aquí?**
   Sin ella, cualquier canal de venta (taquilla, app, call center) tendría que conocer
   4 subsistemas y llamarlos en el orden correcto, duplicando y arriesgando ese orden.

2. **¿Qué hace `comprarTiquete(...)` internamente, en orden?**
   Valida disponibilidad → valida medio de pago → bloquea la silla → cobra → factura →
   notifica. Las dos validaciones van primero, con `return false` si fallan.

3. **¿Por qué la validación va antes de bloquear la silla?**
   Porque bloquear cambia el estado del sistema; si se bloqueara antes de validar el
   pago y este fallara, la silla quedaría atascada como ocupada sin ninguna compra real.

4. **¿Por qué `VentaTiquetesFacade` crea sus 4 subsistemas en la declaración del atributo
   y no en un constructor explícito?**
   Es equivalente (Java lo ejecuta igual al construir el objeto); se prefiere porque es
   más corto, ya que ninguno depende de un parámetro externo.

5. **¿Qué pasa si se compra dos veces la misma silla?**
   La segunda falla en el primer paso: `estaDisponible` devuelve `false` porque la
   instancia de `InventarioSillas` es la misma durante toda la ejecución y ya la
   recuerda como ocupada.

6. **¿Por qué `FE-1001` aparece dos veces en la salida completa (sin fachada y con
   fachada)?**
   Porque `Main` (sin fachada) y la fachada crean cada uno su propia instancia de
   `ServicioFacturacion`, con su propio `consecutivo` que empieza en 1000.

7. **¿Qué riesgo tiene que la fachada crezca demasiado?**
   Se convierte en un "objeto Dios" si acumula lógica de negocio propia (descuentos,
   reglas) en vez de solo coordinar llamadas a los subsistemas.

8. **¿Qué ventaja hay si mañana se agrega un paso nuevo (puntos de fidelidad)?**
   Solo se modifica la fachada; ningún cliente que ya la use tiene que cambiar.

9. **¿La fachada reemplaza a los subsistemas?**
   No. Siguen existiendo y son accesibles si alguien los necesita directamente (así lo
   demuestra el propio bloque "sin fachada" de la demo); la fachada solo los coordina.

10. **¿Por qué en el diagrama "correcto" `Main` depende solo de la fachada, si en el
    código también llama a los subsistemas directamente?**
    El bloque "sin fachada" del código es solo un recurso didáctico para mostrar el
    contraste; el diseño recomendado en producción es que el cliente conozca únicamente
    la fachada.

### Hoja de repaso — Facade (1 página, 10 min)
- **Problema:** coordinar 4 subsistemas (inventario, pago, facturación, notificaciones)
  sin que el cliente conozca a los 4 ni su orden.
- **Pieza clave:** `VentaTiquetesFacade.comprarTiquete(...)` — valida, luego actúa.
- **Números:** silla 12 (Ana Ruiz, sin fachada) OK; silla 7 (Carlos Gómez, con fachada)
  OK; silla 7 otra vez (Luisa Mora) → "Silla no disponible".
- **Frase clave:** "un punto de entrada simple reduce el acoplamiento".

---

## FLYWEIGHT — 10 preguntas

1. **¿Qué es estado intrínseco y qué es extrínseco aquí?**
   Intrínseco (compartido, no cambia): marca, modelo, capacidad, el recurso pesado
   (`modelo3D`). Extrínseco (propio de cada bus): placa, latitud, longitud.

2. **¿Por qué `TipoBus` recibe placa/lat/lon como parámetros de `dibujar()` en vez de
   guardarlos?**
   Porque son extrínsecos: si `TipoBus` los guardara, dejaría de ser compartible entre
   buses del mismo modelo.

3. **¿Por qué `TipoBus` es `final` con todos sus atributos `final`?**
   Es un objeto compartido por miles de `BusEnRuta` a la vez; si algo lo modificara, el
   cambio se vería reflejado en todos los que lo comparten.

4. **¿Por qué el caché de la fábrica (`CACHE`) y `obtener()` son `static`?**
   Para que exista un único punto de caché compartido por toda la aplicación; si no
   fuera estático, cada instancia de la fábrica tendría su propio mapa y se duplicarían
   `TipoBus` para el mismo modelo.

5. **¿Cuántas veces se ejecuta realmente `new TipoBus(...)` con 5.000 buses y 3 modelos?**
   Exactamente 3 veces — una por cada clave nueva que `computeIfAbsent` encuentra por
   primera vez.

6. **¿Por qué la relación `BusEnRuta` → `TipoBus` es agregación y no composición?**
   `BusEnRuta` no es dueño del ciclo de vida del `TipoBus`; lo crea y administra la
   fábrica, y es compartido por miles de `BusEnRuta` a la vez.

7. **¿Por qué el número de MB/KB de memoria no es un dato confiable para comparar entre
   computadores?**
   `Runtime.totalMemory()/freeMemory()` depende de la JVM, el recolector de basura y el
   momento exacto de la medición. Lo confiable es la relación estructural: miles de
   objetos contra 3, sin importar el número exacto.

8. **¿Por qué se usa `Random(42)` con semilla fija en vez de `new Random()`?**
   Para que la secuencia de coordenadas sea reproducible en cualquier ejecución y
   comparable contra la salida esperada.

9. **¿Por qué se hizo `TipoBus.clave(...)` estático en vez de solo un método de
   instancia?**
   Porque la fábrica necesita calcular la clave **antes** de tener un `TipoBus` creado,
   para decidir si crear uno nuevo o reutilizar uno existente.

10. **¿Por qué se forzó `Locale.US` en `String.format` dentro de `dibujar()`?**
    Para que el punto decimal salga igual sin importar la configuración regional del
    computador donde se ejecute — importante para reproducir la salida el día de la
    exposición en una máquina distinta.

### Hoja de repaso — Flyweight (1 página, 10 min)
- **Problema:** 5.000 buses en un mapa, pero solo 3 modelos reales — no duplicar el
  recurso pesado (ícono ~20 KB) por cada bus.
- **Piezas:** `TipoBus` (flyweight, intrínseco) — `BusEnRuta` (contexto, extrínseco) —
  `TipoBusFactory` (caché con `computeIfAbsent`).
- **Números:** 5.000 objetos `TipoBus` sin el patrón → 3 con el patrón.
- **Frase clave:** "compartir en vez de duplicar lo que es igual entre muchos objetos".

---

## PROXY — 10 preguntas

1. **¿Qué tres usos de Proxy combina `ProxyReportes` en el mismo método?**
   Protección (valida rol), virtual (crea el objeto real solo cuando hace falta) y
   caché (evita repetir consultas costosas).

2. **¿Por qué la validación de rol va antes que la consulta a caché?**
   La seguridad no puede depender de si algo ya fue consultado antes; si un practicante
   pidiera un reporte que un gerente ya consultó y está en caché, no debería poder verlo
   solo por eso — la caché nunca es una puerta trasera del control de acceso.

3. **¿Por qué el constructor de `ProxyReportes` no crea el objeto real?**
   Para lograr la creación perezosa (proxy virtual): el costo de conexión (~0,8 s) solo
   se paga la primera vez que de verdad se necesita una consulta real.

4. **¿En qué línea se crea el objeto real y qué condición lo determina?**
   `if (real == null) { real = new RepositorioReportesReal(); }` — se crea solo la
   primera vez, cuando `real` sigue siendo `null`.

5. **¿Por qué `cache` es un atributo de instancia y qué implica?**
   Cada `ProxyReportes` tiene su propio mapa; el proxy de un practicante nunca vería lo
   que cacheó el proxy de un gerente, aunque pidieran el mismo reporte.

6. **¿Qué riesgo tiene devolver `null` cuando el acceso es denegado?**
   Quien llama y olvida comprobar `null` puede toparse con un `NullPointerException`
   lejos de la causa real; en un sistema real sería preferible lanzar una excepción
   específica (`SecurityException`).

7. **¿Por qué `Thread.currentThread().interrupt()` en el `catch` de `dormir()`, en vez de
   dejarlo vacío?**
   `Thread.sleep` borra el estado de interrupción del hilo al lanzar la excepción;
   volver a marcarlo evita "tragarse" en silencio una señal de cancelación que código
   más arriba podría necesitar revisar.

8. **Proxy vs. Decorator: misma estructura, ¿qué cambia?**
   Ambos implementan la misma interfaz que el objeto que envuelven. Decorator agrega
   funcionalidad y el cliente arma la cadena; Proxy controla el acceso y es el propio
   proxy quien crea/administra el objeto real, sin que el cliente lo sepa.

9. **¿Qué tipo de proxy es cada bloque de `obtenerReporte`?**
   Protección (validación de rol) → caché (`cache.containsKey`) → virtual (creación
   perezosa de `real`).

10. **¿Por qué el caso 4 (otro reporte) tarda ~1 s pero no repite el costo de conexión?**
    Porque `real` ya no es `null` desde el caso 2 (se reutiliza la misma conexión), pero
    ese reporte no estaba en caché, así que sí se ejecuta la consulta completa.

### Hoja de repaso — Proxy (1 página, 10 min)
- **Problema:** acceso controlado y eficiente a reportes gerenciales confidenciales
  (consulta costosa: ~0,8 s conectar + ~1 s consultar).
- **Piezas:** `RepositorioReportes` (interfaz) — `RepositorioReportesReal` (sujeto real,
  caro) — `ProxyReportes` (protección + virtual + caché).
- **Números:** denegado ≈0 ms; 1ª consulta ≈1.800 ms; misma consulta ≈0 ms (caché); otro
  reporte ≈1.000 ms (sin repetir conexión).
- **Frase clave:** "el proxy decide si la llamada llega al objeto real, y cuándo".

---

## Preguntas transversales (comparando los 4)

- **¿Cuáles patrones se parecen estructuralmente y por qué se diferencian en intención?**
  Decorator y Proxy: misma estructura (implementan la interfaz del objeto que envuelven),
  distinta intención (agregar vs. controlar acceso).

- **¿Cuál de los 4 patrones reduce cuántos objetos existen, y cuál reduce cuánto necesita
  saber el cliente?**
  Flyweight reduce objetos (comparte instancias); Facade reduce lo que el cliente
  necesita conocer (una interfaz simple en vez de varios subsistemas).

- **¿Los cuatro son creacionales o estructurales?**
  Los cuatro son **estructurales**: organizan cómo se componen clases y objetos, sin
  definir cómo se crean (eso serían los creacionales) ni cómo se comunican en
  comportamiento (esos serían los de comportamiento).
