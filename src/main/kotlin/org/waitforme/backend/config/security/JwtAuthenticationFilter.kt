package org.waitforme.backend.config.security

import io.jsonwebtoken.io.IOException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationFilter(private val jwtTokenProvider: JwtTokenProvider) : GenericFilterBean() {

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpServletRequest = request as HttpServletRequest
        val httpServletResponse = response as HttpServletResponse
        val requestURI = httpServletRequest.requestURI

        val jwt = jwtTokenProvider.resolveToken(request)

        // 유효한 토큰인지 확인
        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateAccessToken(jwt)) {
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옴.
            val authentication: Authentication = jwtTokenProvider.getAuthentication(jwt)

            // SecurityContext 에 Authentication 객체 (인증 정보)를 저장.
            SecurityContextHolder.getContext().authentication = authentication
            logger.info("[${request.method}] $requestURI - Security Context에 '${authentication.name}' 인증 정보를 저장했습니다")
        } else {
            logger.info("[${request.method}] $requestURI - 유효한 토큰 정보가 없습니다.")
        }

        chain?.doFilter(request, response)
    }
}
