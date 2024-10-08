package com.clv.kanbanapp.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @Column(name="title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_group_task", nullable = false)
    private boolean groupTask;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    @JsonIgnore
    private AppUser assignedUser;

    @CreatedBy
    @Column(name = "created_by_user", updatable = false)
    private String createdByUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.TODO;

    @Column(nullable = false)
    private Integer position;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    @JsonIgnore
    private TaskTag tag;

    @Column(name="due_time")
    private Instant dueTime;

    @Column(name = "created_date", updatable = false)
    @CreatedDate
    private Instant createdDate;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private List<Image> images;

    @Column(name = "updated_date")
    @LastModifiedDate
    private Instant updatedDate;
}
