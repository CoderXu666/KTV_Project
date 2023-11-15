package com.ktv.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Version 1.0
 * @Description: 响应封装类状态码 + 响应信息
 */
@Getter
@AllArgsConstructor
public enum ResponseEnum {
    /**
     * 通用
     */
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败");

    private Integer code;
    private String msg;
}