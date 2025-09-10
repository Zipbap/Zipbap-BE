package zipbap.app.domain.base

import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import org.springframework.data.domain.Persistable

@MappedSuperclass
abstract class StringIdBaseEntity(
) : BaseEntity(), Persistable<String> {

    @Transient
    override fun isNew(): Boolean {
        return this.createdAt == null
    }

}