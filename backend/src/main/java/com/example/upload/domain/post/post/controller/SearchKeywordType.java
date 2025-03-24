package com.example.upload.domain.post.post.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SearchKeywordType {
    title("title"),
    content("content"),
    author("author"),
    all("all");

    private final String value;
}
