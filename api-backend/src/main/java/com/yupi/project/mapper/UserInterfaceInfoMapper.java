package com.yupi.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.skyme.apicommon.model.entity.UserInterfaceInfo;

import java.util.List;


/**
* @author 41239
* @description 针对表【user_interface_info(用户接口关系)】的数据库操作Mapper
* @createDate 2024-01-26 21:02:35
* @Entity com.yupi.project.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);

}




