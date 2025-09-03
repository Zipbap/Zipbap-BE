package zipbap.api.domain.category.level

import jakarta.persistence.*
import zipbap.api.domain.base.BaseEntity

@Entity
@Table(name = "category_level")
class Level(

        @Column(name = "level", nullable = false,
                length = 50, unique = true)
        var level: String,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null
) : BaseEntity()
