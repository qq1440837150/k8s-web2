package com.example.k8s_web.apitest.repository;

import com.example.k8s_web.apitest.entry.RequestInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestInfoRepository extends JpaRepository<RequestInfo, Long> {
}