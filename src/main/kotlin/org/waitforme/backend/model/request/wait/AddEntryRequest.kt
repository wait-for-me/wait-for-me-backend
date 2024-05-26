package org.waitforme.backend.model.request.wait

import kotlinx.serialization.Serializable

data class AddEntryRequest(
    val phoneNumber: String?,
    val headCount: Int = 1,
    val password: String = "",
    val entryCode: String,
) {
    fun toEntryRequest(shopId: Int, userId: Int?) =
        EntryRequest(
            phoneNumber = this.phoneNumber,
            headCount = this.headCount,
            password = this.password,
            entryCode = this.entryCode,
            userId = userId,
            shopId = shopId
        )
}

data class EntryRequest(
    val phoneNumber: String?,
    val headCount: Int = 1,
    val password: String = "",
    val entryCode: String,
    var userId: Int? = null,
    var shopId: Int = 0
)
