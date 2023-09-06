package com.example.lovenotebook_back.config;

import com.example.lovenotebook_back.interceptor.AdminLoginAuthenticationInterceptor;
import com.example.lovenotebook_back.interceptor.LoginAuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 添加拦拦截器的截路径和排除拦截路径
 *
 * @author sun0316
 * @date 2022/5/14 16:26
 */
@Configuration
public class AuthenticationWebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginAuthenticationInterceptor loginAuthenticationInterceptor;
    @Autowired
    private AdminLoginAuthenticationInterceptor adminLoginAuthenticationInterceptor;

    /**
     * 添加拦截规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器
        registry.addInterceptor(loginAuthenticationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/wechatLogin")
                // 测试
                .excludePathPatterns("/api/upload/file")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**")
                .excludePathPatterns("/doc.html/**");
        // 管理员拦截器
        registry.addInterceptor(adminLoginAuthenticationInterceptor)
                .excludePathPatterns("/api/**")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**")
                .excludePathPatterns("/doc.html/**");
        ;
    }

    /**
     * CORS是一个W3C标准，全称是”跨域资源共享”（Cross-origin resource sharing），
     * 允许浏览器向跨源服务器，发出XMLHttpRequest请求，从而克服了AJAX只能同源使用的限制。
     * <p>
     * 它通过服务器增加一个特殊的Header[Access-Control-Allow-Origin]来告诉客户端跨域
     * 的限制，如果浏览器支持CORS、并且判断Origin通过的话，就会允许XMLHttpRequest发起
     * 跨域请求。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //是否发送Cookie
                .allowCredentials(true)
                //放行哪些原始域
                //.allowedOrigins("*")
                // spring boot 2.4.0 版本之前方法名为 allowedOrigins()
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .exposedHeaders("*")
                // 跨域允许时间
                .maxAge(3600);
    }
}
