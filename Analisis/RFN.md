# Requisitos No Funcionales (RNF)

## 7.1 Capacidad de interacción / usabilidad

### RNF-U01 · Operabilidad
- **Descripción:** Un usuario que conozca la descripción básica del sistema deberá poder registrar, asignar, finalizar y cancelar un turno desde la interfaz sin usar comandos internos ni editar archivos.
- **Verificación:** Recorrido funcional de las cuatro operaciones con la interfaz.

### RNF-U02 · Mensajes útiles
- **Descripción:** Ante acciones no permitidas, el sistema deberá explicar la causa (por ejemplo, ventanilla ocupada o turno no cancelable) e indicar el identificador implicado cuando corresponda.
- **Verificación:** Ejecutar cinco errores representativos y examinar mensajes.

### RNF-U03 · Estado identificable
- **Descripción:** La interfaz deberá distinguir visualmente y mediante texto los estados En espera, En atención, Atendido y Cancelado; no deberá depender exclusivamente de colores.
- **Verificación:** Inspección del historial y consulta de fila/ventanillas.

### RNF-U04 · Prevención de errores
- **Descripción:** Antes de cancelar un turno pendiente, la interfaz deberá solicitar confirmación explícita; una cancelación rechazada no deberá producir cambios.
- **Verificación:** Intentar cancelar y rechazar confirmación; estado inalterado.

---

## 7.2 Seguridad y protección de la información

### RNF-S01 · Validación en lógica de negocio
- **Descripción:** El sistema deberá aplicar las reglas de estado y asignación en la lógica del dominio, aun si una acción se invoca sin utilizar los controles habituales de la interfaz.
- **Verificación:** Pruebas de funciones o servicios con estados inválidos.

### RNF-S02 · Sin secretos embebidos
- **Descripción:** El código y la documentación entregados no deberán incluir contraseñas reales, tokens, claves API ni rutas a datos privados de los integrantes.
- **Verificación:** Revisión de repositorio, configuración y documentación.

### RNF-S03 · Alcance local
- **Descripción:** La solución deberá funcionar localmente sin exponer obligatoriamente datos ni operaciones del sistema a internet. No se requiere autenticación porque la especificación contempla un único operador local.
- **Verificación:** Comprobar funcionamiento desconectado de internet y configuración de red, si aplica.

### RNF-S04 · Manejo seguro de entradas
- **Descripción:** Los identificadores y demás entradas recibidas deberán validarse antes de usarse, evitando fallos no controlados o ejecución no intencionada de instrucciones sobre datos.
- **Verificación:** Probar IDs vacíos, malformados e inexistentes; revisar acceso a datos.

---

## 7.3 Mantenibilidad y calidad de construcción

### RNF-M01 · Separación de responsabilidades
- **Descripción:** Las reglas FIFO y transiciones de estado deberán poder verificarse sin ejecutar la interfaz gráfica; se deberá evitar mezclar su implementación con código de presentación y almacenamiento.
- **Verificación:** Inspección de estructura y ejecución de pruebas unitarias aisladas.

### RNF-M02 · Estándar y legibilidad
- **Descripción:** El código deberá seguir un estándar de codificación identificado, con nombres significativos, métodos de responsabilidad clara y sin duplicación injustificada de reglas de negocio.
- **Verificación:** Revisión técnica por muestreo y contraste con estándar elegido.

### RNF-M03 · Pruebas automatizadas
- **Descripción:** El proyecto deberá incorporar pruebas unitarias automatizadas para FIFO, estados, asignación múltiple, cancelación, finalización y rechazos; las pruebas deberán poder ejecutarse mediante instrucciones documentadas.
- **Verificación:** Ejecutar batería de pruebas en el entorno entregado.

### RNF-M04 · Documentación mínima
- **Descripción:** El repositorio deberá contener instrucciones para preparar, ejecutar y probar la aplicación, además de explicaciones de decisiones técnicas no evidentes.
- **Verificación:** Seguir instrucciones en instalación limpia compatible.

### RNF-M05 · Revisión final
- **Descripción:** El equipo deberá identificar oportunidades reales de mejora de diseño o legibilidad encontradas al revisar su versión final, sin obligación de aplicar refactorización en esta actividad.
- **Verificación:** Registro breve de hallazgos, incluso si la revisión concluye que no hay cambios pertinentes.

---

## 7.4 Fiabilidad y eficiencia

### RNF-R01 · Recuperación tras reinicio
- **Descripción:** Después de un cierre normal, la siguiente ejecución deberá recuperar íntegramente las operaciones previamente confirmadas y preservar los turnos En atención.
- **Verificación:** Reinicio con turnos en cuatro estados.

### RNF-R02 · Integridad ante rechazos
- **Descripción:** Toda operación inválida deberá dejar inalterados los datos y el orden de la fila.
- **Verificación:** Comparar estado antes y después de operaciones rechazadas.

### RNF-P01 · Respuesta interactiva
- **Descripción:** Con hasta 200 turnos almacenados, las operaciones normales de registro, consulta de fila, asignación y finalización deberán responder en no más de 2 segundos en el equipo utilizado para la demostración, excluyendo el arranque inicial.
- **Verificación:** Medición local de operaciones con 200 turnos de prueba; registrar características del equipo.