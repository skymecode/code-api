package com.skyme.apicommon.model.dto;

import lombok.Data;

@Data
public class RequestParam {
    private String id;
    private String fieldName;
    private String type;
    private String desc;
    private String required;
}
