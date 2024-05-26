package org.waitforme.backend.common

import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class UserUtil {

    // nickname
    fun generateRandomName(): String {
        return adjectives.random() + " 팝" + generateRandomString()
    }

    fun checkValidName(name: String): Boolean {
        val pattern = Regex("^[a-zA-Z0-9]{2,10}$")
        return pattern.matches(name)
    }

    private fun generateRandomString(length: Int = 5): String {
        // 사용할 문자열 설정 (알파벳 대소문자와 숫자)
        val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val stringBuilder = StringBuilder()

        // 무작위로 문자 선택하여 조합
        repeat(length) {
            val randomIndex = Random.nextInt(characters.length)
            stringBuilder.append(characters[randomIndex])
        }

        return stringBuilder.toString()
    }

    private val adjectives = listOf(
        "기쁜",
        "벅찬",
        "포근한",
        "흐뭇한",
        "상쾌한",
        "짜릿한",
        "시원한",
        "반가운",
        "후련한",
        "살맛나는",
        "신나버린",
        "아늑한",
        "흥분되는",
        "온화한",
        "안전한",
        "느긋한",
        "끝내주는",
        "날아가는",
        "괜찮은",
        "쌈박한",
        "정다운",
        "그리운",
        "화사한",
        "자유로운",
        "따사로운",
        "감미로운",
        "황홀한",
        "상큼한",
        "평화로운",
        "활기찬",
        "힘찬",
        "생생한",
        "의기양양한",
        "든든한",
        "격렬한",
        "열렬한",
        "당당한",
        "팔팔한",
        "엄청난",
        "자신만만한",
        "패기만만한",
    )
}
