package com.example.k8s_web.apitest.entry;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Map;
@Entity
@Data
public class RequestInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 请求方式
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
