package com.construccion.software;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class SistemaGestionTurnosTest {

    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-11T10:00:00Z"), ZoneOffset.UTC);
    private final SistemaGestionTurnos sistema = new SistemaGestionTurnos(reloj);

    @Test
    void registraTurnosConSecuenciaEIdUnicos() {
        Turno t1 = sistema.registrarTurno();
        Turno t2 = sistema.registrarTurno();
        Turno t3 = sistema.registrarTurno();

        assertEquals(List.of(1L, 2L, 3L), List.of(t1.getSecuencia(), t2.getSecuencia(), t3.getSecuencia()));
        assertEquals(3, List.of(t1.getId(), t2.getId(), t3.getId()).stream().distinct().count());
    }

    @Test
    void turnoRegistradoQuedaEnEsperaConTimestamp() {
        Turno turno = sistema.registrarTurno();
        assertEquals(EstadoTurno.EN_ESPERA, turno.getEstado());
        assertEquals(LocalDateTime.of(2026, 11, 11, 10, 0), turno.getFechaHoraRegistro());
    }

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
    void filaExcluyeTurnosQueYaNoEstanEnEspera() {
        Turno t1 = sistema.registrarTurno();
        Turno t2 = sistema.registrarTurno();
        Turno t3 = sistema.registrarTurno();
        t1.iniciarAtencion("V01");
        t2.cancelar();
        assertEquals(List.of(t3), sistema.consultarFila());
    }

    @Test
    void ordenFifoNoDependeDelOrdenAlfabeticoDelId() {
        List<Turno> registrados = java.util.stream.IntStream.range(0, 12)
                .mapToObj(i -> sistema.registrarTurno())
                .collect(Collectors.toList());
        assertEquals(registrados, sistema.consultarFila());
    }
}
