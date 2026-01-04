package com.zmy.pojo.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

@TableName("task_sub")
@Data
public class Task_sub {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField(value = "subtask_id")
    private Integer subtaskId;
    @TableField(value = "task_id")
    private Integer taskId;
}
