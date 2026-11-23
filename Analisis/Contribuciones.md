# Contribuciones individuales y uso de IA

Datos obtenidos del historial de issues, ramas y pull requests de GitHub (a fecha 2026-11-23). «Revisó/aprobó» indica quién dejó una aprobación en el PR.

## Tabla de contribuciones

| Issue | Tema | Responsable(s) asignado(s) | Rama | PR (autor → aprobó) |
|---|---|---|---|---|
| #2 | Modelos base: Turno, Ventanilla, EstadoTurno | MateoAlejandroCaamalTencle | `feat/modelos-turno-y-cola-fifo` | #7 (KERMITH79 → ivn-chipo). El PR #18 de Mateo (`feature/issue-1-modelos-base`) se cerró sin fusionar. |
| #3 | Cola FIFO y registro de turnos | KERMITH79 | `feat/modelos-turno-y-cola-fifo` | #7 (KERMITH79 → ivn-chipo) |
| #4 | Asignación y finalización de atenciones | KERMITH79, BraulioTelloMancilla | `feat/asignacion-y-finalizacion` | #9 (KERMITH79 → ivn-chipo) |
| #5 | Cancelación de turnos e historial | KERMITH79, ivn-chipo | `feat/cancelacion-y-historial` | #13 (KERMITH79 → ivn-chipo) |
| #14 | Pruebas de registro, cola FIFO y cancelación | KERMITH79 | `test/suite-registro-fifo-cancelacion` | #19 (KERMITH79 → ivn-chipo) |
| #15 | Pruebas de asignación, rechazos y transiciones | BraulioTelloMancilla | `15-suite-de-pruebas-junit-5-para-asignación-rechazos-y-transiciones` | #23 (BraulioTelloMancilla → KERMITH79). El PR #21 se cerró sin fusionar. |
| #16 | Almacenamiento local y recuperación | MateoAlejandroCaamalTencle | — | La clase `AlmacenamientoArchivoTurnos` llegó en el commit `e435cc3` de ivn-chipo, dentro del PR #22 |
| #17 | Manejo de errores de almacenamiento | ivn-chipo | `17-manejo-de-errores-de-almacenamiento-y-validación-de-datos` | #22 (ivn-chipo → MateoAlejandroCaamalTencle) |
| #20 | Carpeta de evidencias de pruebas | BraulioTelloMancilla | — | Evidencias en `Analisis/Pruebas_de_Testeo/` (sin PR propio identificado) |
| #9 | Vista principal: ventanillas y fila | MateoAlejandroCaamalTencle | `feature/issue-10-gui-controles-historial` | #31 (MateoAlejandroCaamalTencle → ivn-chipo) |
| #10 | Registro, cancelación con confirmación, historial y alertas | ivn-chipo | `26-10-implementar-registro-cancelación-con-confirmación-historial-y-alertas` | #32 (ivn-chipo → KERMITH79). La interfaz de este issue y del #9 se integró en #31. |
| #11 | Clase `Main` y arranque seguro | KERMITH79 | `feat/main-arranque-seguro` | #30 (KERMITH79 → ivn-chipo) |
| #12 | Verificación de casos, desempeño y seguridad | BraulioTelloMancilla | `28-12-verificación-de-casos-desempeño-200-turnos-y-seguridad` | #33 (BraulioTelloMancilla → KERMITH79) |
| #13 | README, matriz, revisión final y contribuciones | Los cuatro integrantes | `docs/readme-matriz-revision-final` | Este documento |

Otros PR: #1 (CI con SonarQube, KERMITH79); #6 (documentos de análisis, ivn-chipo); #8 y #10 (eliminación de `CalculadoraTest`, KERMITH79); #11 y #12 (eliminación de `Calculadora`, ivn-chipo); #24 (`cancelarTurno` y validación de reloj, MateoAlejandroCaamalTencle); #34 y #35 (integración de la vista en `Main`, MateoAlejandroCaamalTencle).

## Registro de uso y validación de IA

Reglas de uso del agente: [`CLAUDE.md`](../CLAUDE.md). El agente implementa en una rama; **la persona** revisa, ejecuta `git add`, `git commit` y `git push`, y abre el PR.

### Claude Code (usado por KERMITH79)

| Tarea | Aporte de la IA | Verificación humana |
|---|---|---|
| Issues #2 y #3 | Clases `EstadoTurno`, `Turno`, `Ventanilla`, `SistemaGestionTurnos` (registro y fila) y sus pruebas | Se ejecutó `mvn test`; revisión y aprobación del PR #7 por ivn-chipo |
| Issue #4 | Asignación y finalización, `consultarVentanillas` y pruebas | `mvn test`; PR #9 aprobado por ivn-chipo |
| Issue #5 | Cancelación e historial con pruebas | `mvn test`; PR #13 aprobado por ivn-chipo |
| Issue #14 | Reorganización de pruebas en `RegistroTurnosTest`, `ColaFifoTest`, `CancelacionTurnosTest` y `HistorialTurnosTest` | `mvn test` con `BUILD SUCCESS`; PR #19 aprobado por ivn-chipo |
| Issue #11 | `Main`, configuración de `exec-maven-plugin` y `maven-jar-plugin`, `MainTest` | `mvn test`; ejecución de la aplicación y del jar; el quality gate falló por cobertura (26.7 %) y se corrigieron las pruebas antes de fusionar; PR #30 aprobado por ivn-chipo |
| Issue #13 | Borrador del README, la matriz de cumplimiento, la revisión final y esta tabla, a partir del código, de las pruebas y del historial de GitHub | Revisión por el equipo antes de fusionar (pendiente al redactar este documento) |

### Otros integrantes

El uso de IA por parte de MateoAlejandroCaamalTencle, ivn-chipo y BraulioTelloMancilla no consta en el repositorio. **Cada integrante debe completar aquí su propio registro** (tarea, aporte de la IA y verificación humana) o indicar que no usó IA.

| Integrante | Tarea | Aporte de la IA | Verificación humana |
|---|---|---|---|
| MateoAlejandroCaamalTencle | _Por completar_ | _Por completar_ | _Por completar_ |
| ivn-chipo | _Por completar_ | _Por completar_ | _Por completar_ |
| BraulioTelloMancilla | _Por completar_ | _Por completar_ | _Por completar_ |
