package com.example.upload.domain.post.post.dto;

import com.example.upload.domain.post.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageDto {

    @NonNull
    List<PostDto> items;
    @NonNull
    int totalPages;
    @NonNull
    int totalItems;
    @NonNull
    int currentPageNo;
    @NonNull
    int pageSize;

    public PageDto(Page<Post> postPage) {

        this.items = postPage.getContent().stream()
                .map(PostDto::new)
                .toList();

        this.totalPages = postPage.getTotalPages();
        this.totalItems = (int) postPage.getTotalElements();
        this.currentPageNo = postPage.getNumber() + 1;
        this.pageSize = postPage.getSize();
    }

}
