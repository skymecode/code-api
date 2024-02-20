package com.skyme.project.exception;

import cn.hutool.core.thread.ThreadUtil;
import com.skyme.apiclientsdk.async.LogTask;
import com.skyme.apiclientsdk.utils.FeishuUtils;
import com.skyme.project.common.BaseResponse;
import com.skyme.project.common.ErrorCode;
import com.skyme.project.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author yupi
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) throws Exception {
        log.error("businessException: " + e.getMessage(), e);
        FeishuUtils.SendLogInfo("ERROR","businessException:"+e.getMessage()+e);
        ThreadUtil.execAsync(new LogTask("ERROR","businessException:"+e.getMessage()+e));
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) throws Exception {
        log.error("runtimeException", e);
        ThreadUtil.execAsync(new LogTask("ERROR","runtimeException:"+e.getMessage()+e));

        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }
}
