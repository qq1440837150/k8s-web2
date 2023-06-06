package com.example.k8s_web.apitest.controller;

import com.example.k8s_web.apitest.dto.RequestInfoDto;
import com.example.k8s_web.apitest.service.ApiTestService;
import com.example.k8s_web.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apiTest")
public class ApiTestController {
    @Autowired
    private ApiTestService apiTestService;

    /**
     * 返回请求内容
     * @param requestInfoDto
     * @return
     */
    @GetMapping("/response")
    public ApiResult requestWithResponse(RequestInfoDto requestInfoDto){
        return ApiResult.success(apiTestService.requestForResponse(requestInfoDto));
    }

    /**
     * 判断返回请求内容是否与期望内容一致
     * @param requestInfoDto
     * @return
     */
    @GetMapping("/assertEquals")
    public ApiResult assertEquals(RequestInfoDto requestInfoDto){
        return ApiResult.success(apiTestService.assertEquals(requestInfoDto));
    }
}
