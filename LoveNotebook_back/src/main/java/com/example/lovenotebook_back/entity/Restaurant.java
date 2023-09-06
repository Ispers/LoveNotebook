package com.example.lovenotebook_back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 餐厅表 tb_restaurants
 *
 * @author sun0316
 * @date 2023/5/19 14:49
 */
@Data
@TableName("tb_restaurants")
@ApiModel(value = "餐厅表")
public class Restaurant {
    /**
     * 餐厅ID
     */
    @TableId(value = "restaurant_id", type = IdType.AUTO)
    @ApiModelProperty(value = "餐厅ID", dataType = "Integer")
    private Integer restaurantId;
    /**
     * 餐厅名字
     */
    @ApiModelProperty(value = "餐厅名字", dataType = "String")
    private String restaurantName;
    /**
     * 餐厅等级
     */
    @ApiModelProperty(value = "餐厅等级", dataType = "Integer")
    private Integer restaurantGrade;
    /**
     * 餐厅老板ID
     */
    @ApiModelProperty(value = "餐厅老板ID", dataType = "Long")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long restaurantBossId;
    /**
     * 餐厅顾客ID
     */
    @ApiModelProperty(value = "餐厅顾客ID", dataType = "Long")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long restaurantCustomerId;
    /**
     * 餐厅老板  --外键对象
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private User boos;
    /**
     * 餐厅顾客  --外键对象
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private User customer;
}
