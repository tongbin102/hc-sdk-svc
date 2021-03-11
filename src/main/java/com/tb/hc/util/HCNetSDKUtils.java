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

  public void init() {
    boolean initResult = hCNetSDK.NET_DVR_Init();
    if (!initResult) {
      log.error("初始化失败！");
    }

  }

  public static void main(String[] args) {
    HCNetSDKUtils sdkUtils = new HCNetSDKUtils();
    sdkUtils.init();
    hCNetSDK.NET_DVR_SetConnectTime(2000, 1);
    hCNetSDK.NET_DVR_SetReconnect(10000, true);

    sdkUtils.login();
  }

  public NativeLong login() {
    HCNetSDK.NET_DVR_DEVICEINFO_V30 dvrDeviceinfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
    return hCNetSDK.NET_DVR_Login_V30(host, Short.parseShort(port), username, password, dvrDeviceinfo);
  }

  public void searchFile(NativeLong userId, NativeLong channel) {
    HCNetSDK.NET_DVR_FILECOND dvrFileCond = new HCNetSDK.NET_DVR_FILECOND();

    // 查找录像文件
    NativeLong searchResult = hCNetSDK.NET_DVR_FindFile_V30(userId, dvrFileCond);

    if (searchResult.longValue() < 0) {
      log.error("Search file error: {}", hCNetSDK.NET_DVR_GetLastError());
    }
  }
}
