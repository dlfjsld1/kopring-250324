package com.example.upload.standard.util;

import com.example.upload.global.app.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class FileUtTest {

    @Test
    @DisplayName("downloadByHttp")
    public void t1() {
        String newFilePath = Ut.file.downloadByHttp("https://picsum.photos/id/237/200/300", AppConfig.getTempDirPath());

        // newFilePath 의 확장자가 jpg 인지 확인
        assertThat(newFilePath).endsWith(".jpg");

        Ut.file.delete(newFilePath);
    }

    @Test
    @DisplayName("getExtensionByTika")
    void tika() {
        String newFilePath = Ut.file.downloadByHttp("https://picsum.photos/id/237/200/300", AppConfig.getTempDirPath());

        String ext = Ut.file.getExtensionByTika(newFilePath);
        assertThat(ext).isEqualTo("jpg");

        Ut.file.delete(newFilePath);
    }
}