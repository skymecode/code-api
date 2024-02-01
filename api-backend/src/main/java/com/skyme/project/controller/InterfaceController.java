package com.skyme.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.skyme.apiclientsdk.client.ApiClient;

import com.skyme.apicommon.model.entity.InterfaceInfo;
import com.skyme.apicommon.model.entity.User;
import com.skyme.project.annotation.AuthCheck;
import com.skyme.project.common.*;
import com.skyme.project.constant.CommonConstant;
import com.skyme.project.exception.BusinessException;
import com.skyme.project.mapper.mapper.UserSignatureMapper;
import com.skyme.project.model.dto.interfaceinfo.InterfaceAddRequest;
import com.skyme.project.model.dto.interfaceinfo.InterfaceInvokeRequest;
import com.skyme.project.model.dto.interfaceinfo.InterfaceQueryRequest;
import com.skyme.project.model.dto.interfaceinfo.InterfaceUpdateRequest;


import com.skyme.project.model.entity.UserSignature;
import com.skyme.project.model.enums.InterfaceStatusEnum;
import com.skyme.project.service.InterfaceInfoService;
import com.skyme.project.service.UserService;
import com.skyme.project.service.service.UserSignatureService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 *
 * @author yupi
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private UserSignatureService userSignatureService;


    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceUpdateRequest interfaceInfoUpdateRequest,
                                            HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // content 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    // endregion
    /**
     * 上线（仅管理员可使用）
     *
     * @param idRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/online")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断接口是否存在
        Long id = idRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //判断接口是否可以调用
        com.skyme.apiclientsdk.model.User user1 = new com.skyme.apiclientsdk.model.User();
        user1.setUsername("skyme");
        ApiClient apiClient = getAPIClient(request);
        String userNameByPost = apiClient.getUserNameByPost(user1);
        if (StringUtils.isBlank(userNameByPost)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口调用失败");
        }

        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceStatusEnum.ONLINE.getValue());
        User user = userService.getLoginUser(request);
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //更新
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 下线（仅管理员可使用）
     *
     * @param idRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/offline")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断接口是否存在
        Long id = idRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        //判断接口是否可以调用
        com.skyme.apiclientsdk.model.User user1 = new com.skyme.apiclientsdk.model.User();
        user1.setUsername("skyme");
        ApiClient apiClient = getAPIClient(request);
        String userNameByPost = apiClient.getUserNameByPost(user1);
        if (StringUtils.isBlank(userNameByPost)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口调用失败");
        }

        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceStatusEnum.OFFLINE.getValue());
        // 仅本人或管理员可修改
        User user = userService.getLoginUser(request);
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }
    /**
     *
     *
     * @param invokeRequest
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInvokeRequest invokeRequest,
                                                      HttpServletRequest request) {
        if (invokeRequest == null || invokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断接口是否存在
        Long id = invokeRequest.getId();
        String userRequestParams = invokeRequest.getUserRequestParams();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if(oldInterfaceInfo.getStatus()==InterfaceStatusEnum.OFFLINE.getValue()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口已经关闭");
        }

//        Gson gson=new Gson();
//        com.skyme.apiclientsdk.model.User user = gson.fromJson(userRequestParams, com.skyme.apiclientsdk.model.User.class);;
        //判断接口是否可以调用
        ApiClient apiClient = getAPIClient(request);
        String userNameByPost = apiClient.getNameByGet(userRequestParams);
        if (StringUtils.isBlank(userNameByPost)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口调用失败");
        }
        return ResultUtils.success(userNameByPost);
    }

    /**
     * 获取APICLIENT
     * @param request
     * @return
     */
    private ApiClient getAPIClient(HttpServletRequest request){
        QueryWrapper<UserSignature> userSignatureQueryWrapper = new QueryWrapper<UserSignature>();
        User loginUser = userService.getLoginUser(request);
        userSignatureQueryWrapper.eq("userId",loginUser.getId());
        userSignatureQueryWrapper.eq("status",0);
        UserSignature realSg = userSignatureService.getOne(userSignatureQueryWrapper);
        String accessKey = realSg.getAccessKey();
        String accessSecret = realSg.getAccessSecret();
        return new ApiClient(accessKey,accessSecret);
    }

}
