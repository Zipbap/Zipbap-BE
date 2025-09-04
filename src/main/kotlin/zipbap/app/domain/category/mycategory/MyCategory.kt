package zipbap.api.domain.category.mycategory

import jakarta.persistence.*
import zipbap.api.domain.base.BaseEntity
import zipbap.api.domain.user.User

@Entity
@Table(name = "my_category",
        uniqueConstraints = [
            UniqueConstraint(columnNames = ["user_id", "name"])
])
class MyCategory(

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,

        @Column(nullable = false, length = 20)
        var name: String,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null

) : BaseEntity() {
}