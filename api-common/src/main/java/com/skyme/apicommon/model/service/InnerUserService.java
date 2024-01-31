package com.skyme.apicommon.model.service;

import com.skyme.apicommon.model.entity.User;

public interface InnerUserService {
    User getIInvokeUser(String ak);

    String getSecretKey(long uid);
}
