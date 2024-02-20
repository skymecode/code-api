package com.skyme.feishubot.config;

import com.lark.oapi.sdk.servlet.ext.ServletAdapter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Feishu {

    // 注入扩展实例到 IOC 容器
    @Bean
    public ServletAdapter getServletAdapter() {
        return new ServletAdapter();
    }
}