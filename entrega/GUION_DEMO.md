# Guion de demostración en vivo — Grupo 3

Orden: Decorator (Camilo) → Facade (Camilo) → Flyweight (Santiago) → Proxy (Nicolás).
Cada bloque dura 3-4 minutos. Todos los comandos van con `--pausas`; quien habla da **Enter**
cuando el texto dice "[ENTER]". Antes de empezar, tener la terminal en pantalla completa,
letra grande (16-18pt), fondo oscuro, y el directorio ya ubicado en `mi-trabajo/<patron>`.

Compilar cada proyecto ANTES de la demo (no en vivo, para no perder tiempo):
```
javac -encoding UTF-8 -d out (Get-ChildItem src\*.java).FullName
```

---

## 1. Decorator — Camilo (≈3.5 min)

**Comando:**
```
java "-Dstdout.encoding=UTF-8" -cp out Main --pausas
```

**Guion:**
1. "Vamos a vender tiquetes de bus Bogotá-Bucaramanga con extras: seguro, equipaje y
   refrigerio. Sin Decorator necesitaríamos 2³ = 8 subclases para cubrir todas las
   combinaciones; con Decorator, solo 3 clases decoradoras que se apilan como capas."
2. Ejecutar el comando. Sale el tiquete básico: **$85.000**. — [ENTER]
3. "Ahora el mismo tiquete envuelto en un decorador de seguro." Sale **$88.500**.
   "El decorador implementa la misma interfaz `Tiquete` que el básico, así que Main no
   distingue uno de otro." — [ENTER]
4. "Aquí apilamos tres decoradores: seguro, equipaje de 10 kg y refrigerio, en una sola
   expresión." Sale **$108.500** = 85.000 + 3.500 + 12.000 + 8.000. — [ENTER]
5. "Y aquí decoramos en tiempo de ejecución: creamos el tiquete básico y le vamos
   agregando extras después, reasignando la variable." Sale **$131.500**.
6. Cierre: "8 subclases con herencia contra 3 decoradores con Decorator."

**Pregunta que puede caer:** *¿por qué TiqueteDecorator implementa Tiquete en vez de
extender TiqueteBasico?* → Para poder envolver cualquier Tiquete (básico u otro
decorador) sin acoplarse a una clase concreta, y para poder apilarse tantas veces como
se quiera.

---

## 2. Facade — Camilo (≈3.5 min)

**Comando:**
```
java "-Dstdout.encoding=UTF-8" -cp out Main --pausas
```

**Guion:**
1. "Comprar un tiquete de bus implica 4 subsistemas: inventario de sillas, pago,
   facturación y notificaciones. Sin fachada, cada canal de venta tendría que conocer
   los 4 y llamarlos en el orden correcto."
2. Ejecutar. Se ven las 6 líneas del bloque "sin fachada" para la silla 12. — [ENTER]
3. "Ahora la misma operación con la fachada: una sola llamada." Se ven los mismos 6
   pasos, pero disparados por `comprarTiquete(...)`, y termina en "compra exitosa". — [ENTER]
4. "Y si alguien intenta comprar la silla 7 que ya se vendió..." Sale "Silla no
   disponible. Compra cancelada." sin llegar a cobrar ni facturar.
5. Cierre: "El cliente pasó de conocer 4 clases y su orden, a conocer solo una."

**Pregunta que puede caer:** *¿qué riesgo tiene que la fachada crezca demasiado?* →
Se convierte en un "objeto Dios" si empieza a acumular lógica de negocio propia (reglas,
descuentos) en vez de solo coordinar llamadas a los subsistemas.

---

## 3. Flyweight — Santiago (≈3.5 min)

**Comando:**
```
java "-Dstdout.encoding=UTF-8" -cp out Main --pausas
```

**Guion:**
1. "Vamos a poner 5.000 buses en un mapa de monitoreo GPS, pero en la flota real solo
   hay 3 modelos distintos: Mercedes-Benz, Volvo y Scania."
2. Ejecutar. Sale "SIN Flyweight -> objetos TipoBus: 5000 | memoria aprox.: NN MB"
   (el número exacto varía por computador). — [ENTER]
3. "Con Flyweight, la fábrica reutiliza el TipoBus si ya existe para ese modelo." Sale
   "CON Flyweight -> objetos TipoBus: 3 | memoria aprox.: NN KB". "Lo importante no es
   el MB exacto — varía por JVM — sino la relación: miles de objetos contra 3." — [ENTER]
4. Se dibujan 3 buses de ejemplo con su marca, modelo, capacidad y posición.
5. Cierre: "5.000 buses comparten solo 3 objetos TipoBus. Cada bus conserva su propia
   placa y posición — eso nunca se comparte."

**Pregunta que puede caer:** *¿por qué el TipoBus es `final` con todos sus atributos
`final`?* → Porque es un objeto compartido por miles de buses a la vez; si algo lo
pudiera modificar, el cambio se vería reflejado en todos los buses que lo comparten.

---

## 4. Proxy — Nicolás (≈4 min)

**Comando:**
```
java "-Dstdout.encoding=UTF-8" -cp out Main --pausas
```

**Guion:**
1. "Vamos a controlar el acceso a reportes gerenciales confidenciales. La consulta real
   a la base de datos es costosa: simula 0,8 s para conectar y 1 s para consultar."
2. Ejecutar. Caso 1: un practicante pide un reporte. Sale "ACCESO DENEGADO", tiempo casi
   inmediato — nunca se toca la base de datos. — [ENTER]
3. Caso 2: un gerente pide el mismo reporte por primera vez. Se ve la conexión abriéndose
   y la consulta — en total ≈1,8 s. "Aquí se crea el objeto real, por primera y única vez." — [ENTER]
4. Caso 3: el gerente pide el MISMO reporte otra vez. Sale "Respuesta desde caché", ≈0 ms.
   "No se repite la consulta costosa." — [ENTER]
5. Caso 4: el gerente pide un reporte DISTINTO. ≈1 s (la consulta sí se repite porque es
   un reporte nuevo), pero sin repetir la conexión de 0,8 s porque el objeto real ya existe.
6. Cierre: "Un mismo método combina tres usos de Proxy: protección, virtual y caché."

**Pregunta que puede caer:** *¿en qué se diferencia de Decorator?* → Misma estructura
(ambos implementan la interfaz del objeto que envuelven), pero distinta intención:
Decorator agrega funcionalidad y el cliente arma la cadena; Proxy controla el acceso y
es el propio proxy quien crea y administra el objeto real, sin que el cliente lo sepa.

---

## Cierre general (quien module la sesión)

"Los cuatro patrones son estructurales: organizan cómo se componen objetos sin cambiar
su comportamiento interno. Decorator y Proxy comparten estructura (envolver con la misma
interfaz) pero difieren en intención. Facade reduce lo que el cliente necesita conocer.
Flyweight reduce cuántos objetos existen realmente."
