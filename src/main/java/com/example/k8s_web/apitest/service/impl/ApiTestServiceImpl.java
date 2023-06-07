package com.example.k8s_web.apitest.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.example.k8s_web.apitest.constant.RequestMethodConstant;
import com.example.k8s_web.apitest.dto.RequestInfoDto;
import com.example.k8s_web.apitest.dto.TestTaskDto;
import com.example.k8s_web.apitest.service.ApiTestService;
import org.apache.tomcat.jni.Time;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

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
//             responseContent = HttpUtil.get(requestInfoDto.getPath(), requestInfoDto.getParams());
            responseContent = HttpUtil.get(requestInfoDto.getPath());

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
        System.out.println(request);
        return request.trim().equals(requestInfoDto.getExpectResponse().trim());

    }

    @Override
    public void startOneTest(TestTaskDto testTaskDto) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(testTaskDto.getTotalThreadNums(),
                testTaskDto.getTotalThreadNums(),1, TimeUnit.HOURS,new LinkedBlockingQueue<>(testTaskDto.getTotalRequestNums()));
        for (int i = 0; i < testTaskDto.getTotalRequestNums(); i++) {
            threadPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(assertEquals(testTaskDto.getRequestInfoDto()));
                }
            });
        }
    }

    public static void main(String[] args) {
        System.out.println(HttpUtil.get("http://www.baidu.com"));
    }
}
