package com.skyme.project.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建请求
 *
 * @TableName product
 */
@Data
public class InterfaceAddRequest implements Serializable {

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    private String requestParams;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 描述
     */
    private String description;


}