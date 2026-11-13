package com.construccion.software;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Abstracción para cargar y guardar el estado persistido de los turnos. */
public interface PersistenciaTurnos {

    /**
     * Carga los registros almacenados.
     *
     * @return registros recuperados, o una lista vacía si todavía no hay datos
     * @throws IOException si no se pueden leer o validar los datos almacenados
     */
    List<Registro> cargar() throws IOException;

    /**
     * Guarda el estado completo recibido.
     *
     * @throws IOException si no se pudo guardar el estado
     */
    void guardar(List<Registro> registros) throws IOException;

    /** Representación inmutable de un turno para persistencia. */
    record Registro(
            String id,
            long secuencia,
            LocalDateTime fechaHoraRegistro,
            EstadoTurno estado,
            String ventanillaAsignada) {

        public Registro {
            Objects.requireNonNull(id, "El id del turno es obligatorio");
            Objects.requireNonNull(fechaHoraRegistro, "La fecha de registro es obligatoria");
            Objects.requireNonNull(estado, "El estado del turno es obligatorio");
        }
    }
}
