package zipbap.global.domain.file

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import zipbap.global.domain.base.BaseEntity
import zipbap.global.domain.recipe.Recipe
import zipbap.global.domain.user.User

@Entity
@Table(name = "file")
class FileEntity(

        @Column(nullable = false)
    val fileUrl: String, // S3 접근 가능한 전체 URL

        @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    var status: FileStatus = FileStatus.TEMPORARY_UPLOAD,

        @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    var recipe: Recipe? = null,

        @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
        @OnDelete(action = OnDeleteAction.CASCADE)
    var user: User? = null,

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity()
