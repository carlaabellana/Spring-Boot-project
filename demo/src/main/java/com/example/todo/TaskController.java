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
    
    // ===== OPERACIONS CRUD BÀSIQUES =====
    
    /**
     * GET /api/tasks - Obtenir totes les tasques
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * GET /api/tasks/{id} - Obtenir una tasca per ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * POST /api/tasks - Crear nova tasca
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
     * PUT /api/tasks/{id} - Actualitzar tasca existent
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
     * DELETE /api/tasks/{id} - Eliminar tasca
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
    
    // ===== OPERACIONS ESPECÍFIQUES =====
    
    /**
     * PATCH /api/tasks/{id}/complete - Marcar tasca com a completada
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
     * PATCH /api/tasks/{id}/uncomplete - Marcar tasca com a pendent
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
     * PATCH /api/tasks/{id}/priority - Canviar prioritat de tasca
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
    
    // ===== CONSULTES AVANÇADES =====
    
    /**
     * GET /api/tasks/pending - Obtenir tasques pendents
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Task>> getPendingTasks() {
        List<Task> pendingTasks = taskService.getPendingTasks();
        return ResponseEntity.ok(pendingTasks);
    }
    
    /**
     * GET /api/tasks/completed - Obtenir tasques completades
     */
    @GetMapping("/completed")
    public ResponseEntity<List<Task>> getCompletedTasks() {
        List<Task> completedTasks = taskService.getCompletedTasks();
        return ResponseEntity.ok(completedTasks);
    }
    
    /**
     * GET /api/tasks/by-priority/{priority} - Obtenir tasques per prioritat
     */
    @GetMapping("/by-priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable Task.Priority priority) {
        List<Task> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * GET /api/tasks/search?q={query} - Cercar tasques per descripció
     */
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam("q") String query) {
        List<Task> tasks = taskService.searchTasksByDescription(query);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * GET /api/tasks/pending/by-priority - Obtenir tasques pendents ordenades per prioritat
     */
    @GetMapping("/pending/by-priority")
    public ResponseEntity<List<Task>> getPendingTasksByPriority() {
        List<Task> tasks = taskService.getPendingTasksByPriority();
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * GET /api/tasks/urgent - Obtenir tasques urgents
     */
    @GetMapping("/urgent")
    public ResponseEntity<List<Task>> getUrgentTasks() {
        List<Task> urgentTasks = taskService.getUrgentTasks();
        return ResponseEntity.ok(urgentTasks);
    }
    
    /**
     * GET /api/tasks/today - Obtenir tasques creades avui
     */
    @GetMapping("/today")
    public ResponseEntity<List<Task>> getTodaysTasks() {
        List<Task> todaysTasks = taskService.getTasksCreatedToday();
        return ResponseEntity.ok(todaysTasks);
    }
    
    /**
     * GET /api/tasks/recently-completed?days={days} - Obtenir tasques completades recentment
     */
    @GetMapping("/recently-completed")
    public ResponseEntity<List<Task>> getRecentlyCompleted(@RequestParam(defaultValue = "7") int days) {
        List<Task> recentlyCompleted = taskService.getRecentlyCompleted(days);
        return ResponseEntity.ok(recentlyCompleted);
    }
    
    // ===== ESTADÍSTIQUES =====
    
    /**
     * GET /api/tasks/stats - Obtenir estadístiques de tasques
     */
    @GetMapping("/stats")
    public ResponseEntity<TaskService.TaskStats> getTaskStats() {
        TaskService.TaskStats stats = taskService.getTaskStats();
        return ResponseEntity.ok(stats);
    }
    
    // ===== OPERACIONS EN LOT =====
    
    /**
     * PATCH /api/tasks/complete-all - Marcar totes les tasques com a completades
     */
    @PatchMapping("/complete-all")
    public ResponseEntity<Void> markAllAsCompleted() {
        taskService.markAllAsCompleted();
        return ResponseEntity.ok().build();
    }
    
    /**
     * DELETE /api/tasks/completed - Eliminar totes les tasques completades
     */
    @DeleteMapping("/completed")
    public ResponseEntity<Void> deleteCompletedTasks() {
        taskService.deleteCompletedTasks();
        return ResponseEntity.noContent().build();
    }
    
    /**
     * POST /api/tasks/sample-data - Crear dades d'exemple
     */
    @PostMapping("/sample-data")
    public ResponseEntity<List<Task>> createSampleTasks() {
        List<Task> sampleTasks = taskService.createSampleTasks();
        return ResponseEntity.status(HttpStatus.CREATED).body(sampleTasks);
    }
    
    // ===== GESTIÓ D'ERRORS =====
    
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
    
    // Classe per a respostes d'error
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