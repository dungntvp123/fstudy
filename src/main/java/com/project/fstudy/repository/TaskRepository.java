package com.project.fstudy.repository;

import com.project.fstudy.data.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND DATE(t.startTime) = DATE(:date)")
    List<Task> findTasksByUserAndDate(@Param("userId") int userId, @Param("date") LocalDateTime date);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId ")
    List<Task> findTasksByUser(@Param("userId") int userId);
}
