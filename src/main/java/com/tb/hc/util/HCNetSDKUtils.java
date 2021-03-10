package com.tb.hc.util;

import com.sun.jna.NativeLong;
import com.tb.hc.sdk.HCNetSDK;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bin.tong
 * @since 2021/3/10 10:56
 **/
@Slf4j
public class HCNetSDKUtils {

  private static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

  String host = "192.168.0.177";
  String port = "8000";
  String username = "admin";
  String password = "hik12345+";

  public void init(){
    boolean initResult = hCNetSDK.NET_DVR_Init();
    if(!initResult){
      log.error("初始化失败！");
    }

  }

  public static void main(String[] args) {
    HCNetSDKUtils sdkUtils = new HCNetSDKUtils();
    sdkUtils.init();
    hCNetSDK.NET_DVR_SetConnectTime(2000,1);
    hCNetSDK.NET_DVR_SetReconnect(10000,true);

    sdkUtils.login();
  }

  public void login(){
    HCNetSDK.NET_DVR_DEVICEINFO_V30 dvrDeviceinfo =  new HCNetSDK.NET_DVR_DEVICEINFO_V30();
    NativeLong userId = hCNetSDK.NET_DVR_Login_V30(host,Short.parseShort(port),username, password, dvrDeviceinfo);
    if (userId.longValue() == -1) {
      log.error("注册失败！", hCNetSDK.NET_DVR_GetLastError());
      hCNetSDK.NET_DVR_Cleanup();
      return;
    }
  }
}
