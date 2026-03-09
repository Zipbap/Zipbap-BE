plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

dependencies {
    implementation(project(":global"))

    // Web & Swagger
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.0")

    // Security (필요 정책에 맞춰 resource-server 또는 client 선택)
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    // 또는
    // implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")


    // JDBC 드라이버: 앱 모듈에 둬서 연결 계정/권한 분리 유연화
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")



    // 💡 MockK 기본 라이브러리 (단위 테스트용)
    testImplementation("io.mockk:mockk:1.13.10")

    // 테스트용 h2
    testImplementation("com.h2database:h2")


    // 💡 api-user 테스트 코드가 global 모듈의 testFixtures를 가져다 사용
    testImplementation(testFixtures(project(":global")))

    // 💡 (선택/추천) Spring Boot 통합 테스트 환경(@SpringBootTest)에서
    // 스프링 빈(Bean)을 가짜로 교체하는 @MockkBean을 쓰려면 아래 의존성도 같이 넣어주세요!
    testImplementation("com.ninja-squad:springmockk:4.0.2")
}
