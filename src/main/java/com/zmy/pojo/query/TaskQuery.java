package com.zmy.pojo.query;

import lombok.Data;

@Data
public class TaskQuery extends BaseQuery{
    private Long userId;
    private Long subjectId;
    private String taskName;
    private String priority;
    private String status;
}
