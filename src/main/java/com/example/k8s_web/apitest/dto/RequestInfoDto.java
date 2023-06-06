package com.example.k8s_web.apitest.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.Map;

@Data
public class RequestInfoDto {
    private Long id;
    private Long  method;
    private Map<String,String> params;
    private Map<String,String> data;
    private String respectResponse;
}
