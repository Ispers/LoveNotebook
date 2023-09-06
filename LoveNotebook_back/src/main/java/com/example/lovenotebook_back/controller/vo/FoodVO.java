package com.example.lovenotebook_back.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "食物VO")
public class FoodVO {
    @ApiModelProperty(value = "食物ID", dataType = "Integer")
    private Integer foodId;
    @ApiModelProperty(value = "食物名字", dataType = "String")
    private String foodName;
    @ApiModelProperty(value = "食物图片url", dataType = "String")
    private String foodImg;
    @ApiModelProperty(value = "食物爱心数量", dataType = "Integer")
    private Integer foodHeartCount;
    @ApiModelProperty(value = "购物车食物数量", dataType = "Integer")
    private Integer cartCount;
    @ApiModelProperty(value = "食物已售数量", dataType = "Integer")
    private Integer foodSellCount;
    @ApiModelProperty(value = "食物类别ID", dataType = "Integer")
    private Integer foodCategoryId;
    @ApiModelProperty(value = "食物--餐馆ID", dataType = "Integer")
    private Integer restaurantId;
}
