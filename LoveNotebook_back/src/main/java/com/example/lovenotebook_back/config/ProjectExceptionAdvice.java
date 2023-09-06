package com.example.lovenotebook_back.config;

import com.example.lovenotebook_back.utils.Info;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理
 *
 * @author sun0316
 * @date 2022/5/1 0:52
 */
@RestControllerAdvice
public class ProjectExceptionAdvice {

    @ExceptionHandler
    public Info doException(Exception e) {
        e.printStackTrace();
        return new Info(false, "服务器故障，请稍后再试");
    }
}
