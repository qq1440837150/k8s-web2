package com.example.k8s_web.controller;

import cn.hutool.json.JSONUtil;
import com.example.k8s_web.vo.ApiResult;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/node")
public class NodeController {
    @Autowired
    private CoreV1Api coreV1Api;
    @GetMapping("/list")
    public ApiResult nodeList() throws IOException, ApiException {
        V1NodeList nodeList = coreV1Api.listNode(null,null,null,null,null,null
        ,null,null,null,null);

        return ApiResult.success(nodeList);
    }
    @GetMapping("/listPod")
    public ApiResult nodePodList(String name) throws IOException, ApiException {
        V1PodList list = coreV1Api.listPodForAllNamespaces(true,null, null, null, null, null, null, null, null, null);
        List<V1Pod> collect = list.getItems().stream().filter((v1Pod ->
                v1Pod.getSpec().getNodeName().equals(name))).collect(Collectors.toList());
        return ApiResult.success(JSONUtil.toJsonStr(collect));
    }
    @GetMapping("/computeResource")
    public ApiResult computeResource(String name) throws IOException, ApiException {
        BigDecimal requestCpu = new BigDecimal(0);
        BigDecimal requestMem = new BigDecimal(0);
        BigDecimal limitCpu = new BigDecimal(0);
        BigDecimal limitMem = new BigDecimal(0);
        V1PodList list = coreV1Api.listPodForAllNamespaces(true,null, null, null, null, null, null, null, null, null);
        List<V1Pod> collect = list.getItems().stream().filter((v1Pod ->
                v1Pod.getSpec().getNodeName().equals(name))).collect(Collectors.toList());
        for (V1Pod v1Pod : collect) {
            List<V1Container> containers = v1Pod.getSpec().getContainers();
            for (V1Container v1Container : containers) {
                Map<String, Quantity> requests = v1Container.getResources().getRequests();
                if (requests!=null&&requests.containsKey("cpu")) {
                    requestCpu = requestCpu.add(requests.get("cpu").getNumber());

                }
                if (requests!=null&&requests.containsKey("memory")) {
                    requestMem = requestMem.add(requests.get("memory").getNumber());
                }
                Map<String, Quantity> limits = v1Container.getResources().getLimits();

                if (limits!=null &&limits.containsKey("cpu")) {
                    limitCpu = limitCpu.add(limits.get("cpu").getNumber());

                }
                if (limits!=null &&limits.containsKey("memory")) {
                    limitMem = limitMem.add(limits.get("memory").getNumber());
                }
            }
        }

        requestMem = requestMem.divide(new BigDecimal(1024*1024));
        limitMem = limitMem.divide(new BigDecimal(1024*1024));

        HashMap<Object, Object> res = new HashMap<>();
        res.put("requestCpu",requestCpu.toString());
        res.put("requestMem",requestMem.toString());
        res.put("limitCpu",limitCpu.toString());
        res.put("limitMem",limitMem.toString());
        return ApiResult.success(res);
    }
    @GetMapping("/podCount")
    public ApiResult podCount() throws IOException, ApiException {
        V1NodeList nodeList = coreV1Api.listNode(null,null,null,null,
                null,null,null,null,null,null );
        HashMap<String, Long> res = new HashMap<>();
        V1PodList list = coreV1Api.listPodForAllNamespaces(true,null, null, null, null, null, null, null, null, null);

        for (V1Node item : nodeList.getItems()) {
            long count = list.getItems().stream().filter((v1Pod ->
                    v1Pod.getSpec().getNodeName().equals(item.getMetadata().getName()))).count();
            res.put(item.getMetadata().getName(),count);

        }

        return ApiResult.success(res);
    }
}
