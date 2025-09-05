package zipbap.app.api.user.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zipbap.app.domain.user.SocialType
import zipbap.app.domain.user.User
import zipbap.app.domain.user.UserRepository

@Service
@Transactional(readOnly = true)
class UserService(
        private val userRepository: UserRepository
) {

    fun isUserExists(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    @Transactional
    fun register(registrationId: String, username: String, email: String) {
        if (isUserExists(email)) return

        val Social = SocialType.valueOf(registrationId.uppercase())
        val user = User(
                email = email,
                nickname = username,
                socialType = Social
        )

        userRepository.save(user)
    }
}