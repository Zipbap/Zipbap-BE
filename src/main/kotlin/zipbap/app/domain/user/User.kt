package zipbap.app.domain.user

import jakarta.persistence.*
import zipbap.app.domain.base.BaseEntity

@Entity
class User(

        @Column(name = "email", unique = true,
                nullable = false, length = 100)
        val email: String,

        @Column(name = "nickname", nullable = false,
                length = 30)
        var nickname: String,

        @Enumerated(EnumType.STRING)
        @Column(name = "social_type", nullable = false)
        val socialType: SocialType,

        @Column(name = "is_private", nullable = false)
        var isPrivate: Boolean = false,

        @Column(name = "profile_image")
        var profileImage: String? = null,

        @Column(name = "status_message", length = 100)
        var statusMessage: String? = null,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null
) : BaseEntity() {

        fun update(nickname:String, isPrivate: Boolean,
                   profileImage: String?, statusMessage: String?) {
                this.nickname = nickname
                this.isPrivate = isPrivate
                this.profileImage = profileImage
                this.statusMessage = statusMessage
        }
}