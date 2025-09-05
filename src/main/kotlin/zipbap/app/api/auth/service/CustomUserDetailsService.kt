package zipbap.app.api.auth.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import zipbap.app.api.auth.domain.oauth2user.entity.CustomUserDetails
import zipbap.app.domain.user.UserRepository
import zipbap.app.global.code.status.ErrorStatus
import zipbap.app.global.exception.GeneralException

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
}
