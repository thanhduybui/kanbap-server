package com.clv.kanbanapp.repository;

import com.clv.kanbanapp.entity.Task;
import com.clv.kanbanapp.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.groupTask = false " +
            "AND t.assignedUser.email = :userEmail ORDER BY t.position DESC")
    List<Task> findPrivateTasks(
            @Param("status") TaskStatus status,
            @Param("userEmail") String userEmail
    );

    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.groupTask = true ORDER BY t.position DESC")
    List<Task> findPublicTasksByStatus(TaskStatus status);


//    @Query("SELECT t FROM Task t WHERE t.status = :status " +
//            "AND ((:isGroupTask = true) OR " +
//            "(:isGroupTask = false AND t.assignedUser.email = :userEmail))")
//    List<Task> findTasks(
//            @Param("status") TaskStatus status,
//            @Param("isGroupTask") boolean isPublic,
//            @Param("userEmail") String userEmail
//    );



    // find the max position of the task in the task list
//    @Query("SELECT MAX(t.position) FROM Task t WHERE t.status = :status and t.groupTask = false " +
//            "AND t.assignedUser.email = :userEmail")
//    Integer findMaxPrivatePosition(@Param("status") TaskStatus status,
//                            @Param("userEmail") String userEmail);
//
//    @Query("SELECT MAX(t.position) FROM Task t WHERE t.status = :status and t.groupTask = true")
//    Integer findMaxPublicListPosition(TaskStatus status);

    @Query("SELECT MAX(t.position) FROM Task t WHERE t.status = :status " +
            "AND ((:isGroupTask = true) OR " +
            "(:isGroupTask = false AND t.assignedUser.email = :userEmail))")
    Integer findMaxPosition(@Param("status") TaskStatus status,
                            @Param("userEmail") String userEmail,
                            @Param("isGroupTask") boolean isPublic);
}
