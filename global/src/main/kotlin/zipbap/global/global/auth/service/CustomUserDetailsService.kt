package zipbap.global.global.auth.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import zipbap.global.domain.user.User
import zipbap.global.domain.user.UserRepository
import zipbap.global.global.auth.domain.userdetails.CustomUserDetails

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    /**
     * 유저의 email을 기반으로 user를 찾는다.
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByEmail(username).orElse(null)?.
                let {
                    CustomUserDetails(it)
                } ?: throw UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다.")
    }

    fun loadUserByUsername(user: User): UserDetails {
        return CustomUserDetails(user)
    }
}
