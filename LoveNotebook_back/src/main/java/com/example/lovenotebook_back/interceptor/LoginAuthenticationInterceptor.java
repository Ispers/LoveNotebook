package com.example.lovenotebook_back.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.example.lovenotebook_back.entity.User;
import com.example.lovenotebook_back.utils.Info;
import com.example.lovenotebook_back.utils.JedisUtils;
import com.example.lovenotebook_back.utils.JwtHelper;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class LoginAuthenticationInterceptor implements HandlerInterceptor {
    /**
     * 拦截所有需要认证的请求，验证token
     *
     * @return boolean
     * @author sun0316
     * @date 2022/5/14 16:28
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        response.setContentType("text/javascript; charset=utf-8");
        //跨域
        response.setHeader("Access-Control-Allow-Origin", "*");

        //取token
        String token = request.getHeader("token");

        if (StrUtil.isEmpty(token)) {
            token = request.getParameter("token");
        }

/*        if (StrUtil.isEmpty(token)) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (Objects.equals(cookie.getName(), "token")) {
                    token = cookie.getValue();
                }
            }
        }*/
        //前端没有携带token
        if (StrUtil.isBlank(token)) {

            response.getWriter().write(JSON.toJSONString("用户未登录"));
            return false;
        }

        //解析客户端token
        Claims claims = JwtHelper.verifyJwt(token);

        //判断token是否能被解析，解析失败，返回失败信息
        if (claims == null) {
            Object json = JSON.toJSON(new Info(false, "用户信息异常，请重新登录"));
            response.getWriter().write(JSON.toJSONString(json));
            return false;
        }

        Object object = claims.get("user");

        User user = JSON.parseObject(JSON.toJSONString(object), User.class);

        //从redis中查询token
        String redistoken = JedisUtils.get("token:" + user.getUserId());

        //判断token是否存在,不存在返回失败信息
        if (StringUtils.isBlank(redistoken)) {
            Object json = JSON.toJSON(new Info(false, "登录过期，请重新登录"));
            response.getWriter().write(JSON.toJSONString(json));
            return false;
        }

        // 解析redis中的token，获取user信息
        Claims redisClaims = JwtHelper.verifyJwt(redistoken);
        Object redisObject = redisClaims.get("user");
        User redisUser = JSON.parseObject(JSON.toJSONString(redisObject), User.class);

        //判断客户端token中的Uid和redis中的token的是否一致
        if (!Objects.equals(redisUser.getUserId(), user.getUserId())) {
            Object json = JSON.toJSON(new Info(false, "用户信息异常，请重新登录"));
            response.getWriter().write(JSON.toJSONString(json));
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
