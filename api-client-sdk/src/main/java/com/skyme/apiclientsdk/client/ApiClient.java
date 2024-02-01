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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
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

    public String getNameByGet(String sort)  {

        HashMap<String,Object> paramMap=new HashMap<>();
        paramMap.put("sort",sort);
        HttpResponse res=HttpRequest.get(API_URL+"/api/name/?sort="+sort).addHeaders(getHeaderMap(sort)).charset("utf-8").execute();
        System.out.println(res.body());
        return res.body();
    }

    public String getNameByPost(String name){
        HashMap<String,Object> paramMap=new HashMap<>();
        paramMap.put("name",name);
        String res=HttpUtil.post(API_URL+"/api/name/", paramMap);
        System.out.println(res);
        return res;
    }
    public Map<String,String> getHeaderMap(String body)  {
        Map<String,String>hashMap=new HashMap<>();
        hashMap.put("accessKey",accessKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
        //解决参数中文乱码
        String encode ="";
        try {
            encode = URLEncoder.encode(body, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        hashMap.put("body",encode);
        hashMap.put("sign", SignUtils.genSign(body,secretKey));
        return  hashMap;
    }

    public String getUserNameByPost(User user)  {

        String jsonStr = JSONUtil.toJsonStr(user);
        HttpResponse res= HttpRequest.post(API_URL+"/api/name/user").addHeaders(getHeaderMap(jsonStr)).body(jsonStr).charset("utf-8").execute();
        System.out.println(res.getStatus());
        System.out.println(res.body());
        return res.body();

    }
}

