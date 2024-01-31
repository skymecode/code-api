package com.yupi.project.model.dto.interfaceinfo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用
 *
 * @TableName product
 */
@Data
public class InterfaceInvokeRequest implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String userRequestParams;



}