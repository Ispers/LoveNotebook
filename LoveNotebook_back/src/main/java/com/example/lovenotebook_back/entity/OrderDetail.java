package com.example.lovenotebook_back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 订单详情表 tb_order_details
 *
 * @author sun0316
 * @return
 * @date 2023/5/24 15:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_order_details")
@ApiModel(value = "订单详情表 ")
public class OrderDetail {
    /**
     * 订单详情id
     */
    @TableId(value = "order_details_id", type = IdType.AUTO)
    @ApiModelProperty(value = "订单详情id", dataType = "Integer")
    private Integer orderDetailsId;
    /**
     * 订单id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "订单id", dataType = "Long")
    private Long orderId;
    /**
     * 订单详情-食物id
     */
    @ApiModelProperty(value = "订单详情-食物id", dataType = "Integer")
    private Integer orderDetailsFoodId;
    /**
     * 订单详情-食物名字
     */
    @ApiModelProperty(value = "订单详情-食物名字", dataType = "String")
    private String orderDetailsFoodName;
    /**
     * 订单详情-食物图片
     */
    @ApiModelProperty(value = "订单详情-食物图片", dataType = "String")
    private String orderDetailsFoodImg;
    /**
     * 订单详情-食物爱心数量
     */
    @ApiModelProperty(value = "订单详情-食物爱心数量", dataType = "Integer")
    private Integer orderDetailsFoodHeartCount;
    /**
     * 订单详情-食物数量
     */
    @ApiModelProperty(value = "订单详情-食物数量", dataType = "Integer")
    private Integer orderDetailsFoodCount;
    /**
     * 订单详情-创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "订单详情-创建时间", dataType = "Date")
    private Date orderDetailsCreateTime;
}
