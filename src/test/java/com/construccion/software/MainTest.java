package com.construccion.software;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {

    @TempDir
    Path directorio;

    @AfterEach
    void limpiarPropiedad() {
        System.clearProperty(Main.PROPIEDAD_ARCHIVO);
    }

    @Test
    void sinArchivoInicia() {
        AtomicReference<SistemaGestionTurnos> vista = new AtomicReference<>();

        int codigo = Main.iniciar(directorio.resolve("turnos.dat"), vista::set, mensaje -> {
            throw new AssertionError("No debía mostrar error: " + mensaje);
        });

        assertEquals(0, codigo);
        assertNotNull(vista.get());
        assertTrue(vista.get().consultarHistorial().isEmpty());
    }

    @Test
    void recuperaTurnosSecuenciaYVentanillasOcupadasAlReiniciar() {
        Path archivo = directorio.resolve("datos").resolve("turnos.dat");
        AtomicReference<SistemaGestionTurnos> primera = new AtomicReference<>();
        Main.iniciar(archivo, primera::set, mensaje -> { });
        primera.get().registrarTurno();
        primera.get().registrarTurno();
        primera.get().registrarTurno();
        primera.get().asignarSiguiente("V02");

        AtomicReference<SistemaGestionTurnos> segunda = new AtomicReference<>();
        int codigo = Main.iniciar(archivo, segunda::set, mensaje -> { });

        assertEquals(0, codigo);
        SistemaGestionTurnos sistema = segunda.get();
        assertEquals(3, sistema.consultarHistorial().size());
        assertEquals(List.of("T2", "T3"), sistema.consultarFila().stream().map(Turno::getId).toList());
        assertFalse(sistema.consultarVentanillas().get(1).estaLibre());
        assertEquals("T1", sistema.consultarVentanillas().get(1).getTurnoActual().getId());
        assertEquals("T4", sistema.registrarTurno().getId());
    }

    @Test
    void archivoCorruptoInformaElErrorYNoLoSobrescribe() throws IOException {
        Path archivo = directorio.resolve("turnos.dat");
        byte[] corrupto = "contenido que no es un archivo de turnos válido, ni de lejos".getBytes();
        Files.write(archivo, corrupto);
        List<String> errores = new ArrayList<>();
        AtomicReference<SistemaGestionTurnos> vista = new AtomicReference<>();

        int codigo = Main.iniciar(archivo, vista::set, errores::add);

        assertEquals(1, codigo);
        assertEquals(1, errores.size());
        assertTrue(errores.get(0).contains("no fue modificado"));
        assertSame(null, vista.get());
        assertArrayEquals(corrupto, Files.readAllBytes(archivo));
    }

    @Test
    void mainUsaElArchivoIndicadoPorPropiedadYArrancaSinSalir() {
        Path archivo = directorio.resolve("turnos.dat");
        System.setProperty(Main.PROPIEDAD_ARCHIVO, archivo.toString());

        Main.main(new String[0]);

        assertFalse(Files.exists(archivo));
    }

    @Test
    void mensajesSeMuestranSinInterfazGrafica() {
        SistemaGestionTurnos sistema = new SistemaGestionTurnos();
        sistema.registrarTurno();

        Main.abrirVista(sistema);
        Main.mostrarError("error de prueba");
    }
}
