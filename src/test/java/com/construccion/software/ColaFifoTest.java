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

class ColaFifoTest {

    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-11T10:00:00Z"), ZoneOffset.UTC);
    private final SistemaGestionTurnos sistema = new SistemaGestionTurnos(reloj);

    @Test
    void filaVaciaSinTurnos() {
        assertTrue(sistema.consultarFila().isEmpty());
    }

    @Test
    void consultarFilaDevuelveOrdenFifo() {
        Turno t1 = sistema.registrarTurno();
        Turno t2 = sistema.registrarTurno();
        Turno t3 = sistema.registrarTurno();

        assertEquals(List.of(t1, t2, t3), sistema.consultarFila());
    }

    @Test
    void ordenFifoNoDependeDelOrdenAlfabeticoDelId() {
        List<Turno> registrados = IntStream.range(0, 12).mapToObj(i -> sistema.registrarTurno()).toList();

        assertEquals(registrados, sistema.consultarFila());
    }

    @Test
    void filaExcluyeTurnosQueYaNoEstanEnEspera() {
        Turno t1 = sistema.registrarTurno();
        Turno t2 = sistema.registrarTurno();
        Turno t3 = sistema.registrarTurno();
        sistema.asignarSiguiente("V01");
        sistema.cancelarTurno(t2.getId());

        assertEquals(List.of(t3), sistema.consultarFila());
        assertEquals(EstadoTurno.EN_ATENCION, t1.getEstado());
    }

    @Test
    void asignacionesSiguenElOrdenFifoSinImportarLaVentanilla() {
        Turno t1 = sistema.registrarTurno();
        Turno t2 = sistema.registrarTurno();
        Turno t3 = sistema.registrarTurno();

        assertSame(t1, sistema.asignarSiguiente("V02"));
        assertSame(t2, sistema.asignarSiguiente("V01"));
        assertEquals(List.of(t3), sistema.consultarFila());
    }

    @Test
    void filaDevueltaNoPermiteModificarElSistema() {
        sistema.registrarTurno();

        assertThrows(UnsupportedOperationException.class,
                () -> sistema.consultarFila().clear());
        assertEquals(1, sistema.consultarFila().size());
    }
}
