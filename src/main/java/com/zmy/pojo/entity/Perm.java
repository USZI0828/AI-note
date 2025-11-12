package com.zmy.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName perm
 */
@TableName(value ="perm")
@Data
public class Perm implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "perm_id", type = IdType.AUTO)
    private Integer permId;

    /**
     * 目标权限
     */
    @TableField(value = "perm_target")
    private String permTarget;

    /**
     * 权限详情
     */
    @TableField(value = "perm_detail")
    private String permDetail;

    /**
     *
     */
    @TableField(value = "status")
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
