package org.waitforme.backend.config.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationFilter(private val jwtTokenProvider: JwtTokenProvider) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val requestURI = request.requestURI
        val token = jwtTokenProvider.resolveToken(request)

        // 유효한 토큰인지 확인
        if (StringUtils.hasText(token) && jwtTokenProvider.validateAccessToken(token)) {
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옴.
            val authentication = jwtTokenProvider.getAuthentication(token)

            // SecurityContext 에 Authentication 객체 (인증 정보)를 저장.
            SecurityContextHolder.getContext().authentication = authentication
            logger.info("[${request.method}] $requestURI - Security Context에 '${authentication.name}' 인증 정보를 저장했습니다")
        } else {
            logger.info("[${request.method}] $requestURI - 유효한 토큰 정보가 없습니다.")
        }

        filterChain.doFilter(request, response)
    }
}
