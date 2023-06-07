package com.example.k8s_web.apitest.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestTaskRepositoryTest {
    @Autowired
    private TestTaskRepository testTaskRepository;
    @Test
    void findAllInfo2() {
        System.out.println(testTaskRepository.findAllInfo(PageRequest.of(0,10)));
    }
}