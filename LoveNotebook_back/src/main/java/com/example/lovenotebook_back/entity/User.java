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
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户表tb_users
 *
 * @author sun0316
 * @date 2023/5/15 18:23
 */
@Data
@TableName("tb_users")
@ApiModel(value = "用户表")
public class User {
    /**
     * 主键--用户ID
     */
    // 解决js中Number精度与java中Long精度不一致
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "user_id")
    @ApiModelProperty(value = "主键--用户ID", dataType = "Long")
    private Long userId;
    /**
     * 用户密码（管理员需要，用户均微信登录）
     */
    @ApiModelProperty(value = "用户密码（管理员需要，用户均微信登录）", dataType = "String")
    private String userPassword;
    /**
     * 用户性别  0-未设置 1-男 2-女
     */
    @ApiModelProperty(value = "用户性别 0-未设置 1-男 2-女", dataType = "Integer")
    private Integer userSex;
    /**
     * 用户爱心数量 --初始50个
     */
    @ApiModelProperty(value = "用户爱心数量 --初始50个", dataType = "Integer")
    private Integer userHeartCount;
    /**
     * 用户注册日期
     */
    // 传入时间格式
    @DateTimeFormat(pattern = "YYYY-MM-dd")
    // 返回时间格式
    @JsonFormat(timezone = "GMT+8", pattern = "YYYY-MM-dd")
    @ApiModelProperty(value = "用户注册日期", dataType = "Date")
    private Date userRegisterDate;
    /**
     * 用户是否恋爱
     */
    @ApiModelProperty(value = "用户是否恋爱", dataType = "Integer")
    private Integer userIsLove;
    /**
     * 用户恋爱日期
     */
    @DateTimeFormat(pattern = "YYYY-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "YYYY-MM-dd")
    @ApiModelProperty(value = "用户恋爱日期", dataType = "Date")
    private Date userLoveDate;
    /**
     * 用户微信登录  OpenId
     */
    @ApiModelProperty(value = "用户微信登录 OpenId", dataType = "String")
    private String userOpenId;
    /**
     * 用户微信登录  微信昵称
     */
    @ApiModelProperty(value = "用户微信登录 微信昵称", dataType = "String")
    private String userNickName;
    /**
     * 用户微信登录  微信头像
     */
    @ApiModelProperty(value = "用户微信登录 微信头像", dataType = "String")
    private String userAvatar;
    /**
     * 餐厅角色 0-顾客 1-老板
     */
    @ApiModelProperty(value = "餐厅角色 0-顾客 1-老板", dataType = "Integer")
    private Integer userRestaurantLever;
    /**
     * 用户权限 0-用户 1-管理员
     */
    @ApiModelProperty(value = "用户权限 0-用户 1-管理员", dataType = "Integer")
    private Integer userLever;
    /**
     * 用户恋人id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "用户恋人id", dataType = "Long")
    private Long userLoveId;
    /**
     * 用户微信登录  session_key
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "用户微信登录 session_key", dataType = "String")
    private String userSessionKey;
    /**
     * 用户恋人信息  --外键对象
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private User lover;
}
