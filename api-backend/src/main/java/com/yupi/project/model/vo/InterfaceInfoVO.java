package com.yupi.project.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.skyme.apicommon.model.entity.InterfaceInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class InterfaceInfoVO extends InterfaceInfo {
        private Integer totalNum;

}