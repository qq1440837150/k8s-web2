package com.example.k8s_web.apitest.entry;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class TestTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date generateTime;
    private Integer totalRequestNums;
    private Integer totalThreadNums;
    private Long requestInfoId;
}
