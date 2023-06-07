package com.example.k8s_web.apitest.service.impl;

import com.example.k8s_web.apitest.constant.RequestMethodConstant;
import com.example.k8s_web.apitest.dto.RequestInfoDto;
import com.example.k8s_web.apitest.dto.TestTaskDto;
import com.example.k8s_web.apitest.service.ApiTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ApiTestServiceImplTest {
    private ApiTestService apiTestService = new ApiTestServiceImpl();
    @Test
    void requestForResponse() {
        RequestInfoDto requestInfoDto = new RequestInfoDto();
        requestInfoDto.setData(null);
        requestInfoDto.setParams(null);
        requestInfoDto.setPath("https://www.baidu.com");
        requestInfoDto.setMethod(RequestMethodConstant.GET);
        System.out.println(apiTestService.requestForResponse(requestInfoDto));
    }


    @Test
    void assertEquals() {
        TestTaskDto testTaskDto = new TestTaskDto();
        testTaskDto.setGenerateTime(new Date());
        testTaskDto.setTotalRequestNums(100);
        testTaskDto.setTotalThreadNums(10);
        RequestInfoDto requestInfoDto = new RequestInfoDto();
        requestInfoDto.setData(null);
        requestInfoDto.setParams(null);
        requestInfoDto.setPath("https://www.baidu.com");
        requestInfoDto.setMethod(RequestMethodConstant.GET);
        testTaskDto.setRequestInfoDto(requestInfoDto);
        apiTestService.startOneTest(testTaskDto);
    }
}