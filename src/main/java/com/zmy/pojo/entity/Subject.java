package com.zmy.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@TableName(value = "subject")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subject implements Serializable {

    @TableId(value = "subject_id", type = IdType.AUTO)
    private Long subjectId;

    //学科名称
    @TableField(value = "subject_name")
    private String subjectName;

    //描述
    @TableField(value = "description")
    private String description;


}
