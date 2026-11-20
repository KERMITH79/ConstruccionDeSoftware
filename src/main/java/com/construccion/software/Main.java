package com.construccion.software;

import java.awt.GraphicsEnvironment;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Punto de entrada: carga los datos locales y abre la vista, o informa el error sin tocar el archivo. */
public final class Main {

    static final Path ARCHIVO_DATOS = Path.of("datos", "turnos.dat");

    private Main() {
    }

    public static void main(String[] args) {
        int codigo = iniciar(ARCHIVO_DATOS, Main::abrirVista, Main::mostrarError);
        if (codigo != 0) {
            System.exit(codigo);
        }
    }

    /**
     * Crea el sistema sobre el archivo indicado. Si la lectura falla o los datos son inválidos, el constructor
     * no guarda nada: se informa el error y se devuelve un código de salida distinto de cero.
     */
    static int iniciar(Path archivo, Consumer<SistemaGestionTurnos> abrirVista, Consumer<String> mostrarError) {
        SistemaGestionTurnos sistema;
        try {
            sistema = new SistemaGestionTurnos(archivo);
        } catch (UncheckedIOException | IllegalStateException | IllegalArgumentException ex) {
            mostrarError.accept("No se pudo iniciar el sistema de turnos.\n" + ex.getMessage()
                    + "\n\nEl archivo " + archivo + " no fue modificado. Revíselo o restáurelo antes de volver a abrir la aplicación.");
            return 1;
        }
        abrirVista.accept(sistema);
        return 0;
    }

    /** Enlace con la vista principal; se reemplaza por la ventana Swing al integrar los issues de interfaz (#9, #10). */
    private static void abrirVista(SistemaGestionTurnos sistema) {
        String resumen = "Sistema iniciado: " + sistema.consultarHistorial().size() + " turno(s) registrado(s), "
                + sistema.consultarFila().size() + " en espera.";
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println(resumen);
            return;
        }
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, resumen, "Gestión de turnos",
                JOptionPane.INFORMATION_MESSAGE));
    }

    private static void mostrarError(String mensaje) {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println(mensaje);
            return;
        }
        JOptionPane.showMessageDialog(null, mensaje, "Error al iniciar", JOptionPane.ERROR_MESSAGE);
    }
}
