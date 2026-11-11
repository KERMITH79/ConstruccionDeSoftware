package com.construccion.software;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SistemaGestionTurnos {

    private final Clock reloj;
    private final List<Turno> turnos = new ArrayList<>();
    private long ultimaSecuencia = 0;

    public SistemaGestionTurnos() {
        this(Clock.systemDefaultZone());
    }

    public SistemaGestionTurnos(Clock reloj) {
        this.reloj = reloj;
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
}
