package com.construccion.software;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class RegistroTurnosTest {

    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-11T10:00:00Z"), ZoneOffset.UTC);
    private final SistemaGestionTurnos sistema = new SistemaGestionTurnos(reloj);

    @Test
    void secuenciaEsIncrementalDesdeUno() {
        List<Long> secuencias = IntStream.range(0, 5)
                .mapToObj(i -> sistema.registrarTurno().getSecuencia())
                .toList();

        assertEquals(List.of(1L, 2L, 3L, 4L, 5L), secuencias);
    }

    @Test
    void idsYSecuenciasSonUnicos() {
        List<Turno> turnos = IntStream.range(0, 50).mapToObj(i -> sistema.registrarTurno()).toList();

        assertEquals(50, turnos.stream().map(Turno::getId).distinct().count());
        assertEquals(50, turnos.stream().map(Turno::getSecuencia).distinct().count());
    }

    @Test
    void turnoRegistradoQuedaEnEsperaSinVentanillaYConTimestamp() {
        Turno turno = sistema.registrarTurno();

        assertEquals(EstadoTurno.EN_ESPERA, turno.getEstado());
        assertNull(turno.getVentanillaAsignada());
        assertEquals(LocalDateTime.of(2026, 11, 11, 10, 0), turno.getFechaHoraRegistro());
    }

    @Test
    void turnoRegistradoSeAgregaAlFinalDeLaFila() {
        Turno primero = sistema.registrarTurno();
        Turno segundo = sistema.registrarTurno();

        assertEquals(List.of(primero, segundo), sistema.consultarFila());
        assertNotEquals(primero.getId(), segundo.getId());
    }

    @Test
    void registrarNoAlteraElHistorialPrevio() {
        Turno t1 = sistema.registrarTurno();
        sistema.registrarTurno();

        assertTrue(sistema.consultarHistorial().contains(t1));
        assertEquals(2, sistema.consultarHistorial().size());
    }
}
