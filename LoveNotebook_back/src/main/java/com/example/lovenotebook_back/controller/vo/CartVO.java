package com.example.lovenotebook_back.controller.vo;

import com.example.lovenotebook_back.entity.Food;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "购物车VO")
public class CartVO {
    @ApiModelProperty(value = "购物车Id", dataType = "Integer")
    private Integer cartId;
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "购物车购买用户Id", dataType = "Long")
    private Long cartUserId;
    @ApiModelProperty(value = "购物购买食物Id", dataType = "Integer")
    private Integer cartFoodId;
    @ApiModelProperty(value = "购物车购买食物数量", dataType = "Integer")
    private Integer cartFoodCount;
    @ApiModelProperty(value = "食物", dataType = "Food")
    private Food food;
    @ApiModelProperty(value = "总爱心数量", dataType = "Integer")
    private Integer cartTotalHeartCount;
    @ApiModelProperty(value = "购物车食物是否选中", dataType = "Integer")
    private Integer cartIsCheck;
}
