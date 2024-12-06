package com.calcifer.weight.service;

import com.calcifer.weight.utils.LPRSDK;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Slf4j
//@Service
public class PlateReaderService {
    private LPRSDK lprsdk = null;
    private static final String LIB_FILE = System.getProperty("os.arch").equals("amd64") ? "SDK64" : "SDK86";

    @Value("${calcifer.weight.enable-plate-reader:true}")
    private boolean enablePlateReader;

    static {
        log.info("VzLPRSDK lib path: {}", LIB_FILE);
    }

    @PostConstruct
    public void init() throws IOException {
        if (!enablePlateReader) return;
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 获取当前类的资源
        Resource resource = resolver.getResource("file:" + LIB_FILE + "/VzLPRSDK.dll");
        // 获取文件绝对路径
        File file = resource.getFile();
        lprsdk = (LPRSDK) Native.load(file.getAbsolutePath(), LPRSDK.class);
        int initResult = lprsdk.VzLPRClient_Setup();
        System.out.println("initResult: " + initResult);
        int handle = lprsdk.VzLPRClient_Open("192.168.1.100", 80, "admin", "admin");
        int callbackindex = lprsdk.VzLPRClient_SetPlateInfoCallBack(handle,
                new LPRSDK.VZLPRC_PLATE_INFO_CALLBACK() {
                    @Override
                    public void invoke(int handle, Pointer pUserData, LPRSDK.TH_PlateResult_Pointer.ByReference pResult, int uNumPlates, int eResultType, LPRSDK.VZ_LPRC_IMAGE_INFO_Pointer.ByReference pImgFull, LPRSDK.VZ_LPRC_IMAGE_INFO_Pointer.ByReference pImgPlateClip) {
                        int type = LPRSDK.VZ_LPRC_RESULT_TYPE.VZ_LPRC_RESULT_REALTIME.ordinal();
                        if (true) {
                            // 根据设备句柄，获取当前回调结果设备的IP
                            byte[] device_ip = new byte[32];
                            lprsdk.VzLPRClient_GetDeviceIP(handle, device_ip, 32);

                            String ip = new String(device_ip);
                            System.out.println("device ip:" + ip);

                            try {
                                String license = new String(pResult.license, "gb2312");
                                System.out.println(license);
                            } catch (UnsupportedEncodingException e) {
                                System.out.println("exception msg:" + e.getMessage());
                            }
                        }
                    }
                }
                , Pointer.NULL, 1);
        System.out.println(callbackindex);
    }

    @PreDestroy
    public void clean() {
        if (!enablePlateReader) return;
        lprsdk.VzLPRClient_Cleanup();
        log.info("Temporary DLL API library is cleaned on exit");
    }


    public void voice(String content) {
        if (!enablePlateReader) return;

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 获取当前类的资源
        Resource resource = resolver.getResource("file:" + LIB_FILE + "/VzLPRSDK.dll");
        // 获取文件绝对路径
        File file = resource.getFile();
        LPRSDK lprsdk = (LPRSDK) Native.load(file.getAbsolutePath(), LPRSDK.class);
        int initResult = lprsdk.VzLPRClient_Setup();
        System.out.println("initResult: " + initResult);
//        int handle = lprsdk.VzLPRClient_Open("192.168.1.100", 80, "admin", "admin");
//        int findResult = lprsdk.VZLPRClient_StartFindDeviceEx(new LPRSDK.VZLPRC_FIND_DEVICE_CALLBACK_EX() {
//            @Override
//            public void invoke(String pStrDevName, String pStrIPAddr, short usPort1, short usType, long SL, long SH, String netmask, String gateway, Pointer pUserData) {
//                System.out.println(pStrDevName);
//                System.out.println(pStrIPAddr);
//                System.out.println(usPort1);
//                System.out.println(usType);
//                System.out.println(SL);
//                System.out.println(SH);
//                System.out.println(netmask);
//                System.out.println(gateway);
//                System.out.println(pUserData);
//
//            }
//        }, null);
//        System.out.println("findResult: "+ findResult);

        int handle = lprsdk.VzLPRClient_Open("192.168.1.100", 80, "admin", "admin");


        int callbackindex = lprsdk.VzLPRClient_SetPlateInfoCallBack(handle,
                new LPRSDK.VZLPRC_PLATE_INFO_CALLBACK() {
                    int count = 0;

                    @Override
                    public void invoke(int handle, Pointer pUserData, LPRSDK.TH_PlateResult_Pointer.ByReference pResult, int uNumPlates, int eResultType, LPRSDK.VZ_LPRC_IMAGE_INFO_Pointer.ByReference pImgFull, LPRSDK.VZ_LPRC_IMAGE_INFO_Pointer.ByReference pImgPlateClip) {
                        int type = LPRSDK.VZ_LPRC_RESULT_TYPE.VZ_LPRC_RESULT_REALTIME.ordinal();

                        if (true) {
                            count++;
                            System.out.println(eResultType);
                            // 根据设备句柄，获取当前回调结果设备的IP
                            byte[] device_ip = new byte[32];
                            lprsdk.VzLPRClient_GetDeviceIP(handle, device_ip, 32);

                            String ip = new String(device_ip);
                            System.out.println("device ip:" + ip + "count: " + count);

                            try {
                                String license = new String(pResult.license, "gb2312");
                                System.out.println(license);
                            } catch (UnsupportedEncodingException e) {
                                System.out.println("exception msg:" + e.getMessage());
                            }
                        }
                    }
                }
                , Pointer.NULL, 1);
        System.out.println(callbackindex);
        while (true) {
            byte[] state = new byte[1];
            lprsdk.VzLPRClient_IsConnected(handle, state);
            System.out.println("state: " + state[0]);

            lprsdk.VzLPRClient_ForceTrigger(handle);
            Thread.sleep(1000);
        }
    }
}
