package com.zmy.pojo.query;

import lombok.Data;


@Data
public class TaskQuery extends BaseQuery{
    private Long userId;
    private Long subjectId;
    private String taskName;
    private String priority;
    private String status;
    //排序字段
    private String orderBy;
    //排序方式
    private String orderDirection;
}
