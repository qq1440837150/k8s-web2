package com.example.k8s_web.controller;

import cn.hutool.json.JSONUtil;
import com.example.k8s_web.vo.ApiResult;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
