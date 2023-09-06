package com.example.lovenotebook_back.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单评论点赞表
 *
 * @author sun0316
 * @date 2023/6/1 16:21
 */
@Data
@TableName("tb_order_comment_likes")
@ApiModel(value = "订单评论点赞表")
public class OrderCommentLike {
    /**
     * 订单评论点赞表主键 -- id
     */
    @TableId(value = "order_comment_like_id", type = IdType.AUTO)
    @ApiModelProperty(value = "订单评论点赞表主键 -- id", dataType = "integer")
    private Integer orderCommentLikeId;
    /**
     * 订单评论id
     */
    @ApiModelProperty(value = "订单评论id", dataType = "integer")
    private Integer orderCommentId;
    /**
     * 订单id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "订单评论id", dataType = "long")
    private Long orderId;
    /**
     * 订单评论点赞用户id
     */
    @ApiModelProperty(value = "订单评论点赞用户id", dataType = "long")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderCommentLikeUserId;
}
