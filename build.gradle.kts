plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "zipbap"
version = "0.0.1-SNAPSHOT"
description = "Zipbap API Application"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Web
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Kotlin 지원
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// AWS SDK
	implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

	// JPA & Hibernate
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// validation
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.0")


	// MariaDB Driver (JDBC 연결)
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

	// 테스트 (Spring Boot Test + JUnit5)
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
