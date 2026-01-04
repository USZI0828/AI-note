package com.zmy.pojo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class NoteQuery extends BaseQuery {
    private Integer userId;
    private Integer subjectId;
    private String tagId;
    private String title;
    private LocalDateTime createTimeStart;
    private LocalDateTime createTimeEnd;
}

