package com.tb.hc.task;

import com.sun.jna.NativeLong;
import com.tb.hc.sdk.HCNetSDK;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;

/**
 * @author bin.tong
 * @since 2021/3/11 18:36
 **/
@Slf4j
public class DownloadFileTask extends TimerTask {
  private static HCNetSDK hcNetSDK = HCNetSDK.INSTANCE;

  NativeLong lDownloadFile;

  public DownloadFileTask(NativeLong lDownloadFile) {
    this.lDownloadFile = lDownloadFile;
  }

  // 定时器函数
  @Override
  public void run() {
    int iPos = hcNetSDK.NET_DVR_GetDownloadPos(lDownloadFile);
    log.info("Pos: {}", iPos);
    if (iPos < 0) {
      // Fail
      hcNetSDK.NET_DVR_StopGetFile(lDownloadFile);
      lDownloadFile.setValue(-1);
      log.error("获取下载进度失败！");
      this.cancel();
    } else if (iPos == 100) {
      // End download
      hcNetSDK.NET_DVR_StopGetFile(lDownloadFile);
      lDownloadFile.setValue(-1);
      log.info("下载完毕！");
      this.cancel();
    } else if (iPos > 100){
      // Download exception for network problems or DVR hasten
      hcNetSDK.NET_DVR_StopGetFile(lDownloadFile);
      lDownloadFile.setValue(-1);
      log.error("由于网络原因或DVR忙，下载异常终止！");
      this.cancel();
    }
  }


}
