package com.yupi.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyme.apicommon.model.entity.User;
import com.skyme.apicommon.model.service.InnerUserService;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.mapper.UserMapper;
import com.yupi.project.mapper.mapper.UserSignatureMapper;
import com.yupi.project.model.entity.UserSignature;
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
        UserSignature userSignature = userSignatureMapper.selectOne(queryWrapper);
        return userSignature.getAccessSecret();

    }
}
