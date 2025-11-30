package com.zmy.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskVo {
    private Long taskId;
    private Long userId;
    private Long subjectId;
    private Long tagId;
    private String taskName;
    private String description;
    private LocalDateTime deadline;
    private String priority;
    private String status;
    private LocalDateTime remindTime;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
    // 关联查询的科目名称
    private String subjectName;
    // 关联查询的标签名称
    private String tagName;
}

