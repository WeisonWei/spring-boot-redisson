package com.weison.sbr.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class TaskException extends RuntimeException {

    private Integer code;

    public TaskException() {
        super();
    }

    public TaskException(int code, String message) {
        super(message);
        this.code = code;
    }

    public TaskException(Throwable cause) {
        super(cause);
    }

    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

}
