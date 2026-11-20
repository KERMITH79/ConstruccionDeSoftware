package com.construccion.software;

import java.awt.GraphicsEnvironment;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Punto de entrada: carga los datos locales y abre la vista, o informa el error sin tocar el archivo. */
public final class Main {

    /** Propiedad del sistema que permite cambiar la ubicación del archivo de datos. */
    static final String PROPIEDAD_ARCHIVO = "turnos.archivo";
    private static final String ARCHIVO_POR_DEFECTO = "datos/turnos.dat";

    private Main() {
    }

    public static void main(String[] args) {
        int codigo = iniciar(Path.of(System.getProperty(PROPIEDAD_ARCHIVO, ARCHIVO_POR_DEFECTO)),
                Main::abrirVista, Main::mostrarError);
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
    static void abrirVista(SistemaGestionTurnos sistema) {
        notificar("Gestión de turnos", "Sistema iniciado: " + sistema.consultarHistorial().size()
                + " turno(s) registrado(s), " + sistema.consultarFila().size() + " en espera.",
                JOptionPane.INFORMATION_MESSAGE);
    }

    static void mostrarError(String mensaje) {
        notificar("Error al iniciar", mensaje, JOptionPane.ERROR_MESSAGE);
    }

    /** Muestra un diálogo; sin entorno gráfico escribe el mensaje en la consola. */
    private static void notificar(String titulo, String mensaje, int tipo) {
        if (GraphicsEnvironment.isHeadless()) {
            (tipo == JOptionPane.ERROR_MESSAGE ? System.err : System.out).println(mensaje);
            return;
        }
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, mensaje, titulo, tipo));
    }
}
