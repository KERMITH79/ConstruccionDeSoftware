package com.construccion.software.modelo;

public class Ventanilla {
    private String id;
    private String turnoActual;

    public Ventanilla(String id) {
        this.id = id;
        this.turnoActual = null;
    }

    public boolean estaDisponible() {
        return turnoActual == null;
    }

    //Métodos getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTurnoActual() {
        return turnoActual;
    }

    public void setTurnoActual(String turnoActual) {
        this.turnoActual = turnoActual;
    }
}
