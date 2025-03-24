package com.example.upload.domain.post.post.dto;

import com.example.upload.domain.post.post.controller.SearchKeywordType;
import lombok.Builder;

@Builder
public record PostListParamDto(
        SearchKeywordType keywordType,
        String keyword,
        Boolean listed,
        Boolean published,
        Integer pageSize,
        Integer page

) {

    public int getPage() {
        return page == null ? 0 : page;
    }

    public int getPageSize() {
        return pageSize == null ? 10 : pageSize;
    }
}
