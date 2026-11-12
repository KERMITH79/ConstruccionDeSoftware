package com.construccion.software;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Coordina turnos, ventanillas y, opcionalmente, su persistencia local. */
public class SistemaGestionTurnos {

    private final Clock reloj;
    private final PersistenciaTurnos persistencia;
    private final List<Turno> turnos = new ArrayList<>();
    private final Map<String, Ventanilla> ventanillas = new LinkedHashMap<>();
    private long ultimaSecuencia = 0;

    public SistemaGestionTurnos() {
        this(Clock.systemDefaultZone());
    }

    /** Crea un sistema en memoria, conservando el reloj indicado. */
    public SistemaGestionTurnos(Clock reloj) {
        this(reloj, (PersistenciaTurnos) null);
    }

    /** Crea un sistema que recupera y guarda sus datos en el archivo indicado. */
    public SistemaGestionTurnos(Path archivoPersistencia) {
        this(Clock.systemDefaultZone(), new AlmacenamientoArchivoTurnos(archivoPersistencia));
    }

    /** Crea un sistema persistente con un reloj configurable. */
    public SistemaGestionTurnos(Clock reloj, Path archivoPersistencia) {
        this(reloj, new AlmacenamientoArchivoTurnos(archivoPersistencia));
    }

    /**
     * Crea el sistema con una implementación de persistencia opcional.
     * Si la carga falla o contiene datos inconsistentes, el constructor falla y no guarda datos.
     */
    public SistemaGestionTurnos(Clock reloj, PersistenciaTurnos persistencia) {
        this.reloj = reloj;
        this.persistencia = persistencia;
        Ventanilla.IDS_VALIDOS.forEach(id -> ventanillas.put(id, new Ventanilla(id)));
        if (persistencia != null) {
            cargarEstado();
        }
    }

    /** Crea un turno EN_ESPERA al final de la cola. */
    public Turno registrarTurno() {
        long secuencia = ultimaSecuencia + 1;
        Turno turno = new Turno("T" + secuencia, secuencia, LocalDateTime.now(reloj));
        List<PersistenciaTurnos.Registro> candidato = snapshotPersistido();
        candidato.add(registroDe(turno));
        guardar(candidato, "registrar el turno");
        ultimaSecuencia = secuencia;
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
        List<PersistenciaTurnos.Registro> candidato = snapshotPersistido();
        reemplazarRegistro(candidato, registroDe(turno, EstadoTurno.EN_ATENCION, ventanilla.getId()));
        guardar(candidato, "asignar el turno");
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
        Turno turno = ventanilla.getTurnoActual();
        List<PersistenciaTurnos.Registro> candidato = snapshotPersistido();
        reemplazarRegistro(candidato, registroDe(turno, EstadoTurno.ATENDIDO, turno.getVentanillaAsignada()));
        guardar(candidato, "finalizar la atención");
        ventanilla.liberar();
        turno.finalizar();
        return turno;
    }

    /** Cancela un turno EN_ESPERA; lo retira de la fila pero lo conserva en el historial. */
    public Turno cancelarTurno(String idTurno) {
        Turno turno = buscarTurno(idTurno);
        List<PersistenciaTurnos.Registro> candidato = snapshotPersistido();
        reemplazarRegistro(candidato, registroDe(turno, EstadoTurno.CANCELADO, null));
        guardar(candidato, "cancelar el turno");
        turno.cancelar();
        return turno;
    }

    /** Todos los turnos registrados, sin importar su estado, en orden de secuencia. */
    public List<Turno> consultarHistorial() {
        return List.copyOf(turnos);
    }

