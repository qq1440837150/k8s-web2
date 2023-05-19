package com.example.k8s_web.controller;

import cn.hutool.core.io.resource.ClassPathResource;
import com.example.k8s_web.vo.ApiResult;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapList;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PodControllerTest {
    @Autowired
    PodController podController;
    @Autowired
    private CoreV1Api coreV1Api;
    @Test
    void logInfo() throws IOException, ApiException {
        ClassPathResource classPathResource = new ClassPathResource("policy_cpu.yaml");
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