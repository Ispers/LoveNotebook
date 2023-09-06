package com.example.lovenotebook_back.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 食物类别表 tb_food_categorys
 *
 * @author sun0316
 * @date 2023/5/19 14:49
 */
@Data
@TableName("tb_food_categorys")
@ApiModel(value = "食物类别表")
public class FoodCategory {
    /**
     * 食物类别ID
     */
    @TableId(value = "food_category_id", type = IdType.AUTO)
    @ApiModelProperty(value = "食物类别ID", dataType = "Integer")
    private Integer foodCategoryId;
    /**
     * 食物类别名称
     */
    @ApiModelProperty(value = "食物类别名称", dataType = "String")
    private String foodCategoryName;
    /**
     * 食物类别 --餐厅ID
     */
    @ApiModelProperty(value = "食物类别 --餐厅ID", dataType = "Integer")
    private Integer restaurantId;
    /**
     * 餐馆 --外键对象
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private Restaurant restaurant;
}
