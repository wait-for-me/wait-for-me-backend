package org.waitforme.backend.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private const val SECURITY_SCHEME_NAME = "authorization"

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .components(
            Components().addSecuritySchemes(
                SECURITY_SCHEME_NAME,
                SecurityScheme()
                    .name(SECURITY_SCHEME_NAME)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT"),
            ),
        )
        .addSecurityItem(SecurityRequirement().addList(SECURITY_SCHEME_NAME))
        .info(
            apiInfo()
                .title("Wait-For-Me API Docs")
                .description("웨잇포미 API 문서입니다")
                .version("1.0.0"),
        )

    private fun apiInfo() = Info()
        .title("Springdoc 테스트")
        .description("Springdoc을 사용한 Swagger UI 테스트")
        .version("1.0.0")
}
