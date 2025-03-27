package com.example.upload.domain.post.post.dto

import com.example.upload.standard.search.SearchKeywordType

@JvmRecord
data class PostListParamDto(
    val keywordType: SearchKeywordType,
    val keyword: String,
    val listed: Boolean?,
    val published: Boolean?,
    val page: Int?,
    val pageSize: Int?
) {
    fun getPage(): Int {
        return page ?: 0
    }

    fun getPageSize(): Int {
        return pageSize ?: 10
    }
}
