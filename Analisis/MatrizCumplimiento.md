# Matriz de cumplimiento

Vincula cada requisito del SGTU (ERS-SGTU-001) con la clase Java que lo implementa y con la prueba o evidencia que lo respalda. Los nombres de pruebas son métodos de `src/test/java/com/construccion/software/`; las capturas están en [`Comprobacion CV/`](./Comprobacion%20CV/) y el detalle de la verificación en [`Verificacion(CasosDesempeñoySeguridad).md`](<./Verificacion(CasosDesempeñoySeguridad).md>).

**Estado:** *Cumple* = implementado y con prueba automatizada o evidencia; *Parcial* = implementado, con una limitación indicada; *Sin prueba automatizada* = implementado pero verificado solo por inspección o captura.

## Requisitos funcionales

| Requisito | Clase(s) | Prueba / evidencia | Estado |
|---|---|---|---|
| RF-01 Consultar ventanillas | `SistemaGestionTurnos.consultarVentanillas`, `Ventanilla`, `VentanaPrincipal` | `SistemaGestionTurnosTest.ventanillasMuestranTurnoActualOLibre`; captura CV-03 | Cumple |
| RF-02 Registrar turno | `SistemaGestionTurnos.registrarTurno`, `Turno` | `RegistroTurnosTest` (5 pruebas: secuencia, ids únicos, estado inicial, final de fila); CV-01 | Cumple |
| RF-03 Consultar fila | `SistemaGestionTurnos.consultarFila` | `ColaFifoTest.consultarFilaDevuelveOrdenFifo`, `filaExcluyeTurnosQueYaNoEstanEnEspera`; CV-02 | Cumple |
| RF-04 Asignar siguiente | `SistemaGestionTurnos.asignarSiguiente` | `SistemaGestionTurnosTest.asignaTurnosEnOrdenFifoSinImportarLaVentanilla`; CV-03 | Cumple |
| RF-05 Rechazar asignaciones imposibles | `SistemaGestionTurnos.asignarSiguiente` | `rechazaAsignarSobreVentanillaOcupadaSinCambios`, `rechazaAsignarConFilaVaciaSinCambios`, `conTresVentanillasOcupadasNoHayCuartaAtencion`; CV-04, CV-05 | Cumple |
| RF-06 Finalizar atención | `SistemaGestionTurnos.finalizarAtencion`, `Turno.finalizar`, `Ventanilla.liberar` | `cicloRegistrarAsignarFinalizarYReasignarConservaEstados`; CV-06 | Cumple |
| RF-07 Rechazar finalización inválida | `SistemaGestionTurnos.finalizarAtencion` | `rechazaFinalizarVentanillaLibreSinCambios`; CV-07 | Cumple |
| RF-08 Cancelar turno pendiente | `SistemaGestionTurnos.cancelarTurno`, `Turno.cancelar` | `CancelacionTurnosTest.cancelarTurnoEnEsperaLoRetiraDeLaFilaYLoConservaEnHistorial`; CV-08 | Cumple |
| RF-09 Rechazar cancelación inválida | `SistemaGestionTurnos.cancelarTurno` / `buscarTurno` | `cancelarIdInexistenteOInvalidoSeRechaza`, `cancelarTurnoEnAtencionAtendidoOCanceladoSeRechazaSinCambios`; CV-09 | Cumple |
| RF-10 Consultar historial | `SistemaGestionTurnos.consultarHistorial`, `VentanaPrincipal` | `HistorialTurnosTest.historialMuestraLosCuatroEstadosEnOrden`; CV-10 | Cumple |
| RF-11 Persistir y recuperar estados | `AlmacenamientoArchivoTurnos`, `PersistenciaTurnos`, `SistemaGestionTurnos.cargarEstado` | `PersistenciaTurnosTest.recuperaTurnosEstadosYAsignacionesAlReiniciar`; `MainTest.recuperaTurnosSecuenciaYVentanillasOcupadasAlReiniciar`; CV-10 | Cumple |
| RF-12 Retroalimentación de operaciones | `VentanaPrincipal` (diálogos y `mostrarError`), excepciones de `SistemaGestionTurnos` | `PersistenciaTurnosTest.falloAlRegistrarNoConsumeSecuenciaNiAgregaTurno`, `fallosDeAsignarFinalizarOCancelarConservanEstadoEnMemoria`; capturas CV-04 a CV-09 | Cumple (los mensajes de la interfaz se verificaron solo con capturas) |

