package com.example.upload.domain.post.genFile.controller;


import com.example.upload.domain.member.member.service.MemberService;
import com.example.upload.domain.post.genFile.entity.PostGenFile;
import com.example.upload.domain.post.post.entity.Post;
import com.example.upload.domain.post.post.service.PostService;
import com.example.upload.global.app.AppConfig;
import com.example.upload.standard.util.SampleResource;
import com.example.upload.standard.util.Ut;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiV1PostGenFileControllerTest {
    @Autowired
    private PostService postService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("다건 조회")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/1/genFiles")
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostGenFileController.class))
                .andExpect(handler().methodName("items"))
                .andExpect(status().isOk());

        List<PostGenFile> postGenFiles = postService
                .getItem(1).get().getGenFiles();

        for (int i = 0; i < postGenFiles.size(); i++) {
            PostGenFile postGenFile = postGenFiles.get(i);
            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(postGenFile.getId()))
                    .andExpect(jsonPath("$[%d].createDate".formatted(i)).value(Matchers.startsWith(postGenFile.getCreatedDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].modifyDate".formatted(i)).value(Matchers.startsWith(postGenFile.getModifiedDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].postId".formatted(i)).value(postGenFile.getPost().getId()))
                    .andExpect(jsonPath("$[%d].typeCode".formatted(i)).value(postGenFile.getTypeCode().toString()))
                    .andExpect(jsonPath("$[%d].fileExtTypeCode".formatted(i)).value(postGenFile.getFileExtTypeCode()))
                    .andExpect(jsonPath("$[%d].fileExtType2Code".formatted(i)).value(postGenFile.getFileExtType2Code()))
                    .andExpect(jsonPath("$[%d].fileSize".formatted(i)).value(postGenFile.getFileSize()))
                    .andExpect(jsonPath("$[%d].fileNo".formatted(i)).value(postGenFile.getFileNo()))
                    .andExpect(jsonPath("$[%d].fileExt".formatted(i)).value(postGenFile.getFileExt()))
                    .andExpect(jsonPath("$[%d].fileDateDir".formatted(i)).value(postGenFile.getFileDateDir()))
                    .andExpect(jsonPath("$[%d].originalFileName".formatted(i)).value(postGenFile.getOriginalFileName()))
                    .andExpect(jsonPath("$[%d].downloadUrl".formatted(i)).value(postGenFile.getDownloadUrl()))
                    .andExpect(jsonPath("$[%d].publicUrl".formatted(i)).value(postGenFile.getPublicUrl()))
                    .andExpect(jsonPath("$[%d].fileName".formatted(i)).value(postGenFile.getFileName()));
        }
    }

    @Test
    @DisplayName("새 파일 등록")
    @WithUserDetails("user2")
    void t2() throws Exception {
        String newFilePath = Ut.file.downloadByHttp("https://picsum.photos/id/237/200/300", AppConfig.getTempDirPath());

        ResultActions resultActions = mvc
                .perform(
                        multipart("/api/v1/posts/9/genFiles/" + PostGenFile.TypeCode.attachment)
                                .file(new MockMultipartFile("files", SampleResource.IMG_JPG_SAMPLE1.getOriginalFileName(), SampleResource.IMG_JPG_SAMPLE1.getContentType(), new FileInputStream(newFilePath)))
                )
                .andDo(print());

        Post post = postService.getItem(9).get();
        System.out.println(post.getGenFiles().size());
        List<PostGenFile> genFiles = post.getGenFiles();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostGenFileController.class))
                .andExpect(handler().methodName("makeNewItems"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value("1개의 파일이 생성되었습니다."))
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].createDate").isString())
                .andExpect(jsonPath("$.data[0].modifyDate").isString())
                .andExpect(jsonPath("$.data[0].postId").value(9))
                .andExpect(jsonPath("$.data[0].typeCode").value(PostGenFile.TypeCode.attachment.name()))
                .andExpect(jsonPath("$.data[0].fileExtTypeCode").value(SampleResource.IMG_JPG_SAMPLE1.getFileExtTypeCode()))
                .andExpect(jsonPath("$.data[0].fileExtType2Code").value(SampleResource.IMG_JPG_SAMPLE1.getFileExtType2Code()))
                .andExpect(jsonPath("$.data[0].fileSize").isNumber())
                .andExpect(jsonPath("$.data[0].fileNo").value(1))
                .andExpect(jsonPath("$.data[0].fileExt").value(SampleResource.IMG_JPG_SAMPLE1.getFileExt()))
                .andExpect(jsonPath("$.data[0].fileDateDir").isString())
                .andExpect(jsonPath("$.data[0].originalFileName").value(SampleResource.IMG_JPG_SAMPLE1.getOriginalFileName()))
                .andExpect(jsonPath("$.data[0].downloadUrl").isString())
                .andExpect(jsonPath("$.data[0].publicUrl").isString())
                .andExpect(jsonPath("$.data[0].fileName").isString());

        Ut.file.rm(newFilePath);
    }

    @Test
    @DisplayName("새 파일 등록(다건)")
    @WithUserDetails("user2")
    void t4() throws Exception {
        String newFilePath1 = SampleResource.IMG_JPG_SAMPLE1.makeCopy();
        String newFilePath2 = SampleResource.IMG_JPG_SAMPLE2.makeCopy();

        ResultActions resultActions = mvc
                .perform(
                        multipart("/api/v1/posts/9/genFiles/" + PostGenFile.TypeCode.attachment)
                                .file(new MockMultipartFile("files", SampleResource.IMG_JPG_SAMPLE1.getOriginalFileName(), SampleResource.IMG_JPG_SAMPLE1.getContentType(), new FileInputStream(newFilePath1)))
                                .file(new MockMultipartFile("files", SampleResource.IMG_JPG_SAMPLE2.getOriginalFileName(), SampleResource.IMG_JPG_SAMPLE2.getContentType(), new FileInputStream(newFilePath2)))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostGenFileController.class))
                .andExpect(handler().methodName("makeNewItems"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201-1"))
                .andExpect(jsonPath("$.msg").value("2개의 파일이 생성되었습니다."))
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].createDate").isString())
                .andExpect(jsonPath("$.data[0].modifyDate").isString())
                .andExpect(jsonPath("$.data[0].postId").value(9))
                .andExpect(jsonPath("$.data[0].typeCode").value(PostGenFile.TypeCode.attachment.name()))
                .andExpect(jsonPath("$.data[0].fileExtTypeCode").value(SampleResource.IMG_JPG_SAMPLE1.getFileExtTypeCode()))
                .andExpect(jsonPath("$.data[0].fileExtType2Code").value(SampleResource.IMG_JPG_SAMPLE1.getFileExtType2Code()))
                .andExpect(jsonPath("$.data[0].fileSize").isNumber())
                .andExpect(jsonPath("$.data[0].fileNo").value(1))
                .andExpect(jsonPath("$.data[0].fileExt").value(SampleResource.IMG_JPG_SAMPLE1.getFileExt()))
                .andExpect(jsonPath("$.data[0].fileDateDir").isString())
                .andExpect(jsonPath("$.data[0].originalFileName").value(SampleResource.IMG_JPG_SAMPLE1.getOriginalFileName()))
                .andExpect(jsonPath("$.data[0].downloadUrl").isString())
                .andExpect(jsonPath("$.data[0].publicUrl").isString())
                .andExpect(jsonPath("$.data[0].fileName").isString())
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[1].createDate").isString())
                .andExpect(jsonPath("$.data[1].modifyDate").isString())
                .andExpect(jsonPath("$.data[1].postId").value(9))
                .andExpect(jsonPath("$.data[1].typeCode").value(PostGenFile.TypeCode.attachment.name()))
                .andExpect(jsonPath("$.data[1].fileExtTypeCode").value(SampleResource.IMG_JPG_SAMPLE2.getFileExtTypeCode()))
                .andExpect(jsonPath("$.data[1].fileExtType2Code").value(SampleResource.IMG_JPG_SAMPLE2.getFileExtType2Code()))
                .andExpect(jsonPath("$.data[1].fileSize").isNumber())
                .andExpect(jsonPath("$.data[1].fileNo").value(2))
                .andExpect(jsonPath("$.data[1].fileExt").value(SampleResource.IMG_JPG_SAMPLE2.getFileExt()))
                .andExpect(jsonPath("$.data[1].fileDateDir").isString())
                .andExpect(jsonPath("$.data[1].originalFileName").value(SampleResource.IMG_JPG_SAMPLE2.getOriginalFileName()))
                .andExpect(jsonPath("$.data[1].downloadUrl").isString())
                .andExpect(jsonPath("$.data[1].publicUrl").isString())
                .andExpect(jsonPath("$.data[1].fileName").isString());

        Ut.file.rm(newFilePath1);
        Ut.file.rm(newFilePath2);
    }
}