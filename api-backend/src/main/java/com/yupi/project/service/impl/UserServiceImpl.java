package com.yupi.project.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.skyme.apicommon.model.entity.User;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.config.JwtConfig;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.mapper.UserMapper;

import com.yupi.project.service.UserService;
import com.yupi.project.type.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.yupi.project.constant.UserConstant.ADMIN_ROLE;
import static com.yupi.project.constant.UserConstant.USER_LOGIN_STATE;


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

}




