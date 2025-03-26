package com.example.upload.global.init;

import com.example.upload.domain.member.member.entity.Member;
import com.example.upload.domain.member.member.service.MemberService;
import com.example.upload.domain.post.genFile.entity.PostGenFile;
import com.example.upload.domain.post.post.entity.Post;
import com.example.upload.domain.post.post.service.PostService;
import com.example.upload.global.app.AppConfig;
import com.example.upload.standard.util.SampleResource;
import com.example.upload.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final PostService postService;
    private final MemberService memberService;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            self.memberInit();
            self.postInit();
        };
    }

    @Transactional
    public void memberInit() {

        if(memberService.count() > 0) {
            return;
        }

        if (AppConfig.isTest()) {
            Ut.file.rm(AppConfig.getGenFileDirPath());
        }

        // 회원 샘플데이터 생성
        memberService.join("system", "system1234", "시스템", "");
        memberService.join("admin", "admin1234", "관리자", "");
        memberService.join("user1", "user11234", "유저1", "");
        memberService.join("user2", "user21234", "유저2", "");
        memberService.join("user3", "user31234", "유저3", "");

    }

    @Transactional
    public void postInit() {

        if(postService.count() > 0) {
            return;
        }

        Member user1 = memberService.findByUsername("user1").get();
        Member user2 = memberService.findByUsername("user2").get();

        Post post1 = postService.write(user1, "축구 하실분 모집합니다.", "저녁 6시까지 모여주세요.", true, true);
        post1.addComment(user1, "저 참석하겠습니다.");
        post1.addComment(user2, "공격수 자리 있나요?");

        Post post2 = postService.write(user1, "농구하실분?", "3명 모집", true, false);
        post2.addComment(user1, "저는 이미 축구하기로 함..");

        postService.write(user2, "title3", "content3", false, true);
        postService.write(user1, "title4", "content4", true, true);
        postService.write(user1, "title5", "content5", true, true);
        postService.write(user2, "title6", "content6", true, true);
        postService.write(user2, "title7", "content7", true, true);
        postService.write(user2, "title8", "content8", true, true);
        postService.write(user2, "title9", "content9", true, true);

        String genFile1FilePath = SampleResource.IMG_GIF_SAMPLE1.makeCopy();
        post1.addGenFile(PostGenFile.TypeCode.attachment, genFile1FilePath);

        String genFile2FilePath = SampleResource.IMG_GIF_SAMPLE1.makeCopy();
        post1.addGenFile(PostGenFile.TypeCode.attachment, genFile2FilePath);

        String genFile3FilePath = SampleResource.IMG_GIF_SAMPLE1.makeCopy();
        post1.addGenFile(PostGenFile.TypeCode.thumbnail, genFile3FilePath);

        post1.deleteGenFile(PostGenFile.TypeCode.attachment, 2);

        Post post10 = postService.write(
                user2,
                "테니스 하실 분있나요?",
                "테니스 강력 추천합니다.",
                true,
                true
        );

        String genFile4FilePath = SampleResource.IMG_WEBP_SAMPLE1.makeCopy();
        post10.addGenFile(PostGenFile.TypeCode.attachment, genFile4FilePath);

        String genFile5FilePath = SampleResource.AUDIO_M4A_SAMPLE1.makeCopy();
        post10.addGenFile(PostGenFile.TypeCode.attachment, genFile5FilePath);

        String genFile6FilePath = SampleResource.AUDIO_MP3_SAMPLE1.makeCopy();
        post10.addGenFile(PostGenFile.TypeCode.attachment, genFile6FilePath);

        String genFile7FilePath = SampleResource.AUDIO_MP3_SAMPLE2.makeCopy();
        post10.addGenFile(PostGenFile.TypeCode.attachment, genFile7FilePath);

        String genFile8FilePath = SampleResource.VIDEO_MOV_SAMPLE1.makeCopy();
        post10.addGenFile(PostGenFile.TypeCode.attachment, genFile8FilePath);

        String genFile9FilePath = SampleResource.VIDEO_MP4_SAMPLE1.makeCopy();
        post10.addGenFile(PostGenFile.TypeCode.attachment, genFile9FilePath);

        String genFile10FilePath = SampleResource.VIDEO_MP4_SAMPLE2.makeCopy();
        post10.addGenFile(PostGenFile.TypeCode.attachment, genFile10FilePath);

        for(int i = 10; i <= 100; i++) {
            postService.write(user1, "title" + i, "content" + i, i % 2 != 0, i % 3 != 0);
        }

        for(int i = 101; i <= 200; i++) {
            postService.write(user2, "title" + i, "content" + i, i % 4 != 0, i % 5 != 0);
        }


    }



}
