package com.construccion.software;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Almacena turnos en un archivo binario con versión y verificación de integridad. */
public final class AlmacenamientoArchivoTurnos implements PersistenciaTurnos {

    private static final int MAGIC = 0x53475455;
    private static final int VERSION = 1;
    private static final int CHECKSUM_BYTES = 32;
    private static final int MAX_FILE_BYTES = 16 * 1024 * 1024;
    private static final int MAX_TURNOS = 100_000;

    private final Path archivo;

    /** Crea el almacenamiento para la ruta indicada. */
    public AlmacenamientoArchivoTurnos(Path archivo) {
        this.archivo = archivo.toAbsolutePath().normalize();
    }

    /**
     * Lee y valida el archivo antes de devolver cualquier registro.
     * Un archivo existente pero inválido produce un error; nunca se trata como vacío.
     */
    @Override
    public List<Registro> cargar() throws IOException {
        if (Files.notExists(archivo)) {
            return List.of();
        }
        if (!Files.exists(archivo)) {
            throw new IOException("No se pudo determinar si existe el archivo de persistencia");
        }

        long tamano = Files.size(archivo);
        if (tamano < CHECKSUM_BYTES || tamano > MAX_FILE_BYTES) {
            throw new IOException("El archivo de turnos tiene un tamaño inválido");
        }
        byte[] contenido = Files.readAllBytes(archivo);
        if (contenido.length < CHECKSUM_BYTES || contenido.length > MAX_FILE_BYTES) {
            throw new IOException("El archivo de turnos tiene un tamaño inválido");
        }

        int longitudDatos = contenido.length - CHECKSUM_BYTES;
        byte[] datos = java.util.Arrays.copyOfRange(contenido, 0, longitudDatos);
        byte[] checksum = java.util.Arrays.copyOfRange(contenido, longitudDatos, contenido.length);
        if (!MessageDigest.isEqual(checksum, calcularChecksum(datos))) {
            throw new IOException("La verificación de integridad del archivo de turnos falló");
        }

        try (DataInputStream entrada = new DataInputStream(new ByteArrayInputStream(datos))) {
            if (entrada.readInt() != MAGIC || entrada.readInt() != VERSION) {
                throw new IOException("El archivo de turnos tiene un formato o versión no compatible");
            }
            int cantidad = entrada.readInt();
            if (cantidad < 0 || cantidad > MAX_TURNOS) {
                throw new IOException("La cantidad de turnos del archivo es inválida");
            }

            List<Registro> registros = new ArrayList<>(cantidad);
            for (int i = 0; i < cantidad; i++) {
                String id = entrada.readUTF();
                long secuencia = entrada.readLong();
                LocalDateTime fechaHora = LocalDateTime.parse(entrada.readUTF());
                EstadoTurno estado = EstadoTurno.valueOf(entrada.readUTF());
                String ventanilla = entrada.readBoolean() ? entrada.readUTF() : null;
                registros.add(new Registro(id, secuencia, fechaHora, estado, ventanilla));
            }
            if (entrada.available() != 0) {
                throw new IOException("El archivo de turnos contiene datos adicionales inválidos");
            }
            return List.copyOf(registros);
        } catch (IllegalArgumentException ex) {
            throw new IOException("El archivo de turnos contiene datos inválidos", ex);
        }
    }

    /**
     * Escribe el estado completo en un archivo temporal y lo reemplaza atómicamente.
     * Si el reemplazo atómico no está disponible, falla sin reemplazar el archivo original.
     */
    @Override
    public void guardar(List<Registro> registros) throws IOException {
        byte[] datos = codificar(registros);
        byte[] checksum = calcularChecksum(datos);
        byte[] contenido = new byte[datos.length + checksum.length];
        System.arraycopy(datos, 0, contenido, 0, datos.length);
        System.arraycopy(checksum, 0, contenido, datos.length, checksum.length);
        if (contenido.length > MAX_FILE_BYTES) {
            throw new IOException("El archivo de turnos excede el tamaño permitido");
        }

        Path directorio = archivo.getParent();
        Files.createDirectories(directorio);
        Path temporal = Files.createTempFile(directorio, "turnos-", ".tmp");
        try {
            Files.write(
                    temporal,
                    contenido,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            try (FileChannel canal = FileChannel.open(temporal, StandardOpenOption.WRITE)) {
                canal.force(true);
            }
            try {
                Files.move(temporal, archivo, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                throw new IOException("El sistema de archivos no permite guardar turnos de forma atómica", ex);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    /** Codifica la versión del formato y todos los registros antes de escribirlos. */
    private byte[] codificar(List<Registro> registros) throws IOException {
        if (registros.size() > MAX_TURNOS) {
            throw new IOException("La cantidad de turnos excede el máximo permitido");
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream salida = new DataOutputStream(bytes)) {
            salida.writeInt(MAGIC);
            salida.writeInt(VERSION);
            salida.writeInt(registros.size());
            for (Registro registro : registros) {
                salida.writeUTF(registro.id());
                salida.writeLong(registro.secuencia());
                salida.writeUTF(registro.fechaHoraRegistro().toString());
                salida.writeUTF(registro.estado().name());
                salida.writeBoolean(registro.ventanillaAsignada() != null);
                if (registro.ventanillaAsignada() != null) {
                    salida.writeUTF(registro.ventanillaAsignada());
                }
            }
        }
        return bytes.toByteArray();
    }

    /** Calcula la huella usada para detectar cambios o daños accidentales en el archivo. */
    private byte[] calcularChecksum(byte[] datos) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(datos);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no está disponible", ex);
        }
    }
}
