# Pruebas unitarias de reglas de negocio

La suite automatizada de JUnit 5 verifica la lógica del dominio sin iniciar una
interfaz. Los casos se ejecutan contra `SistemaGestionTurnos`, `Turno` y
`Ventanilla`; las operaciones rechazadas comprueban también que se conserva el
estado previo.

## Trazabilidad de reglas de negocio

| Regla | Cobertura principal |
| :--- | :--- |
| RN-01 — Cola FIFO compartida | `consultarFilaDevuelveOrdenFifo`, `asignaTurnosEnOrdenFifoSinImportarLaVentanilla` |
| RN-02 — Secuencia creciente independiente del ID o reloj | `registraTurnosConSecuenciaEIdUnicos`, `ordenFifoNoDependeDelOrdenAlfabeticoDelId` |
| RN-03 — Ventanillas predefinidas | `aceptaSoloIdsPredefinidos`, `rechazaVentanillaInexistenteOInvalida` |
| RN-04 — Una atención por ventanilla | `noAdmiteDosTurnosALaVez`, `rechazaAsignarSobreVentanillaOcupadaSinCambios` |
| RN-05 — Un turno no se asigna a dos ventanillas | `asignaTurnosEnOrdenFifoSinImportarLaVentanilla`, `conTresVentanillasOcupadasNoHayCuartaAtencion` |
| RN-06 — Asignar inicia la atención | `asignaTurnosEnOrdenFifoSinImportarLaVentanilla`, `cicloRegistrarAsignarFinalizarYReasignarConservaEstados` |
| RN-07 — Solo se cancela un turno en espera | `cancelarTurnoEnEsperaLoRetiraDeLaFilaYLoConservaEnHistorial`, `cancelarTurnoEnAtencionSeRechazaSinCambios`, `cancelarTurnoAtendidoOCanceladoSeRechazaSinCambios` |
| RN-08 — Solo se finaliza un turno en atención | `cicloRegistrarAsignarFinalizarYReasignarConservaEstados`, `rechazaFinalizarVentanillaLibreSinCambios`, `noSePuedeFinalizarUnTurnoEnEspera` |
| RN-09 — Estados terminales e historial | `estadosTerminalesNoTienenTransiciones`, `historialMuestraLosCuatroEstadosEnOrden`, `historialDevueltoNoPermiteModificarElSistema` |
| RN-10 — Asignar con fila vacía no altera los datos | `rechazaAsignarConFilaVaciaSinCambios`, `rechazaAsignarSinTurnosPendientesSinAlterarEstadosExistentes` |


## Ejecución

Desde la raíz del proyecto, con JDK 17 y Maven instalados:

```shell
mvn test
```

Para ejecutar además la fase de verificación y generar el reporte de JaCoCo:

```shell
mvn verify
```

El reporte se genera en `target/site/jacoco/index.html`. El proyecto no impone
un umbral mínimo de cobertura; la trazabilidad anterior identifica las reglas
ejercitadas por la suite.
