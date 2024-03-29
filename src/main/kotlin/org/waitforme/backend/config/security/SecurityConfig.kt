package org.waitforme.backend.config.security

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler,
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    private val swaggerPatterns = arrayOf(
        "/swagger-resources/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/v3/api-docs",
    )

    private val allowedUrls =
        arrayOf(
            "/",
            "/v1/admin/sign-up",
            "/v1/admin/login",
            "/v1/auth/**",
            "/v1/notice/**",
        ) + swaggerPatterns

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): DefaultSecurityFilterChain {
        return http
            .csrf().disable()
            .headers { it.frameOptions().sameOrigin() } // H2 콘솔 사용을 위한 설정
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // 세션을 사용하지 않으므로 STATELESS 설정
            .authorizeHttpRequests {
                it.requestMatchers(*convertToRequestMatchers(allowedUrls)).permitAll() // 인자로 전달된 url은 모두에게 허용
                it.requestMatchers(PathRequest.toH2Console()).permitAll() // H2 콘솔 접속은 모두에게 허용
                    .anyRequest().authenticated() // 그 외의 모든 요청은 인증 필요
            }
            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)
            .and()
            .addFilterBefore(
                jwtAuthenticationFilter,
                BasicAuthenticationFilter::class.java,
            ) // security 로직에 Jwt 인증 Filter 등록
            .build()
    }

    private fun convertToRequestMatchers(paths: Array<String>): Array<AntPathRequestMatcher> {
        return paths.map { AntPathRequestMatcher(it) }.toTypedArray()
    }
}
