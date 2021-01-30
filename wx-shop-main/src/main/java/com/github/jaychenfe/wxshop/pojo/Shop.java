package com.github.jaychenfe.wxshop.pojo;

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
 * @since 2021-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("SHOP")
public class Shop implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @TableField("NAME")
    private String name;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("IMG_URL")
    private String imgUrl;

    @TableField("OWNER_USER_ID")
    private Long ownerUserId;

    @TableField("STATUS")
    private String status;

    @TableField(value = "CREATED_AT", fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(value = "UPDATED_AT", fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;


}
