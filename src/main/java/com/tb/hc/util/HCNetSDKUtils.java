package com.tb.hc.util;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.tb.hc.sdk.HCNetSDK;
import com.tb.hc.task.DownloadFileTask;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

/**
 * @author bin.tong
 * @since 2021/3/10 10:56
 **/
@Slf4j
public class HCNetSDKUtils {

    private static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

    //    String host = "192.168.0.177";
//    String host = "10.97.154.236";
    String port = "8000";
    String username = "admin";
    String password = "Hik12345";

    public void init() {
        boolean initResult = hCNetSDK.NET_DVR_Init();
        if (!initResult) {
            log.error("初始化失败！ {}", hCNetSDK.NET_DVR_GetLastError());
            return;
        }
        setLinuxPath();
//        initResult  = hCNetSDK.NET_DVR_SetLogToFile(3, "C:\\logs\\",false);
        initResult = hCNetSDK.NET_DVR_SetLogToFile(3, "/opt/logs/", false);
    }

    private void setLinuxPath() {

        // 设置HCNetSDKCom组件库所在路径
        String strPathCom = "/usr/lib/";
        HCNetSDK.NET_DVR_LOCAL_SDK_PATH struComPath = new HCNetSDK.NET_DVR_LOCAL_SDK_PATH();
        System.arraycopy(strPathCom.getBytes(), 0, struComPath.sPath, 0, strPathCom.length());
        struComPath.write();
        hCNetSDK.NET_DVR_SetSDKInitCfg(2, struComPath.getPointer());

        // 设置libcrypto.so所在路径
        HCNetSDK.BYTE_ARRAY ptrByteArrayCrypto = new HCNetSDK.BYTE_ARRAY(256);
        String strPathCrypto = "/usr/lib/libcrypto.so";
        System.arraycopy(strPathCrypto.getBytes(), 0, ptrByteArrayCrypto.byValue, 0, strPathCrypto.length());
        ptrByteArrayCrypto.write();
        hCNetSDK.NET_DVR_SetSDKInitCfg(3, ptrByteArrayCrypto.getPointer());

        // 设置libssl.so所在路径
        HCNetSDK.BYTE_ARRAY ptrByteArraySsl = new HCNetSDK.BYTE_ARRAY(256);
        String strPathSsl = "/usr/lib/libssl.so";
        System.arraycopy(strPathSsl.getBytes(), 0, ptrByteArraySsl.byValue, 0, strPathSsl.length());
        ptrByteArraySsl.write();
        hCNetSDK.NET_DVR_SetSDKInitCfg(4, ptrByteArraySsl.getPointer());
    }

