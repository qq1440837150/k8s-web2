package com.example.k8s_web.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * api接口返回结果
 *
 * @author huang
 * @date 2021/1/25 15:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码（200=成功）
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 结果
     */
    private T data;

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static <T> ApiResult<T> success() {
        return ApiResult.success ("操作成功", null);
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static <T> ApiResult<T> success(T data) {
        return ApiResult.success ("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static <T> ApiResult<T> success(String msg, T data) {
        return new ApiResult<> (200, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static <T> ApiResult<T> error() {
        return ApiResult.error ("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static <T> ApiResult<T> error(String msg) {
        return ApiResult.error (msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static <T> ApiResult<T> error(String msg, T data) {
        return ApiResult.error (500, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static <T> ApiResult<T> error(int code, String msg, T data) {
        return new ApiResult<> (code, msg, data);
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    public static <T> ApiResult<T> toResult(int rows) {
        return rows > 0 ? ApiResult.success () : ApiResult.error ();
    }

    /**
     * 响应返回结果
     *
     * @param tag 操作结果
     * @return 操作结果
     */
    public static <T> ApiResult<T> toResult(Boolean tag) {
        return tag ? ApiResult.success () : ApiResult.error ();
    }

}
