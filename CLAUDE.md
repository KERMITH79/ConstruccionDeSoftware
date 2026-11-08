# CLAUDE.md — Reglas de uso del agente

Este archivo define cómo debe trabajar Claude en este repositorio.

## Principio general

Claude implementa lo solicitado. **El usuario es quien registra los cambios en el repositorio** (`add`, `commit`, `push`).

## Permisos de Git

### Reservado exclusivamente al usuario

Claude **NO** debe ejecutar, bajo ninguna circunstancia, salvo orden explícita en el mensaje actual:

- `git add`
- `git commit` (incluido `--amend`)
- `git push` (incluido `--force`)
- Crear o modificar Pull Requests, tags o releases
- `git reset --hard`, `git clean`, reescritura de historial

### Permitido a Claude

- Crear ramas (`git branch`, `git checkout -b`, `git switch -c`).
- Cambiar y trabajar entre ramas (`git switch`, `git checkout`).
- Crear y gestionar **worktrees** (`git worktree add/remove`) cuando el trabajo lo requiera, por ejemplo tareas paralelas o aisladas.
- Comandos de solo lectura: `git status`, `git diff`, `git log`, `git branch`, `git show`.
- Editar, crear y borrar archivos del proyecto dentro del alcance de la tarea.

## Convenciones de ramas

- Formato: `tipo/descripcion-corta` (ej. `feat/login`, `fix/null-pointer`, `docs/readme`).
- Claude nunca trabaja directamente sobre `main`/`master`: crea una rama antes de modificar código.
- Al terminar, Claude indica en qué rama quedaron los cambios para que el usuario haga commit y push.

## Flujo esperado

1. Claude lee el pedido y, si hace falta, crea una rama o worktree.
2. Implementa los cambios y los verifica (compilar/tests cuando aplique).
3. Entrega un resumen: archivos modificados, rama usada y cómo probarlo.
4. El usuario revisa, hace `git add`, `git commit` y `git push`.

## Reglas de trabajo

- Hacer solo lo pedido; no ampliar el alcance sin consultar.
- Respetar el estilo y las convenciones del código existente.
- No tocar secretos, credenciales ni archivos `.env`.
- Antes de borrar o sobrescribir algo, revisar su contenido.
- Si algo es ambiguo o riesgoso, preguntar antes de actuar.
- Informar con honestidad: si una prueba falla o un paso se omitió, decirlo.
- Responder en español.
