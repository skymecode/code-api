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
import com.skyme.apicommon.model.dto.RequestParam;
import com.skyme.apicommon.model.entity.InterfaceInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiClient {
    private String accessKey;
    private String secretKey;


    private static String API_URL="http://api-gateway:8090";

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
    //参数:1.接口信息 2.请求头 3.请求体
    //todo 如果是json格式就要转成json处理,如果是url后面加的就要转成参数处理
    public String request(InterfaceInfo interfaceInfo,String[] params){
        //首先是判断请求
        String method = interfaceInfo.getMethod();
        String url=interfaceInfo.getUrl();
        HttpResponse res = null;
        if (method.equals("GET")){
            //拼接后面字符串
            StringBuilder stringBuilder = new StringBuilder(url);
            String requestParams = interfaceInfo.getRequestParams();
            //参数不一定是必须,但给了的参数一定是必须的
            List<RequestParam> requestList = JSONUtil.toList(requestParams, RequestParam.class);
            for (int i = 0; i < params.length; i++) {
                if (i==0){
                    stringBuilder.append("?");
                    stringBuilder.append(requestList.get(i).getFieldName());
                    stringBuilder.append("=");
                    stringBuilder.append(params[i]);
                }else{
                    stringBuilder.append("&");
                    stringBuilder.append(requestList.get(i).getFieldName());
                    stringBuilder.append("=");
                    stringBuilder.append(params[i]);
                }


            }
            if (params.length==0){

                res=HttpRequest.get(stringBuilder.toString()).addHeaders(getHeaderMap("nothing")).charset("utf-8").execute();
            }else{
                String param = params[0];
                res=HttpRequest.get(stringBuilder.toString()).addHeaders(getHeaderMap(param)).charset("utf-8").execute();
            }

        }else if (method.equals("POST")){

        }
        return res.body();
    }
}

