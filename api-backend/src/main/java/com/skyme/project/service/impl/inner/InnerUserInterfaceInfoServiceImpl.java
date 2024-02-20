package com.skyme.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyme.apicommon.model.entity.InterfaceInfo;
import com.skyme.apicommon.model.entity.User;
import com.skyme.apicommon.model.entity.UserInterfaceInfo;
import com.skyme.apicommon.model.service.InnerUserInterfaceInfoService;
import com.skyme.project.mapper.UserInterfaceInfoMapper;
import com.skyme.project.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {
    @Resource
    UserInterfaceInfoService userInterfaceInfoService;
    @Resource
    UserInterfaceInfoMapper userInterfaceInfoMapper;
    @Override
    public boolean invokeCount(long l, long l1) {
        return userInterfaceInfoService.invokeCount(l,l1);
    }

    @Override
    public UserInterfaceInfo queryUserInterfaceInfo(InterfaceInfo interfaceInfo, User user) {
        //首先是拿到用户名,接口信息
        QueryWrapper<UserInterfaceInfo> userInterfaceInfoQueryWrapper = new QueryWrapper<>();
        userInterfaceInfoQueryWrapper.eq("userId",user.getId());
        userInterfaceInfoQueryWrapper.eq("interfaceInfoId",interfaceInfo.getId());
        return userInterfaceInfoMapper.selectOne(userInterfaceInfoQueryWrapper);
    }
}
