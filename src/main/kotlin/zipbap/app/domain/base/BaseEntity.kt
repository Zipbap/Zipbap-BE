package zipbap.api.domain.base

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(

        /* ERD 상에는 not null이지만, 객체 생성시에는 null 이여야 할 것 같아서 nullable로
        생성했습니다. */
        @CreatedDate
        @Column(name = "created_at", nullable = false, updatable = false,
                columnDefinition = "TIMESTAMP")
        var createdAt: Instant? = null,

        @LastModifiedDate
        @Column(name = "updated_at",
                columnDefinition = "TIMESTAMP")
        var updatedAt: Instant? = null,

        @Column(name = "deleted_at",
                columnDefinition = "TIMESTAMP")
        var deletedAt: Instant? = null
) {
}