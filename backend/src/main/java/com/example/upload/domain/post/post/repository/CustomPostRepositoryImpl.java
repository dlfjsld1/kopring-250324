package com.example.upload.domain.post.post.repository;

import com.example.upload.domain.member.member.entity.Member;
import com.example.upload.domain.post.post.controller.SearchKeywordType;
import com.example.upload.domain.post.post.dto.PostListParamDto;
import com.example.upload.domain.post.post.entity.Post;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import static com.example.upload.domain.post.post.entity.QPost.post;

@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> findByParam(PostListParamDto postListParamDto, Member author, Pageable pageable) {
        BooleanBuilder builder = getParamBuilder(postListParamDto);

        if(author != null) {
            builder.and(post.author.eq(author));
        }

        return getPagedItems(pageable, builder);
    }
    @Override
    public Page<Post> findByParam(PostListParamDto postListParamDto, Pageable pageable) {
        BooleanBuilder builder = getParamBuilder(postListParamDto);
        return getPagedItems(pageable, builder);
    }

    public Page<Post> getPagedItems(Pageable pageable, BooleanBuilder builder) {
        JPAQuery<Post> postJPAQuery = queryFactory.select(post)
                .from(post)
                .where(builder);

        postJPAQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        JPAQuery<Long> totalQuery = queryFactory.select(post.count())
                .from(post)
                .where(builder);

        applySorting(pageable, postJPAQuery);

        return PageableExecutionUtils.getPage(postJPAQuery.fetch(), pageable, totalQuery::fetchOne);
    }

    public BooleanBuilder getParamBuilder(PostListParamDto postListParamDto) {
        String keyword = postListParamDto.keyword();
        SearchKeywordType keywordType = postListParamDto.keywordType();

        BooleanBuilder builder = new BooleanBuilder();
        if (postListParamDto.listed() != null) {
            builder.and(post.listed.eq(postListParamDto.listed()));
        }
        if (postListParamDto.published() != null) {
            builder.and(post.published.eq(postListParamDto.published()));
        }

        switch (keywordType) {
            case title -> builder.and(post.title.containsIgnoreCase(keyword));
            case content -> builder.and(post.content.containsIgnoreCase(keyword));
            case author -> builder.and(post.author.nickname.containsIgnoreCase(keyword));
            default -> builder.and(
                    post.title.containsIgnoreCase(keyword)
                            .or(post.content.containsIgnoreCase(keyword)));
        }

        return builder;
    }

    public void applySorting(Pageable pageable, JPAQuery<Post> postJPAQuery) {
        pageable.getSort().stream()
                .forEach(order -> {
                    PathBuilder pathBuilder = new PathBuilder<>(Post.class, "post");
                    postJPAQuery.orderBy(
                            new OrderSpecifier(order.getDirection().isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(order.getProperty())));
                });
    }
}
