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
 * KTV包房
 * </p>
 *
 * @author ${author}
 * @since 2023-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ktv_house")
public class KtvHouse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 包房大小
     */
    @TableField("size")
    private String size;

    /**
     * 包房价格
     */
    @TableField("price")
    private Integer price;

    /**
     * 包房图片
     */
    @TableField("url")
    private String url;

    /**
     * 包房总数量
     */
    @TableField("count")
    private Integer count;

    /**
     * 已使用包房数量
     */
    @TableField("use_count")
    private Integer useCount;


}
