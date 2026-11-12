package com.construccion.software;

import java.time.LocalDateTime;
import java.util.Objects;

public class Turno {

    private final String id;
    private final long secuencia;
    private final LocalDateTime fechaHoraRegistro;
    private EstadoTurno estado;
    private String ventanillaAsignada;

    /** Crea un turno nuevo, inicialmente en espera y sin ventanilla asignada. */
    public Turno(String id, long secuencia, LocalDateTime fechaHoraRegistro) {
        this(id, secuencia, fechaHoraRegistro, EstadoTurno.EN_ESPERA, null);
    }

    /** Restaura un turno desde los datos recuperados de persistencia. */
    Turno(
            String id,
            long secuencia,
            LocalDateTime fechaHoraRegistro,
            EstadoTurno estado,
            String ventanillaAsignada) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del turno es obligatorio");
        }
        if (secuencia <= 0) {
            throw new IllegalArgumentException("La secuencia debe ser positiva");
        }
        this.id = id;
        this.secuencia = secuencia;
        this.fechaHoraRegistro = Objects.requireNonNull(fechaHoraRegistro, "La fecha de registro es obligatoria");
        this.estado = Objects.requireNonNull(estado, "El estado del turno es obligatorio");
        this.ventanillaAsignada = ventanillaAsignada;
    }

    public String getId() {
        return id;
    }

    public long getSecuencia() {
        return secuencia;
    }

    public LocalDateTime getFechaHoraRegistro() {
        return fechaHoraRegistro;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    /** Es nula mientras el turno no ha sido asignado. */
    public String getVentanillaAsignada() {
        return ventanillaAsignada;
    }

    /** EN_ESPERA -> EN_ATENCION. */
    public void iniciarAtencion(String ventanillaId) {
        exigirEstado(EstadoTurno.EN_ESPERA, "iniciar la atención");
        if (ventanillaId == null || ventanillaId.isBlank()) {
            throw new IllegalArgumentException("La ventanilla es obligatoria");
        }
        this.estado = EstadoTurno.EN_ATENCION;
        this.ventanillaAsignada = ventanillaId;
    }

    /** EN_ATENCION -> ATENDIDO. */
    public void finalizar() {
        exigirEstado(EstadoTurno.EN_ATENCION, "finalizar");
        this.estado = EstadoTurno.ATENDIDO;
    }

    /** EN_ESPERA -> CANCELADO. */
    public void cancelar() {
        exigirEstado(EstadoTurno.EN_ESPERA, "cancelar");
        this.estado = EstadoTurno.CANCELADO;
    }

    private void exigirEstado(EstadoTurno requerido, String operacion) {
        if (estado != requerido) {
            throw new IllegalStateException(
                    "No se puede " + operacion + " el turno " + id + ": está " + estado + " y debe estar " + requerido);
        }
    }

    @Override
    public String toString() {
        return id + " [" + estado + (ventanillaAsignada != null ? " en " + ventanillaAsignada : "") + "]";
    }
}
