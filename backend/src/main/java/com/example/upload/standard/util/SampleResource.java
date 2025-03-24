package com.example.upload.standard.util;

import com.example.upload.global.app.AppConfig;
import lombok.Getter;

@Getter
public enum SampleResource {
    AUDIO_M4A_SAMPLE1("sample1-728s.m4a", 0, 0, 728),
    AUDIO_MP3_SAMPLE1("sample1-42s.mp3", 0, 0, 42),
    AUDIO_MP3_SAMPLE2("sample2-9s.mp3", 0, 0, 9),
    IMG_GIF_SAMPLE1("sample1-150x189.gif", 150, 189, 0),
    IMG_JPG_SAMPLE1("sample1-200x300.jpg", 200, 300, 0),
    IMG_JPG_SAMPLE2("sample2-300x300.jpg", 300, 300, 0),
    IMG_JPG_SAMPLE3("sample3-400x300.jpg", 400, 300, 0),
    IMG_JPG_SAMPLE4("sample4-500x500.jpg", 500, 500, 0),
    IMG_JPG_SAMPLE5("sample5-200x300.jpg", 200, 300, 0),
    IMG_WEBP_SAMPLE1("sample1-1280x531.webp", 1280, 531, 0),
    VIDEO_MOV_SAMPLE1("sample1-1280x720x319s.mov", 1280, 720, 319),
    VIDEO_MP4_SAMPLE1("sample1-640x480x5s.mp4", 640, 480, 5),
    VIDEO_MP4_SAMPLE2("sample2-1280x720x117s.mp4", 1280, 720, 117);

    private final String fileExtTypeCode;
    private final String fileExtType2Code;
    private final String fileExt; // 확장자 (mp3, jpg 등)
    private final String fileName;  // 파일명
    private final int width;
    private final int height;
    private final int duration;

    SampleResource(String fileName, int width, int height, int duration) {
        String fileExt = Ut.file.getFileExt(fileName);
        String fileExtTypeCode = Ut.file.getFileExtTypeCodeFromFileExt(fileExt);
        String fileExtType2Code = Ut.file.getFileExtType2CodeFromFileExt(fileExt);

        this.fileExtTypeCode = fileExtTypeCode;
        this.fileExtType2Code = fileExtType2Code;
        this.fileExt = fileExt;
        this.fileName = fileName;
        this.width = width;
        this.height = height;
        this.duration = duration;
    }

    public String getFilePath() {
        return AppConfig.getResourcesSampleDirPath() + "/" + getFileExtTypeCode() + "/" + getFileExtType2Code() + "/" + getFileName();
    }

    public String makeCopy() {
        String newFilePath = AppConfig.getTempDirPath() + "/" + getFileName();
        Ut.file.copy(getFilePath(), newFilePath);

        return newFilePath;
    }

    public String getOriginalFileName() {
        return getFileName();
    }

    public String getContentType() {
        return Ut.file.getContentType(getFileExt());
    }
}