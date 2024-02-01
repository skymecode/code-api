package com.skyme.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyme.apicommon.model.entity.User;
import com.skyme.apicommon.model.service.InnerUserService;
import com.skyme.project.common.ErrorCode;
import com.skyme.project.exception.BusinessException;
import com.skyme.project.mapper.UserMapper;
import com.skyme.project.mapper.mapper.UserSignatureMapper;
import com.skyme.project.model.entity.UserSignature;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
@DubboService
public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    UserMapper userMapper;
    @Resource
    UserSignatureMapper userSignatureMapper;
    @Override
    public User getIInvokeUser(String ak) {
        if (StringUtils.isAnyBlank(ak)){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserSignature> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey",ak);
        queryWrapper.eq("status",0);
        UserSignature userSignature = userSignatureMapper.selectOne(queryWrapper);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id",userSignature.getUserId());
        return  userMapper.selectOne(userQueryWrapper);
    }

    /**
     *
     * @param uid
     * @return String
     */
    @Override
    public String getSecretKey(long uid) {
        if (uid<=0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<UserSignature> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",uid);
        queryWrapper.eq("status",0);
        UserSignature userSignature = userSignatureMapper.selectOne(queryWrapper);
        return userSignature.getAccessSecret();

    }
}
