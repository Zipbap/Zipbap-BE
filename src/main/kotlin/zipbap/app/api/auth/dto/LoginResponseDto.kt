package zipbap.app.api.auth.dto

data class LoginResponseDto(
        val accessToken: String,
        val refreshToken: String
)
