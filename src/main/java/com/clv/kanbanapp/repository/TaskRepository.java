package com.clv.kanbanapp.repository;

import com.clv.kanbanapp.entity.Task;
import com.clv.kanbanapp.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatusAndGroupTaskAndCreatedByUser(TaskStatus status, boolean isPublic, String email);
    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.groupTask = :isGroupTask AND " +
            "(t.createdByUser = :userEmail OR t.assignedUser.email = :userEmail)")
    List<Task> findTasks(
            @Param("status") TaskStatus status,
            @Param("isGroupTask") boolean isPublic,
            @Param("userEmail") String userEmail
    );
}
