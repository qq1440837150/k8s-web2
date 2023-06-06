package com.example.k8s_web.apitest.controller;

import com.example.k8s_web.apitest.dto.RequestInfoDto;
import com.example.k8s_web.vo.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apiTest")
public class ApiTestController {

    @GetMapping("/response")
    public ApiResult requestWithResponse(RequestInfoDto requestInfoDto){

        return null;
    }
}
