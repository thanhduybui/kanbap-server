package com.clv.kanbanapp.repository;

import com.clv.kanbanapp.entity.TaskTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<TaskTag, Integer> {
}
