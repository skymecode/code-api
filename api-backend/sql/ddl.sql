-- 创建库
create database if not exists klapi;

-- 切换库
use klapi;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userName     varchar(256)                           null comment '用户昵称',
    userAccount  varchar(256)                           not null comment '账号',
    userAvatar   varchar(1024)                          null comment '用户头像',
    gender       tinyint                                null comment '性别',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user / admin',
    userPassword varchar(512)                           not null comment '密码',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    constraint uni_userAccount
        unique (userAccount)
) comment '用户';

-- 帖子表
create table if not exists post
(
    id            bigint auto_increment comment 'id' primary key,
    age           int comment '年龄',
    gender        tinyint  default 0                 not null comment '性别（0-男, 1-女）',
    education     varchar(512)                       null comment '学历',
    place         varchar(512)                       null comment '地点',
    job           varchar(512)                       null comment '职业',
    contact       varchar(512)                       null comment '联系方式',
    loveExp       varchar(512)                       null comment '感情经历',
    content       text                               null comment '内容（个人介绍）',
    photo         varchar(1024)                      null comment '照片地址',
    reviewStatus  int      default 0                 not null comment '状态（0-待审核, 1-通过, 2-拒绝）',
    reviewMessage varchar(512)                       null comment '审核信息',
    viewNum       int                                not null default 0 comment '浏览数',
    thumbNum      int                                not null default 0 comment '点赞数',
    userId        bigint                             not null comment '创建用户 id',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除'
) comment '帖子';


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
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment '接口信息';

insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (1, 'W0C', 'www.francisco-hill.co', 'l81J', 'FBAtr', 'GET', 'F3i0', 4, 1, '2022-12-28 13:11:20', '2022-10-27 09:01:55', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (2, 'M5i', 'www.dominique-daniel.com', 'wh', 'FxF', 'GET', 'tV', 5, 1, '2022-06-23 18:27:36', '2022-11-23 05:46:09', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (3, 'W16O', 'www.felice-ebert.info', 'hJ1vl', 'ixj', 'GET', 'N8n', 5864153580, 1, '2022-02-28 07:38:09', '2022-07-16 12:37:58', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (4, '3zDV', 'www.gema-shields.co', 'nfv', 'PFthM', 'GET', 'X0V', 36, 1, '2022-08-23 21:51:06', '2022-10-05 08:29:28', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (5, '5H7zZ', 'www.colton-olson.io', 'HcGen', '0JH0x', 'GET', 'av', 85425, 1, '2022-02-18 15:24:22', '2022-09-16 13:09:27', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (6, 'gaGDW', 'www.nelda-balistreri.info', '47pA', 'rhilz', 'GET', 'VcIb', 452557, 1, '2022-07-12 16:11:55', '2022-05-26 18:51:15', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (7, '0s', 'www.leo-kuphal.name', '8W', 'yr', 'GET', 'lu', 1547587, 1, '2022-12-23 16:59:30', '2022-07-14 21:34:10', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (8, 'wAukU', 'www.halina-sauer.net', 'iae', 'kUu', 'GET', '7SGI', 9, 1, '2022-07-07 16:06:45', '2022-11-07 16:28:57', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (9, 'rNtR', 'www.doyle-halvorson.name', 'Qq', 'm41l', 'GET', 'Pnlw', 95768, 1, '2022-12-11 02:39:36', '2022-07-11 04:42:15', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (10, '1kSbs', 'www.corina-kunde.io', '9M75w', 'PnRV', 'GET', 'UgG', 610471, 1, '2022-05-14 01:49:24', '2022-02-07 10:01:29', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (11, 'BAQg', 'www.jeff-nitzsche.biz', 'Atm', 'dR', 'GET', 'mCG2', 726993, 1, '2022-05-28 14:57:45', '2022-12-02 08:01:07', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (12, 'JnD', 'www.drew-waelchi.co', 'Vn8Wr', 'lV67', 'GET', 'HHGqQ', 1105633, 1, '2022-03-24 01:34:01', '2022-06-08 21:15:02', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (13, 'nA', 'www.johnson-hamill.biz', 'jaR68', 'Yy8', 'GET', 'xOGw1', 8, 1, '2022-04-10 09:59:29', '2022-01-07 03:22:44', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (14, 'ug', 'www.jame-grimes.co', 'ScFCf', 'oahm', 'GET', 'Da', 1182553, 1, '2022-09-17 01:22:44', '2022-11-20 14:34:41', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (15, 'KO', 'www.mohamed-quigley.io', 'g1c', 'JDl', 'GET', 'EMH', 487697, 1, '2022-05-30 08:04:27', '2022-09-12 20:14:30', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (16, 'ZPLdH', 'www.shawnda-dach.io', 'lS', 'BLeQ', 'GET', 'qQd', 39, 1, '2022-04-04 21:46:17', '2022-12-09 13:54:36', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (17, 'nohxN', 'www.dino-volkman.name', 'X0oBU', 'yBhI', 'GET', 'Pg', 782053, 1, '2022-05-30 04:11:18', '2022-01-10 15:15:29', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (18, '4T', 'www.kendall-pouros.com', 'DOJ6C', 'pd5', 'GET', 'pA', 1, 1, '2022-05-30 21:25:38', '2022-01-24 20:11:00', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (19, 'ax', 'www.jimmie-rogahn.org', 'EAem', 'ZcD', 'GET', 'Qn', 801, 1, '2022-10-03 08:53:03', '2022-01-24 10:52:46', 0);
insert into klapi.`interface_info` (`id`, `name`, `url`, `requestHeader`, `responseHeader`, `method`, `description`, `userId`, `status`, `createTime`, `updateTime`, `isDelete`) values (20, 'vO', 'www.wade-morissette.name', '8F8X', 'QITe', 'GET', 'PB', 859, 1, '2022-10-13 11:19:26', '2022-08-22 00:42:07', 0);



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