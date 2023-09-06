package com.example.lovenotebook_back.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 食物表 tb_foods
 *
 * @author sun0316
 * @date 2023/5/19 14:49
 */
@Data
@TableName("tb_foods")
@ApiModel(value = "食物表")
public class Food {
    /**
     * 食物ID
     */
    @TableId(value = "food_id", type = IdType.AUTO)
    @ApiModelProperty(value = "食物ID", dataType = "Integer")
    private Integer foodId;
    /**
     * 食物名字
     */
    @ApiModelProperty(value = "食物名字", dataType = "String")
    private String foodName;
    /**
     * 食物图片url
     */
    @ApiModelProperty(value = "食物图片url", dataType = "String")
    private String foodImg;
    /**
     * 食物爱心数量
     */
    @ApiModelProperty(value = "食物爱心数量", dataType = "Integer")
    private Integer foodHeartCount;
    /**
     * 食物已售数量
     */
    @ApiModelProperty(value = "食物已售数量", dataType = "Integer")
    private Integer foodSellCount;
    /**
     * 食物类别ID
     */
    @ApiModelProperty(value = "食物类别ID", dataType = "Integer")
    private Integer foodCategoryId;
    /**
     * 食物 --餐馆ID
     */
    @ApiModelProperty(value = "食物--餐馆ID", dataType = "Integer")
    private Integer restaurantId;
    /**
     * 餐馆 --外键对象
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Restaurant restaurant;
    /**
     * 食物类别  --外键对象
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private FoodCategory foodCategory;
}
