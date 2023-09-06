package com.example.lovenotebook_back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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

/**
 * 订单评论表
 *
 * @author sun0316
 * @date 2023/6/1 16:21
 */
@Data
@TableName("tb_order_comments")
@ApiModel(value = "订单评论表")
public class OrderComment {
    /**
     * 订单评论表主键 -- id
     */
    @TableId(value = "order_comment_id", type = IdType.AUTO)
    @ApiModelProperty(value = "订单评论表主键 -- id", dataType = "Integer")
    private Integer orderCommentId;
    /**
     * 订单id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "订单id", dataType = "Long")
    private Long orderId;
    /**
     * 订单评论用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "订单评论用户id", dataType = "Long")
    private Long orderUserId;
    /**
     * 订单评论时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "订单评论时间", dataType = "Date")
    private Date orderCommentCreateTime;
    /**
     * 订单评论内容
     */
    @ApiModelProperty(value = "订单评论内容", dataType = "String")
    private String orderCommentContent;
    /**
     * 订单评论点赞数量
     */
    @ApiModelProperty(value = "订单评论点赞数量", dataType = "Integer")
    @TableField(exist = false)
    private Integer likeCount;
    /**
     * 当前用户是否点赞 0未点赞 1已点赞
     */
    @ApiModelProperty(value = "当前用户是否点赞 0未点赞 1已点赞", dataType = "Integer")
    @TableField(exist = false)
    private Integer userIsLike;
    /**
     * 用户信息
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private User orderCommentUserInfo;
}
