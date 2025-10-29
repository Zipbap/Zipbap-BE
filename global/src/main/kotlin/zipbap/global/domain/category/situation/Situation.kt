package zipbap.global.domain.category.situation

import jakarta.persistence.*
import zipbap.global.domain.base.BaseEntity

@Entity
@Table(name = "category_situation")
class Situation(

        @Column(name = "situation", nullable = false,
                length = 50, unique = true)
        var situation: String,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null
) : BaseEntity()
