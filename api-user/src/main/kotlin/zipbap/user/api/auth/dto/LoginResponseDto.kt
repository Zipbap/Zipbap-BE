package zipbap.user.api.auth.dto

data class LoginResponseDto(
        val accessToken: String,
        val refreshToken: String
)
