package com.example.k8s_web.error_enum;

import com.example.k8s_web.vo.ApiResult;

public enum ErrorEnum {
    EXIST_TASK(50001,"存在任务"),
    DATA_NON_EXIST(50002,"数据不存在");
    ;

    ErrorEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public ApiResult res(){
        return new ApiResult(this.code,this.msg,null);
    }
    private Integer code;



    /**
     * 提示信息
     */
    private String msg;
}
