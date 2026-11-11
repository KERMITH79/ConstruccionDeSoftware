package com.construccion.software;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class TurnoTest {

    private Turno nuevo() {
        return new Turno("T1", 1, LocalDateTime.of(2026, 11, 11, 9, 0));
    }

    @Test
    void turnoNuevoEstaEnEsperaSinVentanilla() {
        Turno turno = nuevo();
        assertEquals(EstadoTurno.EN_ESPERA, turno.getEstado());
        assertNull(turno.getVentanillaAsignada());
    }

    @Test
    void flujoCompletoHastaAtendido() {
        Turno turno = nuevo();
        turno.iniciarAtencion("V02");
        assertEquals(EstadoTurno.EN_ATENCION, turno.getEstado());
        assertEquals("V02", turno.getVentanillaAsignada());
        turno.finalizar();
        assertEquals(EstadoTurno.ATENDIDO, turno.getEstado());
    }

    @Test
    void cancelarDesdeEnEspera() {
        Turno turno = nuevo();
        turno.cancelar();
        assertEquals(EstadoTurno.CANCELADO, turno.getEstado());
    }

    @Test
    void noSePuedeCancelarUnTurnoEnAtencion() {
        Turno turno = nuevo();
        turno.iniciarAtencion("V01");
        assertThrows(IllegalStateException.class, turno::cancelar);
        assertEquals(EstadoTurno.EN_ATENCION, turno.getEstado());
    }

    @Test
    void noSePuedeFinalizarUnTurnoEnEspera() {
        assertThrows(IllegalStateException.class, () -> nuevo().finalizar());
    }

    @Test
    void estadosTerminalesNoTienenTransiciones() {
        Turno cancelado = nuevo();
        cancelado.cancelar();
        assertThrows(IllegalStateException.class, () -> cancelado.iniciarAtencion("V01"));
        assertThrows(IllegalStateException.class, cancelado::cancelar);

        Turno atendido = nuevo();
        atendido.iniciarAtencion("V01");
        atendido.finalizar();
        assertThrows(IllegalStateException.class, atendido::finalizar);
        assertThrows(IllegalStateException.class, atendido::cancelar);
    }

    @Test
    void validaDatosDeConstruccion() {
        LocalDateTime ahora = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new Turno(" ", 1, ahora));
        assertThrows(IllegalArgumentException.class, () -> new Turno("T1", 0, ahora));
        assertThrows(NullPointerException.class, () -> new Turno("T1", 1, null));
    }

    @Test
    void iniciarAtencionExigeVentanilla() {
        assertThrows(IllegalArgumentException.class, () -> nuevo().iniciarAtencion(null));
    }

    @Test
    void toStringIncluyeVentanillaCuandoExiste() {
        Turno turno = nuevo();
        turno.iniciarAtencion("V03");
        assertTrue(turno.toString().contains("V03"));
    }
}
