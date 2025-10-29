package zipbap.global.domain.bookmark

import jakarta.persistence.*
import zipbap.global.domain.base.StringIdBaseEntity
import zipbap.global.domain.recipe.Recipe
import zipbap.global.domain.user.User

/*
    개인 북마크 조회, 북마크 count query 시 index가 있으면 좋을거 같아서 추가했습니다.
 */
@Entity
@Table(name = "bookmark", uniqueConstraints = [
    UniqueConstraint(columnNames = ["user_id", "recipe_id"])
], indexes = [Index(columnList = "recipe_id")])
class Bookmark(

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "recipe_id", nullable = false)
        val recipe: Recipe,

        @Id
        @Column(name = "id", length = 40)
        val bookmarkId: String, // BM-{userId}-{sequence}
) : StringIdBaseEntity() {
        override fun getId(): String {
                return this.bookmarkId
        }
}