package com.zmy.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value = "task")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task implements Serializable {

    @TableId(value = "task_id", type = IdType.AUTO)
    private Long taskId;

    //用户id
    @TableField(value = "user_id")
    private Long userId;

    //科目id
    @TableField(value = "subject_id")
    private Long subjectId;

    //任务名称
    @TableField(value = "task_name")
    private String taskName;

    //截止时间
    @TableField(value = "deadline")
    private LocalDateTime deadline;

    //描述
    @TableField(value = "description")
    private String description;

    //优先级
    @TableField(value = "priority")
    private String priority;

    //状态
    @TableField(value = "status")
    private String status;

    //创建时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    //完成时间
    @TableField(value = "finish_time")
    private LocalDateTime finishTime;
}
