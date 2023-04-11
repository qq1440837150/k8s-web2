package com.example.k8s_web.controller;

import com.example.k8s_web.entiry.RunRecord;
import com.example.k8s_web.repository.RunRecordRepository;
import com.example.k8s_web.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/record")
public class RunRecordController {
    @Autowired
    private RunRecordRepository runRecordRepository;
    @GetMapping("/all")
    public ApiResult listAll(Long taskId,Integer page,Integer size){
        PageRequest pageRequest = PageRequest.of(page,size);
        Page<RunRecord> result = null;
        if (taskId == null) {
            result = this.runRecordRepository.findAll(pageRequest);
        }else{
            result = this.runRecordRepository.findAllByTaskIdEquals(taskId,pageRequest);
        }
        return ApiResult.success(result);
    }
}
