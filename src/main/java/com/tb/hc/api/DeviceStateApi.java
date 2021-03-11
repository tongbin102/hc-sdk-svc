package com.tb.hc.api;

import com.sun.jna.NativeLong;
import com.tb.hc.sdk.HCNetSDK;
import com.tb.hc.util.HCNetSDKUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bin.tong
 * @since 2021/3/11 16:45
 **/
@Slf4j
@RestController
@RequestMapping("/deviceStateApi")
public class DeviceStateApi {
  static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

  @GetMapping(value = "fetchDeviceState")
  public HCNetSDK.NET_DVR_WORKSTATE_V30 fetchDeviceState() {
    HCNetSDKUtils sdkUtils = new HCNetSDKUtils();

    sdkUtils.init();
    NativeLong lUserId = sdkUtils.login();
    if (lUserId.longValue() == -1) {
      log.error("注册失败！{}", hCNetSDK.NET_DVR_GetLastError());
      hCNetSDK.NET_DVR_Cleanup();
      return null;
    }
    // 调用接口获取设备工作状态
    HCNetSDK.NET_DVR_WORKSTATE_V30 dvrWorkState = new HCNetSDK.NET_DVR_WORKSTATE_V30();
    boolean getWorkStateResult = hCNetSDK.NET_DVR_GetDVRWorkState_V30(lUserId, dvrWorkState);
    if (!getWorkStateResult) {
      log.error("获取设备状态失败！{}", hCNetSDK.NET_DVR_GetLastError());
      return null;
    }
    return dvrWorkState;
  }
}
