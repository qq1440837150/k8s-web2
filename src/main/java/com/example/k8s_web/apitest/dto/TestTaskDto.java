package com.example.k8s_web.apitest.dto;

import com.example.k8s_web.apitest.entry.TestTask;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class TestTaskDto {
    private Long id;
    private Date generateTime;
    private Integer totalRequestNums;
    private Integer totalThreadNums;
    private Long requestInfoId;
    private RequestInfoDto requestInfoDto;
    @NotNull
    public static TestTask transfer(TestTaskDto testTaskDto){
        TestTask testTask = new TestTask();
        BeanUtils.copyProperties(testTaskDto,testTask);
        return testTask;
    }
}
