package zipbap.global.domain.cookingorder

import jakarta.persistence.*
import zipbap.global.domain.base.BaseEntity
import zipbap.global.domain.recipe.Recipe

@Entity
@Table(name = "cooking_order", uniqueConstraints = [
    UniqueConstraint(columnNames = ["recipe_id", "turn"])
], indexes = [
    Index(columnList = "recipe_id")
])
class CookingOrder(

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "recipe_id", nullable = false)
        val recipe: Recipe,

        @Column(name = "image", length = 300)
        var image: String?,

        @Column(name = "description", length = 500)
        var description: String,

        @Column(name = "turn", nullable = false)
        var turn: Int,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null
) : BaseEntity(){
}