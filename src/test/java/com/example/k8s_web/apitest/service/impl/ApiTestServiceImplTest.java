package com.example.k8s_web.apitest.service.impl;

import com.example.k8s_web.apitest.constant.RequestMethodConstant;
import com.example.k8s_web.apitest.dto.RequestInfoDto;
import com.example.k8s_web.apitest.service.ApiTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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


}