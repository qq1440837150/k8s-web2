package com.example.k8s_web.apitest.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.Map;

@Data
public class RequestInfoDto {
    private Long id;
    private Integer  method;
    private String path;
    private Map<String,Object> params;
    private Map<String,Object> data;
    private String expectResponse;
}