## Reglas de negocio

La trazabilidad por pruebas de cada RN está también en [`PruebasUnitarias.md`](./PruebasUnitarias.md).

| Regla | Clase(s) | Prueba / evidencia | Estado |
|---|---|---|---|
| RN-01 Cola FIFO única, sin prioridades | `SistemaGestionTurnos` | `ColaFifoTest.consultarFilaDevuelveOrdenFifo` | Cumple |
| RN-02 Orden por secuencia, no por ID ni reloj | `SistemaGestionTurnos.registrarTurno`, `consultarFila` | `ColaFifoTest.ordenFifoNoDependeDelOrdenAlfabeticoDelId` | Cumple |
| RN-03 Solo V01, V02 y V03 | `Ventanilla.IDS_VALIDOS` | `VentanillaTest.aceptaSoloIdsPredefinidos`; `rechazaVentanillaInexistenteOInvalida` | Cumple |
| RN-04 Un turno en atención por ventanilla | `Ventanilla.ocupar` | `VentanillaTest.noAdmiteDosTurnosALaVez`; `rechazaAsignarSobreVentanillaOcupadaSinCambios` | Cumple |
| RN-05 Un turno no está en dos ventanillas | `SistemaGestionTurnos.asignarSiguiente`, `Turno.iniciarAtencion` | `conTresVentanillasOcupadasNoHayCuartaAtencion`; `TurnoTest.estadosTerminalesNoTienenTransiciones` | Cumple |
| RN-06 Asignar inicia la atención | `SistemaGestionTurnos.asignarSiguiente` | `asignaTurnosEnOrdenFifoSinImportarLaVentanilla` | Cumple |
| RN-07 Solo se cancela un turno en espera | `Turno.cancelar`, `SistemaGestionTurnos.cancelarTurno` | `cancelarTurnoEnAtencionSeRechazaSinCambios`, `cancelarTurnoAtendidoOCanceladoSeRechazaSinCambios` | Cumple |
| RN-08 Solo se finaliza un turno en atención | `Turno.finalizar` | `TurnoTest.noSePuedeFinalizarUnTurnoEnEspera` | Cumple |
| RN-09 Atendido y Cancelado son terminales y permanecen en el historial | `Turno`, `SistemaGestionTurnos.consultarHistorial` | `TurnoTest.estadosTerminalesNoTienenTransiciones`; `HistorialTurnosTest.turnoAtendidoPermaneceEnHistorial` | Cumple |
| RN-10 Asignar sin pendientes no altera datos | `SistemaGestionTurnos.asignarSiguiente` | `rechazaAsignarSinTurnosPendientesSinAlterarEstadosExistentes` | Cumple |

## Datos, interfaz y restricciones

| Requisito | Clase(s) | Prueba / evidencia | Estado |
|---|---|---|---|
| RD-01 Almacenamiento local | `AlmacenamientoArchivoTurnos` (archivo `datos/turnos.dat`) | `PersistenciaTurnosTest`; sin referencias a red en `src/` | Cumple |
| RD-02 Integridad ante fallo de guardado | `SistemaGestionTurnos.guardar` (guarda el estado candidato antes de cambiar memoria), escritura atómica en `AlmacenamientoArchivoTurnos` | `PersistenciaTurnosTest.fallosDeAsignarFinalizarOCancelarConservanEstadoEnMemoria` | Cumple |
| RD-03 Archivo dañado no se sobrescribe | `AlmacenamientoArchivoTurnos.cargar` (tamaño, checksum, formato), `SistemaGestionTurnos.validarEstadoPersistido`, `Main.iniciar` | `PersistenciaTurnosTest.archivoCorruptoRechazaElArranqueSinSobrescribirlo`; `MainTest.archivoCorruptoInformaElErrorYNoLoSobrescribe` | Cumple |
| RI-01 Operaciones visibles | `VentanaPrincipal` (Registrar turno, Asignar siguiente, Finalizar, Cancelar, tablas de fila e historial) | Capturas CV-01 a CV-10 | Sin prueba automatizada |
| RI-02 Visualización consistente | `VentanaPrincipal.actualizarVista` | Capturas CV-10 antes y después de reiniciar | Sin prueba automatizada |
| RI-03 Etiquetas en español diferenciadas | `VentanaPrincipal` | Inspección de la ventana | Sin prueba automatizada |
| RC-01 a RC-05, RC-07 | Java 17, Maven, Swing, archivo local, sin autenticación | `pom.xml`; README | Cumple |
| RC-06 Sin refactorización obligatoria | — | Hallazgos registrados sin aplicar en [`RevisionFinal.md`](./RevisionFinal.md) | Cumple |

