package com.weison.sbr.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {

    USER_NOT_FOUND(1000, "用户不存在"),
    UPDATE_INVITE_ACCOUNT_ERROR(1001, "更新账户失败"),
    ACCOUNT_NOT_FOUND(1002, "未找到账户信息"),
    TASK_NOT_FOUND(1003, "任务不存在"),
    TASK_LOG_NOT_FOUND(1004, "未领取本任务"),
    TASK_LOG_COMPLETED(1005, "任务奖励已领取"),
    TASK_COMPLETE_ERROR(1006, "任务完成失败"),
    TaskLog_FAILED(1007, "交易失败"),
    ACCOUNT_BALANCE_INSUFFICIENT(1008, "账户余额不足"),

    DEFAULT(0000, "系统内部错误");

    private int code;
    private String desc;

    ExceptionCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
