package com.example.k8s_web.controller;

import com.example.k8s_web.entiry.Task;
import com.example.k8s_web.error_enum.ErrorEnum;
import com.example.k8s_web.repository.RunRecordRepository;
import com.example.k8s_web.repository.TaskRepository;
import com.example.k8s_web.task.RunTask;
import com.example.k8s_web.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private RunRecordRepository runRecordRepository;
    private Thread thread;
    @GetMapping("/list")
    public ApiResult list(Integer page,Integer size){
        PageRequest pageRequest = PageRequest.of(page,size);
        Page<Task> all = this.taskRepository.findAll(pageRequest);
        return ApiResult.success(all);
    }
    @GetMapping("/start")
    public ApiResult startTask(Long id){
        // 1. 查询是否有任务启动，默认只能启动一个任务
        if (taskRepository.existsByStatusEquals((short) 1)) {
            return ErrorEnum.EXIST_TASK.res();
        }else{
            Optional<Task> byId = taskRepository.findById(id);
            if (byId.isPresent()) {
                Task task = byId.get();
                task.setStatus((short) 1);
                taskRepository.save(task);
                RunTask runTask = new RunTask();
                runTask.setTaskId(task.getId());
                runTask.setTaskRepository(taskRepository);
                runTask.setRunRecordRepository(runRecordRepository);
                thread = new Thread(runTask);
                thread.start();
                return ApiResult.success();
            }else{
                return ErrorEnum.DATA_NON_EXIST.res();
            }
        }
    }
    @GetMapping("/stop")
    public ApiResult stopTask(Long id){
        // 1. 查询是否有任务启动
        Optional<Task> byId = taskRepository.findById(id);
        Task task = byId.get();
        task.setStatus((short) 0);
        taskRepository.save(task);
        if (thread != null) {
            thread.interrupt();
        }
        return ApiResult.success();
    }
    @PostMapping("/add")
    public ApiResult addTask(@RequestBody Task task){
        taskRepository.save(task);
        return ApiResult.success();
    }
    @PutMapping("/update")
    public ApiResult updateTask(@RequestBody Task task){
        taskRepository.save(task);
        return ApiResult.success();
    }

    @DeleteMapping("/delete")
    @Transactional
    public ApiResult deleteTask(Long id){
        taskRepository.deleteById(id);
        return ApiResult.success();
    }
    @GetMapping("/one")
    public ApiResult ontainOne(Long id){
        Optional<Task> byId = taskRepository.findById(id);

        return ApiResult.success(byId.get());
    }
}
