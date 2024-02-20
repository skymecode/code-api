package com.skyme.apiinterface.controller;

import cn.hutool.http.HttpUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/music")
public class MusicController {
    @GetMapping("/wycloud")
    public String getNameByGet( String type,@RequestParam("sort") String sort){
        System.out.println(sort);
        String url="https://api.vvhan.com/api/rand.music?type="+type+"&sort=" + sort;
        String s = HttpUtil.get("https://api.vvhan.com/api/rand.music?type="+type+"&sort=" + sort );
        System.out.println(url);
        System.out.println(s);
        return  s;
    }
}