    private Turno buscarTurno(String idTurno) {
        if (idTurno == null || idTurno.isBlank()) {
            throw new IllegalArgumentException("El id del turno es obligatorio");
        }
        return turnos.stream()
                .filter(t -> t.getId().equals(idTurno.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El turno " + idTurno + " no existe"));
    }

    private Ventanilla buscarVentanilla(String idVentanilla) {
        Ventanilla ventanilla = idVentanilla == null ? null : ventanillas.get(idVentanilla.trim());
        if (ventanilla == null) {
            throw new IllegalArgumentException("Ventanilla inexistente: " + idVentanilla
                    + ". Válidas: " + Ventanilla.IDS_VALIDOS);
        }
        return ventanilla;
    }

    private void cargarEstado() {
        List<PersistenciaTurnos.Registro> registros;
        try {
            registros = persistencia.cargar();
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudo leer el archivo de persistencia; no se modificaron sus datos", ex);
        }
        validarEstadoPersistido(registros);
        for (PersistenciaTurnos.Registro registro : registros) {
            Turno turno = new Turno(
                    registro.id(),
                    registro.secuencia(),
                    registro.fechaHoraRegistro(),
                    registro.estado(),
                    registro.ventanillaAsignada());
            turnos.add(turno);
            if (registro.estado() == EstadoTurno.EN_ATENCION) {
                ventanillas.get(registro.ventanillaAsignada()).ocupar(turno);
            }
            ultimaSecuencia = Math.max(ultimaSecuencia, registro.secuencia());
        }
    }

    /** Rechaza duplicados, secuencias inválidas y relaciones inconsistentes entre turnos y ventanillas. */
    private void validarEstadoPersistido(List<PersistenciaTurnos.Registro> registros) {
        if (registros == null) {
            throw new IllegalStateException("El archivo de persistencia devolvió datos inválidos; no se sobrescribió");
        }
        Set<String> ids = new HashSet<>();
        Set<String> ventanillasOcupadas = new HashSet<>();
        long secuenciaAnterior = 0;
        for (PersistenciaTurnos.Registro registro : registros) {
            if (registro == null
                    || registro.id().isBlank()
                    || registro.secuencia() <= secuenciaAnterior
                    || !registro.id().equals("T" + registro.secuencia())
                    || !ids.add(registro.id())) {
                throw new IllegalStateException("El archivo de persistencia contiene turnos inconsistentes; no se sobrescribió");
            }
            secuenciaAnterior = registro.secuencia();

            String ventanillaId = registro.ventanillaAsignada();
            if (registro.estado() == EstadoTurno.EN_ESPERA || registro.estado() == EstadoTurno.CANCELADO) {
                if (ventanillaId != null) {
                    throw new IllegalStateException("El archivo de persistencia contiene una asignación inválida; no se sobrescribió");
                }
            } else if (ventanillaId == null
                    || !ventanillas.containsKey(ventanillaId)
                    || (registro.estado() == EstadoTurno.EN_ATENCION && !ventanillasOcupadas.add(ventanillaId))) {
                throw new IllegalStateException("El archivo de persistencia contiene ventanillas inconsistentes; no se sobrescribió");
            }
        }
    }

    private List<PersistenciaTurnos.Registro> snapshotPersistido() {
        List<PersistenciaTurnos.Registro> registros = new ArrayList<>(turnos.size());
        for (Turno turno : turnos) {
            registros.add(registroDe(turno));
        }
        return registros;
    }

    private PersistenciaTurnos.Registro registroDe(Turno turno) {
        return registroDe(turno, turno.getEstado(), turno.getVentanillaAsignada());
    }

    private PersistenciaTurnos.Registro registroDe(Turno turno, EstadoTurno estado, String ventanilla) {
        return new PersistenciaTurnos.Registro(
                turno.getId(), turno.getSecuencia(), turno.getFechaHoraRegistro(), estado, ventanilla);
    }

    private void reemplazarRegistro(
            List<PersistenciaTurnos.Registro> registros,
            PersistenciaTurnos.Registro actualizado) {
        for (int i = 0; i < registros.size(); i++) {
            if (registros.get(i).id().equals(actualizado.id())) {
                registros.set(i, actualizado);
                return;
            }
        }
        throw new IllegalStateException("No se encontró el turno en la instantánea de persistencia");
    }

    private void guardar(List<PersistenciaTurnos.Registro> registros, String operacion) {
        if (persistencia == null) {
            return;
        }
        try {
            // Se guarda el estado candidato antes de cambiar los objetos que consulta la aplicación.
            persistencia.guardar(List.copyOf(registros));
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudo " + operacion + "; el estado en memoria se conservó", ex);
        }
    }
}
