package zipbap.api.domain.category.mainingredient

import jakarta.persistence.*
import zipbap.api.domain.base.BaseEntity

@Entity
@Table(name = "category_main_ingredient")
class MainIngredient(

        @Column(name = "ingredient", nullable = false,
                length = 50, unique = true)
        var ingredient: String,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null
) : BaseEntity() {
}