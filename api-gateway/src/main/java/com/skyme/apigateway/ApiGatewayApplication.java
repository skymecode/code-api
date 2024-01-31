package com.skyme.apigateway;

import com.yupi.project.provider.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDubbo
public class ApiGatewayApplication {
    @DubboReference
    DemoService demoService;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ApiGatewayApplication.class, args);
        ApiGatewayApplication bean = context.getBean(ApiGatewayApplication.class);
        String s = bean.doSayHello("hello");


    }
    public String doSayHello(String name){
        String result = demoService.sayHello("world");
        System.out.println("Receive result ======> " + result);
        return result;
    }

}
