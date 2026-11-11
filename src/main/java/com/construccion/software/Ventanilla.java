package com.construccion.software;

import java.util.List;

public class Ventanilla {

    public static final List<String> IDS_VALIDOS = List.of("V01", "V02", "V03");

    private final String id;
    private Turno turnoActual;

    public Ventanilla(String id) {
        if (id == null || !IDS_VALIDOS.contains(id)) {
            throw new IllegalArgumentException("Ventanilla inexistente: " + id + ". Válidas: " + IDS_VALIDOS);
        }
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /** Es nulo cuando la ventanilla está libre. */
    public Turno getTurnoActual() {
        return turnoActual;
    }

    public boolean estaLibre() {
        return turnoActual == null;
    }

    public void ocupar(Turno turno) {
        if (turno == null) {
            throw new IllegalArgumentException("El turno es obligatorio");
        }
        if (!estaLibre()) {
            throw new IllegalStateException("La ventanilla " + id + " ya atiende el turno " + turnoActual.getId());
        }
        this.turnoActual = turno;
    }

    public Turno liberar() {
        if (estaLibre()) {
            throw new IllegalStateException("La ventanilla " + id + " está libre");
        }
        Turno finalizado = turnoActual;
        this.turnoActual = null;
        return finalizado;
    }
}
