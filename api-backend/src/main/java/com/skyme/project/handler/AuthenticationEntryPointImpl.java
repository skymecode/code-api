package com.skyme.project.handler;

import com.alibaba.fastjson.JSON;

import com.skyme.project.common.BaseResponse;
import com.skyme.project.common.ErrorCode;
import com.skyme.project.common.ResultUtils;
import com.skyme.project.common.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //给前端ResponseResult 的json
        String json = JSON.toJSONString(ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "统一认证失败"));
        WebUtils.renderString(response,json);

    }
}