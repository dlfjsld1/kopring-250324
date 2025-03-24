package com.example.upload.domain.post.post.dto;

import com.example.upload.domain.post.post.entity.Post;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Getter
public class PostDto {

    @NonNull
    private long id;
    @NonNull
    private LocalDateTime createdDate;
    @NonNull
    private LocalDateTime modifiedDate;
    @NonNull
    private String title;
    @NonNull
    private long authorId;
    @NonNull
    private String authorName;
    @NonNull
    private boolean published;
    @NonNull
    private boolean listed;

    public PostDto(Post post) {
        this.id = post.getId();
        this.createdDate = post.getCreatedDate();
        this.modifiedDate = post.getModifiedDate();
        this.title = post.getTitle();
        this.authorId = post.getAuthor().getId();
        this.authorName = post.getAuthor().getNickname();
        this.published = post.isPublished();
        this.listed = post.isListed();
    }
}
