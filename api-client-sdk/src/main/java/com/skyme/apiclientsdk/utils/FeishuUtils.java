package com.skyme.apiclientsdk.utils;

import cn.hutool.json.JSONUtil;
import com.lark.oapi.Client;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.im.v1.model.*;
import com.skyme.apiclientsdk.model.card.Root;
import lombok.Data;


import java.util.UUID;

/*
作用于飞书机器人,输出日志到指定群聊进行监控
 */
@Data
public class FeishuUtils {

    public static String chat_id="";
    public static String appId;
    public static  String appSecret;



    public static void SendLogInfo(String level, String context) throws Exception {

        Client client = Client.newBuilder(appId, appSecret).build();
        // 发送请求
        Root root = CardUtils.getCard(level,context);
        String jsonStr = JSONUtil.toJsonStr(root);
        System.out.println(jsonStr);
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .receiveIdType("chat_id")
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .receiveId(chat_id)
                        .msgType("interactive")
                        .content(jsonStr)
                        .uuid(UUID.randomUUID().toString())
                        .build())
                .build();

        // 发起请求
        CreateMessageResp resp = client.im().message().create(req);
        if(!resp.success()) {
            System.out.println(String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(), resp.getRequestId()));
            return;
        }

        // 业务数据处理
        System.out.println(Jsons.DEFAULT.toJson(resp.getData()));

    }
}
