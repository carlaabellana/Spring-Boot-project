package com.example.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Endpoints bàsics CRUD

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    // Consultar tasca per id
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear tasca
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Actualitzar tasca
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

    // Eliminar tasca
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (TaskService.TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoints d'accions específiques

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {
        try {
            Task completedTask = taskService.completeTask(id);
            return ResponseEntity.ok(completedTask);
        } catch (TaskService.TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Desmarcar tasca com a completada
    @PatchMapping("/{id}/uncomplete")
    public ResponseEntity<Task> uncompleteTask(@PathVariable Long id) {
        try {
            Task uncompletedTask = taskService.uncompleteTask(id);
            return ResponseEntity.ok(uncompletedTask);
        } catch (TaskService.TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Canviar prioritat
    @PatchMapping("/{id}/priority")
    public ResponseEntity<Task> changePriority(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String priorityStr = request.get("priority");
            if (priorityStr == null) {
                return ResponseEntity.badRequest().build();
            }

            Task.Priority priority = Task.Priority.valueOf(priorityStr.toUpperCase());
            Task updatedTask = taskService.changePriority(id, priority);
            return ResponseEntity.ok(updatedTask);
        } catch (TaskService.TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoints de consultes

    @GetMapping("/pending")
    public ResponseEntity<List<Task>> getPendingTasks() {
        List<Task> pendingTasks = taskService.getPendingTasks();
        return ResponseEntity.ok(pendingTasks);
    }

    // Tasques completades
    @GetMapping("/completed")
    public ResponseEntity<List<Task>> getCompletedTasks() {
        List<Task> completedTasks = taskService.getCompletedTasks();
        return ResponseEntity.ok(completedTasks);
    }

    // Tasques per prioritat
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable String priority) {
        try {
            Task.Priority taskPriority = Task.Priority.valueOf(priority.toUpperCase());
            List<Task> tasks = taskService.getTasksByPriority(taskPriority);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Cerca de tasques
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Task> tasks = taskService.searchTasksByDescription(q);
        return ResponseEntity.ok(tasks);
    }

    // Tasques pendents per prioritat
    @GetMapping("/pending/by-priority")
    public ResponseEntity<List<Task>> getPendingTasksByPriority() {
        List<Task> tasks = taskService.getPendingTasksByPriority();
        return ResponseEntity.ok(tasks);
    }

    // Tasques urgents
    @GetMapping("/urgent")
    public ResponseEntity<List<Task>> getUrgentTasks() {
        List<Task> urgentTasks = taskService.getUrgentTasks();
        return ResponseEntity.ok(urgentTasks);
    }

    // Tasques creades avui
    @GetMapping("/today")
    public ResponseEntity<List<Task>> getTasksCreatedToday() {
        List<Task> todayTasks = taskService.getTasksCreatedToday();
        return ResponseEntity.ok(todayTasks);
    }

    // Tasques recentment completades
    @GetMapping("/recently-completed")
    public ResponseEntity<List<Task>> getRecentlyCompleted(@RequestParam(defaultValue = "7") int days) {
        if (days < 1 || days > 365) {
            return ResponseEntity.badRequest().build();
        }
        List<Task> recentTasks = taskService.getRecentlyCompleted(days);
        return ResponseEntity.ok(recentTasks);
    }

    // Gestió global d'errors

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Argument invàlid");
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Error general
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericError(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error intern del servidor");
        error.put("message", "S'ha produït un error inesperat");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Estatístiques de tasques
    @GetMapping("/stats")
    public ResponseEntity<TaskService.TaskStats> getTaskStats() {
        TaskService.TaskStats stats = taskService.getTaskStats();
        return ResponseEntity.ok(stats);
    }

    // Marcar totes les tasques com a completades
    @PatchMapping("/complete-all")
    public ResponseEntity<Void> markAllAsCompleted() {
        taskService.markAllAsCompleted();
        return ResponseEntity.ok().build();
    }

    // Eliminar totes les tasques completades
    @DeleteMapping("/completed")
    public ResponseEntity<Void> deleteCompletedTasks() {
        taskService.deleteCompletedTasks();
        return ResponseEntity.noContent().build();
    }

    // Crear dades d'exemple
    @PostMapping("/sample-data")
    public ResponseEntity<List<Task>> createSampleTasks() {
        List<Task> sampleTasks = taskService.createSampleTasks();
        return ResponseEntity.status(HttpStatus.CREATED).body(sampleTasks);
    }

    // Gestió d'errors

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
        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}