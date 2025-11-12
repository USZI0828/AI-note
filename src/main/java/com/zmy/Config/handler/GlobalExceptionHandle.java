package com.zmy.Config.handler;

import com.arrend_system.common.Result;
import com.arrend_system.exception.OrderException.CompletedOrderException;
import com.arrend_system.exception.OrderException.NoEnoughCountException;
import com.arrend_system.exception.UserException.*;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandle {
    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException e) {
        List<FieldError> allErrors = e.getFieldErrors();
        String errorMessage = allErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return Result.fail(100, "参数不合法", errorMessage);
    }

    @ExceptionHandler(CodeExpiredException.class)
    public Result<?> CodeExpiredException() {
        return Result.fail(101,"验证码过期",null);
    }

    @ExceptionHandler(EmailUsedException.class)
    public Result<?> EmailUsedException() {
        return Result.fail(102,"邮箱已被使用",null);
    }

    @ExceptionHandler(UserDeletedException.class)
    public Result<?> UserDeletedException() {
        return Result.fail(103,"用户已经被删除",null);
    }

    @ExceptionHandler(UserExitedException.class)
    public Result<?> UserExitedException() {
        return Result.fail(104,"用户已存在",null);
    }

    @ExceptionHandler(UserNoExistedException.class)
    public Result<?> UserNoExistedException() {
        return Result.fail(105,"用户不存在",null);
    }

    @ExceptionHandler(NoEnoughCountException.class)
    public Result<?> NoEnoughCountException() {
        return Result.fail(201,"用户余额不足",null);
    }

    @ExceptionHandler(CompletedOrderException.class)
    public Result<?> CompletedOrderException() {
        return Result.fail(202,"订单已完成，无法取消",null);
    }

}
