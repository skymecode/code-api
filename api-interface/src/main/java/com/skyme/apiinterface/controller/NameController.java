package com.skyme.apiinterface.controller;


import cn.hutool.http.HttpUtil;
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
    public String getNameByGet(String sort){
        String s = HttpUtil.get("https://api.uomg.com/api/rand.avatar?sort=" + sort + "&format=json");

        return  s;

    }
    @PostMapping("/")
    public String getNameByPost(@RequestParam String name){
        return  "POST 你的名字是"+name;

    }
    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request){

        return  "POST 用户名"+user.getUsername();

    }

}
