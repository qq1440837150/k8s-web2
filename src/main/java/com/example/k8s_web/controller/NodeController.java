package com.example.k8s_web.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.k8s_web.util.RestTemplateUtils;
import com.example.k8s_web.vo.ApiResult;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/node")
public class NodeController {
    String httpPrefix = "/api/v1/query";
    @Value("${app.config.kubernetes-api}")
    private String kubernetesAPI;
    @PostConstruct
    public void init(){
        httpPrefix = kubernetesAPI +httpPrefix;
    }
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
        HashMap<Object, Object> res = new HashMap<>();
        computeResource(name, res);
        return ApiResult.success(res);
    }
    @GetMapping("/computeRealResource")
    public ApiResult computeRealResource() throws ApiException {
        HashMap<String, String> cpuValue = new HashMap<>();
        HashMap<String, String> memValue = new HashMap<>();
        long current = System.currentTimeMillis();
        obtainRealResource(cpuValue, "cpu_usage_active", (double) current);
        obtainRealResource(memValue, "mem_usage_active", (double) current);
        // 获取总容量
        V1NodeList nodeList = this.coreV1Api.listNode(null, null, null, null, null, null, null
                , null, null, null);
        List<V1Node> items = nodeList.getItems();
        HashMap<String, Object> capacitys = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            V1Node v1Node = items.get(i);
            Map<String, Quantity> capacity = v1Node.getStatus().getCapacity();
            capacitys.put(v1Node.getMetadata().getName(),capacity);
        }
        // 返回值
        HashMap<String, Object> res = new HashMap<>();
        res.put("cpu",cpuValue);
        res.put("mem",memValue);
        res.put("capacitys",capacitys);

        return ApiResult.success(res);
    }

    private void obtainRealResource(HashMap<String, String> cpuValue, String metricStr, double current) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("query", metricStr);
        jsonObject.putOpt("time",String.format("%.3f", current /1000));
        String http = RestTemplateUtils.getHttp(httpPrefix, jsonObject);
        JSONObject res = JSONUtil.parseObj(http);
        JSONObject data = res.getJSONObject("data");
        JSONArray result = data.getJSONArray("result");
        for (int i = 0; i < result.size(); i++) {
            JSONObject metric = result.getJSONObject(i);
            JSONArray value = metric.getJSONArray("value");
            String str = value.getStr(1);
            JSONObject metricInfo = metric.getJSONObject("metric");
            String instance = metricInfo.getStr("instance");
            cpuValue.put(instance,str);
        }

    }

    private void computeRealResource(String name, HashMap<Object, Object> res) {

    }

    private void computeResource(String name, HashMap<Object, Object> res) throws ApiException {
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

        res.put("requestCpu",requestCpu.toString());
        res.put("requestMem",requestMem.toString());
        res.put("limitCpu",limitCpu.toString());
        res.put("limitMem",limitMem.toString());
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
