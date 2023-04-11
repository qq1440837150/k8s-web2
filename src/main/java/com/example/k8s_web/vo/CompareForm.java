package com.example.k8s_web.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
public class CompareForm {
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date start1;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date end1;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date start2;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date end2;
}
