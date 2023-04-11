package com.example.k8s_web.entiry;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String container;
    private Short status;
    private String podName;
    private Integer podNums;
    @Column(name = "timeInterval")
    private Integer interval;
    private Integer scheduler;

}
