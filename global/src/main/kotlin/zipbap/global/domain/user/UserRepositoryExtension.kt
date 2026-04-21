package zipbap.global.domain.user

import org.springframework.data.repository.findByIdOrNull
import zipbap.global.global.code.status.ErrorStatus
import zipbap.global.global.exception.GeneralException

fun UserRepository.findByIdOrThrow(id: Long): User {
    return this.findByIdOrNull(id)
        ?: throw GeneralException(ErrorStatus.USER_NOT_FOUND)
}