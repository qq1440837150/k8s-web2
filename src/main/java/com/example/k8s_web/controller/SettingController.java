package com.example.k8s_web.controller;

import cn.hutool.core.io.resource.ClassPathResource;
import com.example.k8s_web.repository.RunRecordRepository;
import com.example.k8s_web.vo.ApiResult;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@RequestMapping("/setting")
@RestController
public class SettingController {
    @Autowired
    private CoreV1Api coreV1Api;
    @GetMapping("/schedulerCpuConfig")
    public ApiResult schedulerCpuConfig() throws ApiException {
        // 设置policy
        setPolicy("policy_cpu.yaml");
        // 删除pod
        String lableSelector = "component=scheduler";
        V1PodList list = coreV1Api.listPodForAllNamespaces(true,null, null, lableSelector, null, null, null, null, null, null);
        List<V1Pod> items = list.getItems();
        if (items.size()>0) {
            V1Pod v1Pod = items.get(0);
            coreV1Api.deleteNamespacedPod(v1Pod.getMetadata().getName(),v1Pod.getMetadata().getNamespace(),null,
                    null,null,null,null,null);
        }
        return ApiResult.success();
    }
    @GetMapping("/schedulerMemConfig")
    public ApiResult schedulerMemConfig() throws ApiException {
        // 设置policy
        setPolicy("policy_mem.yaml");
        // 删除pod
        String lableSelector = "component=scheduler";
        V1PodList list = coreV1Api.listPodForAllNamespaces(true,null, null, lableSelector, null, null, null, null, null, null);
        List<V1Pod> items = list.getItems();
        if (items.size()>0) {
            V1Pod v1Pod = items.get(0);
            coreV1Api.deleteNamespacedPod(v1Pod.getMetadata().getName(),v1Pod.getMetadata().getNamespace(),null,
                    null,null,null,null,null);
        }
        return ApiResult.success();
    }
    @GetMapping("/policyInfo")
    public ApiResult policyInfo() throws ApiException, IOException {
        V1ConfigMapList v1ConfigMapList = coreV1Api.listNamespacedConfigMap("crane-system",null,
                null,null,"metadata.name=dynamic-scheduler-policy",null,null,null,null,null,null);

        if (v1ConfigMapList.getItems().size()>0) {
            V1ConfigMap v1ConfigMap = v1ConfigMapList.getItems().get(0);
            String content = v1ConfigMap.getData().get("policy.yaml");
            Yaml yaml = new Yaml();
            Object load = yaml.load(content);
            String dump = yaml.dump(load);
            return ApiResult.success(dump);
        }
        return ApiResult.error();
    }
    private void setPolicy(String policyName) throws ApiException {
        ClassPathResource classPathResource = new ClassPathResource(policyName);
        String content = classPathResource.readStr(Charset.defaultCharset());
        V1ConfigMapList v1ConfigMapList = coreV1Api.listNamespacedConfigMap("crane-system",null,
                null,null,"metadata.name=dynamic-scheduler-policy",null,null,null,null,null,null);

        if (v1ConfigMapList.getItems().size()>0) {
            V1ConfigMap v1ConfigMap = v1ConfigMapList.getItems().get(0);
            v1ConfigMap.getData().put("policy.yaml",content);
            coreV1Api.replaceNamespacedConfigMap(v1ConfigMap.getMetadata().getName(),v1ConfigMap.getMetadata().getNamespace()
                    ,v1ConfigMap,null,null,null,null);
        }
    }
}
