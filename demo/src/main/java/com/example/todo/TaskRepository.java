package com.example.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Buscar tareas por estado de completado
    List<Task> findByCompleted(boolean completed);
    
    // Buscar tareas por prioridad
    List<Task> findByPriority(Task.Priority priority);
    
    // Buscar tareas pendientes por prioridad
    List<Task> findByCompletedFalseAndPriority(Task.Priority priority);
    
    // Buscar tareas que contengan texto en la descripción
    List<Task> findByDescriptionContainingIgnoreCase(String description);
    
    // Buscar tareas creadas en un rango de fechas
    List<Task> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Buscar tareas pendientes ordenadas por prioridad
    @Query("SELECT t FROM Task t WHERE t.completed = false ORDER BY " +
           "CASE t.priority " +
           "WHEN com.example.todo.Task.Priority.URGENT THEN 1 " +
           "WHEN com.example.todo.Task.Priority.HIGH THEN 2 " +
           "WHEN com.example.todo.Task.Priority.MEDIUM THEN 3 " +
           "WHEN com.example.todo.Task.Priority.LOW THEN 4 " +
           "END, t.createdAt ASC")
    List<Task> findPendingTasksByPriorityOrder();
    
    // Buscar tareas completadas en los últimos días
    @Query("SELECT t FROM Task t WHERE t.completed = true AND t.completedAt >= :since")
    List<Task> findCompletedSince(@Param("since") LocalDateTime since);
    
    // Contar tareas por estado
    long countByCompleted(boolean completed);
    
    // Contar tareas por prioridad
    long countByPriority(Task.Priority priority);
    
    // Buscar tareas que vencen pronto (basado en algún criterio personalizado)
    @Query("SELECT t FROM Task t WHERE t.completed = false AND " +
           "(t.priority = com.example.todo.Task.Priority.URGENT OR " +
           "t.priority = com.example.todo.Task.Priority.HIGH) " +
           "ORDER BY t.priority, t.createdAt")
    List<Task> findUrgentAndHighPriorityTasks();
} 