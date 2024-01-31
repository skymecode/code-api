package com.yupi.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.skyme.apicommon.model.entity.InterfaceInfo;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.service.InterfaceInfoService;
import com.yupi.project.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;

/**
* @author 41239
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2024-01-20 19:27:11
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        String name = interfaceInfo.getName();
        if (add){
            if(StringUtils.isAnyBlank(name)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }

        }
        if (StringUtils.isNotBlank(name)&&name.length()>50){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"内容过长");
        }
    }
}




