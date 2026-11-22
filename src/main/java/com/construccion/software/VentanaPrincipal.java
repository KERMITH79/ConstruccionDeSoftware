package com.construccion.software;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Interfaz gráfica principal del Sistema de Gestión de Turnos Universitarios (SGTU).
 * Desacoplada de las reglas de negocio: delega toda validación y persistencia a SistemaGestionTurnos.
 */
public class VentanaPrincipal extends JFrame {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SistemaGestionTurnos sistema;
    private final Map<String, JLabel> etiquetasVentanillas = new LinkedHashMap<>();
    private final DefaultTableModel modeloFila;
    private final DefaultTableModel modeloHistorial;
    private final JTextField txtIdCancelar;

    public VentanaPrincipal(SistemaGestionTurnos sistema) {
        this.sistema = Objects.requireNonNull(sistema, "El sistema de gestión de turnos es obligatorio");

        setTitle("Sistema de Gestión de Turnos Universitarios (SGTU)");
        setSize(960, 640);
        setMinimumSize(new java.awt.Dimension(820, 540));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Barra superior: Registro de turno y Cancelación por ID (RF-02, RF-08, RI-01, RI-03)
        JPanel panelControles = new JPanel(new BorderLayout(10, 10));
        panelControles.setBorder(BorderFactory.createEmptyBorder(10, 12, 0, 12));

        JPanel panelRegistro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelRegistro.setBorder(BorderFactory.createTitledBorder("Nuevo Turno"));
        JButton btnRegistrar = new JButton("Registrar turno");
        btnRegistrar.setFont(btnRegistrar.getFont().deriveFont(Font.BOLD));
        btnRegistrar.addActionListener(e -> registrarTurno());
        panelRegistro.add(btnRegistrar);

        JPanel panelCancelacion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        panelCancelacion.setBorder(BorderFactory.createTitledBorder("Cancelar Turno en Espera"));
        panelCancelacion.add(new JLabel("ID de turno (ej. T1):"));
        txtIdCancelar = new JTextField(10);
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> cancelarTurno());
        panelCancelacion.add(txtIdCancelar);
        panelCancelacion.add(btnCancelar);

        panelControles.add(panelRegistro, BorderLayout.WEST);
        panelControles.add(panelCancelacion, BorderLayout.EAST);
        add(panelControles, BorderLayout.NORTH);

        // 2. Panel central: Ventanillas V01-V03 + Tablas de Fila FIFO e Historial
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        JPanel panelVentanillas = construirPanelVentanillas();
        panelCentral.add(panelVentanillas, BorderLayout.NORTH);

        // Tabla de Fila en espera (FIFO)
        modeloFila = crearModeloNoEditable(new String[]{"ID", "Secuencia", "Fecha y hora de registro", "Estado"});
        JTable tablaFila = new JTable(modeloFila);
        JScrollPane scrollFila = new JScrollPane(tablaFila);
        scrollFila.setBorder(BorderFactory.createTitledBorder("Fila en Espera (Orden FIFO)"));

        // Tabla de Historial completo (4 estados: RF-10, RNF-U03)
        modeloHistorial = crearModeloNoEditable(
                new String[]{"ID", "Fecha y hora de registro", "Estado", "Ventanilla asignada"});
        JTable tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.getColumnModel().getColumn(2).setCellRenderer(new RenderizadorEstado());
        JScrollPane scrollHistorial = new JScrollPane(tablaHistorial);
        scrollHistorial.setBorder(BorderFactory.createTitledBorder("Historial General de Turnos"));

