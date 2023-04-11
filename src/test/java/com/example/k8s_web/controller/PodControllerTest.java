package com.example.k8s_web.controller;

import com.example.k8s_web.vo.ApiResult;
import io.kubernetes.client.openapi.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class PodControllerTest {
    @Autowired
    PodController podController;
    @Test
    void logInfo() throws IOException, ApiException {
    }
}