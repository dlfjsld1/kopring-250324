package com.example.upload.global.security;

import com.example.upload.domain.member.member.entity.Member
import com.example.upload.domain.member.member.service.MemberService
import com.example.upload.global.Rq
import com.example.upload.global.app.AppConfig
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationSuccessHandler(
    private val  rq: Rq,
    private val  memberService: MemberService
): AuthenticationSuccessHandler {



    override fun onAuthenticationSuccess(request: HttpServletRequest,  response: HttpServletResponse,  authentication: Authentication) {
        val session: HttpSession = request.session

        var redirectUrl = session.getAttribute("redirectUrl") as String

        if (redirectUrl.isBlank()) {
            redirectUrl = AppConfig.getSiteBackUrl()
        }

        session.removeAttribute("redirectUrl")

        val actor: Member = rq.getRealActor(rq.actor)
        val accessToken = memberService.genAccessToken(actor)

        rq.addCookie("accessToken", accessToken)
        rq.addCookie("apiKey", actor.apiKey)

        response.sendRedirect(redirectUrl)
    }
}
