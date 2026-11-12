package com.construccion.software;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class HistorialTurnosTest {

    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-11T10:00:00Z"), ZoneOffset.UTC);
    private final SistemaGestionTurnos sistema = new SistemaGestionTurnos(reloj);

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
    void turnoAtendidoPermaneceEnHistorial() {
        Turno turno = sistema.registrarTurno();
        sistema.asignarSiguiente("V03");
        sistema.finalizarAtencion("V03");

        assertTrue(sistema.consultarHistorial().contains(turno));
        assertTrue(sistema.consultarFila().isEmpty());
    }

    @Test
    void historialVacioSinTurnos() {
        assertTrue(sistema.consultarHistorial().isEmpty());
    }

    @Test
    void historialDevueltoNoPermiteModificarElSistema() {
        sistema.registrarTurno();

        assertThrows(UnsupportedOperationException.class, () -> sistema.consultarHistorial().clear());
        assertEquals(1, sistema.consultarHistorial().size());
    }
}
