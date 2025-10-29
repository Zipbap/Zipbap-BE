package zipbap.global.domain.like

import jakarta.persistence.*
import zipbap.global.domain.base.BaseEntity
import zipbap.global.domain.recipe.Recipe
import zipbap.global.domain.user.User

/*
    좋아요 수 counting을 위한 index 적용입니다!
 */
@Entity
@Table(name = "recipe_like", uniqueConstraints = [
    UniqueConstraint(columnNames = ["user_id", "recipe_id"])
], indexes = [
        Index(columnList = "recipe_id")
])
class RecipeLike(

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