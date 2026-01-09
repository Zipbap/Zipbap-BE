plugins {
	// 중앙에서 버전 관리를 하면서, 중앙에는 이 플러그인들이 적용되지 않도록 apply false 적용
	// GPT 권장사항에 따라, boot 버전에 맞는 kotlin 버전 적용
	kotlin("jvm") version "2.0.10" apply false
	kotlin("plugin.spring") version "2.0.10" apply false
	kotlin("plugin.jpa") version "2.0.10" apply false
	kotlin("kapt") version "2.0.10" apply false

	// querydsl 호환성을 위한 추가
	id("com.google.devtools.ksp") version "2.0.10-1.0.24" apply false

	id("org.springframework.boot") version "3.5.5" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
	group = "zipbap"
	version = "0.1.0"
	repositories { mavenCentral() }
}

subprojects {
	plugins.withType<JavaPlugin> {
		extensions.configure<JavaPluginExtension> {
			toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
		}
	}
	plugins.withId("org.jetbrains.kotlin.jvm") {
		extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
			jvmToolchain(17)
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}


