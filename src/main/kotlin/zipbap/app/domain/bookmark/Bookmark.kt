package zipbap.api.domain.bookmark

import jakarta.persistence.*
import zipbap.api.domain.base.BaseEntity
import zipbap.api.domain.recipe.Recipe
import zipbap.api.domain.user.User

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
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null
) : BaseEntity() {
}