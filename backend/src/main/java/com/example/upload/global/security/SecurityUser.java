package com.example.upload.global.security;

import com.example.upload.domain.member.member.entity.Member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class SecurityUser extends User implements OAuth2User {


    private long id;

    public long getId() {
        return id;
    }

    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public SecurityUser(long id, String username, String password, String nickname, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.nickname = nickname;
    }

    public SecurityUser(Member member) {
        this(member.getId(), member.getUsername(), member.getPassword(), member.getNickname(), member.getAuthorities());
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public String getName() {
        return this.getUsername();
    }
}