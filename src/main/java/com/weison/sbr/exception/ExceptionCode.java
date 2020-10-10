package com.weison.sbr.exception;

import lombok.Getter;

/**
 * @author WeisonWei
 * @date 2020/10/10
 */
@Getter
public enum ExceptionCode {

    DEFAULT(0000, "系统内部错误");

    private int code;
    private String desc;

    ExceptionCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
