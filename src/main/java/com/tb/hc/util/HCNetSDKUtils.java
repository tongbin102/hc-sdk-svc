package com.tb.hc.util;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;
import com.tb.hc.sdk.HCNetSDK;
import com.tb.hc.task.DownloadFileTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;

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
  String password = "hik12345";

  public void init() {
    boolean initResult = hCNetSDK.NET_DVR_Init();
    if (!initResult) {
      log.error("初始化失败！ {}", hCNetSDK.NET_DVR_GetLastError());
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

  public void searchFile(NativeLong lUserId, NativeLong lChannel) {
    HCNetSDK.NET_DVR_FILECOND dvrFileCond = new HCNetSDK.NET_DVR_FILECOND();

    // dvrFileCond.dwFileType = 0xFF;
    // // 通道号
    // dvrFileCond.lChannel = lChannel;
    // dvrFileCond.dwIsLocked = 0xFF;
    // // 是否使用卡号
    // dvrFileCond.dwUseCardNo = 0;

    // 查找录像文件
    NativeLong lFindFile = hCNetSDK.NET_DVR_FindFile_V30(lUserId, dvrFileCond);

    if (lFindFile.longValue() < 0) {
      log.error("Search file error: {}", hCNetSDK.NET_DVR_GetLastError());
      return;
    }
    HCNetSDK.NET_DVR_FINDDATA_V30 dvrFileData = new HCNetSDK.NET_DVR_FINDDATA_V30();
    NativeLong lNext;

    while (true) {
      // 逐个获取查找到的文件信息
      lNext = hCNetSDK.NET_DVR_FindNextFile_V30(lFindFile, dvrFileData);
      if (lNext.longValue() == HCNetSDK.NET_DVR_FILE_SUCCESS) {
        // 搜索成功
        String fileName = new String(dvrFileData.sFileName);
        downloadFile(lUserId, fileName, fileName);
      } else if (lNext.longValue() == HCNetSDK.NET_DVR_ISFINDING) {
        log.info("搜索中……");
        continue;
      } else if (lNext.longValue() == HCNetSDK.NET_DVR_FILE_NOFIND) {
        log.info("没有搜索到文件");
        return;
      } else {
        log.info("搜索文件结束！");
        boolean flag = hCNetSDK.NET_DVR_FindClose_V30(lFindFile);
        if (!flag) {
          log.error("结束搜索失败！");
        }
      }
    }
  }

  public void downloadFile(NativeLong lUserId, String sourceFileName, String destFileName) {
    // 按文件名下载录像
    NativeLong lDownloadFile = hCNetSDK.NET_DVR_GetFileByName(lUserId, sourceFileName, destFileName);
    if (lDownloadFile.longValue() < 0) {
      log.error("下载文件失败！{}", hCNetSDK.NET_DVR_GetLastError());
      return;
    }

    // 开始下载
    boolean playStartResult = hCNetSDK.NET_DVR_PlayBackControl(lDownloadFile, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
    if (!playStartResult) {
      log.error("开始下载文件失败！{}", hCNetSDK.NET_DVR_GetLastError());
    }
    // 开始计时器
    // 新建定时器
    Timer downloadTimer = new Timer();
    // 0秒后开始响应函数
    downloadTimer.schedule(new DownloadFileTask(lDownloadFile), 0, 1000);
  }

}
