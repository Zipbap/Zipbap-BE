package zipbap.global.domain.user

import jakarta.persistence.*
import zipbap.global.domain.base.BaseEntity
import zipbap.global.domain.comment.Comment
import zipbap.global.domain.recipe.Recipe
import zipbap.global.domain.bookmark.Bookmark
import zipbap.global.domain.category.mycategory.MyCategory
import zipbap.global.domain.like.RecipeLike
import zipbap.global.domain.follow.Follow
import zipbap.global.domain.file.FileEntity
import zipbap.global.global.auth.domain.token.entity.RefreshToken

@Entity
class User(

        @Column(name = "email", unique = true, nullable = false, length = 100)
        val email: String,

        @Column(name = "nickname", nullable = false, length = 30)
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


        /**
         *  댓글 삭제 (유저 → 댓글)
         */
        @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
        val comments: MutableList<Comment> = mutableListOf()

        /**
         *  레시피 삭제 (유저 → 레시피)
         */
        @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
        val recipes: MutableList<Recipe> = mutableListOf()

        /**
         *  레시피 좋아요 삭제
         */
        @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
        val likes: MutableList<RecipeLike> = mutableListOf()

        /**
         *  북마크 삭제
         */
        @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
        val bookmarks: MutableList<Bookmark> = mutableListOf()

        /**
         *  팔로우 관계 삭제 (내가 누군가를 팔로우한 관계)
         */
        @OneToMany(mappedBy = "follower", cascade = [CascadeType.REMOVE], orphanRemoval = true)
        val following: MutableList<Follow> = mutableListOf()

        /**
         *  팔로우 관계 삭제 (누가 나를 팔로우한 관계)
         */
        @OneToMany(mappedBy = "following", cascade = [CascadeType.REMOVE], orphanRemoval = true)
        val followers: MutableList<Follow> = mutableListOf()

        /**
         * 파일 레코드 삭제 (S3는 그대로, row만 삭제)
         */
        @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
        val files: MutableList<FileEntity> = mutableListOf()

        @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
        val myCategories: MutableList<MyCategory> = mutableListOf()


        fun update(nickname:String, isPrivate: Boolean, profileImage: String?, statusMessage: String?) {
                this.nickname = nickname
                this.isPrivate = isPrivate
                this.profileImage = profileImage
                this.statusMessage = statusMessage
        }
}
