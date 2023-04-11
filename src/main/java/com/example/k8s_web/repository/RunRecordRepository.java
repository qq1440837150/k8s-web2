package com.example.k8s_web.repository;

import com.example.k8s_web.entiry.RunRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunRecordRepository extends JpaRepository<RunRecord, Long> {
    Page<RunRecord> findAllByTaskIdEquals(Long taskId, Pageable page);

}