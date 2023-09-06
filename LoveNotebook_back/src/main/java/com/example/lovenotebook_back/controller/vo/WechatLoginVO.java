package com.example.lovenotebook_back.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "微信登录VO")
public class WechatLoginVO {
    @ApiModelProperty(value = "微信-code", dataType = "String")
    private String code;
    @ApiModelProperty(value = "微信-昵称", dataType = "String")
    private String nickName;
    @ApiModelProperty(value = "微信-头像", dataType = "String")
    private String avatar;
    @ApiModelProperty(value = "微信-性别", dataType = "int")
    private int sex;
}
