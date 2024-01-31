package com.skyme.project.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.skyme.project.model.entity.UserSignature;
import com.skyme.project.service.service.UserSignatureService;
import com.skyme.project.mapper.mapper.UserSignatureMapper;
import org.springframework.stereotype.Service;

/**
* @author 41239
* @description 针对表【user_signature(用户aksk)】的数据库操作Service实现
* @createDate 2024-02-01 00:16:43
*/
@Service
public class UserSignatureServiceImpl extends ServiceImpl<UserSignatureMapper, UserSignature>
    implements UserSignatureService{

}




