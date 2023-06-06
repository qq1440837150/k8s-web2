package com.example.k8s_web.apitest.service;

import com.example.k8s_web.apitest.dto.RequestInfoDto;

public interface ApiTestService {
    String requestForResponse(RequestInfoDto requestInfoDto);

    Boolean assertEquals(RequestInfoDto requestInfoDto);
}
