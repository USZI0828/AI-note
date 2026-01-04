package com.zmy.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class TaskVo {
    private Long taskId;
    private Long userId;
    private Long subjectId;
    private String tagId;
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
    private String tagNames;
    // 子任务完成百分比（0-100），若无子任务则为 0
    private Integer subtaskPercent;
    // 为该任务推荐的笔记（最多3篇），按相关度降序
    private List<com.zmy.pojo.vo.NoteVo> recommendedNotes;
    // 仅返回推荐笔记的 id 列表（供前端使用）
    private List<Integer> recommendedNoteIds;
}

