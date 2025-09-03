package zipbap.app.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
                .addServersItem(Server().url("http://localhost:8080")) // local용
                .addServersItem(Server().url("https://zipbap.store")) // 배포 서버용
                /*
                    이전 프로젝트에서 ssl 연동시, www 없는 API는 스웨거 사용이 안되는 문제가 있어서
                    따로 추가하였습니다.
                 */
                .addServersItem(Server().url("https://www.zipbap.store"))
                .components(Components()
                        .addSecuritySchemes(BEARER_KEY,
                                SecurityScheme()
                                        .name(BEARER_KEY)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT") // UI에 표기용
                        )
                )
                .addSecurityItem(SecurityRequirement().addList(BEARER_KEY))
                .info(apiInfo())
    }

    private fun apiInfo(): Info {
        return Info()
                .title("Zipbap")
                .description("집밥 스웨거입니다.")
                .version("1.0.1")
    }

    companion object {
        private const val BEARER_KEY = "bearerAuth"
    }
}
