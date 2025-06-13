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
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(task -> ResponseEntity.ok(task))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
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
    
    @PatchMapping("/{id}/uncomplete")
    public ResponseEntity<Task> uncompleteTask(@PathVariable Long id) {
        try {
            Task uncompletedTask = taskService.uncompleteTask(id);
            return ResponseEntity.ok(uncompletedTask);
        } catch (TaskService.TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
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
    
    @GetMapping("/completed")
    public ResponseEntity<List<Task>> getCompletedTasks() {
        List<Task> completedTasks = taskService.getCompletedTasks();
        return ResponseEntity.ok(completedTasks);
    }
    
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
    
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Task> tasks = taskService.searchTasksByDescription(q);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/pending/by-priority")
    public ResponseEntity<List<Task>> getPendingTasksByPriority() {
        List<Task> tasks = taskService.getPendingTasksByPriority();
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/urgent")
    public ResponseEntity<List<Task>> getUrgentTasks() {
        List<Task> urgentTasks = taskService.getUrgentTasks();
        return ResponseEntity.ok(urgentTasks);
    }
    
    @GetMapping("/today")
    public ResponseEntity<List<Task>> getTasksCreatedToday() {
        List<Task> todayTasks = taskService.getTasksCreatedToday();
        return ResponseEntity.ok(todayTasks);
    }
    
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
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericError(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error intern del servidor");
        error.put("message", "S'ha produït un error inesperat");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}