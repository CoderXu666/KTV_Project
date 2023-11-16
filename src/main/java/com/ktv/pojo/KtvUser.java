package com.ktv.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 账号
 * </p>
 *
 * @author ${author}
 * @since 2023-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ktv_user")
public class KtvUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 昵称
     */
    @TableField("nick_name")
    private String nickName;

    /**
     * 账号
     */
    @TableField("account_id")
    private String accountId;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 金额
     */
    @TableField("money")
    private Integer money;

    /**
     * 电话号
     */
    @TableField("phone")
    private String phone;

    /**
     * 角色（1：经理，2：收银，3：服务，4：后厨，5：顾客，6：会员）
     */
    @TableField("role")
    private Integer role;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 删除标识
     */
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Boolean deleted;


}
