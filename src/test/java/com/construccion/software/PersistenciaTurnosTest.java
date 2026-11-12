package com.construccion.software;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Comprueba recuperación, rechazo de archivos corruptos y fallos de guardado. */
class PersistenciaTurnosTest {

    private static final Clock RELOJ =
            Clock.fixed(Instant.parse("2026-11-11T10:00:00Z"), ZoneOffset.UTC);

    @TempDir
    Path directorioTemporal;

    @Test
    void recuperaTurnosEstadosYAsignacionesAlReiniciar() {
        Path archivo = directorioTemporal.resolve("turnos.dat");
        SistemaGestionTurnos sistema = new SistemaGestionTurnos(RELOJ, archivo);
        Turno atendido = sistema.registrarTurno();
        Turno enAtencion = sistema.registrarTurno();
        Turno cancelado = sistema.registrarTurno();
        sistema.asignarSiguiente("V01");
        sistema.finalizarAtencion("V01");
        sistema.asignarSiguiente("V02");
        sistema.cancelarTurno(cancelado.getId());

        SistemaGestionTurnos reiniciado = new SistemaGestionTurnos(RELOJ, archivo);

        assertEquals(List.of(EstadoTurno.ATENDIDO, EstadoTurno.EN_ATENCION, EstadoTurno.CANCELADO),
                reiniciado.consultarHistorial().stream().map(Turno::getEstado).toList());
        assertEquals("V01", reiniciado.consultarHistorial().get(0).getVentanillaAsignada());
        assertEquals(enAtencion.getId(), reiniciado.consultarVentanillas().get(1).getTurnoActual().getId());
        assertEquals("T4", reiniciado.registrarTurno().getId());
    }

    @Test
    void archivoCorruptoRechazaElArranqueSinSobrescribirlo() throws IOException {
        Path archivo = directorioTemporal.resolve("turnos.dat");
        new SistemaGestionTurnos(RELOJ, archivo).registrarTurno();
        byte[] corrupto = Files.readAllBytes(archivo);
        corrupto[0] ^= 0x01;
        Files.write(archivo, corrupto);

        assertThrows(UncheckedIOException.class, () -> new SistemaGestionTurnos(RELOJ, archivo));

        assertArrayEquals(corrupto, Files.readAllBytes(archivo));
    }

    @Test
    void falloAlRegistrarNoConsumeSecuenciaNiAgregaTurno() {
        PersistenciaFalla persistencia = new PersistenciaFalla();
        SistemaGestionTurnos sistema = new SistemaGestionTurnos(RELOJ, persistencia);

        persistencia.fallar = true;
        assertThrows(UncheckedIOException.class, sistema::registrarTurno);

        assertTrue(sistema.consultarHistorial().isEmpty());
        persistencia.fallar = false;
        assertEquals("T1", sistema.registrarTurno().getId());
    }

    @Test
    void fallosDeAsignarFinalizarOCancelarConservanEstadoEnMemoria() {
        PersistenciaFalla persistencia = new PersistenciaFalla();
        SistemaGestionTurnos sistema = new SistemaGestionTurnos(RELOJ, persistencia);
        Turno asignado = sistema.registrarTurno();
        Turno pendiente = sistema.registrarTurno();

        persistencia.fallar = true;
        assertThrows(UncheckedIOException.class, () -> sistema.asignarSiguiente("V01"));
        assertEquals(EstadoTurno.EN_ESPERA, asignado.getEstado());
        assertTrue(sistema.consultarVentanillas().get(0).estaLibre());

        persistencia.fallar = false;
        sistema.asignarSiguiente("V01");
        persistencia.fallar = true;
        assertThrows(UncheckedIOException.class, () -> sistema.finalizarAtencion("V01"));
        assertEquals(EstadoTurno.EN_ATENCION, asignado.getEstado());
        assertEquals(asignado, sistema.consultarVentanillas().get(0).getTurnoActual());

        assertThrows(UncheckedIOException.class, () -> sistema.cancelarTurno(pendiente.getId()));
        assertEquals(EstadoTurno.EN_ESPERA, pendiente.getEstado());
    }

    private static final class PersistenciaFalla implements PersistenciaTurnos {

        private List<Registro> registros = List.of();
        private boolean fallar;

        @Override
        public List<Registro> cargar() {
            return registros;
        }

        @Override
        public void guardar(List<Registro> registros) throws IOException {
            if (fallar) {
                throw new IOException("Fallo de escritura simulado");
            }
            this.registros = List.copyOf(registros);
        }
    }
}
