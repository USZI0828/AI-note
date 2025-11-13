package com.zmy.config.handler;

import com.zmy.common.Result;
import com.zmy.exception.TaskException.TaskExistedException;
import com.zmy.exception.TaskException.TaskNoExistedException;
import com.zmy.exception.UserException.*;
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

    @ExceptionHandler(TaskNoExistedException.class)
    public Result<?> TaskNoExistedException() {
        return Result.fail(110,"任务不存在",null);
    }

    @ExceptionHandler(TaskExistedException.class)
    public Result<?> TaskExistedException() {
        return Result.fail(110,"任务已存在",null);
    }


}
