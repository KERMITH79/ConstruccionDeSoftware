package com.construccion.software;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class VentanillaTest {

    private Turno turno(long secuencia) {
        return new Turno("T" + secuencia, secuencia, LocalDateTime.now());
    }

    @Test
    void aceptaSoloIdsPredefinidos() {
        for (String id : Ventanilla.IDS_VALIDOS) {
            assertEquals(id, new Ventanilla(id).getId());
        }
        assertThrows(IllegalArgumentException.class, () -> new Ventanilla("V04"));
        assertThrows(IllegalArgumentException.class, () -> new Ventanilla(null));
    }

    @Test
    void nuevaVentanillaEstaLibre() {
        Ventanilla ventanilla = new Ventanilla("V01");
        assertTrue(ventanilla.estaLibre());
        assertNull(ventanilla.getTurnoActual());
    }

    @Test
    void ocuparYLiberar() {
        Ventanilla ventanilla = new Ventanilla("V01");
        Turno turno = turno(1);
        ventanilla.ocupar(turno);
        assertFalse(ventanilla.estaLibre());
        assertSame(turno, ventanilla.getTurnoActual());
        assertSame(turno, ventanilla.liberar());
        assertTrue(ventanilla.estaLibre());
    }

    @Test
    void noAdmiteDosTurnosALaVez() {
        Ventanilla ventanilla = new Ventanilla("V02");
        Turno primero = turno(1);
        ventanilla.ocupar(primero);
        assertThrows(IllegalStateException.class, () -> ventanilla.ocupar(turno(2)));
        assertSame(primero, ventanilla.getTurnoActual());
    }

    @Test
    void noSePuedeLiberarUnaVentanillaLibre() {
        assertThrows(IllegalStateException.class, () -> new Ventanilla("V03").liberar());
    }

    @Test
    void noAceptaTurnoNulo() {
        assertThrows(IllegalArgumentException.class, () -> new Ventanilla("V01").ocupar(null));
    }
}
