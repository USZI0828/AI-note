package com.zmy.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @TableName user_perm
 */
@TableName(value ="user_perm")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPerm implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "u_p_id", type = IdType.AUTO)
    private Integer uPId;

    /**
     *  用户id
     */
    @TableField(value = "user_id")
    private Integer userId;

    /**
     * 权限id'
     */
    @TableField(value = "perm_id")
    private Integer permId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
