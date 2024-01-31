package com.yupi.project.service.impl;



import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.skyme.apicommon.model.entity.UserInterfaceInfo;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.mapper.UserInterfaceInfoMapper;
import com.yupi.project.model.entity.InterfaceInfo;

import com.yupi.project.service.UserInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author 41239
* @description 针对表【user_interface_info(用户接口关系)】的数据库操作Service实现
* @createDate 2024-01-26 21:02:35
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {

        if (add){
            if(userInterfaceInfo.getUserId()<0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }

        }

    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {

        if (interfaceInfoId<=0||userId<=0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId",interfaceInfoId);
        updateWrapper.eq("userId",userId);
        updateWrapper.setSql("leftNum=leftNum-1,totalNum=totalNum+1");
        return  this.update(updateWrapper);
    }
}




