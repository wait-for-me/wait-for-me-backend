package org.waitforme.backend.enums

enum class UserRole(role: String) {
    ADMIN("ROLE_ADMIN"), // 관리자
    OWNER("ROLE_OWNER"), // 점주
    USER("ROLE_USER"), // 사용자
}
