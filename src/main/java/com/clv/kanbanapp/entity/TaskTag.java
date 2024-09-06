package com.clv.kanbanapp.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "task_tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 255, unique = true)
    private String name;

    @Column(name = "color", length = 7)
    private String color;

    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private List<Task> tasks;
}
