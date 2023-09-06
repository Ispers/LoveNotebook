package com.example.lovenotebook_back.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 订单表 tb_orders
 *
 * @author sun0316
 * @return
 * @date 2023/5/24 15:40
 */
@Data
@TableName("tb_orders")
@ApiModel(value = "订单表")
public class Order {
    /**
     * 订单id 主键--雪花算法随机生成
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "order_id")
    @ApiModelProperty(value = "订单id 主键--雪花算法随机生成", dataType = "Long")
    private Long orderId;
    /**
     * 下单用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "下单用户id", dataType = "Long")
    private Long orderUserId;
    /**
     * 订单总食物爱心数量
     */
    @ApiModelProperty(value = "订单总食物爱心数量", dataType = "Integer")
    private Integer orderTotalFoodHeartCount;
    /**
     * 订单总食物种类数量
     */
    @ApiModelProperty(value = "订单总食物种类数量", dataType = "Integer")
    private Integer orderTotalFoodTypeCount;
    /**
     * 订单备注
     */
    @ApiModelProperty(value = "订单备注", dataType = "String")
    private String orderNote;
    /**
     * 订单标题 --- 一句情话
     */
    @ApiModelProperty(value = "订单标题 --- 一句情话", dataType = "String")
    private String orderTitle;
    /**
     * 订单状态
     */
    @ApiModelProperty(value = "订单状态", dataType = "Integer")
    private Integer orderStatus;
    /**
     * 订单是否删除 0未删除 1已删除
     */
    @ApiModelProperty(value = "订单是否删除 0未删除 1已删除", dataType = "Integer")
    private Integer orderIsDelete;
    /**
     * 订单是否分享 0未分享 1已分享
     */
    @ApiModelProperty(value = "订单是否分享 0未分享 1已分享", dataType = "Integer")
    private Integer orderIsShare;
    /**
     * 订单 老板是否已读 0未读 1已读
     */
    @ApiModelProperty(value = "订单 老板是否已读 0未读 1已读", dataType = "Integer")
    private Integer orderBossIsRead;
    /**
     * 订单点赞数量
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "订单点赞数量", dataType = "Integer")
    private Integer likeCount;
    /**
     * 当前用户是否点赞 0未点赞 1已点赞
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "当前用户是否点赞 0未点赞 1已点赞", dataType = "Integer")
    private Integer userIsLike;
    /**
     * 餐厅名字
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "餐厅名字", dataType = "String")
    private String restaurantName;
    /**
     * 用户信息
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private User userInfo;
    /**
     * 订单创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "订单创建时间", dataType = "Date")
    private Date orderCreateTime;

    /**
     * 订单外键---订单详情
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<OrderDetail> orderDetails;
}
