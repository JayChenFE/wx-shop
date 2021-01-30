package com.github.jaychenfe.api.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author jaychenfe
 * @since 2021-01-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ORDERS")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("USER_ID")
    private Long userId;

    @TableField("TOTAL_PRICE")
    private Long totalPrice;

    @TableField("ADDRESS")
    private String address;

    @TableField("EXPRESS_COMPANY")
    private String expressCompany;

    @TableField("EXPRESS_ID")
    private String expressId;

    @TableField("STATUS")
    private String status;

    @TableField(value = "CREATED_AT", fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(value = "UPDATED_AT", fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    @TableField("SHOP_ID")
    private Long shopId;


}
