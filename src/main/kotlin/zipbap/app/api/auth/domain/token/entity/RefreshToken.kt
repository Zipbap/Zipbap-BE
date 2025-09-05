package zipbap.app.api.auth.domain.token.entity

import jakarta.persistence.*
import zipbap.app.domain.base.BaseEntity
import zipbap.app.domain.user.User

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
