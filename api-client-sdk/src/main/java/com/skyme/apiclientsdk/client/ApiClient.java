package com.skyme.apiclientsdk.client;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.skyme.apiclientsdk.model.User;
import com.skyme.apiclientsdk.utils.SignUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ApiClient {
    private String accessKey;
    private String secretKey;


    private static String API_URL="http://localhost:8090";

    public ApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name){
        HashMap<String,Object> paramMap=new HashMap<>();
        paramMap.put("name",name);
        String res = HttpUtil.get(API_URL+"/api/name/", paramMap);
        System.out.println(res);
        return res;
    }

    public String getNameByPost(String name){
        HashMap<String,Object> paramMap=new HashMap<>();
        paramMap.put("name",name);
        String res=HttpUtil.post(API_URL+"/api/name/", paramMap);
        System.out.println(res);
        return res;
    }
    public Map<String,String> getHeaderMap(String body){
        Map<String,String>hashMap=new HashMap<>();
        hashMap.put("accessKey",accessKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
        hashMap.put("body",body);
        hashMap.put("sign", SignUtils.genSign(body,secretKey));
        return  hashMap;
    }

    public String getUserNameByPost(User user){

        String jsonStr = JSONUtil.toJsonStr(user);
        HttpResponse res= HttpRequest.post(API_URL+"/api/name/user").addHeaders(getHeaderMap(jsonStr)).body(jsonStr).execute();
        System.out.println(res.getStatus());
        System.out.println(res.body());
        return res.body();

    }
}

