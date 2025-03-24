package com.example.upload.global.security;

import com.example.upload.domain.member.member.entity.Member;
import com.example.upload.domain.member.member.service.MemberService;
import com.example.upload.global.Rq;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Rq rq;
    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();

        String redirectUrl = (String)session.getAttribute("redirectUrl");
        if(redirectUrl == null) {
            redirectUrl = "http://localhost:3000";
        }
        session.removeAttribute("redirectUrl");
        Member actor = rq.getRealActor(rq.getActor());
        String accessToken = memberService.genAccessToken(actor);

        rq.addCookie("accessToken", accessToken);
        rq.addCookie("apiKey", actor.getApiKey());

        response.sendRedirect(redirectUrl);
    }
}
