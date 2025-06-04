package com.example.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskService taskService;
    
    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    // ===== OPERACIONES CRUD BÁSICAS =====
    
    /**
     * GET /api/tasks - Obtener todas las tareas
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * GET /api/tasks/{id} - Obtener una tarea por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * POST /api/tasks - Crear nueva tarea
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * PUT /api/tasks/{id} - Actualizar tarea existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        try {
            Task updatedTask = taskService.updateTask(id, task);
            return ResponseEntity.ok(updatedTask);
        } catch (TaskService.TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * DELETE /api/tasks/{id} - Eliminar tarea
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (TaskService.TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ===== OPERACIONES ESPECÍFICAS =====
    
    /**
     * PATCH /api/tasks/{id}/complete - Marcar tarea como completada
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {
        try {
            Task completedTask = taskService.completeTask(id);
            return ResponseEntity.ok(completedTask);
        } catch (TaskService.TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * PATCH /api/tasks/{id}/uncomplete - Marcar tarea como pendiente
     */
    @PatchMapping("/{id}/uncomplete")
    public ResponseEntity<Task> uncompleteTask(@PathVariable Long id) {
        try {
            Task uncompletedTask = taskService.uncompleteTask(id);
            return ResponseEntity.ok(uncompletedTask);
        } catch (TaskService.TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * PATCH /api/tasks/{id}/priority - Cambiar prioridad de tarea
     */
    @PatchMapping("/{id}/priority")
    public ResponseEntity<Task> changePriority(@PathVariable Long id, @RequestParam Task.Priority priority) {
        try {
            Task updatedTask = taskService.changePriority(id, priority);
            return ResponseEntity.ok(updatedTask);
        } catch (TaskService.TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ===== CONSULTAS AVANZADAS =====
    
    /**
     * GET /api/tasks/pending - Obtener tareas pendientes
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Task>> getPendingTasks() {
        List<Task> pendingTasks = taskService.getPendingTasks();
        return ResponseEntity.ok(pendingTasks);
    }
    
    /**
     * GET /api/tasks/completed - Obtener tareas completadas
     */
    @GetMapping("/completed")
    public ResponseEntity<List<Task>> getCompletedTasks() {
        List<Task> completedTasks = taskService.getCompletedTasks();
        return ResponseEntity.ok(completedTasks);
    }
    
    /**
     * GET /api/tasks/by-priority/{priority} - Obtener tareas por prioridad
     */
    @GetMapping("/by-priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable Task.Priority priority) {
        List<Task> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * GET /api/tasks/search?q={query} - Buscar tareas por descripción
     */
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam("q") String query) {
        List<Task> tasks = taskService.searchTasksByDescription(query);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * GET /api/tasks/pending/by-priority - Obtener tareas pendientes ordenadas por prioridad
     */
    @GetMapping("/pending/by-priority")
    public ResponseEntity<List<Task>> getPendingTasksByPriority() {
        List<Task> tasks = taskService.getPendingTasksByPriority();
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * GET /api/tasks/urgent - Obtener tareas urgentes
     */
    @GetMapping("/urgent")
    public ResponseEntity<List<Task>> getUrgentTasks() {
        List<Task> urgentTasks = taskService.getUrgentTasks();
        return ResponseEntity.ok(urgentTasks);
    }
    
    /**
     * GET /api/tasks/today - Obtener tareas creadas hoy
     */
    @GetMapping("/today")
    public ResponseEntity<List<Task>> getTodaysTasks() {
        List<Task> todaysTasks = taskService.getTasksCreatedToday();
        return ResponseEntity.ok(todaysTasks);
    }
    
    /**
     * GET /api/tasks/recently-completed?days={days} - Obtener tareas completadas recientemente
     */
    @GetMapping("/recently-completed")
    public ResponseEntity<List<Task>> getRecentlyCompleted(@RequestParam(defaultValue = "7") int days) {
        List<Task> recentlyCompleted = taskService.getRecentlyCompleted(days);
        return ResponseEntity.ok(recentlyCompleted);
    }
    
    // ===== ESTADÍSTICAS =====
    
    /**
     * GET /api/tasks/stats - Obtener estadísticas de tareas
     */
    @GetMapping("/stats")
    public ResponseEntity<TaskService.TaskStats> getTaskStats() {
        TaskService.TaskStats stats = taskService.getTaskStats();
        return ResponseEntity.ok(stats);
    }
    
    // ===== OPERACIONES EN LOTE =====
    
    /**
     * PATCH /api/tasks/complete-all - Marcar todas las tareas como completadas
     */
    @PatchMapping("/complete-all")
    public ResponseEntity<Void> markAllAsCompleted() {
        taskService.markAllAsCompleted();
        return ResponseEntity.ok().build();
    }
    
    /**
     * DELETE /api/tasks/completed - Eliminar todas las tareas completadas
     */
    @DeleteMapping("/completed")
    public ResponseEntity<Void> deleteCompletedTasks() {
        taskService.deleteCompletedTasks();
        return ResponseEntity.noContent().build();
    }
    
    /**
     * POST /api/tasks/sample-data - Crear datos de ejemplo
     */
    @PostMapping("/sample-data")
    public ResponseEntity<List<Task>> createSampleTasks() {
        List<Task> sampleTasks = taskService.createSampleTasks();
        return ResponseEntity.status(HttpStatus.CREATED).body(sampleTasks);
    }
    
    // ===== MANEJO DE ERRORES =====
    
    @ExceptionHandler(TaskService.TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskService.TaskNotFoundException e) {
        ErrorResponse error = new ErrorResponse("TASK_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
        ErrorResponse error = new ErrorResponse("BAD_REQUEST", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    // Clase para respuestas de error
    public static class ErrorResponse {
        private String code;
        private String message;
        
        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
        
        // Getters
        public String getCode() { return code; }
        public String getMessage() { return message; }
    }
} 