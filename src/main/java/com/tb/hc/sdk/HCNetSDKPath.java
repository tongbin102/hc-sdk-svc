package com.tb.hc.sdk;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * @author bin.tong
 * @since 2021/3/10 8:49
 **/
@Slf4j
public class HCNetSDKPath {
    public static String DLL_PATH;

    static {
        String path = (HCNetSDKPath.class.getResource("/").getPath() + "lib/").replaceAll("%20", " ").substring(1).replace("bin", "lib").replace("/", "\\");
        log.info("path: ", path);
        try {
            DLL_PATH = java.net.URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException: ", e.getMessage());
        }
    }
}
