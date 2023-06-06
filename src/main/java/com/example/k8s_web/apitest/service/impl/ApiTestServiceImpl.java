package com.example.k8s_web.apitest.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.example.k8s_web.apitest.constant.RequestMethodConstant;
import com.example.k8s_web.apitest.dto.RequestInfoDto;
import com.example.k8s_web.apitest.service.ApiTestService;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class ApiTestServiceImpl implements ApiTestService {
    @Override
    public String requestForResponse(RequestInfoDto requestInfoDto) {
        String responseContent = request(requestInfoDto);
        return responseContent;
    }

    @Nullable
    private String request(RequestInfoDto requestInfoDto) {
        String responseContent = null;
        if (requestInfoDto.getMethod()== RequestMethodConstant.GET) {
             responseContent = HttpUtil.get(requestInfoDto.getPath(), requestInfoDto.getParams());
        }else if(requestInfoDto.getMethod()== RequestMethodConstant.POST){
             responseContent = HttpUtil.post(requestInfoDto.getPath(), JSONUtil.toJsonStr(requestInfoDto.getData()));
        }else if(requestInfoDto.getMethod()== RequestMethodConstant.PUT){

        }else if(requestInfoDto.getMethod()== RequestMethodConstant.DELETE){

        }
        return responseContent;
    }

    @Override
    public Boolean assertEquals(RequestInfoDto requestInfoDto) {
        String request = request(requestInfoDto);
        return request.trim().equals(requestInfoDto.getExpectResponse().trim());

    }
}
