plugins {
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    // spring-boot plugin은 적용하지 않음 (라이브러리 모듈)

    // ✅ KSP 사용
    id("com.google.devtools.ksp")
}

dependencyManagement {
    imports {
        // 루트에서 쓰는 Spring Boot 버전에 맞춰 주세요
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.5")
        // (옵션) Spring Cloud/AWS를 쓴다면 대응 BOM도 함께
        // Boot 3.x 계열에서는 아래 중 하나만 선택해 사용하세요

        // 1) Spring Cloud 전체 BOM (필요할 때만)
        // mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")

        // 2) AWS Spring Cloud 3.x (권장: 구버전 starter-aws 대신)
        // mavenBom("io.awspring.cloud:spring-cloud-aws-dependencies:3.1.0")
    }
}

    dependencies {
        // 공통
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        // Spring Web
        implementation("org.springframework.boot:spring-boot-starter-web")

        // 보안 베이스(공통)
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
        // JWT (공통)
        implementation("io.jsonwebtoken:jjwt-api:0.11.5")
        runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
        runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

        // 데이터 계층
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        // ⚠️ JDBC 드라이버는 애플리케이션 모듈(api-*)에 두는 걸 권장. (여기선 뺌)

        // Querydsl (APT는 여기서!)
        val querydslVersion = "7.1"
        implementation("io.github.openfeign.querydsl:querydsl-jpa:$querydslVersion")
        ksp("io.github.openfeign.querydsl:querydsl-ksp-codegen:$querydslVersion")

        // Swagger/OpenAPI (springdoc 2.x, Spring Boot 3.x 호환)
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.0")

        // 로그
        implementation("net.logstash.logback:logstash-logback-encoder:8.1")

        // (선택) AWS 공통 어댑터가 정말 global에 필요하다면 아래를 두되,
        // Boot 3.x와의 호환성 이슈가 있는 구버전임에 유의
        implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")
    }

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
}

 kotlin {
     sourceSets.main {
         kotlin.srcDir("build/generated/ksp/main/kotlin")
     }
 }

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
