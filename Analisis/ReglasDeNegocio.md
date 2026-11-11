# Reglas de Negocio (RN)

| ID | Regla vinculante |
| :--- | :--- |
| **RN-01** | Todos los turnos comparten una sola cola FIFO, sin prioridades. |
| **RN-02** | El orden FIFO se determina por una secuencia creciente asignada al registrar el turno; no por orden alfabético del ID ni por precisión del reloj. |
| **RN-03** | Solo existen las ventanillas V01, V02 y V03; están predefinidas. |
| **RN-04** | Solo puede existir un turno En atención por ventanilla. |
| **RN-05** | Un turno no puede estar En atención en dos ventanillas. |
| **RN-06** | Asignar un turno implica iniciar inmediatamente su atención. |
| **RN-07** | Un turno En espera puede cancelarse; uno En atención, Atendido o Cancelado no puede cancelarse. |
| **RN-08** | Solo un turno En atención puede finalizarse y pasar a Atendido. |
| **RN-09** | Los turnos Atendido y Cancelado no regresan a En espera ni se eliminan del historial. |
| **RN-10** | Si no existen turnos pendientes, una solicitud de asignación no altera los datos. |