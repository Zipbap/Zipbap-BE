package zipbap.api.domain.comment

import jakarta.persistence.*
import zipbap.api.domain.base.BaseEntity
import zipbap.api.domain.recipe.Recipe
import zipbap.api.domain.user.User

@Entity
@Table(name = "comment", indexes = [
    Index(columnList = "recipe_id"),
    Index(columnList = "user_id"),
        Index(columnList = "parent_id")
])
class Comment(

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "recipe_id", nullable = false)
        val recipe: Recipe,

        @Column(name = "content", nullable = false,
                length = 300)
        var content: String,

        @OneToMany(mappedBy = "parent")
        val children: MutableList<Comment> = mutableListOf(),

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "parent_id")
        val parent: Comment? = null,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null
) : BaseEntity() {
}