package com.construccion.software;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RendimientoTurnosTest {

    private static final long LIMITE_NANOS = TimeUnit.SECONDS.toNanos(2);

    @TempDir
    Path directorio;

    @Test
    void operacionesConDoscientosTurnosRespondenEnMenosDeDosSegundos() {
        Path archivo = directorio.resolve("turnos.dat");
        SistemaGestionTurnos sistema = new SistemaGestionTurnos(Clock.systemUTC(), archivo);
        for (int i = 0; i < 199; i++) {
            sistema.registrarTurno();
        }

        long inicio = System.nanoTime();
        Turno turnoRegistrado = sistema.registrarTurno();
        long registroNanos = System.nanoTime() - inicio;
        assertEquals(200, sistema.consultarHistorial().size());

        inicio = System.nanoTime();
        List<Turno> fila = sistema.consultarFila();
        long consultaNanos = System.nanoTime() - inicio;
        assertEquals(200, fila.size());

        inicio = System.nanoTime();
        Turno asignado = sistema.asignarSiguiente("V01");
        long asignacionNanos = System.nanoTime() - inicio;
        assertEquals(fila.get(0), asignado);

        inicio = System.nanoTime();
        Turno finalizado = sistema.finalizarAtencion("V01");
        long finalizacionNanos = System.nanoTime() - inicio;
        assertEquals(asignado, finalizado);
        assertEquals(EstadoTurno.ATENDIDO, asignado.getEstado());
        assertEquals(EstadoTurno.EN_ESPERA, turnoRegistrado.getEstado());

        assertDentroDelLimite("registrar", registroNanos);
        assertDentroDelLimite("consultar fila", consultaNanos);
        assertDentroDelLimite("asignar", asignacionNanos);
        assertDentroDelLimite("finalizar", finalizacionNanos);

        System.out.printf(
                Locale.ROOT,
                "RNF-P01 (200 turnos): registrar=%.3f ms, consultarFila=%.3f ms, asignar=%.3f ms, finalizar=%.3f ms%n",
                aMilisegundos(registroNanos),
                aMilisegundos(consultaNanos),
                aMilisegundos(asignacionNanos),
                aMilisegundos(finalizacionNanos));
    }

    private static void assertDentroDelLimite(String operacion, long duracionNanos) {
        assertTrue(
                duracionNanos <= LIMITE_NANOS,
                () -> operacion + " tardó " + aMilisegundos(duracionNanos) + " ms; máximo: 2000 ms");
    }

    private static double aMilisegundos(long duracionNanos) {
        return duracionNanos / 1_000_000.0;
    }
}
