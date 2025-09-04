package zipbap.app.domain.category.cookingtype

import jakarta.persistence.*
import zipbap.api.domain.base.BaseEntity

@Entity
@Table(name = "category_cooking_type")
class CookingType(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(name = "type", nullable = false,
                length = 50, unique = true)
        val type: String

) : BaseEntity() {
}