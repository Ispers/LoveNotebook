package com.example.lovenotebook_back.controller.vo;

import com.example.lovenotebook_back.entity.OrderComment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "订单评论VO")
public class OrderCommentVO {
    @ApiModelProperty(value = "订单评论数量", dataType = "Integer")
    private Integer orderCommentCount;
    @ApiModelProperty(value = "全部订单评论信息", dataType = "List<OrderComment>")
    private List<OrderComment> orderComments;
}
