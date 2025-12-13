package com.zmy.pojo.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
@TableName("note")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Note implements Serializable{
    @TableId(value = "note_id", type = IdType.AUTO)
    private Integer noteId;
    @TableField(value = "user_id")
    private Integer userId;
    @TableField(value = "subject_id")
    private Integer subjectId;
    @TableField(value = "tag_id")
    private String tagId;
    @TableField(value = "title")
    private String title;
    @TableField(value = "content")
    private String content;
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
    @TableField(value = "delete_flag")
    private Integer deleteFlag;

}
