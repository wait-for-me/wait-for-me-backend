package org.waitforme.backend.util

import java.security.InvalidParameterException

object StringUtil {
    fun validateRegistrationNumber(registrationNumber: String): Boolean {
        val checkNumber = registrationNumber.replace("-", "")
        if (checkNumber.length != 10) throw InvalidParameterException(
            "사업자 등록 번호는 10자리 숫자입니다."
        )
        if (checkNumber.find { !it.isDigit() } != null) {
            throw InvalidParameterException(
                "사업자 등록번호는 숫자로만 이뤄져야 합니다."
            )
        }

        var checkSum = 0
        val validateList = listOf(1, 3, 7, 1, 3, 7, 1, 3, 5)
        checkNumber.forEachIndexed { index, c ->
            checkSum += c.digitToInt() * validateList[index]
        }

        checkSum += (checkNumber[8].digitToInt() * 5).floorDiv(10)
        return checkNumber.last() == checkSum.floorDiv(10).toChar()
    }
}