package com.skyme.project.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.skyme.apicommon.model.entity.User;
import com.skyme.project.common.ErrorCode;
import com.skyme.project.config.JwtConfig;
import com.skyme.project.exception.BusinessException;
import com.skyme.project.mapper.UserMapper;

import com.skyme.project.mapper.mapper.UserSignatureMapper;
import com.skyme.project.model.dto.usersignature.UserSignatureQueryRequest;
import com.skyme.project.model.entity.UserSignature;
import com.skyme.project.model.vo.UserVO;
import com.skyme.project.service.UserService;
import com.skyme.project.service.service.UserSignatureService;
import com.skyme.project.type.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.skyme.project.constant.UserConstant.ADMIN_ROLE;
import static com.skyme.project.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户服务实现类
 *
 * @author yupi
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RedisTemplate redisTemplate;

    @Resource
    UserSignatureMapper userSignatureMapper;

    @Resource
    UserSignatureService userSignatureService;

    @Resource
    private JwtConfig jwtConfig ;
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = passwordEncoder.encode(userPassword);
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public JSONObject userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        //登录授权
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userAccount,userPassword);
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if(Objects.isNull(authenticate)){
            //说明失败
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUser user = (LoginUser) (authenticate.getPrincipal());
        String userId=user.getUser().getId().toString();
        //redis作为分布式缓存
        //生成token
        String token = jwtConfig.createToken(userId);
        redisTemplate.opsForValue().setIfAbsent("token:" + userId,token,1, TimeUnit.HOURS);
        redisTemplate.opsForValue().set("login:"+userId,user,1, TimeUnit.HOURS);
        JSONObject json = new JSONObject();
        if (!org.springframework.util.StringUtils.isEmpty(token)) {
            json.put("token",token) ;
        }
//        // 2. 加密
//        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
//        // 查询用户是否存在
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("userAccount", userAccount);
//        queryWrapper.eq("userPassword", encryptPassword);
//        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
//        if (user == null) {
//            log.info("user login failed, userAccount cannot match userPassword");
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
//        }
//        // 3. 记录用户的登录态
//        request.getSession().setAttribute(USER_LOGIN_STATE, user);
//        System.out.println(request.getSession().getId());
        return json;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {

        // 先判断是否已登录
        String userID = jwtConfig.getUsernameFromToken(request.getHeader("token"));
        LoginUser loginUser= (LoginUser) redisTemplate.opsForValue().get("login:" + userID);
        User user = loginUser.getUser();
        if (user == null ||  user.getId()== null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return user;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
                */
        @Override
        public boolean isAdmin(HttpServletRequest request) {
            // 仅管理员可查询
            String userID = jwtConfig.getUsernameFromToken(request.getHeader("token"));
            LoginUser loginUser= (LoginUser) redisTemplate.opsForValue().get("login:" + userID);
            User user = loginUser.getUser();

        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 申请AKSK
     * @param request
     * @return
     */
    @Override
    public UserSignature subscribeAKSK(HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        UserSignature userSignature = new UserSignature();
        userSignature.setUserId(loginUser.getId());
        Digester s=new Digester(DigestAlgorithm.SHA256);
        //先将所有得拿出来变成状态变为失效,再放入一个新的
        QueryWrapper<UserSignature> userSignatureQueryWrapper = new QueryWrapper<>();
        userSignatureQueryWrapper.eq("status",0);
        List<UserSignature> userSignatures = userSignatureMapper.selectList(userSignatureQueryWrapper);
        userSignatures.forEach(test -> {
            if (test.getStatus() == 0) {
                test.setStatus(1);
                userSignatureMapper.updateById(test);
            }
        });
        userSignature.setAccessKey(s.digestHex(loginUser.getUserAccount()+System.currentTimeMillis()/1000+ RandomUtil.randomNumbers(4)));
        String ak=s.digestHex(loginUser.getUserPassword()+"ak"+System.currentTimeMillis()/1000+RandomUtil.randomNumbers(4));
        userSignature.setAccessSecret(ak);
        int insert = userSignatureMapper.insert(userSignature);
        if (insert<=0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"申请失败");
        }
        return userSignature;
    }

    @Override
    public Page<UserSignature> subscribeGetAKSK(UserSignatureQueryRequest userSignatureQueryRequest,HttpServletRequest request) {
        long current=1;
        long size=10;

        if (userSignatureQueryRequest!=null){
            current=userSignatureQueryRequest.getCurrent();
            size=userSignatureQueryRequest.getPageSize();
        }
        User loginUser = getLoginUser(request);
        QueryWrapper<UserSignature> userSignatureQueryWrapper = new QueryWrapper<>();
        userSignatureQueryWrapper.eq("userId",loginUser.getId());
        Page<UserSignature> signPage= userSignatureService.page(new Page<>(current,size),userSignatureQueryWrapper);
        return signPage;
    }

}




