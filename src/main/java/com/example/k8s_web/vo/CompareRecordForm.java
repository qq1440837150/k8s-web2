package com.example.k8s_web.vo;

import lombok.Data;

@Data
public class CompareRecordForm {
    private Long recordId1;
    private Long recordId2;
    private Long type;
}
