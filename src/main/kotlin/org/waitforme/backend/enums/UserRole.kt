package org.waitforme.backend.enums

enum class UserRole(val authorities: List<String>) {
    ADMIN(listOf("ROLE_ADMIN", "ROLE_OWNER", "ROLE_USER")), // 관리자
    OWNER(listOf("ROLE_OWNER", "ROLE_USER")), // 점주
    USER(listOf("ROLE_USER")), // 사용자
}
