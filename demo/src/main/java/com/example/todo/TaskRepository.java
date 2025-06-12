package com.example.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByCompleted(boolean completed);
    
    List<Task> findByPriority(Task.Priority priority);
    
    List<Task> findByCompletedFalseAndPriority(Task.Priority priority);
    
    List<Task> findByDescriptionContainingIgnoreCase(String description);
    
    List<Task> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM Task t WHERE t.completed = false ORDER BY " +
           "CASE t.priority " +
           "WHEN 'URGENT' THEN 1 " +
           "WHEN 'HIGH' THEN 2 " +
           "WHEN 'MEDIUM' THEN 3 " +
           "WHEN 'LOW' THEN 4 " +
           "END, t.createdAt ASC")
    List<Task> findPendingTasksByPriorityOrder();
    
    @Query("SELECT t FROM Task t WHERE t.completed = true AND t.completedAt >= :since")
    List<Task> findCompletedSince(@Param("since") LocalDateTime since);
    
    long countByCompleted(boolean completed);
    
    long countByPriority(Task.Priority priority);
    
    @Query("SELECT t FROM Task t WHERE t.completed = false AND " +
           "(t.priority = 'URGENT' OR t.priority = 'HIGH') " +
           "ORDER BY t.priority, t.createdAt")
    List<Task> findUrgentAndHighPriorityTasks();
} 