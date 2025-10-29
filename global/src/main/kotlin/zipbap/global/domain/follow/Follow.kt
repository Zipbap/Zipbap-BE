package zipbap.global.domain.follow

import jakarta.persistence.*
import zipbap.global.domain.base.BaseEntity
import zipbap.global.domain.user.User

@Entity
@Table(uniqueConstraints = [
    UniqueConstraint(columnNames = ["follower", "following"])
], indexes = [
        Index(columnList = "following")
])
class Follow(

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "following", nullable = false)
        val following: User,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "follower", nullable = false)
        val follower: User,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null
) : BaseEntity() {
}