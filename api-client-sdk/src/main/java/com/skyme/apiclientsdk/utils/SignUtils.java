package com.skyme.apiclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

public class SignUtils {
    public  static  String genSign(String body,String secretKey){
        Digester s=new Digester(DigestAlgorithm.SHA256);
        String content=body+"."+secretKey;
        return s.digestHex(content);
    }
}
