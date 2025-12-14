package com.zmy.pojo.query;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDeadlineQuery extends BaseQuery {
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
