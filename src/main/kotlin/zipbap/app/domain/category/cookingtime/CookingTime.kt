package zipbap.api.domain.category.cookingtime

import jakarta.persistence.*
import zipbap.api.domain.base.BaseEntity

@Entity
@Table(name = "category_cooking_time")
class CookingTime(

        @Column(name = "cooking_time", nullable = false,
                length = 50, unique = true)
        var cookingTime: String,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null
) : BaseEntity()
