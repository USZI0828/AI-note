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

@TableName(value = "subtask")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subtask implements Serializable {

    @TableId(value = "subtask_id", type = IdType.AUTO)
    private Long subtaskId;

    //taskid
    @TableField(value = "task_id")
    private Long taskId;

    //任务名称
    @TableField(value = "task_name")
    private String taskName;

    //描述
    @TableField(value = "description")
    private String description;

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