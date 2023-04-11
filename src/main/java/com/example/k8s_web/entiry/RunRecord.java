package com.example.k8s_web.entiry;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class RunRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long taskId;
    private Date startDate;
    private Date endDate;
    @Column(columnDefinition = "longtext")
    private String cpuRecord;
    @Column(columnDefinition = "longtext")
    private String memRecord;
}
