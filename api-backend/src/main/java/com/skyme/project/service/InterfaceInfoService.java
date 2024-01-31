package com.skyme.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.skyme.apicommon.model.entity.InterfaceInfo;


/**
* @author 41239
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-01-20 19:27:11
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
 void validInterfaceInfo(InterfaceInfo interfaceInfo,boolean add);
}
