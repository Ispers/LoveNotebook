package com.example.lovenotebook_back.controller.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "订单VO")
public class OrderVO {
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "下单用户id", dataType = "Long")
    private Long orderUserId;
    @ApiModelProperty(value = "订单总食物爱心数量", dataType = "Integer")
    private Integer orderTotalFoodHeartCount;
    @ApiModelProperty(value = "订单总食物种类数量", dataType = "Integer")
    private Integer orderTotalFoodTypeCount;
    @ApiModelProperty(value = "订单备注", dataType = "String")
    private String orderNote;
    @ApiModelProperty(value = "订单标题 --- 一句情话", dataType = "String")
    private String orderTitle;
    @ApiModelProperty(hidden = true)
    private List<FoodVO> foodList;
}
