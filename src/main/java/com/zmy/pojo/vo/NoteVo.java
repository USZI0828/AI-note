package com.zmy.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteVo {
    private Integer noteId;
    private Integer userId;
    private Integer subjectId;
    private String tagId;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleteFlag;
    // 关联查询的标签名称（多个标签名称，用逗号分隔）
    private String tagNames;
    // 关联查询的科目名称
    private String subjectName;
}

