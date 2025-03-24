package com.example.upload.domain.post.genFile.controller;

import com.example.upload.domain.member.member.entity.Member;
import com.example.upload.domain.post.genFile.dto.PostGenFileDto;
import com.example.upload.domain.post.genFile.entity.PostGenFile;
import com.example.upload.domain.post.post.entity.Post;
import com.example.upload.domain.post.post.service.PostService;
import com.example.upload.global.Rq;
import com.example.upload.global.app.AppConfig;
import com.example.upload.global.dto.RsData;
import com.example.upload.global.exception.ServiceException;
import com.example.upload.standard.util.Ut;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/v1/posts/{postId}/genFiles")
@RequiredArgsConstructor
@Tag(name = "ApiV1PostGenFileController", description = "API 글 파일 컨트롤러")
@SecurityRequirement(name = "bearerAuth")
public class ApiV1PostGenFileController {

    private final PostService postService;
    private final Rq rq;

    @PostMapping(value = "/{typeCode}", consumes = MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "다건등록")
    @Transactional
    public RsData<List<PostGenFileDto>> makeNewItems(
            @PathVariable long postId,
            @PathVariable PostGenFile.TypeCode typeCode,
            @Parameter(
                    description = "업로드할 파일 목록",
                    content = @Content(
                            mediaType = "multipart/form-data"
                    )
            )
            @NonNull @RequestPart("files") MultipartFile[] files
    ) {
        Member actor = rq.getActor();

        Post post = postService.getItem(postId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(postId))
        );

        post.checkActorCanMakeNewGenFile(actor);

        List<PostGenFile> postGenFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String filePath = Ut.file.toFile(file, AppConfig.getTempDirPath());

            postGenFiles.add(
                    post.addGenFile(
                            typeCode,
                            filePath
                    ));
        }

        postService.flush();

        return new RsData<>(
                "201-1",
                "%d개의 파일이 생성되었습니다.".formatted(postGenFiles.size()),
                postGenFiles.stream().map(PostGenFileDto::new).toList()
        );
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건조회")
    public List<PostGenFileDto> items(
            @PathVariable long postId
    ) {
        Post post = postService.getItem(postId).orElseThrow(
                () -> new ServiceException("404-1", "%d번 글은 존재하지 않습니다.".formatted(postId))
        );

        return post
                .getGenFiles()
                .stream()
                .map(PostGenFileDto::new)
                .toList();
    }
}