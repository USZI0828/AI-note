package com.zmy.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultDetail {

    success(200, "成功"),
    //默认失败信息
    fail(400, "失败");

    private final Integer code;
    private final String msg;

}
