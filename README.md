# 📝 To-Do List Spring Boot

![Java](https://img.shields.io/badge/Java-17-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?logo=springboot)
![Maven](https://img.shields.io/badge/Maven-3.8+-orange?logo=apachemaven)
![H2 Database](https://img.shields.io/badge/H2-Database-lightgrey?logo=h2)

## Características

- API RESTful completa para gestión de tareas
- Interfaz web moderna y responsiva
- Filtros por estado, prioridad, urgencia y fecha
- Estadísticas en tiempo real
- Base de datos en memoria H2
- Validación de datos y gestión de errores
- Acciones rápidas: completar todas, eliminar completadas, crear datos de ejemplo

---

## Tecnologías usadas

| Lenguaje | Frameworks/Librerías | Base de Datos | Frontend |
|----------|----------------------|--------------|----------|
| Java 17  | Spring Boot 3.2.5, Spring Data JPA, Spring Validation | H2 (in-memory) | HTML5, CSS3 |

---

## 📚 Endpoints principales de la API

<details>
<summary>Ver tabla de endpoints</summary>

| Método | Endpoint                        | Descripción                                 |
|--------|----------------------------------|---------------------------------------------|
| GET    | `/api/tasks`                    | Listar todas las tareas                     |
| GET    | `/api/tasks/{id}`               | Obtener tarea por ID                        |
| POST   | `/api/tasks`                    | Crear nueva tarea                           |
| PUT    | `/api/tasks/{id}`               | Actualizar tarea existente                  |
| DELETE | `/api/tasks/{id}`               | Eliminar tarea por ID                       |
| PATCH  | `/api/tasks/{id}/complete`      | Marcar tarea como completada                |
| PATCH  | `/api/tasks/{id}/uncomplete`    | Marcar tarea como pendiente                 |
| PATCH  | `/api/tasks/{id}/priority`      | Cambiar prioridad de la tarea               |
| GET    | `/api/tasks/pending`            | Listar tareas pendientes                    |
| GET    | `/api/tasks/completed`          | Listar tareas completadas                   |
| GET    | `/api/tasks/by-priority/{prio}` | Listar tareas por prioridad                 |
| GET    | `/api/tasks/search?q=texto`     | Buscar tareas por descripción               |
| GET    | `/api/tasks/urgent`             | Listar tareas urgentes                      |
| GET    | `/api/tasks/today`              | Listar tareas creadas hoy                   |
| GET    | `/api/tasks/stats`              | Obtener estadísticas de tareas              |
| PATCH  | `/api/tasks/complete-all`       | Marcar todas las tareas como completadas    |
| DELETE | `/api/tasks/completed`          | Eliminar todas las tareas completadas       |
| POST   | `/api/tasks/sample-data`        | Crear datos de ejemplo                      |

</details>
