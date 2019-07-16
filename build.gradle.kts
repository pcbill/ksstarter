import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("plugin.jpa") version "1.2.71"
	id("org.springframework.boot") version "2.1.6.RELEASE"
	id("io.spring.dependency-management") version "1.0.7.RELEASE"
	kotlin("jvm") version "1.2.71"
	kotlin("plugin.spring") version "1.2.71"
}

group = "tw.pcbill"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
	runtimeClasspath {
		extendsFrom(developmentOnly)
	}
}

repositories {
	mavenCentral()
}



var profiles = "prod"
if (project.hasProperty("dev")) {
	profiles = "dev"
}

tasks.bootRun {
	args = arrayOf("--spring.profiles.active=" + profiles).toMutableList()
}


dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	if (!project.hasProperty("no_sec")) {
		implementation("org.springframework.boot:spring-boot-starter-security")
		implementation("io.jsonwebtoken:jjwt:0.9.1")
	}


	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("org.liquibase:liquibase-core")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	if (project.hasProperty("dev")) {
		runtimeOnly("com.h2database:h2")
	} else {
		runtimeOnly("org.postgresql:postgresql")
	}

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
