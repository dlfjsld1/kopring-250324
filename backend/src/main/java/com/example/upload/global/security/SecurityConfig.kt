package com.example.upload.global.security

import com.example.upload.global.app.AppConfig
import com.example.upload.global.dto.Empty
import com.example.upload.global.dto.RsData
import com.example.upload.standard.util.Ut
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

// @Configuration: 이 클래스가 스프링의 설정 클래스임을 나타내며, Bean 등록에 사용됩니다.
@Configuration
// @EnableWebSecurity: Spring Security를 활성화합니다.
@EnableWebSecurity
class SecurityConfig(
    // SecurityConfig 클래스의 생성자에서 의존성 주입을 통해 커스텀 필터와 핸들러들을 받습니다.
    private val customAuthenticationFilter: CustomAuthenticationFilter,
    private val customAuthorizationRequestResolver: CustomAuthorizationRequestResolver,
    private val customAuthenticationSuccessHandler: CustomAuthenticationSuccessHandler
) {

    // Bean으로 등록되는 SecurityFilterChain을 정의합니다.
    @Bean
    fun securityFilterChain(http: org.springframework.security.config.annotation.web.builders.HttpSecurity): SecurityFilterChain {
        http {
            // HTTP 요청에 대한 접근 권한 설정
            authorizeHttpRequests {
                // "/h2-console/**" 경로는 모두 접근을 허용합니다.
                authorize("/h2-console/**", permitAll)
                // GET 메서드로 게시글 상세보기, 목록, 댓글, 파일 관련 엔드포인트는 모두 접근을 허용합니다.
                authorize(HttpMethod.GET, "/api/*/posts/{id:\\d+}", permitAll)
                authorize(HttpMethod.GET, "/api/*/posts", permitAll)
                authorize(HttpMethod.GET, "/api/*/posts/{postId:\\d+}/comments", permitAll)
                authorize(HttpMethod.GET, "/api/*/posts/{postId:\\d+}/genFiles", permitAll)
                // 로그인, 회원가입, 로그아웃 경로는 모두 접근을 허용합니다.
                authorize("/api/*/members/login", permitAll)
                authorize("/api/*/members/join", permitAll)
                authorize("/api/*/members/logout", permitAll)
                // "/api/v1/posts/statistics" 경로는 ADMIN 권한이 있어야 접근할 수 있습니다.
                authorize("/api/v1/posts/statistics", hasRole("ADMIN"))
                // "/api/*/**" 경로는 인증된 사용자만 접근할 수 있습니다.
                authorize("/api/*/**", authenticated)
                // 그 외의 모든 요청은 접근을 허용합니다.
                authorize(anyRequest, permitAll)
            }

            // HTTP 응답 헤더 설정
            headers {
                // 동일 출처 정책을 사용하여 frame 옵션을 sameOrigin으로 설정합니다.
                frameOptions {
                    sameOrigin = true
                }
            }

            // CSRF 보호 비활성화 (상태 비저장(stateless) API의 경우 일반적으로 사용)
            csrf { disable() }

            // 세션 관리를 STATELESS로 설정하여 서버에 세션을 생성하지 않습니다.
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            // OAuth2 로그인 설정
            oauth2Login {
                // 인증 성공 시 커스텀 핸들러를 사용
                authenticationSuccessHandler = customAuthenticationSuccessHandler
                // 인증 요청을 처리할 때 사용할 커스텀 요청 리졸버 설정
                authorizationEndpoint {
                    authorizationRequestResolver = customAuthorizationRequestResolver
                }
            }

            // 커스텀 인증 필터를 UsernamePasswordAuthenticationFilter 전에 추가합니다.
            addFilterBefore<UsernamePasswordAuthenticationFilter>(customAuthenticationFilter)

            // 예외 처리 설정
            exceptionHandling {
                // 인증 실패 시 실행되는 로직 (401 에러 처리)
                authenticationEntryPoint = AuthenticationEntryPoint { request, response, authException ->
                    response.contentType = "application/json;charset=UTF-8"
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.writer.write(
                        // JSON 형식의 응답 메시지 출력 (인증키 오류)
                        Ut.json.toString(RsData<Empty>("401-1", "잘못된 인증키입니다."))
                    )
                }

                // 접근 거부 시 실행되는 로직 (403 에러 처리)
                accessDeniedHandler = AccessDeniedHandler { request, response, accessDeniedException ->
                    response.contentType = "application/json;charset=UTF-8"
                    response.status = HttpServletResponse.SC_FORBIDDEN
                    response.writer.write(
                        // JSON 형식의 응답 메시지 출력 (접근 권한 없음)
                        Ut.json.toString(RsData<Empty>("403-1", "접근 권한이 없습니다."))
                    )
                }
            }
        }

        // 설정된 HttpSecurity 객체를 기반으로 SecurityFilterChain을 생성합니다.
        return http.build()
    }

    // CORS 설정을 위한 Bean 정의
    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        // CorsConfiguration 객체 생성 및 설정
        val configuration = CorsConfiguration().apply {
            // 허용할 오리진 (출처) 설정
            allowedOrigins = listOf("https://cdpn.io", AppConfig.getSiteFrontUrl())
            // 허용할 HTTP 메서드 설정
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
            // 자격 증명(쿠키 등) 허용 여부 설정
            allowCredentials = true
            // 허용할 HTTP 헤더 설정 (모든 헤더 허용)
            allowedHeaders = listOf("*")
        }

        // UrlBasedCorsConfigurationSource에 CORS 설정을 등록
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/**", configuration)
        }
    }
}