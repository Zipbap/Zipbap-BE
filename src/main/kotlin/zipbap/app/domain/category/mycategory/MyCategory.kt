package zipbap.app.domain.category.mycategory

import jakarta.persistence.*
import zipbap.app.domain.base.BaseEntity
import zipbap.app.domain.user.User

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
        @Column(name = "id", length = 30)
        val id: String

) : BaseEntity() {
}