# ConstruccionDeSoftware

Repositorio para el curso/proyecto de Construcción de Software.

> Completa esta descripción con el objetivo, tecnologías y alcance del proyecto.

## Estructura

```
ConstruccionDeSoftware/
├── README.md   # Este archivo
└── CLAUDE.md   # Reglas de uso del agente (Claude Code)
```

## Flujo de trabajo

1. Se solicita una tarea al agente (Claude Code).
2. El agente implementa los cambios, usando ramas o worktrees cuando sea necesario.
3. El responsable del repositorio revisa y ejecuta `git add`, `git commit` y `git push`.

Las reglas completas de uso del agente están en [`CLAUDE.md`](./CLAUDE.md).

## Requisitos

- Git
- Claude Code (opcional, para asistencia con el agente)

## Ejecución y pruebas

Requiere JDK 17 o superior y Maven.

```
mvn test            # ejecuta las pruebas unitarias
mvn compile exec:java   # inicia la aplicación
```

Los turnos se guardan en `datos/turnos.dat` (relativo al directorio desde donde se ejecuta; ignorado por git).
Si el archivo está dañado o es inválido, la aplicación muestra el error y se cierra sin modificarlo.

## [Requisitos Funcionales](Analisis/ReglasDeNegocio.md)

Esta lista de requisitos funcionales nos ayudará a definir las funcionalidades del sistema.

## [Requisitos No Funcionales](Analisis/ReglasDeNegocio.md)

Los siguientes requisitos son criterios de calidad de producto seleccionados para el alcance académico. Se distinguen interacción/usabilidad, seguridad, mantenibilidad, fiabilidad y desempeño. Los umbrales operativos son metas didácticas propuestas, no valores prescritos por ISO/IEC 25010:2023.

## [Reglas De Negocio](Analisis/ReglasDeNegocio.md)

Las reglas de negocio son los principios, condiciones, restricciones y políticas operativas que definen cómo debe funcionar una organización o proceso, independientemente de la tecnología que se utilice.

## Autor

Mantenido por `kermit79`.
