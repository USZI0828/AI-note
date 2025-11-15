package com.zmy.pojo.form.Update;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateTaskForm {
    private Long taskId;
    private String taskName;
    private String description;
    private LocalDateTime deadline;
    private String priority;
    private String status;
    private LocalDateTime finishTime;
}
