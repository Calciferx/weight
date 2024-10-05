package com.calcifer.weight.service;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.LibraryLoader;
import com.jacob.com.Variant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class VoiceService {
    private static final String LIB_FILE = System.getProperty("os.arch").equals("amd64") ? "jacob-1.20-x64.dll" : "jacob-1.20-x86.dll";
    private File temporaryDll;

    private ActiveXComponent sapi;

    @Value("${calcifer.weight.enable-voice:true}")
    private boolean enableVoice;

    static {
        log.info("JACOB lib path: {}", LIB_FILE);
        System.setProperty("com.jacob.debug", "true");
    }

    @PostConstruct
    public void init() throws IOException {
        if (!enableVoice) return;
        ClassPathResource classPathResource = new ClassPathResource(LIB_FILE);
        InputStream inputStream = classPathResource.getInputStream();
        temporaryDll = File.createTempFile("jacob", ".dll");
        FileOutputStream outputStream = new FileOutputStream(temporaryDll);
        byte[] array = new byte[8192];
        for (int i = inputStream.read(array); i != -1; i = inputStream.read(array)) {
            outputStream.write(array, 0, i);
        }
        outputStream.close();

        System.setProperty(LibraryLoader.JACOB_DLL_PATH, temporaryDll.getAbsolutePath());
        LibraryLoader.loadJacobLibrary();
        log.info("JACOB library is loaded and ready to use");
        sapi = new ActiveXComponent("sapi.SpVoice");
    }

    @PreDestroy
    public void clean() {
        if (!enableVoice) return;
        sapi.safeRelease();
        temporaryDll.deleteOnExit();
        log.info("Temporary DLL API library is cleaned on exit");
    }


    public void voice(String content) {
        if (!enableVoice) return;
        try {
            // 调节语速 音量大小
            sapi.setProperty("Volume", new Variant(100));
            sapi.setProperty("Rate", new Variant(1));

            Dispatch spVoice = sapi.getObject();
            // 执行朗读
            Dispatch.call(spVoice, "Speak", new Variant(content));
        } catch (Exception e) {
            log.info("voice exception", e);
            sapi.safeRelease();
        }
    }
}
