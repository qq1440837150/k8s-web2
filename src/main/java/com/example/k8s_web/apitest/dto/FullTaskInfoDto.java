package com.example.k8s_web.apitest.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class FullTaskInfoDto {
    private Long id;
    private Date generateTime;
    private Integer totalRequestNums;
    private Integer totalThreadNums;
    private Long requestInfoId;

    private Integer method;
    // 请求路径
    private String path;
    // 请求参数
    private String params;
    // 请求body
    private String data;
    // 期望值
    private String expectResponse;
}
