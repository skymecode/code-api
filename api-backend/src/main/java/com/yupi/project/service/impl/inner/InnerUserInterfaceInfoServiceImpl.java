package com.yupi.project.service.impl.inner;

import com.skyme.apicommon.model.service.InnerUserInterfaceInfoService;
import com.yupi.project.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {
    @Resource
    UserInterfaceInfoService userInterfaceInfoService;
    @Override
    public boolean invokeCount(long l, long l1) {
        return userInterfaceInfoService.invokeCount(l,l1);
    }
}
