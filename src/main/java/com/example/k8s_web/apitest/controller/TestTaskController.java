package com.example.k8s_web.apitest.controller;

import com.example.k8s_web.apitest.dto.TestTaskDto;
import com.example.k8s_web.vo.ApiResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testTask")
public class TestTaskController {
    /**
     * 添加测试任务
     * @param testTaskDto
     * @return
     */
    @PostMapping("/addTestTask")
    public ApiResult addTestTask(@RequestBody TestTaskDto testTaskDto) {
        return null;
    }

    /**
     * 删除一个任务
     * @param id
     * @return
     */
    @DeleteMapping("/delete")
    public ApiResult deleteOneTask(Long id){
        return null;
    }

    /**
     * 更新一个测试任务
     * @param testTaskDto
     * @return
     */
    @PutMapping("/updateTask")
    public ApiResult updateTask(@RequestBody TestTaskDto testTaskDto){
        return null;
    }
    /**
     * 获取测试任务列表
     * @return
     */
    @GetMapping("/list")
    public ApiResult listTask(PageRequest pageRequest){
        return null;
    }

    /**
     * 获取一个测试任务
     * @param id
     * @return
     */
    @GetMapping("/id")
    public ApiResult oneTask(Long id){
        return null;
    }

    /**
     * 启动一个测试任务
     * @return
     */
    @GetMapping("/start")
    public ApiResult runTask(Long id){
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