    public static void main(String[] args) {
        HCNetSDKUtils sdkUtils = new HCNetSDKUtils();
        sdkUtils.init();
        hCNetSDK.NET_DVR_SetConnectTime(2000, 1);
        hCNetSDK.NET_DVR_SetReconnect(10000, true);

        String deviceIp = "10.97.154.236";
        NativeLong lUserId = sdkUtils.login(deviceIp);
        if (lUserId.longValue() == -1) {
            log.error("注册失败！{}", hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Cleanup();
            return;
        }
    }

    public NativeLong login(String deviceIp) {
        HCNetSDK.NET_DVR_DEVICEINFO_V30 dvrDeviceinfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        return hCNetSDK.NET_DVR_Login_V30(deviceIp, Short.parseShort(port), username, password, dvrDeviceinfo);
    }

    public void searchAndDownloadFile(String deviceIp, NativeLong lUserId, NativeLong lChannel, String startTime, String stopTime) {

        String pattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar cStartTime = Calendar.getInstance();
        Calendar cStopTime = Calendar.getInstance();

        try {
            Date dStartTime = sdf.parse(startTime);
            cStartTime.setTime(dStartTime);
            Date dStopTime = sdf.parse(stopTime);
            cStopTime.setTime(dStopTime);
        } catch (ParseException e) {
            log.warn("日期格式错误！");
        }

        HCNetSDK.NET_DVR_FILECOND dvrFileCond = new HCNetSDK.NET_DVR_FILECOND();

        // 是否使用卡号
        dvrFileCond.struStartTime = new HCNetSDK.NET_DVR_TIME();
        dvrFileCond.struStopTime = new HCNetSDK.NET_DVR_TIME();
        dvrFileCond.struStartTime.dwYear = cStartTime.get(Calendar.YEAR);
        dvrFileCond.struStartTime.dwMonth = cStartTime.get(Calendar.MONTH) + 1;
        dvrFileCond.struStartTime.dwDay = cStartTime.get(Calendar.DATE);
        dvrFileCond.struStartTime.dwHour = cStartTime.get(Calendar.HOUR_OF_DAY);
        dvrFileCond.struStartTime.dwMinute = cStartTime.get(Calendar.MINUTE);
        dvrFileCond.struStartTime.dwSecond = cStartTime.get(Calendar.SECOND);
        dvrFileCond.struStopTime.dwYear = cStopTime.get(Calendar.YEAR);
        dvrFileCond.struStopTime.dwMonth = cStopTime.get(Calendar.MONTH) + 1;
        dvrFileCond.struStopTime.dwDay = cStopTime.get(Calendar.DATE);
        dvrFileCond.struStopTime.dwHour = cStopTime.get(Calendar.HOUR_OF_DAY);
        dvrFileCond.struStopTime.dwMinute = cStopTime.get(Calendar.MINUTE);
        dvrFileCond.struStopTime.dwSecond = cStopTime.get(Calendar.SECOND);

        // 通道号
//        dvrFileCond.lChannel = lChannel;
        dvrFileCond.lChannel = new NativeLong(21l);
        dvrFileCond.dwUseCardNo = 0;
        dvrFileCond.dwFileType = 0xff;
        dvrFileCond.dwIsLocked = 0xff;
        // 查找录像文件
        NativeLong lFindFile = hCNetSDK.NET_DVR_FindFile(lUserId, lChannel, 0xff, dvrFileCond.struStartTime, dvrFileCond.struStopTime);

        log.info("Findfile: {}", lFindFile);
        log.info("NET_DVR_GetLastError: {}", hCNetSDK.NET_DVR_GetLastError());

        if (lFindFile.longValue() < 0) {
            log.error("Search file error: {}", hCNetSDK.NET_DVR_GetLastError());
            return;
        }
        NativeLong lNext;
        HCNetSDK.NET_DVR_FINDDATA_V30 dvrFileData = new HCNetSDK.NET_DVR_FINDDATA_V30();

        while (true) {
            // 逐个获取查找到的文件信息
            lNext = hCNetSDK.NET_DVR_FindNextFile_V30(lFindFile, dvrFileData);
            log.info("lNext: {}", lNext);
            if (lNext.longValue() == HCNetSDK.NET_DVR_FILE_SUCCESS) {
                // 搜索成功
                log.info("搜索成功！");
                // 添加文件名信息
                String[] s = new String[2];
                s = new String(dvrFileData.sFileName).split("\0", 2);

                String fileName = new String(s[0]);
                log.info("文件名：{}", fileName);

                String destFileName = deviceIp + "ch" + lChannel.longValue() + startTime.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "") + stopTime.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
                downloadFile(lUserId, fileName, destFileName);
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
                return;
            }
        }
    }

    public void downloadFile(NativeLong lUserId, String sourceFileName, String destFileName) {
        // 按文件名下载录像
        destFileName = "/opt/data/" + destFileName + ".mp4";
        NativeLong lDownloadFile = hCNetSDK.NET_DVR_GetFileByName(lUserId, sourceFileName, destFileName);
        if (lDownloadFile.longValue() < 0) {
            log.error("下载文件失败！{}", hCNetSDK.NET_DVR_GetLastError());
            return;
        }
        boolean ss = hCNetSDK.NET_DVR_PlayBackControl(lDownloadFile, HCNetSDK.NET_DVR_SET_TRANS_TYPE, 5, null);

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
