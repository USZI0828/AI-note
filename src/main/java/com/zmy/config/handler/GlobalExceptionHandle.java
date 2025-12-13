package com.zmy.config.handler;

import com.zmy.common.Result;
import com.zmy.exception.SubjectException.SubjectExistedException;
import com.zmy.exception.SubjectException.SubjectNoExistedException;
import com.zmy.exception.TaskException.TaskExistedException;
import com.zmy.exception.TaskException.TaskNoExistedException;
import com.zmy.exception.TagException.TagExistedException;
import com.zmy.exception.TagException.TagNoExistedException;
import com.zmy.exception.NoteException.NoteExistedException;
import com.zmy.exception.NoteException.NoteNoExistedException;
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

    @ExceptionHandler(SubjectNoExistedException.class)
    public Result<?> SubjectNoExistedException() {return Result.fail(111,"科目不存在",null);}

    @ExceptionHandler(SubjectExistedException.class)
    public Result<?> SubjectExistedException() {return Result.fail(111,"科目已存在",null);}

    @ExceptionHandler(TagNoExistedException.class)
    public Result<?> TagNoExistedException() {
        return Result.fail(120,"标签不存在",null);
    }

    @ExceptionHandler(TagExistedException.class)
    public Result<?> TagExistedException() {
        return Result.fail(121,"标签已存在",null);
    }

    @ExceptionHandler(NoteNoExistedException.class)
    public Result<?> NoteNoExistedException() {
        return Result.fail(130,"笔记不存在",null);
    }

    @ExceptionHandler(NoteExistedException.class)
    public Result<?> NoteExistedException() {
        return Result.fail(131,"笔记已存在",null);
    }


}
