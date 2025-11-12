package com.zmy.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName secret
 */
@TableName(value ="secret")
@Data
public class Secret implements Serializable {
    /**
     *
     */
    @TableId(value = "id")
    private Integer id;

    /**
     *
     */
    @TableField(value = "secret")
    private String secret;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
