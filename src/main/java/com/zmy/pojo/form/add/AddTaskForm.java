package com.zmy.pojo.form.add;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddTaskForm {
    private Long userId;
    private Long subjectId;
    private String taskName;
    private String description;
    private LocalDateTime deadline;
    private String priority;
}
