package zipbap.global.global.auth.domain.token.entity

import jakarta.persistence.*
import zipbap.global.domain.base.BaseEntity
import zipbap.global.domain.user.User

@Entity
class RefreshToken(

        @OneToOne
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,

        @Column(nullable = false)
        var refreshToken: String,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null
) : BaseEntity() {

    fun updateToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    companion object {
        fun createRefreshToken(user: User, refreshToken: String): RefreshToken {
            return RefreshToken(user, refreshToken)
        }
    }
}
