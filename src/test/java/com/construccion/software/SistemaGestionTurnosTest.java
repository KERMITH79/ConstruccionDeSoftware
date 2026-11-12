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
        assertEquals(List.of(t2), sistema.consultarFila());
    }

    @Test
    void rechazaAsignarConFilaVaciaSinCambios() {
        assertThrows(IllegalStateException.class, () -> sistema.asignarSiguiente("V01"));
        assertTrue(sistema.consultarVentanillas().stream().allMatch(Ventanilla::estaLibre));
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
    void finalizarAtencionLiberaLaVentanillaYMarcaAtendido() {
        Turno t1 = sistema.registrarTurno();
        Turno t2 = sistema.registrarTurno();
        sistema.asignarSiguiente("V02");

        assertSame(t1, sistema.finalizarAtencion("V02"));

        assertEquals(EstadoTurno.ATENDIDO, t1.getEstado());
        assertTrue(sistema.consultarVentanillas().get(1).estaLibre());
        assertSame(t2, sistema.asignarSiguiente("V02"));
    }

    @Test
    void rechazaFinalizarVentanillaLibreSinCambios() {
        Turno t1 = sistema.registrarTurno();
        sistema.asignarSiguiente("V01");

        assertThrows(IllegalStateException.class, () -> sistema.finalizarAtencion("V02"));

        assertEquals(EstadoTurno.EN_ATENCION, t1.getEstado());
        assertEquals(t1, sistema.consultarVentanillas().get(0).getTurnoActual());
    }
}
