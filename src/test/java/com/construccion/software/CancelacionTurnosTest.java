package com.construccion.software;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class CancelacionTurnosTest {

    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-11T10:00:00Z"), ZoneOffset.UTC);
    private final SistemaGestionTurnos sistema = new SistemaGestionTurnos(reloj);

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
    void cancelarTurnoEnAtencionAtendidoOCanceladoSeRechazaSinCambios() {
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
    void turnoCanceladoNuncaVuelveALaFila() {
        Turno t1 = sistema.registrarTurno();
        sistema.cancelarTurno(t1.getId());
        sistema.registrarTurno();
        sistema.asignarSiguiente("V01");

        assertTrue(sistema.consultarFila().isEmpty());
        assertEquals(EstadoTurno.CANCELADO, t1.getEstado());
    }
}
