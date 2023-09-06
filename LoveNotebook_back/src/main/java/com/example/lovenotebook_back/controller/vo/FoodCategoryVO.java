package com.example.lovenotebook_back.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "食物类别VO")
public class FoodCategoryVO {
    @ApiModelProperty(value = "食物类别ID", dataType = "Integer")
    private Integer foodCategoryId;
    @ApiModelProperty(value = "食物类别名称", dataType = "String")
    private String foodCategoryName;
    @ApiModelProperty(value = "食物类别 --餐厅ID", dataType = "Integer")
    private Integer restaurantId;
    @ApiModelProperty(value = "食物类别是否被选中", dataType = "boolean")
    private boolean foodCategoryIsCheck;
}
