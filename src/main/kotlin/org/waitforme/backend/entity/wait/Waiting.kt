package org.waitforme.backend.entity.wait

import org.waitforme.backend.common.BaseEntity
import org.waitforme.backend.enums.EntryStatus
import java.time.LocalDateTime
import javax.persistence.*

@Table(name = "shop")
@Entity
data class Waiting(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val userId: Int = 0,
    val phoneNumber: String? = null,
    val shopId: Int = 0,
    val orderNo: Int = 0,
    val headCount: Int = 1,
    var callCount: Int = 0,
    @Enumerated(value = EnumType.STRING)
    var status: EntryStatus = EntryStatus.WAIT,
    var enteredAt: LocalDateTime? = null
) : BaseEntity() {
    fun updateStatus(status: EntryStatus) {
        this.status = status
        enteredAt = LocalDateTime.now()
    }
}
