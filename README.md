# Sistema de Gestión de Turnos Universitarios (SGTU)

Proyecto de ADA-09 · Construcción de Software · Unidad III. Aplicación de escritorio (Java Swing) que administra una cola única de turnos con disciplina FIFO y tres ventanillas lógicas (V01, V02 y V03): registra turnos, asigna el siguiente a una ventanilla libre, finaliza atenciones, cancela turnos en espera y consulta el historial. Los datos se conservan en un archivo local entre ejecuciones.

La especificación (ERS-SGTU-001) está resumida en [`Analisis/`](./Analisis/).

## Requisitos de entorno

- JDK 17 o superior (el proyecto compila con `--release 17`; se ha probado con JDK 25).
- Apache Maven 3.x.
- Un entorno gráfico para abrir la ventana. Sin pantalla (por ejemplo, en CI) la aplicación solo imprime un resumen en consola.
- No necesita conexión a internet ni cuentas para ejecutarse (RD-01, RNF-S03). Maven descarga sus plugins la primera vez.

## Cómo compilar, probar y ejecutar

```
git clone https://github.com/KERMITH79/ConstruccionDeSoftware.git
cd ConstruccionDeSoftware

mvn test                 # ejecuta las pruebas unitarias (JUnit 5)
mvn verify               # pruebas + reporte de cobertura en target/site/jacoco/index.html
mvn compile exec:java    # inicia la aplicación
```

También se puede generar un jar ejecutable:

```
mvn package -DskipTests
java -jar target/construccion-de-software-1.0-SNAPSHOT.jar
```

### Datos locales

Los turnos se guardan en `datos/turnos.dat`, relativo al directorio desde el que se ejecuta la aplicación (la carpeta `datos/` está ignorada por git). La ruta se cambia con `-Dturnos.archivo=<ruta>`, por ejemplo `mvn compile exec:java -Dturnos.archivo=/tmp/turnos.dat`.

Si el archivo está dañado o es inválido, la aplicación muestra el error y se cierra **sin modificarlo** (RD-03). Para empezar desde cero, borre el archivo manualmente.

## Estándar de codificación

Convenciones de código de Java (Oracle Code Conventions): clases en `PascalCase`, métodos y variables en `camelCase`, constantes en `MAYUSCULAS_CON_GUION_BAJO`, sangría de 4 espacios, un tipo público por archivo y comentarios Javadoc breves en español. Los identificadores del dominio también están en español (`Turno`, `Ventanilla`, `asignarSiguiente`). El análisis estático de SonarQube se ejecuta en cada pull request (workflow `ci.yml`).

## Estructura

```
ConstruccionDeSoftware/
├── pom.xml
├── src/main/java/com/construccion/software/
│   ├── Main.java                         # punto de entrada y arranque seguro
│   ├── VentanaPrincipal.java             # interfaz Swing
│   ├── SistemaGestionTurnos.java         # reglas de negocio (cola FIFO, ventanillas, transiciones)
│   ├── Turno.java, Ventanilla.java, EstadoTurno.java   # modelo
│   └── PersistenciaTurnos.java, AlmacenamientoArchivoTurnos.java   # persistencia local
├── src/test/java/com/construccion/software/   # pruebas JUnit 5
├── Analisis/                                  # requisitos, matriz de cumplimiento, evidencias
└── CLAUDE.md                                  # reglas de uso del agente (Claude Code)
```

## Decisiones técnicas no evidentes

- **Secuencia en lugar de reloj para el FIFO.** El orden de la fila lo da una secuencia creciente (`T1`, `T2`...), no la fecha ni el ID alfabético (RN-02). La secuencia no se reutiliza después de reiniciar.
- **Guardar antes de modificar la memoria.** Cada operación arma el estado resultante, lo guarda y solo entonces cambia los objetos en memoria; si el guardado falla, la aplicación conserva el estado anterior y no informa éxito (RD-02, RF-12).
- **Archivo con verificación de integridad.** `AlmacenamientoArchivoTurnos` escribe un archivo binario con una suma de verificación SHA-256, mediante un archivo temporal y un reemplazo atómico, para que una escritura interrumpida no deje datos a medias. Un archivo inexistente equivale a «sin turnos»; uno existente pero inválido nunca se trata como vacío.
- **Reloj inyectable.** `SistemaGestionTurnos` acepta un `Clock` para poder fijar la hora en las pruebas.
- **Pruebas sin interfaz.** El dominio no depende de Swing (RNF-M01). Surefire corre con `java.awt.headless=true`, así las pruebas no abren ventanas.

## Documentación de análisis

- [Requisitos funcionales](Analisis/RF.md), [requisitos no funcionales](Analisis/RFN.md) y [reglas de negocio](Analisis/ReglasDeNegocio.md)
- [Matriz de cumplimiento](Analisis/MatrizCumplimiento.md): cada RF, RN y RNF con su clase y su prueba
- [Pruebas unitarias](Analisis/PruebasUnitarias.md) y [evidencias de verificación](Analisis/Verificacion(CasosDesempeñoySeguridad).md)
- [Revisión final de calidad](Analisis/RevisionFinal.md) (RNF-M05)
- [Contribuciones y uso de IA](Analisis/Contribuciones.md)

## Flujo de trabajo

1. Se solicita una tarea al agente (Claude Code).
2. El agente implementa los cambios en una rama (`tipo/descripcion-corta`), nunca sobre `main`.
3. El responsable del repositorio revisa y ejecuta `git add`, `git commit` y `git push`; los cambios entran por pull request con revisión.

Las reglas completas de uso del agente están en [`CLAUDE.md`](./CLAUDE.md).

## Autores

Equipo de ADA-09: KERMITH79 (Omar Chim Canche), ivn-chipo (Ivan Chi), BraulioTelloMancilla (Braulio Tello) y MateoAlejandroCaamalTencle (Mateo Caamal Tencle).
