package com.skyme.project.model.dto.usersignature;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.skyme.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author yupi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserSignatureQueryRequest extends PageRequest implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;


    private String userId;


    private String accessKey;


    private String accessSecret;




}