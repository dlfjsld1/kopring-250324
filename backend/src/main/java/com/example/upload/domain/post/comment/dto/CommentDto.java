package com.example.upload.domain.post.comment.dto;

import com.example.upload.domain.post.comment.entity.Comment;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Getter
public class CommentDto {

    @NonNull
    private long id;
    @NonNull
    private String content;
    @NonNull
    private long postId;
    @NonNull
    private long authorId;
    @NonNull
    private String authorName;
    @NonNull
    private LocalDateTime createdTime;
    @NonNull
    private LocalDateTime modifiedTime;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.postId = comment.getPost().getId();
        this.authorId = comment.getAuthor().getId();
        this.authorName = comment.getAuthor().getNickname();
        this.createdTime = comment.getCreatedDate();
        this.modifiedTime = comment.getModifiedDate();
    }

}
