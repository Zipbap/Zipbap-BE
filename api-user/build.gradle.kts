plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

// Local comparison lab: no production server, DB, AWS credentials or Apple account required.
tasks.register<Test>("securityLab") {
    description = "Show before/after Apple verification and authenticated user binding"
    group = "verification"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    filter { includeTestsMatching("zipbap.user.security.lab.SecurityLabTest") }
    systemProperty("lab.targetId", providers.gradleProperty("labTargetId").getOrElse("456"))
    testLogging { showStandardStreams = true; events("passed", "failed") }
    outputs.upToDateWhen { false }
}

dependencies {
    implementation(project(":global"))

    // Web & Swagger
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.0")

    // Security (필요 정책에 맞춰 resource-server 또는 client 선택)
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    // 또는
    // implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")



    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")



    // 💡 MockK 기본 라이브러리 (단위 테스트용)
    testImplementation("io.mockk:mockk:1.13.10")

    // 실제 MariaDB를 사용하는 격리된 테스트 컨테이너
    testImplementation("org.testcontainers:mariadb")
    testImplementation("org.springframework.security:spring-security-test")


    // 💡 api-user 테스트 코드가 global 모듈의 testFixtures를 가져다 사용
    testImplementation(testFixtures(project(":global")))

    // 💡 (선택/추천) Spring Boot 통합 테스트 환경(@SpringBootTest)에서
    // 스프링 빈(Bean)을 가짜로 교체하는 @MockkBean을 쓰려면 아래 의존성도 같이 넣어주세요!
    testImplementation("com.ninja-squad:springmockk:4.0.2")
}
