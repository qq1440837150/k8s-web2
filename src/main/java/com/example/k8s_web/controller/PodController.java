package com.example.k8s_web.controller;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.ObjectMapper;
import com.example.k8s_web.vo.ApiResult;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/pod")
public class PodController {
    @Autowired
    private CoreV1Api coreV1Api;
    @GetMapping("/list")
    public ApiResult nodeList() throws IOException, ApiException {

        V1PodList list = coreV1Api.listPodForAllNamespaces(true,null, null, null, null, null, null, null, null, null);
        return ApiResult.success(JSONUtil.toJsonStr(list));
    }
    @GetMapping("/log")
    public void logInfo(String name, HttpServletResponse response) throws IOException, ApiException {
        String fieldSelector = "metadata.name="+name;
        PodLogs logs = new PodLogs();

        V1PodList list = coreV1Api.listPodForAllNamespaces(true,null, fieldSelector, null, null, null, null, null, null, null);
        List<V1Pod> items = list.getItems();
        if (items.size()>0) {
            V1Pod v1Pod = items.get(0);
            InputStream is = logs.streamNamespacedPodLog(v1Pod);
            Streams.copy(is, System.out);
            response.getOutputStream().flush();
        }
    }
    @GetMapping("/logs/range")
    public void logInfo2(String name, Long lines, Boolean reverse, HttpServletResponse response) throws IOException, ApiException {
        String fieldSelector = "metadata.name="+name;
        PodLogs logs = new PodLogs();
        if (reverse == null) {
            reverse = true;
        }
        V1PodList list = coreV1Api.listPodForAllNamespaces(true,null, fieldSelector, null, null, null, null, null, null, null);
        List<V1Pod> items = list.getItems();
        if (items.size()>0) {
            V1Pod v1Pod = items.get(0);
            InputStream is = logs.streamNamespacedPodLog(v1Pod);
            Streams.copy(is, System.out);
            response.getOutputStream().flush();
        }
    }
    @GetMapping("/delete")
    public ApiResult delete(String name, HttpServletResponse response) throws IOException, ApiException {
        String fieldSelector = "metadata.name="+name;
        V1PodList list = coreV1Api.listPodForAllNamespaces(true,null, fieldSelector, null, null, null, null, null, null, null);
        List<V1Pod> items = list.getItems();
        if (items.size()>0) {
            V1Pod v1Pod = items.get(0);
            coreV1Api.deleteNamespacedPod(v1Pod.getMetadata().getName(),v1Pod.getMetadata().getNamespace(),null,
                    null,null,null,null,null);
            return ApiResult.success();
        }
        return ApiResult.error();
    }

}
