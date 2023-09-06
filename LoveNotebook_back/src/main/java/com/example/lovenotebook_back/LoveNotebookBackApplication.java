package com.example.lovenotebook_back;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableSwaggerBootstrapUI
@SpringBootApplication
//public class LoveNotebookBackApplication extends SpringBootServletInitializer {
public class LoveNotebookBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoveNotebookBackApplication.class, args);
    }

/*    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {

        // 注意这里要指向原先用main方法执行的Application启动类
        return builder.sources(LoveNotebookBackApplication.class);
    }*/
}
