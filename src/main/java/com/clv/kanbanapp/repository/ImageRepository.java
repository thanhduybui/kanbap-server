package com.clv.kanbanapp.repository;

import com.clv.kanbanapp.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
