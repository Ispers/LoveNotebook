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
 * 购物车表  tb_carts
 *
 * @author sun0316
 * @return
 * @date 2023/5/24 15:38
 */
@Data
@TableName("tb_carts")
@ApiModel(value = "购物车表 ")
public class Cart {
    /**
     * 购物车Id
     */
    @TableId(value = "cart_id", type = IdType.AUTO)
    @ApiModelProperty(value = "购物车Id", dataType = "Integer")
    private Integer cartId;
    /**
     * 购物车购买用户Id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "购物车购买用户Id", dataType = "Long")
    private Long cartUserId;
    /**
     * 购物购买食物Id
     */
    @ApiModelProperty(value = "购物购买食物Id", dataType = "Integer")
    private Integer cartFoodId;
    /**
     * 购物车购买食物数量
     */
    @ApiModelProperty(value = "购物车购买食物数量", dataType = "Integer")
    private Integer cartFoodCount;
}
