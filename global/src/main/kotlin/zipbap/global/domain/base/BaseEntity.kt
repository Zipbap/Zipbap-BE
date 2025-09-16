package zipbap.global.domain.base

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(

        /**
         * 생성 시각 (INSERT 시 자동)
         * not null 제약이 있으나,
         * 객체 생성 시점에는 null 허용
         */
        @CreatedDate
        @Column(name = "created_at", nullable = false, updatable = false,
                columnDefinition = "TIMESTAMP")
        var createdAt: LocalDateTime? = null,

        /**
         * 수정 시점 (자동 입력)
         */
        @LastModifiedDate
        @Column(name = "updated_at",
                columnDefinition = "TIMESTAMP")
        var updatedAt: LocalDateTime? = null,

        /**
         * 삭제 시점 (Soft Delete 시 기록)
         */
        @Column(name = "deleted_at",
                columnDefinition = "TIMESTAMP")
        var deletedAt: LocalDateTime? = null,
) {
        /**
         * Soft Delete 실행
         */
        fun softDelete() {
                this.deletedAt = LocalDateTime.now()
        }

        /**
         * 삭제 여부 확인
         */
        fun isDeleted(): Boolean = this.deletedAt != null
}