package com.zmy.common;

import lombok.Data;

@Data
public class Result<T>{
    private Integer code;
    private String msg;
    private T data;
    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.code= ResultDetail.success.getCode();
        result.msg=ResultDetail.success.getMsg();
        result.data=data;
        return result;
    }

    public static <T> Result<T> fail(Integer code,String msg,T data){
        Result<T> result = new Result<>();
        if(code == null){
            result.code=ResultDetail.fail.getCode();
        } else if (msg == null) {
            result.msg=ResultDetail.fail.getMsg();
        }
        result.code=code;
        result.msg=msg;
        result.data=data;
        return result;
    }
}