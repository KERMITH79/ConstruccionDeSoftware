# Revisión final de calidad (RNF-M05)

Revisión del código de `src/main` en la versión final. Cumple RC-06: **no se aplicó ninguna refactorización**; los puntos siguientes son oportunidades reales encontradas al leer el código, ordenadas por prioridad.

## Hallazgos

### 1. El servicio mezcla reglas de negocio y persistencia (prioridad alta)
`SistemaGestionTurnos` implementa a la vez la cola/FIFO, las transiciones y el guardado transaccional. Las cuatro operaciones que modifican datos (`registrarTurno`, `asignarSiguiente`, `finalizarAtencion`, `cancelarTurno`) repiten la misma secuencia: `snapshotPersistido()` → modificar el registro candidato → `guardar(...)` → mutar objetos en memoria. Es lo que RNF-M01 pide evitar y la causa de que la clase tenga unas 250 líneas.
**Propuesta:** extraer un colaborador que aplique «cambio + guardado atómico» y deje al servicio solo las reglas.

### 2. Regla de cancelación duplicada (prioridad media)
`SistemaGestionTurnos.cancelarTurno` comprueba `estado != EN_ESPERA` y construye su propio mensaje; después `Turno.cancelar()` vuelve a validar lo mismo con otro texto (`Turno.exigirEstado`). Dos lugares deben cambiar juntos si cambia la regla (RNF-M02).
**Propuesta:** dejar una sola validación y reutilizar su mensaje.

### 3. Formato del ID repartido en dos sitios (prioridad media)
El formato `"T" + secuencia` se construye en `registrarTurno` y se vuelve a exigir en `validarEstadoPersistido`. Cambiar el formato obliga a tocar ambos o la carga de datos rechazará archivos válidos.
**Propuesta:** un único método (por ejemplo `Turno.idPara(secuencia)`).

### 4. Un solo tipo de excepción para situaciones distintas (prioridad media)
Se usa `IllegalStateException` tanto para rechazos de negocio (ventanilla ocupada, turno no cancelable) como para datos persistidos inválidos, y `UncheckedIOException` para fallos de disco. `VentanaPrincipal.mostrarError` y `Main.iniciar` capturan tipos genéricos (`RuntimeException`, `IllegalStateException | IllegalArgumentException`), de modo que no distinguen una regla de negocio de un archivo corrupto.
**Propuesta:** excepciones propias (por ejemplo, regla de negocio y datos inválidos).

### 4b. `VentanaPrincipal` concentra demasiada responsabilidad y no tiene pruebas (prioridad media)
La clase (~340 líneas) construye componentes, define colores, formatea fechas y estados, y traduce excepciones a mensajes. No existe `VentanaPrincipalTest`, de modo que RI-01 a RI-03 y RNF-U01 a U04 solo se respaldan con capturas.
**Propuesta:** separar el formato/mensajes (testeable sin Swing) de la construcción de la ventana.

### 5. Diálogos de error duplicados (prioridad baja)
`Main.notificar` y `VentanaPrincipal.mostrarError` muestran errores con `JOptionPane` por caminos distintos, uno con rama *headless* y el otro sin ella.
**Propuesta:** un único componente de notificación.

### 6. Búsqueda lineal y reordenamiento en cada consulta (prioridad baja)
`buscarTurno` recorre la lista completa y `consultarFila` filtra y ordena en cada llamada. Con 200 turnos las operaciones medidas tardan unos milisegundos (ver `Verificacion(...)`, RNF-P01), así que no hay problema hoy; solo escalaría mal si el volumen creciera mucho.
**Propuesta:** no actuar mientras el alcance siga en ~200 turnos; si cambia, indexar por ID y mantener la fila ordenada.

### 7. Archivo vacío versionado (prioridad baja)
`a.out` (0 bytes) está en la raíz del repositorio, incorporado en el commit `690d212`. Es un residuo de compilación sin función.
**Propuesta:** eliminarlo y, si aplica, agregarlo a `.gitignore`.

### 8. Documentación con estados desactualizados (prioridad baja)
`Verificacion(CasosDesempeñoySeguridad).md` marca CV-01 a CV-10 como «interfaz pendiente» y dice que el punto de entrada solo muestra un diálogo, pero el repositorio ya contiene la interfaz (`VentanaPrincipal`) y las capturas de cada caso. Además, esa tabla menciona `filaExcluyeTurnosQueYaEstanEnEspera`, nombre que no existe en el código (el método es `filaExcluyeTurnosQueYaNoEstanEnEspera`).
**Propuesta:** actualizar estados y nombres.

## Qué se revisó
Las clases `Turno`, `Ventanilla`, `SistemaGestionTurnos`, `AlmacenamientoArchivoTurnos`, `PersistenciaTurnos`, `Main` y `VentanaPrincipal`, y los nombres de las pruebas existentes. No se ejecutó una herramienta de análisis adicional; el análisis estático de SonarQube corre en el CI de cada PR.
