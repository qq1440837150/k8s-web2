package com.example.k8s_web.apitest.controller;

import com.example.k8s_web.apitest.dto.RequestInfoDto;
import com.example.k8s_web.apitest.dto.TestTaskDto;
import com.example.k8s_web.apitest.entry.RequestInfo;
import com.example.k8s_web.apitest.entry.TestTask;
import com.example.k8s_web.apitest.repository.RequestInfoRepository;
import com.example.k8s_web.apitest.repository.TestTaskRepository;
import com.example.k8s_web.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/testTask")
public class TestTaskController {
    @Autowired
    private TestTaskRepository testTaskRepository;
    @Autowired
    private RequestInfoRepository requestInfoRepository;
    /**
     * 添加测试任务
     * @param testTaskDto
     * @return
     */
    @PostMapping("/addTestTask")
    @Transactional
    public ApiResult addTestTask(@RequestBody TestTaskDto testTaskDto) {
        RequestInfo requestInfo = RequestInfoDto.transfer(testTaskDto.getRequestInfoDto());
        requestInfoRepository.save(requestInfo);
        TestTask testTask = TestTaskDto.transfer(testTaskDto);
        testTask.setRequestInfoId(requestInfo.getId());
        testTaskRepository.save(testTask);
        return ApiResult.success();
    }

    /**
     * 删除一个任务
     * @param id
     * @return
     */
    @DeleteMapping("/delete")
    @Transactional
    public ApiResult deleteOneTask(Long id){
        Optional<TestTask> byId = this.testTaskRepository.findById(id);
        if (byId.isPresent()) {
            TestTask testTask = byId.get();
            Long requestInfoId = testTask.getRequestInfoId();
            testTaskRepository.deleteById(id);
            requestInfoRepository.deleteById(requestInfoId);
            return ApiResult.success();
        }else {
            return ApiResult.error("数据不存在");
        }

    }

    /**
     * 更新一个测试任务
     * @param testTaskDto
     * @return
     */
    @PutMapping("/updateTask")
    @Transactional
    public ApiResult updateTask(@RequestBody TestTaskDto testTaskDto){
        TestTask transfer = TestTaskDto.transfer(testTaskDto);
        this.testTaskRepository.save(transfer);
        return ApiResult.success(transfer);
    }
    /**
     * 获取测试任务列表
     * @return
     */
    @GetMapping("/list")
    public ApiResult listTask(PageRequest pageRequest){
        return ApiResult.success(testTaskRepository.findAllInfo(pageRequest));
    }

    /**
     * 获取一个测试任务
     * @param id
     * @return
     */
    @GetMapping("/id")
    public ApiResult oneTask(Long id){
        return ApiResult.success(testTaskRepository.findById(id));
    }

    /**
     * 启动一个测试任务
     * @return
     */
    @GetMapping("/start")
    public ApiResult runTask(Long id){
        // 获取任务启动

        return null;
    }

    /**
     * 停止一个测试任务
     * @return
     */
    @GetMapping("/stop")
    public ApiResult stopTask(Long id){
        return null;
    }
    @GetMapping("/result")
    public ApiResult taskResult(Long id){
        return null;
    }
}
