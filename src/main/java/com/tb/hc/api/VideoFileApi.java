package com.tb.hc.api;

import com.sun.jna.NativeLong;
import com.tb.hc.sdk.HCNetSDK;
import com.tb.hc.util.HCNetSDKUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;

@Slf4j
@RestController
@RequestMapping(value = "/videoFileApi")
public class VideoFileApi {
    static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

    private final String filePath = "";

    @GetMapping(value = "/downloadFile")
    public void downloadVideo(@RequestParam(value = "channel") long lChannel) {
        HCNetSDKUtils sdkUtils = new HCNetSDKUtils();

        sdkUtils.init();
        NativeLong lUserId = sdkUtils.login();
        if (lUserId.longValue() == -1) {
            log.error("注册失败！{}", hCNetSDK.NET_DVR_GetLastError());
            hCNetSDK.NET_DVR_Cleanup();
            return;
        }
        sdkUtils.searchAndDownloadFile(lUserId, new NativeLong(lChannel));
    }

    @GetMapping(value = "/mp4/{fileName}")
    public void getVideo(@PathParam(value = "fileName") String fileName, HttpServletResponse response) {
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            String diskfilename = "final.mp4";
            response.setContentType("video/mp4");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + diskfilename + "\"");
            System.out.println("data.length " + data.length);
            response.setContentLength(data.length);
            response.setHeader("Content-Range", "" + Integer.valueOf(data.length - 1));
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Etag", "W/\"9767057-1323779115364\"");
            OutputStream os = response.getOutputStream();

            os.write(data);
            //先声明的流后关掉！
            os.flush();
            os.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