## Requisitos no funcionales

| Requisito | Clase(s) / artefacto | Prueba / evidencia | Estado |
|---|---|---|---|
| RNF-U01 Operabilidad sin comandos internos | `VentanaPrincipal` | Capturas CV-01 a CV-10 | Sin prueba automatizada |
| RNF-U02 Mensajes útiles con causa e ID | Excepciones de `SistemaGestionTurnos` y `Turno`; `VentanaPrincipal.mostrarError` | Capturas CV-04, CV-05, CV-07, CV-09 | Parcial (el contenido de los mensajes no tiene aserciones) |
| RNF-U03 Estados distinguibles por texto | `VentanaPrincipal.formatearEstado` | Captura CV-10 | Sin prueba automatizada |
| RNF-U04 Confirmación antes de cancelar | `VentanaPrincipal.cancelarTurno` (`showConfirmDialog`) | Capturas CV-08 y CV-09 | Sin prueba automatizada |
| RNF-S01 Reglas en la lógica del dominio | `Turno`, `Ventanilla`, `SistemaGestionTurnos` | Toda la suite invoca el dominio sin interfaz | Cumple |
| RNF-S02 Sin secretos ni rutas privadas | Repositorio completo | Revisión por patrones en la sección RNF-S02 de `Verificacion(...)` | Cumple (la revisión no sustituye un escáner ni cubre el historial Git) |
| RNF-S03 Alcance local | `src/` sin `java.net` ni sockets | Inspección de código | Parcial (no se ejecutó la prueba con la red deshabilitada) |
| RNF-S04 Manejo seguro de entradas | `SistemaGestionTurnos.buscarTurno`, `buscarVentanilla` | `cancelarIdInexistenteOInvalidoSeRechaza`, `rechazaVentanillaInexistenteOInvalida` | Cumple |
| RNF-M01 Separación de responsabilidades | Dominio sin dependencias de Swing; `Main` y `VentanaPrincipal` como presentación | Pruebas de dominio sin GUI; reservas en [`RevisionFinal.md`](./RevisionFinal.md) | Parcial (el servicio mezcla reglas y persistencia) |
| RNF-M02 Estándar y legibilidad | Todo `src/main` | Estándar declarado en el README; análisis SonarQube en CI | Parcial (hay duplicaciones señaladas en la revisión final) |
| RNF-M03 Pruebas automatizadas | `src/test` | `mvn test` | Cumple |
| RNF-M04 Documentación mínima | `README.md` | Seguir el README en una instalación limpia | Cumple |
| RNF-M05 Revisión final | [`RevisionFinal.md`](./RevisionFinal.md) | Documento | Cumple |
| RNF-R01 Recuperación tras reinicio | `AlmacenamientoArchivoTurnos`, `SistemaGestionTurnos.cargarEstado` | `PersistenciaTurnosTest.recuperaTurnosEstadosYAsignacionesAlReiniciar`; `MainTest.recuperaTurnosSecuenciaYVentanillasOcupadasAlReiniciar` | Cumple |
| RNF-R02 Integridad ante rechazos | `SistemaGestionTurnos` | Pruebas «...SinCambios» de asignación, finalización y cancelación | Cumple |
| RNF-P01 Respuesta ≤ 2 s con 200 turnos | `SistemaGestionTurnos` + `AlmacenamientoArchivoTurnos` | `RendimientoTurnosTest.operacionesConDoscientosTurnosRespondenEnMenosDeDosSegundos`; tiempos en `Verificacion(...)` | Cumple |

## Casos de verificación de referencia

La numeración CV-01 a CV-10 de esta matriz es la usada en `Verificacion(CasosDesempeñoySeguridad).md`, que **no coincide** con la numeración del ERS (sección 9); allí cada caso tiene su prueba automatizada y su captura.
