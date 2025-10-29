package zipbap.global.domain.base

import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import org.springframework.data.domain.Persistable
import zipbap.global.domain.base.BaseEntity

@MappedSuperclass
abstract class StringIdBaseEntity(
) : BaseEntity(), Persistable<String> {

    @Transient
    override fun isNew(): Boolean {
        return this.createdAt == null
    }

}