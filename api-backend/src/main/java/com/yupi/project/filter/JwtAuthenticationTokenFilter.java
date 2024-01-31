package com.yupi.project.filter;


import com.alibaba.fastjson.JSON;
import com.yupi.project.common.BaseResponse;
import com.yupi.project.config.JwtConfig;
import com.yupi.project.type.LoginUser;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    RedisTemplate redisTemplate;
    @Resource
    private JwtConfig jwtConfig ;




    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //1获取token  header的token
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            //放行，让后面的过滤器执行
            filterChain.doFilter(request, response);
            return;
        }
        //2解析token
        String userId;
        try {
            Claims claims = jwtConfig.getTokenClaim(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            returnJson(response, JSON.toJSONString(new BaseResponse(4004,"会话已经过期或当前会话有误","没有数据")));
            return;
        }

        //3获取userId, redis获取用户信息
        System.out.println(userId);
        LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get("login:"+userId);
        if (Objects.isNull(loginUser)) {
            returnJson(response, JSON.toJSONString(new BaseResponse(4003,"该会话已过期","没有数据")));
            return;
        }
        //4封装Authentication
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

        //5存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        //放行，让后面的过滤器执行
        filterChain.doFilter(request, response);
    }
    private void returnJson(ServletResponse response, String json) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
            log.error("response error", e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}

