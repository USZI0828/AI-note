package com.zmy.pojo.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

@TableName("review_schedule")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review_schedule {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField(value = "user_id")
    private Integer userId;
    @TableField(value = "note_id")
    private Integer noteId;
    @TableField(value = "review_time")
    private LocalDateTime reviewTime;
    @TableField(value = "status")
    private Integer status;
    @TableField(value = "finish_time")
    private LocalDateTime finishTime;
    @TableField(value = "times")
    private Integer times;
}
