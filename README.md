## 背景:

做一个API接口平台:

1. 防止攻击(安全性)
2. 不能随便调用
3. **统计调用次数**
4. 计费
5. 流量保护
6. API接入

## 项目介绍

用户可以注册登录,开通接口调用权限,用户可以使用接口,并且每次调用进行统计,管理员可以发布接口,下线接口,接入接口,可视化接口得调用情况、数据分析

后续:

除了管理员,我们添加接口市场,能够让用户发布自己的接口(主要问题是怎么接入网关层,进行统一管理)

## 数据库表设计

### 接口信息表

id

Name 接口名称

isDelete

creatTime

updateTime

description

Url 接口地址

Type 请求地址

requestHeader 

responseHeader

Status 接口状态 0关闭 1开启

```TypeScript
create table if not exists klapi.`interface_info`
(
`id` bigint not null auto_increment comment '主键' primary key,
`name` varchar(256) not null comment '接口名称',
`url` varchar(256) not null comment '接口地址',
`requestHeader` text null comment '请求头',
`responseHeader` text null comment '响应头',
`method` varchar(256) not null comment '请求类型',
`description` varchar(256) null comment '描述',
`userId` bigint not null comment '创建人',
`status` tinyint default 0 not null comment '接口状态 0-关闭,1-开启',
`createTime` datetime default 'CURRENT_TIMESTAMP' not null comment '创建时间',
`updateTime` datetime default 'CURRENT_TIMESTAMP' not null on update CURRENT_TIMESTAMP comment '更新时间',
`isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment '接口信息';
```

## API签名

accessKey

accessSecret(不传输的)

用户参数

单向加密,客户端和服务端都单向加密

密文:用户参数+accessSecret

防重发:随机数,时间戳

一个用户对应一个ak,sk

```SQL
create table if not exists klapi.`user_signature`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `userId` bigint not null comment '用户ID',
    `accessKey` varchar(256) null comment 'ak',
    `accessSecret` varchar(256) null comment 'sk',
    `status` tinyint default 0 not null comment ' 0-正常,1-禁用',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
   
) comment '用户aksk';
```

## JWT续签问题

在不更改jwt的情况下续签(实际已经解决)

## 调用

前端调用=>后端平台=>接口平台

主要:后端平台实现中转,界面美化

后续:网关层的加入

## 接口调用次数统计

需求:

1. 用户每次调用接口成功,次数+1
2. 给用户分配接口申请调用次数或自主申请

业务流程:

1. 用户调用接口(之前完成)
2. 修改数据库,调用次数+1

设计库表:

用户和接口->多对多关系

```SQL
create table if not exists klapi.`user_interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `userId` bigint not null comment '调用用户id',
    `interfaceInfoId` bigint not null comment '接口id',
    `totalNum` int default 0 not null comment '总调用次数',
    `leftNum` int default 0 not null comment '剩余调用次数',
    `status` tinyint default 0 not null comment ' 0-正常,1-禁用',
     `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
   
) comment '用户接口关系';
```

### 统一处理跨域

网关统一处理跨域,不在每个项目里单独处理

### 统一业务处理

把一些每个项目中要做的通用逻辑放到上层,统一处理,比如本项目的次数统计

### 访问控制

黑白名单,比如限制DDOS，IP

### 发布控制

灰度发布,比如上线新接口,先给用户分配20%的流量,老接口80%,在慢慢调整

### 流量染色

 给请求(流量)添加一些标识,添加到请求头中

### 接口保护:

1. 限制请求
2. 信息脱敏
3. 降级(熔断)
4. 限流
5. 超时时间

### 统一日志:

统一请求响应

### 统一文档

在一个页面统一查看

### 网关分类:

1. 全局网关:负载均衡，请求日志
2. 业务网关:作用是将请求转发到不同的业务/项目/接口/服务

### 网关业务逻辑:

1. 用户发送请求到API网关
2. 请求日志
3. 黑白名单
4. 用户鉴权aksk
5. 请求的模拟接口是否存在
6. 请求转发,调用模拟接口
7. 响应日志
8. 调用成功,接口调用次数+1
9. 调用失败,返回一个规范的错误码

### 网关路由匹配

```Java
server:
  port: 8090
spring:
  application:
    name: gateway
  cloud:
    gateway:
      routes:
        - id: gateway-api
          uri: http://localhost:8123
          predicates:
            - Path=/api/**
```

//todo api backend应该实现一个根据url来指定具体的调用目标的一个方法(invoke,根据这个接口id,去请求对应的内部接口或外部第三方接口),所以说前端设计的都是统一的请求,但请求的参数封装在一个body里面

## 调用次数统计图

```SQL
select interfaceInfoId,sum(totalNum) as totalNum from user_interface_info group by interfaceInfoId order by totalNum desc limit 3
```