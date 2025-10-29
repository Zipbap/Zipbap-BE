package zipbap.global.domain.category.cookingtype

import jakarta.persistence.*
import zipbap.global.domain.base.BaseEntity

@Entity
@Table(name = "category_cooking_type")
class CookingType(
        @Column(name = "type", nullable = false, length = 50, unique = true)
        val type: String,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null

) : BaseEntity()
