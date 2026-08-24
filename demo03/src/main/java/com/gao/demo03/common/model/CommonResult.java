package com.gao.demo03.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult<T> {

    private Integer code;

    private String message;

    private T data;

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(0, "success", data);
    }

    public static <T> CommonResult<T> fail(String message) {
        return new CommonResult<>(0, message, null);
    }
}
