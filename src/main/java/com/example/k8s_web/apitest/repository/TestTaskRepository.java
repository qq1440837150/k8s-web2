package com.example.k8s_web.apitest.repository;

import com.example.k8s_web.apitest.dto.FullTaskInfoDto;
import com.example.k8s_web.apitest.entry.TestTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestTaskRepository extends JpaRepository<TestTask, Long> {
//    @Query(value = "select * from test_task tt request_info ri where tt.request_info_id =  ri.id",nativeQuery = true)
//    List<FullTaskInfoDto> findAllInfo();
    @Query(value = "select tt.id,tt.requestInfoId,tt.generateTime,tt.totalRequestNums,tt.totalThreadNums ,ri.data,ri.expectResponse,ri.method,ri.params,ri.path from TestTask tt left JOIN  RequestInfo ri on tt.requestInfoId = ri.id")
    Page<FullTaskInfoDto> findAllInfo(Pageable pageable);
    @Query(value = "select tt.id,tt.requestInfoId,tt.generateTime,tt.totalRequestNums,tt.totalThreadNums ,ri.data,ri.expectResponse,ri.method,ri.params,ri.path " +
            "from TestTask tt left JOIN  RequestInfo ri on tt.requestInfoId = ri.id where tt.id=:id")
    FullTaskInfoDto findAllInfoById(@Param("id")Long id);

    boolean existsByStatusEquals(Integer status);
}