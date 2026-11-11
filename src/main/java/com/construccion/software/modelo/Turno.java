package com.construccion.software.modelo;

import java.time.LocalDateTime;

public class Turno {
    private String id;
    private int secuencia;
    private LocalDateTime fechaHoraRegistro;
    private EstadoTurno estado;
    private String ventanillaAsignada;

    //Constructor
    public Turno(String id, int secuencia, LocalDateTime fechaHoraRegistro, EstadoTurno estado,
            String ventanillaAsignada) {
        this.id = id;
        this.secuencia = secuencia;
        this.fechaHoraRegistro = fechaHoraRegistro;
        this.estado = estado;
        this.ventanillaAsignada = ventanillaAsignada;
    }

    //Métodos getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(int secuencia) {
        this.secuencia = secuencia;
    }

    public LocalDateTime getFechaHoraRegistro() {
        return fechaHoraRegistro;
    }

    public void setFechaHoraRegistro(LocalDateTime fechaHoraRegistro) {
        this.fechaHoraRegistro = fechaHoraRegistro;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }

    public String getVentanillaAsignada() {
        return ventanillaAsignada;
    }

    public void setVentanillaAsignada(String ventanillaAsignada) {
        this.ventanillaAsignada = ventanillaAsignada;
    }

    //Método toString
    @Override
    public String toString() {
        return "Turno [id=" + id + ", secuencia=" + secuencia + ", fechaHoraRegistro=" + fechaHoraRegistro + ", estado="
                + estado + ", ventanillaAsignada=" + ventanillaAsignada + "]";
    }
}
