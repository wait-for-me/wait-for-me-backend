package org.waitforme.backend.entity.notice

import org.waitforme.backend.common.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Table(name = "notice")
@Entity
data class Notice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    var title: String,
    var contents: String,
    var isDeleted: Boolean = false,
    var deletedAt: LocalDateTime? = null,
) : BaseEntity() {

    fun getNoticeCreatedAt(): LocalDateTime {
        return createdAt
    }
}
