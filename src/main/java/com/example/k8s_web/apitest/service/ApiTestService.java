package com.example.k8s_web.apitest.service;

import com.example.k8s_web.apitest.dto.RequestInfoDto;
import com.example.k8s_web.apitest.dto.TestTaskDto;

public interface ApiTestService {
    String requestForResponse(RequestInfoDto requestInfoDto);

    Boolean assertEquals(RequestInfoDto requestInfoDto);

    void startOneTest(TestTaskDto testTaskDto);

    void stopTask(Long taskId);
}
