package com.skyme.project.provider.impl;

import com.skyme.project.provider.DemoService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        return "skyme";
    }
}
