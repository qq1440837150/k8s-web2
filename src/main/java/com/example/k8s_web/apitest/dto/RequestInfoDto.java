package com.example.k8s_web.apitest.dto;

import cn.hutool.json.JSONUtil;
import com.example.k8s_web.apitest.entry.RequestInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

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
    public static RequestInfo transfer(RequestInfoDto requestInfoDto){
        RequestInfo requestInfo = new RequestInfo();
        BeanUtils.copyProperties(requestInfoDto,requestInfo);
        requestInfo.setData(JSONUtil.toJsonStr(requestInfoDto.getData()));
        requestInfo.setParams(JSONUtil.toJsonStr(requestInfo.getParams()));
        return requestInfo;
    }
}
