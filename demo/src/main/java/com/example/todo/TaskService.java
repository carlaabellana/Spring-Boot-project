package com.example.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {
    
    private final TaskRepository taskRepository;
    
    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    // Operacions bàsiques CRUD
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    public Task createTask(Task task) {
        // Validacions de negoci
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripció de la tasca no pot estar buida");
        }
        
        // Establir valors per defecte
        if (task.getPriority() == null) {
            task.setPriority(Task.Priority.MEDIUM);
        }
        
        return taskRepository.save(task);
    }
    
    public Task updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    // Actualitzar només els camps permesos
                    if (updatedTask.getDescription() != null && !updatedTask.getDescription().trim().isEmpty()) {
                        existingTask.setDescription(updatedTask.getDescription());
                    }
                    if (updatedTask.getPriority() != null) {
                        existingTask.setPriority(updatedTask.getPriority());
                    }
                    if (updatedTask.getNotes() != null) {
                        existingTask.setNotes(updatedTask.getNotes());
                    }
                    return taskRepository.save(existingTask);
                })
                .orElseThrow(() -> new TaskNotFoundException("Tasca amb ID " + id + " no trobada"));
    }
    
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Tasca amb ID " + id + " no trobada");
        }
        taskRepository.deleteById(id);
    }
    
    // Operacions específiques de tasques
    
    public Task completeTask(Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setCompleted(true);
                    task.setCompletedAt(LocalDateTime.now());
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new TaskNotFoundException("Tasca amb ID " + id + " no trobada"));
    }
    
    public Task uncompleteTask(Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setCompleted(false);
                    task.setCompletedAt(null);
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new TaskNotFoundException("Tasca amb ID " + id + " no trobada"));
    }
    
    public Task changePriority(Long id, Task.Priority newPriority) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setPriority(newPriority);
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new TaskNotFoundException("Tasca amb ID " + id + " no trobada"));
    }
    
    // Consultes avançades
    
    public List<Task> getPendingTasks() {
        return taskRepository.findByCompleted(false);
    }
    
    public List<Task> getCompletedTasks() {
        return taskRepository.findByCompleted(true);
    }
    
    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority);
    }
    
    public List<Task> searchTasksByDescription(String searchTerm) {
        return taskRepository.findByDescriptionContainingIgnoreCase(searchTerm);
    }
    
    public List<Task> getPendingTasksByPriority() {
        return taskRepository.findPendingTasksByPriorityOrder();
    }
    
    public List<Task> getUrgentTasks() {
        return taskRepository.findUrgentAndHighPriorityTasks();
    }
    
    public List<Task> getTasksCreatedToday() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return taskRepository.findByCreatedAtBetween(startOfDay, endOfDay);
    }
    
    public List<Task> getRecentlyCompleted(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return taskRepository.findCompletedSince(since);
    }
    
    // Estadístiques
    
    public TaskStats getTaskStats() {
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByCompleted(true);
        long pendingTasks = taskRepository.countByCompleted(false);
        long urgentTasks = taskRepository.countByPriority(Task.Priority.URGENT);
        long highPriorityTasks = taskRepository.countByPriority(Task.Priority.HIGH);
        
        return new TaskStats(totalTasks, completedTasks, pendingTasks, urgentTasks, highPriorityTasks);
    }
    
    // Operacions en lot
    
    public void markAllAsCompleted() {
        List<Task> pendingTasks = getPendingTasks();
        LocalDateTime now = LocalDateTime.now();
        
        pendingTasks.forEach(task -> {
            task.setCompleted(true);
            task.setCompletedAt(now);
        });
        
        taskRepository.saveAll(pendingTasks);
    }
    
    public void deleteCompletedTasks() {
        List<Task> completedTasks = getCompletedTasks();
        taskRepository.deleteAll(completedTasks);
    }
    
    // Classe interna per a estadístiques
    public static class TaskStats {
        private final long total;
        private final long completed;
        private final long pending;
        private final long urgent;
        private final long highPriority;
        
        public TaskStats(long total, long completed, long pending, long urgent, long highPriority) {
            this.total = total;
            this.completed = completed;
            this.pending = pending;
            this.urgent = urgent;
            this.highPriority = highPriority;
        }
        
        // Getters
        public long getTotal() { return total; }
        public long getCompleted() { return completed; }
        public long getPending() { return pending; }
        public long getUrgent() { return urgent; }
        public long getHighPriority() { return highPriority; }
        
        public double getCompletionPercentage() {
            return total > 0 ? (double) completed / total * 100 : 0;
        }
    }
    
    // Excepció personalitzada
    public static class TaskNotFoundException extends RuntimeException {
        public TaskNotFoundException(String message) {
            super(message);
        }
    }
} 