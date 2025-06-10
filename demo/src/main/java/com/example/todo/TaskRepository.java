package com.example.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Cercar tasques per estat de completat
    List<Task> findByCompleted(boolean completed);
    
    // Cercar tasques per prioritat
    List<Task> findByPriority(Task.Priority priority);
    
    // Cercar tasques pendents per prioritat
    List<Task> findByCompletedFalseAndPriority(Task.Priority priority);
    
    // Cercar tasques que continguin text a la descripció
    List<Task> findByDescriptionContainingIgnoreCase(String description);
    
    // Cercar tasques creades en un rang de dates
    List<Task> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Cercar tasques pendents ordenades per prioritat (consulta corregida)
    @Query("SELECT t FROM Task t WHERE t.completed = false ORDER BY " +
           "CASE t.priority " +
           "WHEN 'URGENT' THEN 1 " +
           "WHEN 'HIGH' THEN 2 " +
           "WHEN 'MEDIUM' THEN 3 " +
           "WHEN 'LOW' THEN 4 " +
           "END, t.createdAt ASC")
    List<Task> findPendingTasksByPriorityOrder();
    
    // Cercar tasques completades en els últims dies
    @Query("SELECT t FROM Task t WHERE t.completed = true AND t.completedAt >= :since")
    List<Task> findCompletedSince(@Param("since") LocalDateTime since);
    
    // Comptar tasques per estat
    long countByCompleted(boolean completed);
    
    // Comptar tasques per prioritat
    long countByPriority(Task.Priority priority);
    
    // Cercar tasques que vencen aviat (consulta corregida)
    @Query("SELECT t FROM Task t WHERE t.completed = false AND " +
           "(t.priority = 'URGENT' OR t.priority = 'HIGH') " +
           "ORDER BY t.priority, t.createdAt")
    List<Task> findUrgentAndHighPriorityTasks();
} 