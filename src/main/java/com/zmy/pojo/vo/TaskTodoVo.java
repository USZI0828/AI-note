package com.zmy.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskTodoVo {
    private Long taskId;
    private Long userId;
    private Long subjectId;
    private String taskName;
    private LocalDateTime deadline;
    // 关联查询的科目名称
    private String subjectName;
}

