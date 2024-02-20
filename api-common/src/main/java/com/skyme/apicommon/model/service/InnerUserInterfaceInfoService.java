package com.skyme.apicommon.model.service;

import com.skyme.apicommon.model.entity.InterfaceInfo;
import com.skyme.apicommon.model.entity.User;
import com.skyme.apicommon.model.entity.UserInterfaceInfo;

public interface InnerUserInterfaceInfoService {



    boolean invokeCount(long interfaceInfoId,long userId);

    UserInterfaceInfo queryUserInterfaceInfo(InterfaceInfo interfaceInfo,User user);

}
