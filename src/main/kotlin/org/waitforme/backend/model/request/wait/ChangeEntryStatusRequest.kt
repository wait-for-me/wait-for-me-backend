package org.waitforme.backend.model.request.wait

import io.swagger.v3.oas.annotations.media.Schema
import org.waitforme.backend.enums.EntryStatus

@Schema(description = "점주의 대기 유저에 대한 상태 변경 요청")
data class ChangeEntryStatusRequest(
    @Schema(description = "유저 아이디")
    val phoneNumber: String,
    @Schema(description = "가게 아이디")
    val shopId: Int,
    @Schema(description = "대기 유저에 대해 적용할 상태값")
    val entryStatus: EntryStatus,
)
