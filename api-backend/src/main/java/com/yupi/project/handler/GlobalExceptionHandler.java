//package com.yupi.project.handler;
//
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
////全局异常捕获
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//    private static final Logger LOGGER= LogManager.getLogger();
//    @ExceptionHandler(value = Exception.class)
//    public BaseResponse handlerLogException(Exception e){
//        LOGGER.error("发生了异常"+e.getMessage());
//        return new BaseResponse(4007,"发生了异常"+e.getMessage(),"错误数据");
//    }
//}
