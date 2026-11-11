package com.construccion.software;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SistemaGestionTurnos {

    private final Clock reloj;
    private final List<Turno> turnos = new ArrayList<>();
    private final Map<String, Ventanilla> ventanillas = new LinkedHashMap<>();
    private long ultimaSecuencia = 0;

    public SistemaGestionTurnos() {
        this(Clock.systemDefaultZone());
    }

    public SistemaGestionTurnos(Clock reloj) {
        this.reloj = reloj;
        Ventanilla.IDS_VALIDOS.forEach(id -> ventanillas.put(id, new Ventanilla(id)));
    }

    /** Crea un turno EN_ESPERA al final de la cola. */
    public Turno registrarTurno() {
        long secuencia = ++ultimaSecuencia;
        Turno turno = new Turno("T" + secuencia, secuencia, LocalDateTime.now(reloj));
        turnos.add(turno);
        return turno;
    }

    /** Turnos EN_ESPERA en orden ascendente de secuencia (FIFO). */
    public List<Turno> consultarFila() {
        return turnos.stream()
                .filter(t -> t.getEstado() == EstadoTurno.EN_ESPERA)
                .sorted(Comparator.comparingLong(Turno::getSecuencia))
                .toList();
    }

    /** Las tres ventanillas con su turno actual (o libres). */
    public List<Ventanilla> consultarVentanillas() {
        return List.copyOf(ventanillas.values());
    }

    /** Asigna el turno EN_ESPERA más antiguo a una ventanilla libre e inicia su atención. */
    public Turno asignarSiguiente(String idVentanilla) {
        Ventanilla ventanilla = buscarVentanilla(idVentanilla);
        if (!ventanilla.estaLibre()) {
            throw new IllegalStateException("La ventanilla " + ventanilla.getId()
                    + " está ocupada con el turno " + ventanilla.getTurnoActual().getId());
        }
        List<Turno> fila = consultarFila();
        if (fila.isEmpty()) {
            throw new IllegalStateException("No hay turnos en espera para asignar a " + ventanilla.getId());
        }
        Turno turno = fila.get(0);
        turno.iniciarAtencion(ventanilla.getId());
        ventanilla.ocupar(turno);
        return turno;
    }

    /** Finaliza el turno en atención de la ventanilla y la deja libre. */
    public Turno finalizarAtencion(String idVentanilla) {
        Ventanilla ventanilla = buscarVentanilla(idVentanilla);
        if (ventanilla.estaLibre()) {
            throw new IllegalStateException("La ventanilla " + ventanilla.getId() + " está libre: no hay atención por finalizar");
        }
        Turno turno = ventanilla.liberar();
        turno.finalizar();
        return turno;
    }

    private Ventanilla buscarVentanilla(String idVentanilla) {
        Ventanilla ventanilla = idVentanilla == null ? null : ventanillas.get(idVentanilla.trim());
        if (ventanilla == null) {
            throw new IllegalArgumentException("Ventanilla inexistente: " + idVentanilla
                    + ". Válidas: " + Ventanilla.IDS_VALIDOS);
        }
        return ventanilla;
    }
}
