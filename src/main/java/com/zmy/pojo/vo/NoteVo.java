package com.zmy.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

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
    // 关联查询的标签名称（数组类型）
    private List<String> tagNames;
    // 关联查询的科目名称
    private String subjectName;
    
    // 用于 MyBatis 映射的临时字段（从数据库查询的逗号分隔字符串）
    private String tagNamesStr;
    
    /**
     * 将逗号分隔的字符串转换为 List
     */
    public void setTagNamesStr(String tagNamesStr) {
        this.tagNamesStr = tagNamesStr;
        if (tagNamesStr != null && !tagNamesStr.trim().isEmpty()) {
            // 分割字符串并去除空格，转换为可修改的 ArrayList
            String[] names = tagNamesStr.split(",");
            this.tagNames = new ArrayList<>();
            for (String name : names) {
                String trimmed = name.trim();
                if (!trimmed.isEmpty()) {
                    this.tagNames.add(trimmed);
                }
            }
        } else {
            this.tagNames = new ArrayList<>();
        }
    }
}

