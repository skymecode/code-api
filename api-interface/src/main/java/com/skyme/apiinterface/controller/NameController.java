package com.skyme.apiinterface.controller;


import com.skyme.apiclientsdk.utils.SignUtils;
import com.skyme.apiinterface.model.User;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
*名称 API
*
* @author skyme
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @GetMapping("/")
    public String getNameByGet(String name){
        return  "GET 你的名字是"+name;

    }
    @PostMapping("/")
    public String getNameByPost(@RequestParam String name){
        return  "POST 你的名字是"+name;

    }
    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request){
        String accessKey= request.getHeader("accessKey");
       String nonce=request.getHeader("nonce");
       String timestamp=request.getHeader("timestamp");
       String sign=request.getHeader("sign");
       String body=request.getHeader("body");
       if (!accessKey.equals("skyme")){
           throw new RuntimeException("无权限");
       }
        if (Long.parseLong(nonce)>10000){
            throw new RuntimeException("无权限");
        }
        //todo 时间不能与当前时间超过5分钟
        //todo 实际是从数据库取出secretkey
        String s = SignUtils.genSign(body, "test");
        if (!sign.equals(s)){
            throw new RuntimeException("无权限");
        }
        return  "POST 用户名"+user.getUsername();

    }
}
