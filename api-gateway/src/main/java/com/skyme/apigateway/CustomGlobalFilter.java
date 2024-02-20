package com.skyme.apigateway;

import cn.hutool.core.thread.ThreadUtil;
import com.skyme.apiclientsdk.async.LogTask;
import com.skyme.apiclientsdk.utils.FeishuUtils;
import com.skyme.apiclientsdk.utils.SignUtils;
import com.skyme.apicommon.model.entity.InterfaceInfo;
import com.skyme.apicommon.model.entity.User;
import com.skyme.apicommon.model.entity.UserInterfaceInfo;
import com.skyme.apicommon.model.service.InnerInterfaceInfoService;
import com.skyme.apicommon.model.service.InnerUserInterfaceInfoService;
import com.skyme.apicommon.model.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    InnerUserService innerUserService;
    @DubboReference
    InnerUserInterfaceInfoService innerUserInterfaceInfoService;
    @DubboReference
    InnerInterfaceInfoService innerInterfaceInfoService;
    private static final String INTERFACE_HOST="http://api-gateway:8090";
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        FeishuUtils.chat_id="oc_a652d2fb490d945d7e2357b2c36e7301";
        //1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        log.info("请求唯一标识:"+request.getId());
        String method = request.getMethod().toString();
        log.info("请求方法:"+method);
        URI uri = request.getURI();
        String url=uri.getScheme()+"://"+uri.getHost()+":"+uri.getPort()+uri.getPath();
        log.info("请求路径:"+url);
        log.info("请求参数:"+request.getQueryParams());
        log.info("请求来源地址:"+request.getRemoteAddress());
        StringBuilder sb=new StringBuilder();
        sb.append("请求唯一标识:");
        sb.append(request.getId());
        sb.append("\n请求方法:");
        sb.append(method);
        sb.append("\n请求路径:");
        sb.append(uri);
        sb.append("\n请求参数");
        sb.append(request.getQueryParams());
        sb.append("\n请求来源地址:");
        sb.append(request.getRemoteAddress());
        try {

            ThreadUtil.execAsync(new LogTask("INFO",sb.toString()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //2.黑名单
        //3. 鉴权
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");

        String body = null;
        //解码，解决中文乱码问题
        body = headers.getFirst("body");
        try {
            body = URLDecoder.decode(body,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String sign = headers.getFirst("sign");
        InterfaceInfo interfaceInfo=null;
        try {
            interfaceInfo= innerInterfaceInfoService.getInterfaceInfo(url,method);
        }catch (Exception e){
            log.error("getinterfaceInfo ERROR:"+e);
        }
        if (interfaceInfo==null){
            log.info("没有信息");
            return  handlNoAuth(response);
        }
        User invokeUser=null;
        try{
           invokeUser = innerUserService.getIInvokeUser(accessKey);
        }catch (Exception e){
            log.error("getInvokeUser ERROR:"+e);
        }
        if (invokeUser==null){
            log.info("执行人错误");
            return  handlNoAuth(response);
        }
        UserInterfaceInfo userInterfaceInfo = innerUserInterfaceInfoService.queryUserInterfaceInfo(interfaceInfo, invokeUser);
        if (userInterfaceInfo==null){
            return handlNoAuth(response);
        }else if (userInterfaceInfo.getLeftNum()<=0){
            return handlNoAuth(response);
        }
        String secretKey = innerUserService.getSecretKey(invokeUser.getId());
        String s = SignUtils.genSign(body, secretKey);
        if (s==null||!s.equals(sign)){
            log.info("校验错误");
           return handlNoAuth(response);
        }
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            HttpStatus statusCode = originalResponse.getStatusCode();

            if(statusCode == HttpStatus.OK){
                InterfaceInfo finalInterfaceInfo = interfaceInfo;
                User finalInvokeUser = invokeUser;
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        //log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                //todo 调用成功,次数加1
                                boolean b = innerUserInterfaceInfoService.invokeCount(finalInterfaceInfo.getId(), finalInvokeUser.getId());
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                //rspArgs.add(requestUrl);
                                String data = new String(content, StandardCharsets.UTF_8);//data
                                log.info("<-- {} {}\n", originalResponse.getStatusCode(), data);
                                try {
                                    ThreadUtil.execAsync(new LogTask("INFO","网关正常响应:"+getStatusCode()));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            try {
                                ThreadUtil.execAsync(new LogTask("ERROR","网关响应异常:"+getStatusCode()));

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        }catch (Exception e){
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public  Mono<Void> handlNoAuth(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }
}
