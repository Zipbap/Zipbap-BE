package zipbap.app.admin.auth.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import zipbap.app.api.auth.domain.token.service.TokenService
import zipbap.app.api.user.service.UserService
import zipbap.app.global.ApiResponse

/**
 * 테스트 용도로 사용할 계정 생성하기 위해 만든 class
 * 기능 제한에 대한 검토 필요
 */
@RestController
@RequestMapping("/admin/auth")
class AuthAdminController(
        private val userService: UserService,
        private val tokenService: TokenService
) {

    @PostMapping("/user")
    fun addTestUser(@RequestParam email: String,
                    @RequestParam nickname: String): ApiResponse<String> {
        userService.register("KAKAO", nickname, email)
        return ApiResponse.onSuccess("Successfully Registered!!!")
    }

    @GetMapping("/access-token")
    fun getAccessToken(@RequestParam email: String) =
        ApiResponse.onSuccess(tokenService.generateAccessToken(email))



}