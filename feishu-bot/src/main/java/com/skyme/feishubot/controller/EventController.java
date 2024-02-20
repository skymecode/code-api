package com.skyme.feishubot.controller;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.utils.Constants;
import com.alibaba.dashscope.utils.JsonUtils;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.event.EventDispatcher;

import com.lark.oapi.service.contact.ContactService;
import com.lark.oapi.service.contact.v3.model.P2UserCreatedV3;

import com.lark.oapi.service.im.ImService;
import com.lark.oapi.service.im.v1.enums.MsgTypeEnum;
import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;

import com.lark.oapi.sdk.servlet.ext.ServletAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lark.oapi.service.im.v1.model.*;
import com.lark.oapi.service.im.v1.model.ext.MessageText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Slf4j
@RestController
public class EventController {



    //1. 注册消息处理器
    private final EventDispatcher EVENT_DISPATCHER = EventDispatcher.newBuilder("2hJMhCQq72CiO4FPKjlhQeImnCSWcONv",
                    "w1aV6DoeSolOTE6AcveR9efEAliOPKCi")
            .onP2MessageReceiveV1(new ImService.P2MessageReceiveV1Handler() {
                @Override
                public void handle(P2MessageReceiveV1 event) throws Exception {
                    System.out.println(1);
                    Constants.apiKey="sk-f74fb00b9cf2478fbb11756317cfbeb6";
                    Generation gen = new Generation();
                    MessageManager msgManager = new MessageManager(10);
                    Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content("You are a helpful assistant.").build();
                    Message userMsg = Message.builder().role(Role.USER.getValue()).content(event.getEvent().getMessage().getContent()).build();
                    msgManager.add(systemMsg);
                    msgManager.add(userMsg);
                    QwenParam param =
                            QwenParam.builder().model(Generation.Models.QWEN_TURBO).messages(msgManager.get())
                                    .resultFormat(QwenParam.ResultFormat.MESSAGE)
                                    .build();
                    GenerationResult result = gen.call(param);

                    HashMap<String, String> text = new HashMap<>();
                    text.put("text",result.getOutput().getChoices().get(0).getMessage().getContent());
                    String json = JsonUtils.toJson(text);
                    Client client = Client.newBuilder("cli_a54180277038900d", "yWYt1D67CmMT8jD7yE1V9g8FVxmkwJyQ").build();
                    // 发送请求
                    ReplyMessageReq req = ReplyMessageReq.newBuilder()
                            .messageId(event.getEvent().getMessage().getMessageId())
                            .replyMessageReqBody(ReplyMessageReqBody.newBuilder()
                                    .content(json)
                                    .msgType("text")
                                    .replyInThread(true)
                                    .uuid(event.getEvent().getMessage().getMessageId())
                                    .build())
                            .build();

                    // 发起请求
                    ReplyMessageResp resp = client.im().message().reply(req);

                    // 处理服务端错误
                    if(!resp.success()) {
                        System.out.println(String.format("code:%s,msg:%s,reqId:%s", resp.getCode(), resp.getMsg(), resp.getRequestId()));
                        return;
                    }

                    // 业务数据处理
                    System.out.println(Jsons.DEFAULT.toJson(resp.getData()));

                }
            })
            .build();

    //2. 注入 ServletAdapter 实例
    @Autowired
    private ServletAdapter servletAdapter;

    //3. 创建路由处理器
    @RequestMapping("/webhook/event")
    public void event(HttpServletRequest request, HttpServletResponse response)
            throws Throwable {
        //3.1 回调扩展包提供的事件回调处理器
        servletAdapter.handleEvent(request, response, EVENT_DISPATCHER);

    }
}
