package com.example.k8s_web;

import com.example.k8s_web.task.RunTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ServletComponentScan
@EnableWebSocket
public class Application {
    @Value("${app.config.kubernetes-api}")
    private  String kubernetesAPI;
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }
    @PostConstruct
    void init(){
        RunTask.httpPrefix = kubernetesAPI+"/api/v1/query_range";
        System.out.println(RunTask.httpPrefix);
    }

}
