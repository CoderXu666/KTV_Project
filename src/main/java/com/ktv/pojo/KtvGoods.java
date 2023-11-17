package com.ktv.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * KTV商品
 * </p>
 *
 * @author ${author}
 * @since 2023-11-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ktv_goods")
public class KtvGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商品名
     */
    @TableField("name")
    private String name;

    /**
     * 商品价格
     */
    @TableField("price")
    private Integer price;

    /**
     * 商品图片
     */
    @TableField("url")
    private String url;


}
