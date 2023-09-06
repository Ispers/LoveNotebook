package com.example.lovenotebook_back.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "封装返回前端数据")
public class Info {
    @ApiModelProperty(value = "请求是否成功", dataType = "boolean")
    private Boolean flag;
    @ApiModelProperty(value = "返回前端数据", dataType = "object")
    private Object data;
    @ApiModelProperty(value = "提示信息", dataType = "string")
    private String msg;

    public Info() {
    }

    public Info(Boolean flag) {
        this.flag = flag;
    }

    public Info(Boolean flag, Object data) {
        this.flag = flag;
        this.data = data;
    }

    public Info(Boolean flag, String msg) {
        this.flag = flag;
        this.msg = msg;
    }

    public Info(Boolean flag, Object data, String msg) {
        this.flag = flag;
        this.data = data;
        this.msg = msg;
    }
}
