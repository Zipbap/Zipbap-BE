package zipbap.global.domain.category.headcount

import jakarta.persistence.*
import zipbap.global.domain.base.BaseEntity

@Entity
@Table(name = "category_headcount")
class Headcount(

        @Column(name = "headcount", nullable = false,
                length = 50, unique = true)
        var headcount: String,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null
) : BaseEntity()
