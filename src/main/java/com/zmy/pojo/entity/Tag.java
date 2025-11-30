package com.zmy.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
@TableName("tag")
@Data
public class Tag implements Serializable{   
    @TableId(value = "tag_id", type = IdType.AUTO)
    private Integer tagId;
    @TableField(value = "tag_name")
    private String tagName;
    
}
