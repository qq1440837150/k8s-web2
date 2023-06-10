package com.example.k8s_web.apitest.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.example.k8s_web.apitest.constant.RequestMethodConstant;
import com.example.k8s_web.apitest.dto.RequestInfoDto;
import com.example.k8s_web.apitest.dto.TestTaskDto;
import com.example.k8s_web.apitest.entry.TestTask;
import com.example.k8s_web.apitest.repository.RequestInfoRepository;
import com.example.k8s_web.apitest.repository.TestTaskRepository;
import com.example.k8s_web.apitest.service.ApiTestService;
import org.apache.tomcat.jni.Time;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.*;

@Service
public class ApiTestServiceImpl implements ApiTestService {
    @Autowired
    private TestTaskRepository testTaskRepository;
    @Autowired
    private RequestInfoRepository requestInfoRepository;
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
        return request.trim().equals(requestInfoDto.getExpectResponse().trim());

    }

    @Override
    @Transactional
    public synchronized void startOneTest(TestTaskDto testTaskDto) {
        if (this.testTaskRepository.existsByStatusEquals(1)) {// 存在已经运行的任务
            return;
        }
        // 设置状态
        Long id = testTaskDto.getId();
        Optional<TestTask> byId = this.testTaskRepository.findById(id);
        if (!byId.isPresent()) {
            return;
        }
        TestTask testTask = byId.get();
        testTask.setStatus(1);
        this.testTaskRepository.save(testTask);
        // 启动任务
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(testTaskDto.getTotalThreadNums(),
                testTaskDto.getTotalThreadNums(),1, TimeUnit.HOURS,new LinkedBlockingQueue<>(testTaskDto.getTotalRequestNums()));
        for (int i = 0; i < testTaskDto.getTotalRequestNums(); i++) {
            threadPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    assertEquals(testTaskDto.getRequestInfoDto());
                }
            });
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 每1s监听一次任务是否停止
                while(true){
                    // 每1s监听一次
                    try {
                        Thread.sleep(1000*1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (threadPoolExecutor.getActiveCount()==0) {
                        Long id = testTaskDto.getId();
                        Optional<TestTask> byId = testTaskRepository.findById(id);
                        if (!byId.isPresent()) {
                            return;
                        }
                        TestTask testTask = byId.get();
                        testTask.setStatus(0);
                        testTaskRepository.save(testTask);
                        break;
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

    }

    @Override
    public synchronized void stopTask(Long taskId) {

    }

    public static void main(String[] args) {
        System.out.println(HttpUtil.get("http://www.baidu.com"));
    }
}
