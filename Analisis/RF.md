# Especificación de Requerimientos Funcionales

## RF-01 · Consultar ventanillas
- **Descripción:** El sistema deberá mostrar las ventanillas V01, V02 y V03 e indicar para cada una si está libre o qué turno se encuentra En atención.
- **Verificación:** Con un turno activo en V02, la consulta muestra V02 ocupada y V01/V03 libres.

## RF-02 · Registrar turno
- **Descripción:** El sistema deberá crear un turno con identificador y secuencia únicos, fecha y hora de registro, estado En espera y ventanilla sin asignar; lo incorporará al final de la cola.
- **Verificación:** Registrar tres turnos y comprobar el orden y unicidad de identificadores.

## RF-03 · Consultar fila
- **Descripción:** El sistema deberá presentar los turnos En espera en orden ascendente de secuencia, incluyendo su identificador y fecha/hora de registro.
- **Verificación:** Crear varios turnos, asignar y cancelar algunos, y comprobar que la cola solo incluye pendientes en orden FIFO.

## RF-04 · Asignar siguiente turno
- **Descripción:** Al solicitar atención desde una ventanilla libre, el sistema deberá seleccionar el turno En espera de menor secuencia, cambiar su estado a En atención y asociarlo exclusivamente a la ventanilla seleccionada.
- **Verificación:** Registrar T1, T2, T3; asignar desde V03 y después V01: deben asignarse T1 y T2, respectivamente.

## RF-05 · Rechazar asignaciones imposibles
- **Descripción:** El sistema deberá impedir la asignación cuando la ventanilla seleccionada esté ocupada o no existan turnos En espera; mostrará una explicación y conservará el estado previo.
- **Verificación:** Intentar asignar sobre ventanilla ocupada y con fila vacía; verificar que nada cambie.

## RF-06 · Finalizar atención
- **Descripción:** El sistema deberá permitir finalizar el turno En atención en una ventanilla seleccionada, cambiarlo a Atendido y dejar libre esa ventanilla.
- **Verificación:** Finalizar en V01 y comprobar estado del turno y disponibilidad de V01.

## RF-07 · Rechazar finalización inválida
- **Descripción:** El sistema deberá impedir finalizar una atención desde una ventanilla libre, sin modificar otros turnos o ventanillas.
- **Verificación:** Solicitar finalizar en V02 libre; verificar mensaje y ausencia de cambios.

## RF-08 · Cancelar turno pendiente
- **Descripción:** El sistema deberá permitir cancelar un turno únicamente si su estado es En espera, cambiándolo a Cancelado y retirándolo de la fila sin eliminar su historial.
- **Verificación:** Cancelar T2 en espera; comprobar que se excluye de la cola y permanece en historial.

## RF-09 · Rechazar cancelación inválida
- **Descripción:** El sistema deberá rechazar la cancelación de un ID inexistente o de un turno que no esté En espera y preservar el estado de la aplicación.
- **Verificación:** Intentar cancelar ID inexistente y turnos En atención, Atendido y Cancelado.

## RF-10 · Consultar historial simple
- **Descripción:** El sistema deberá permitir consultar todos los turnos registrados, incluidos En espera, En atención, Atendido y Cancelado, mostrando como mínimo ID, fecha/hora de registro, estado y ventanilla asignada cuando exista.
- **Verificación:** Crear un turno de cada estado y comprobar que los cuatro aparecen en consulta.

## RF-11 · Persistir y recuperar estados
- **Descripción:** El sistema deberá conservar los turnos, sus estados, secuencias y asignaciones de ventanillas al cerrar normalmente y reiniciar la aplicación.
- **Verificación:** Registrar, asignar, atender y cancelar; reiniciar y comparar datos, incluyendo ventanilla aún ocupada.

## RF-12 · Retroalimentación de operaciones
- **Descripción:** El sistema deberá confirmar las operaciones correctas e informar de forma clara aquellas rechazadas, sin indicar éxito cuando la persistencia necesaria haya fallado.
- **Verificación:** Provocar operación inválida y fallo controlado de escritura; comprobar mensajes y resultado.