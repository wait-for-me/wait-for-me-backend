package org.waitforme.backend.entity.wait

import org.waitforme.backend.common.BaseEntity
import org.waitforme.backend.enums.EntryStatus
import java.time.LocalDateTime
import javax.persistence.*

@Table(name = "waiting")
@Entity
data class Waiting(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    var userId: Int? = 0,
    var phoneNumber: String? = null,
    var password: String? = null,
    val entryCode: String,
    val shopId: Int = 0,
    val orderNo: Int = 0,
    var headCount: Int? = 1,
    var callCount: Int = 0,
    @Enumerated(value = EnumType.STRING)
    var status: EntryStatus = EntryStatus.DEFAULT,
    var enteredAt: LocalDateTime? = null,
    var canceledAt: LocalDateTime? = null,
) : BaseEntity() {
    fun updateStatus(status: EntryStatus) {
        this.status = status
        enteredAt = LocalDateTime.now()
    }

    fun cancel() {
        this.status = EntryStatus.CANCELED
        canceledAt = LocalDateTime.now()

    }

    fun update(userId: Int?, phoneNumber: String?, password: String?, headCount: Int?) {
        userId?.let { this.userId = it }
        phoneNumber?.let { this.phoneNumber = it }
        password?.let { this.password = it }
        headCount?.let { this.headCount = it }
        this.status = EntryStatus.WAIT
    }

    fun getWaitingUpdatedAt(): LocalDateTime {
        return updatedAt
    }
}
