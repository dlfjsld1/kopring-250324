package com.example.upload.domain.post.post.entity;

import com.example.upload.domain.member.member.entity.Member;
import com.example.upload.domain.post.comment.entity.Comment;
import com.example.upload.domain.post.genFile.entity.PostGenFile;
import com.example.upload.global.dto.Empty;
import com.example.upload.global.dto.RsData;
import com.example.upload.global.entity.BaseTime;
import com.example.upload.global.exception.ServiceException;
import com.example.upload.standard.util.Ut;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Post extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;
    private String title;
    private String content;
    private boolean published;
    private boolean listed;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<PostGenFile> genFiles = new ArrayList<>();

    public PostGenFile addGenFile(PostGenFile.TypeCode typeCode, String filePath) {
        return addGenFile(typeCode, 0, filePath);
    }

    private PostGenFile addGenFile(PostGenFile.TypeCode typeCode, int fileNo, String filePath) {

        String originalFileName = Ut.file.getOriginalFileName(filePath);
        String fileExt = Ut.file.getFileExt(filePath);
        String fileExtTypeCode = Ut.file.getFileExtTypeCodeFromFileExt(fileExt);
        String fileExtType2Code = Ut.file.getFileExtType2CodeFromFileExt(fileExt);

        Map<String, Object> metadata = Ut.file.getMetadata(filePath);

        String metadataStr = metadata
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + "-" + entry.getValue())
                .collect(Collectors.joining(";"));

        String fileName = UUID.randomUUID() + "." + fileExt;
        long fileSize = Ut.file.getFileSize(filePath);
        fileNo = fileNo == 0 ? getNextGenFileNo(typeCode) : fileNo;

        PostGenFile genFile = PostGenFile.builder()
                .post(this)
                .typeCode(typeCode)
                .fileNo(fileNo)
                .originalFileName(originalFileName)
                .metadata(metadataStr)
                .fileDateDir(Ut.date.getCurrentDateFormatted("yyyy_MM_dd"))
                .fileExtTypeCode(fileExtTypeCode)
                .fileExtType2Code(fileExtType2Code)
                .fileExt(fileExt)
                .fileName(fileName)
                .fileSize(fileSize)
                .build();

        genFiles.add(genFile);
        Ut.file.mv(filePath, genFile.getFilePath());

        return genFile;
    }

    private int getNextGenFileNo(PostGenFile.TypeCode typeCode) {
        return genFiles.stream()
                .filter(genFile -> genFile.getTypeCode().equals(typeCode))
                .mapToInt(PostGenFile::getFileNo)
                .max()
                .orElse(0) + 1;
    }

    public Optional<PostGenFile> getGenFileByTypeCodeAndFileNo(PostGenFile.TypeCode typeCode, int fileNo) {
        return genFiles.stream()
                .filter(genFile -> genFile.getTypeCode().equals(typeCode))
                .filter(genFile -> genFile.getFileNo() == fileNo)
                .findFirst();
    }

    public void deleteGenFile(PostGenFile.TypeCode typeCode, int fileNo) {
        getGenFileByTypeCodeAndFileNo(typeCode, fileNo)
                .ifPresent(genFile -> {
                    String filePath = genFile.getFilePath();
                    genFiles.remove(genFile);
                    Ut.file.rm(filePath);
                });
    }

    public void modifyGenFile(PostGenFile.TypeCode typeCode, int fileNo, String filePath) {
        getGenFileByTypeCodeAndFileNo(
                typeCode,
                fileNo
        )
                .ifPresent(genFile -> {
                    Ut.file.rm(genFile.getFilePath());

                    String originalFileName = Ut.file.getOriginalFileName(filePath);
                    String fileExt = Ut.file.getFileExt(filePath);
                    String fileExtTypeCode = Ut.file.getFileExtTypeCodeFromFileExt(fileExt);
                    String fileExtType2Code = Ut.file.getFileExtType2CodeFromFileExt(fileExt);

                    Map<String, Object> metadata = Ut.file.getMetadata(filePath);

                    String metadataStr = metadata
                            .entrySet()
                            .stream()
                            .map(entry -> entry.getKey() + "-" + entry.getValue())
                            .collect(Collectors.joining(";"));

                    String fileName = UUID.randomUUID() + "." + fileExt;
                    long fileSize = Ut.file.getFileSize(filePath);

                    genFile.setOriginalFileName(originalFileName);
                    genFile.setMetadata(metadataStr);
                    genFile.setFileDateDir(Ut.date.getCurrentDateFormatted("yyyy_MM_dd"));
                    genFile.setFileExt(fileExt);
                    genFile.setFileExtTypeCode(fileExtTypeCode);
                    genFile.setFileExtType2Code(fileExtType2Code);
                    genFile.setFileName(fileName);
                    genFile.setFileSize(fileSize);

                    Ut.file.mv(filePath, genFile.getFilePath());
                });
    }

    public void putGenFile(PostGenFile.TypeCode typeCode, int fileNo, String filePath) {
        Optional<PostGenFile> opPostGenFile = getGenFileByTypeCodeAndFileNo(
                typeCode,
                fileNo
        );

        if (opPostGenFile.isPresent()) {
            modifyGenFile(typeCode, fileNo, filePath);
        } else {
            addGenFile(typeCode, fileNo, filePath);
        }
    }

    public Comment addComment(Member author, String content) {

        Comment comment = Comment
                .builder()
                .post(this)
                .author(author)
                .content(content)
                .build();

        comments.add(comment);

        return comment;
    }

    public Comment getCommentById(long id) {

        return comments.stream()
                .filter(comment -> comment.getId() == id)
                .findFirst()
                .orElseThrow(
                        () -> new ServiceException("404-2", "존재하지 않는 댓글입니다.")
                );
    }

    public void deleteComment(Comment comment) {
        comments.remove(comment);
    }

    public void canModify(Member actor) {
        if (actor == null) {
            throw new ServiceException("401-1", "인증 정보가 없습니다.");
        }

        if (actor.isAdmin()) return;

        if (actor.equals(this.author)) return;

        throw new ServiceException("403-1", "자신이 작성한 글만 수정 가능합니다.");
    }

    public void canDelete(Member actor) {
        if (actor == null) {
            throw new ServiceException("401-1", "인증 정보가 없습니다.");
        }

        if (actor.isAdmin()) return;

        if (actor.equals(this.author)) return;

        throw new ServiceException("403-1", "자신이 작성한 글만 삭제 가능합니다.");
    }

    public void canRead(Member actor) {
        if (actor.equals(this.author)) return;
        if (actor.isAdmin()) return;

        throw new ServiceException("403-1", "비공개 설정된 글입니다.");
    }

    public Comment getLatestComment() {
        return comments
                .stream()
                .sorted(Comparator.comparing(Comment::getId).reversed())
                .findFirst()
                .orElseThrow(
                        () -> new ServiceException("404-2", "존재하지 않는 댓글입니다.")
                );
    }

    public boolean getHandleAuthority(Member actor) {
        if (actor == null) return false;
        if (actor.isAdmin()) return true;

        return actor.equals(this.author);
    }

    public void checkActorCanMakeNewGenFile(Member actor) {
        Optional.of(
                        getCheckActorCanMakeNewGenFileRs(actor)
                )
                .filter(RsData::isFail)
                .ifPresent(rsData -> {
                    throw new ServiceException(rsData.getMsg(), rsData.getCode());
                });
    }

    public RsData<Empty> getCheckActorCanMakeNewGenFileRs(Member actor) {
        if (actor == null) return new RsData<>("401-1", "로그인 후 이용해주세요.");

        if (actor.equals(author)) return RsData.OK;

        return new RsData<>("403-1", "작성자만 파일을 업로드할 수 있습니다.");
    }

    public boolean isTemp() {
        return !published && "임시글".equals(title);
    }
}
