package com.clv.kanbanapp.repository;

import com.clv.kanbanapp.entity.Task;
import com.clv.kanbanapp.entity.TaskStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.status = :status " +
            "AND ((t.groupTask = false AND t.assignedUser.email = :userEmail) " +
            "OR (t.groupTask = true AND t.assignedUser.email = :userEmail)) " +
            "ORDER BY t.position DESC")
    List<Task> findPrivateTasks(
            @Param("status") TaskStatus status,
            @Param("userEmail") String userEmail
    );

    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.groupTask = true ORDER BY t.position DESC")
    List<Task> findPublicTasksByStatus(TaskStatus status);

    @Query("SELECT MAX(t.position) FROM Task t WHERE t.status = :status " +
            "AND ((:isGroupTask = true) OR " +
            "(:isGroupTask = false AND t.assignedUser.email = :userEmail))")
    Integer findMaxPosition(@Param("status") TaskStatus status,
                            @Param("userEmail") String userEmail,
                            @Param("isGroupTask") boolean isPublic);

}
