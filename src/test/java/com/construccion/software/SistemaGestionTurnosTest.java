package com.construccion.software;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class SistemaGestionTurnosTest {

    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-11T10:00:00Z"), ZoneOffset.UTC);
    private final SistemaGestionTurnos sistema = new SistemaGestionTurnos(reloj);

    @Test
    void asignaTurnosEnOrdenFifoSinImportarLaVentanilla() {
        Turno t1 = sistema.registrarTurno();
        Turno t2 = sistema.registrarTurno();
        Turno t3 = sistema.registrarTurno();

        assertSame(t1, sistema.asignarSiguiente("V02"));
        assertSame(t2, sistema.asignarSiguiente("V01"));

        assertEquals("V02", t1.getVentanillaAsignada());
        assertEquals("V01", t2.getVentanillaAsignada());
        assertEquals(EstadoTurno.EN_ATENCION, t1.getEstado());
        assertEquals(List.of(t3), sistema.consultarFila());
    }

    @Test
    void ventanillasMuestranTurnoActualOLibre() {
        Turno t1 = sistema.registrarTurno();
        sistema.asignarSiguiente("V02");

        List<Ventanilla> ventanillas = sistema.consultarVentanillas();

        assertEquals(List.of("V01", "V02", "V03"), ventanillas.stream().map(Ventanilla::getId).toList());
        assertTrue(ventanillas.get(0).estaLibre());
        assertSame(t1, ventanillas.get(1).getTurnoActual());
        assertTrue(ventanillas.get(2).estaLibre());
    }

    @Test
    void rechazaAsignarSobreVentanillaOcupadaSinCambios() {
        Turno t1 = sistema.registrarTurno();
        Turno t2 = sistema.registrarTurno();
        sistema.asignarSiguiente("V02");

        assertThrows(IllegalStateException.class, () -> sistema.asignarSiguiente("V02"));

        assertEquals(EstadoTurno.EN_ATENCION, t1.getEstado());
        assertEquals(EstadoTurno.EN_ESPERA, t2.getEstado());
        assertSame(t1, sistema.consultarVentanillas().get(1).getTurnoActual());
        assertEquals(List.of(t2), sistema.consultarFila());
        assertEquals(List.of(t1, t2), sistema.consultarHistorial());
    }

    @Test
    void rechazaAsignarConFilaVaciaSinCambios() {
        assertThrows(IllegalStateException.class, () -> sistema.asignarSiguiente("V01"));

        assertTrue(sistema.consultarFila().isEmpty());
        assertTrue(sistema.consultarHistorial().isEmpty());
        assertTrue(sistema.consultarVentanillas().stream().allMatch(Ventanilla::estaLibre));
        assertTrue(sistema.consultarVentanillas().stream()
                .allMatch(ventanilla -> ventanilla.getTurnoActual() == null));
    }

    @Test
    void rechazaAsignarSinTurnosPendientesSinAlterarEstadosExistentes() {
        Turno enAtencion = sistema.registrarTurno();
        Turno cancelado = sistema.registrarTurno();
        sistema.asignarSiguiente("V01");
        sistema.cancelarTurno(cancelado.getId());

        assertThrows(IllegalStateException.class, () -> sistema.asignarSiguiente("V02"));

        assertEquals(EstadoTurno.EN_ATENCION, enAtencion.getEstado());
        assertEquals(EstadoTurno.CANCELADO, cancelado.getEstado());
        assertSame(enAtencion, sistema.consultarVentanillas().get(0).getTurnoActual());
        assertTrue(sistema.consultarVentanillas().get(1).estaLibre());
        assertTrue(sistema.consultarFila().isEmpty());
        assertEquals(List.of(enAtencion, cancelado), sistema.consultarHistorial());
    }

    @Test
    void conTresVentanillasOcupadasNoHayCuartaAtencion() {
        sistema.registrarTurno();
        sistema.registrarTurno();
        sistema.registrarTurno();
        Turno pendiente = sistema.registrarTurno();
        sistema.asignarSiguiente("V01");
        sistema.asignarSiguiente("V02");
        sistema.asignarSiguiente("V03");

        assertThrows(IllegalStateException.class, () -> sistema.asignarSiguiente("V01"));
        assertEquals(List.of(pendiente), sistema.consultarFila());
    }

    @Test
    void rechazaVentanillaInexistenteOInvalida() {
        sistema.registrarTurno();
        assertThrows(IllegalArgumentException.class, () -> sistema.asignarSiguiente("V04"));
        assertThrows(IllegalArgumentException.class, () -> sistema.asignarSiguiente(null));
        assertThrows(IllegalArgumentException.class, () -> sistema.finalizarAtencion(""));
        assertEquals(1, sistema.consultarFila().size());
    }

    @Test
    void cicloRegistrarAsignarFinalizarYReasignarConservaEstados() {
        Turno t1 = sistema.registrarTurno();
        Turno t2 = sistema.registrarTurno();

        assertSame(t1, sistema.asignarSiguiente("V02"));
        assertEquals(EstadoTurno.EN_ATENCION, t1.getEstado());
        assertSame(t1, sistema.consultarVentanillas().get(1).getTurnoActual());
        assertSame(t1, sistema.finalizarAtencion("V02"));
        assertEquals(EstadoTurno.ATENDIDO, t1.getEstado());
        assertTrue(sistema.consultarVentanillas().get(1).estaLibre());

        assertSame(t2, sistema.asignarSiguiente("V02"));
        assertEquals(EstadoTurno.EN_ATENCION, t2.getEstado());
        assertSame(t2, sistema.consultarVentanillas().get(1).getTurnoActual());
        assertEquals(List.of(t1, t2), sistema.consultarHistorial());
    }

    @Test
    void rechazaFinalizarVentanillaLibreSinCambios() {
        Turno t1 = sistema.registrarTurno();
        sistema.asignarSiguiente("V01");

        assertThrows(IllegalStateException.class, () -> sistema.finalizarAtencion("V02"));

        assertEquals(EstadoTurno.EN_ATENCION, t1.getEstado());
        assertEquals(t1, sistema.consultarVentanillas().get(0).getTurnoActual());
    }
<<<<<<< HEAD

    @Test
    void cancelarTurnoEnEsperaLoRetiraDeLaFilaYLoConservaEnHistorial() {
        Turno t1 = sistema.registrarTurno();
        Turno t2 = sistema.registrarTurno();
        Turno t3 = sistema.registrarTurno();

        sistema.cancelarTurno(t2.getId());

        assertEquals(EstadoTurno.CANCELADO, t2.getEstado());
        assertEquals(List.of(t1, t3), sistema.consultarFila());
        assertTrue(sistema.consultarHistorial().contains(t2));
    }

    @Test
    void siguienteAsignacionOmiteElTurnoCancelado() {
        Turno t1 = sistema.registrarTurno();
        Turno t2 = sistema.registrarTurno();
        Turno t3 = sistema.registrarTurno();
        sistema.cancelarTurno(t2.getId());

        assertSame(t1, sistema.asignarSiguiente("V01"));
        assertSame(t3, sistema.asignarSiguiente("V02"));
    }

    @Test
    void cancelarIdInexistenteOInvalidoSeRechaza() {
        Turno t1 = sistema.registrarTurno();
        assertThrows(IllegalArgumentException.class, () -> sistema.cancelarTurno("T99"));
        assertThrows(IllegalArgumentException.class, () -> sistema.cancelarTurno(""));
        assertThrows(IllegalArgumentException.class, () -> sistema.cancelarTurno(null));
        assertThrows(IllegalArgumentException.class, () -> sistema.cancelarTurno("'; DROP TABLE"));
        assertEquals(List.of(t1), sistema.consultarFila());
    }

    @Test
    void cancelarTurnoEnAtencionSeRechazaSinCambios() {
        Turno enAtencion = sistema.registrarTurno();
        Turno enEspera = sistema.registrarTurno();
        sistema.asignarSiguiente("V01");

        assertThrows(IllegalStateException.class, () -> sistema.cancelarTurno(enAtencion.getId()));

        assertEquals(EstadoTurno.EN_ATENCION, enAtencion.getEstado());
        assertSame(enAtencion, sistema.consultarVentanillas().get(0).getTurnoActual());
        assertEquals(List.of(enEspera), sistema.consultarFila());
        assertEquals(List.of(enAtencion, enEspera), sistema.consultarHistorial());
    }

    @Test
    void cancelarTurnoAtendidoOCanceladoSeRechazaSinCambios() {
        Turno enAtencion = sistema.registrarTurno();
        Turno atendido = sistema.registrarTurno();
        Turno cancelado = sistema.registrarTurno();
        Turno enEspera = sistema.registrarTurno();
        sistema.cancelarTurno(cancelado.getId());
        sistema.asignarSiguiente("V01");
        sistema.asignarSiguiente("V02");
        sistema.finalizarAtencion("V02");

        assertThrows(IllegalStateException.class, () -> sistema.cancelarTurno(enAtencion.getId()));
        assertThrows(IllegalStateException.class, () -> sistema.cancelarTurno(atendido.getId()));
        assertThrows(IllegalStateException.class, () -> sistema.cancelarTurno(cancelado.getId()));

        assertEquals(EstadoTurno.EN_ATENCION, enAtencion.getEstado());
        assertEquals(EstadoTurno.ATENDIDO, atendido.getEstado());
        assertSame(enAtencion, sistema.consultarVentanillas().get(0).getTurnoActual());
        assertEquals(List.of(enEspera), sistema.consultarFila());
    }

    @Test
    void historialMuestraLosCuatroEstadosEnOrden() {
        Turno atendido = sistema.registrarTurno();
        Turno enAtencion = sistema.registrarTurno();
        Turno cancelado = sistema.registrarTurno();
        Turno enEspera = sistema.registrarTurno();
        sistema.asignarSiguiente("V01");
        sistema.asignarSiguiente("V02");
        sistema.finalizarAtencion("V01");
        sistema.cancelarTurno(cancelado.getId());

        List<Turno> historial = sistema.consultarHistorial();

        assertEquals(List.of(atendido, enAtencion, cancelado, enEspera), historial);
        assertEquals(4, historial.stream().map(Turno::getEstado).distinct().count());
    }

    @Test
    void historialDevueltoNoPermiteModificarElSistema() {
        sistema.registrarTurno();
        assertThrows(UnsupportedOperationException.class, () -> sistema.consultarHistorial().clear());
    }
=======
>>>>>>> 2bc42a9ed31051bad8ed2cb79362667ee070d3d6
}
