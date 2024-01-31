package com.skyme.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.skyme.apicommon.model.entity.UserInterfaceInfo;


/**
* @author 41239
* @description 针对表【user_interface_info(用户接口关系)】的数据库操作Service
* @createDate 2024-01-26 21:02:35
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean b);
    boolean invokeCount(long interfaceInfoId,long userId);
}
