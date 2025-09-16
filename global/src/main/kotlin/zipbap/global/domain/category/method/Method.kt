package zipbap.global.domain.category.method

import jakarta.persistence.*
import zipbap.global.domain.base.BaseEntity

@Entity
@Table(name = "category_method")
class Method(

        @Column(name = "method", nullable = false,
                length = 50, unique = true)
        var method: String,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null
) : BaseEntity()
