package com.zmy.pojo.form.Update;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateSubtaskForm {
    private Long subtaskId;
    private String taskName;
    private String description;
    private String status;
    private LocalDateTime finishTime;
}


