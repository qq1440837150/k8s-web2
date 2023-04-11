package com.example.k8s_web.repository;

import com.example.k8s_web.entiry.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByStatusEquals(Short status);
}