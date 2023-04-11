package com.example.k8s_web.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ApiClientConfig {
    @Bean
    public CoreV1Api apiClient() throws IOException {
        ApiClient client = Config.defaultClient().setVerifyingSsl(false);
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();
        return api;
    }
}
