package com.skyme.apicommon.model.service;

import com.skyme.apicommon.model.entity.InterfaceInfo;

public interface InnerInterfaceInfoService {
    InterfaceInfo getInterfaceInfo(String url, String method);
}
