package com.clv.kanbanapp.entity;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUser  {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Column(name = "hash_password", length = 255)
    private String password;

    @Column(name = "join_date")
    private Instant joinDate;

    @OneToMany(mappedBy = "assignedUser", fetch = FetchType.LAZY)
    private List<Task> tasks;
}


