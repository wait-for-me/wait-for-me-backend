package org.waitforme.backend.model.response.auth

data class CheckNameResponse(
    val isUnique: Boolean
)

fun Boolean.toCheckNameResponse() = CheckNameResponse(
    isUnique = !this
)