        JSplitPane splitTablas = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollFila, scrollHistorial);
        splitTablas.setResizeWeight(0.45);
        panelCentral.add(splitTablas, BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        actualizarVista();
    }

    private JPanel construirPanelVentanillas() {
        JPanel contenedor = new JPanel(new GridLayout(1, 3, 12, 0));
        contenedor.setBorder(BorderFactory.createTitledBorder("Ventanillas de Atención"));

        for (String idVentanilla : Ventanilla.IDS_VALIDOS) {
            JPanel tarjeta = new JPanel(new BorderLayout(6, 8));
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));

            JLabel lblTitulo = new JLabel("Ventanilla " + idVentanilla, SwingConstants.CENTER);
            lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 14f));

            JLabel lblEstado = new JLabel("Estado: Libre", SwingConstants.CENTER);
            lblEstado.setOpaque(true);
            etiquetasVentanillas.put(idVentanilla, lblEstado);

            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
            JButton btnAsignar = new JButton("Asignar siguiente");
            JButton btnFinalizar = new JButton("Finalizar");

            btnAsignar.addActionListener(e -> asignarSiguiente(idVentanilla));
            btnFinalizar.addActionListener(e -> finalizarAtencion(idVentanilla));

            panelBotones.add(btnAsignar);
            panelBotones.add(btnFinalizar);

            tarjeta.add(lblTitulo, BorderLayout.NORTH);
            tarjeta.add(lblEstado, BorderLayout.CENTER);
            tarjeta.add(panelBotones, BorderLayout.SOUTH);

            contenedor.add(tarjeta);
        }
        return contenedor;
    }

    /** Registra un nuevo turno y confirma visualmente el ID generado (RF-02, RF-12). */
    private void registrarTurno() {
        try {
            Turno creado = sistema.registrarTurno();
            actualizarVista();
            JOptionPane.showMessageDialog(
                    this,
                    "Turno registrado correctamente.\nIdentificador asignado: " + creado.getId(),
                    "Turno Registrado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException | IllegalArgumentException | UncheckedIOException ex) {
            mostrarError("No se pudo registrar el turno", ex);
        }
    }

    /** Solicita confirmación explícita antes de cancelar un turno en espera (RF-08, RNF-U04). */
    private void cancelarTurno() {
        String idIngresado = txtIdCancelar.getText();
        if (idIngresado == null || idIngresado.isBlank()) {
            mostrarError(
                    "Datos incompletos",
                    new IllegalArgumentException("Debe ingresar el identificador del turno que desea cancelar (ejemplo: T1)."));
            return;
        }

        String idLimpio = idIngresado.trim();
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de que desea cancelar el turno " + idLimpio + "?\n"
                        + "El turno será retirado de la fila y pasará al estado Cancelado.",
                "Confirmar cancelación de turno",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        // RNF-U04: Si el operador rechaza la confirmación, no se realiza ningún cambio.
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Turno cancelado = sistema.cancelarTurno(idLimpio);
            txtIdCancelar.setText("");
            actualizarVista();
            JOptionPane.showMessageDialog(
                    this,
                    "El turno " + cancelado.getId() + " fue cancelado y conservado en el historial.",
                    "Cancelación Confirmada",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException | IllegalArgumentException | UncheckedIOException ex) {
            mostrarError("No se pudo cancelar el turno " + idLimpio, ex);
        }
    }

    private void asignarSiguiente(String idVentanilla) {
        try {
            Turno asignado = sistema.asignarSiguiente(idVentanilla);
            actualizarVista();
            JOptionPane.showMessageDialog(
                    this,
                    "Turno " + asignado.getId() + " asignado a la ventanilla " + idVentanilla + ".",
                    "Asignación Realizada",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException | IllegalArgumentException | UncheckedIOException ex) {
            mostrarError("No se pudo asignar turno en " + idVentanilla, ex);
        }
    }

    private void finalizarAtencion(String idVentanilla) {
        try {
            Turno finalizado = sistema.finalizarAtencion(idVentanilla);
            actualizarVista();
            JOptionPane.showMessageDialog(
                    this,
                    "Atención del turno " + finalizado.getId() + " finalizada en " + idVentanilla + ".",
                    "Atención Finalizada",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException | IllegalArgumentException | UncheckedIOException ex) {
            mostrarError("No se pudo finalizar atención en " + idVentanilla, ex);
        }
    }

    /** Refresca ventanillas, fila FIFO e historial para mantener consistencia visual (RI-02). */
    private void actualizarVista() {
        for (Ventanilla ventanilla : sistema.consultarVentanillas()) {
            JLabel etiqueta = etiquetasVentanillas.get(ventanilla.getId());
            if (etiqueta != null) {
                if (ventanilla.estaLibre()) {
                    etiqueta.setText("Estado: Libre");
                    etiqueta.setBackground(new Color(220, 245, 225));
                    etiqueta.setForeground(new Color(20, 90, 35));
                } else {
                    etiqueta.setText("Estado: En atención (" + ventanilla.getTurnoActual().getId() + ")");
                    etiqueta.setBackground(new Color(255, 236, 204));
                    etiqueta.setForeground(new Color(140, 70, 0));
                }
            }
        }

        modeloFila.setRowCount(0);
        List<Turno> fila = sistema.consultarFila();
        for (Turno turno : fila) {
            modeloFila.addRow(new Object[]{
                turno.getId(),
                turno.getSecuencia(),
                turno.getFechaHoraRegistro().format(FORMATO_FECHA),
                formatearEstado(turno.getEstado())
            });
        }

        modeloHistorial.setRowCount(0);
        List<Turno> historial = sistema.consultarHistorial();
        for (Turno turno : historial) {
            String ventanilla = turno.getVentanillaAsignada() != null
                    ? turno.getVentanillaAsignada()
                    : "Sin asignar";
            modeloHistorial.addRow(new Object[]{
                turno.getId(),
                turno.getFechaHoraRegistro().format(FORMATO_FECHA),
                formatearEstado(turno.getEstado()),
                ventanilla
            });
        }
    }

    /** Presenta mensajes comprensibles en español indicando causa e ID implicado (RNF-U02, RF-12). */
    private void mostrarError(String contexto, RuntimeException ex) {
        String detalle;
        if (ex instanceof UncheckedIOException) {
            detalle = "Error de almacenamiento local: " + ex.getMessage()
                    + "\nCausa: " + ex.getCause().getMessage()
                    + "\nLos datos previos en memoria se conservaron sin cambios.";
        } else {
            detalle = ex.getMessage();
        }
        JOptionPane.showMessageDialog(
                this,
                contexto + ":\n" + detalle,
                "Operación no permitida",
                JOptionPane.ERROR_MESSAGE);
    }

    private static String formatearEstado(EstadoTurno estado) {
        return switch (estado) {
            case EN_ESPERA -> "En espera";
            case EN_ATENCION -> "En atención";
            case ATENDIDO -> "Atendido";
            case CANCELADO -> "Cancelado";
        };
    }

    private static DefaultTableModel crearModeloNoEditable(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /** Distingue visualmente los 4 estados acompañando siempre el texto explícito (RNF-U03). */
    private static class RenderizadorEstado extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected && value != null) {
                String estado = value.toString();
                switch (estado) {
                    case "En espera" -> {
                        c.setBackground(new Color(232, 242, 255));
                        c.setForeground(new Color(15, 65, 130));
                    }
                    case "En atención" -> {
                        c.setBackground(new Color(255, 240, 210));
                        c.setForeground(new Color(145, 75, 0));
                    }
                    case "Atendido" -> {
                        c.setBackground(new Color(224, 246, 228));
                        c.setForeground(new Color(20, 95, 35));
                    }
                    case "Cancelado" -> {
                        c.setBackground(new Color(252, 226, 226));
                        c.setForeground(new Color(145, 25, 25));
                    }
                    default -> {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
            }
            return c;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                SistemaGestionTurnos sistema = new SistemaGestionTurnos(Path.of("datos", "turnos.dat"));
                new VentanaPrincipal(sistema).setVisible(true);
            } catch (UncheckedIOException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "No fue posible iniciar el sistema porque los datos guardados están dañados o ilegibles:\n"
                                + ex.getMessage() + "\n\nEl archivo existente no fue sobrescrito.",
                        "Error crítico de persistencia (RD-03)",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}