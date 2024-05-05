import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.17"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
	kotlin("kapt") version "1.7.10" // 1
}

group = "org.wait-for-me"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

val queryDSLVersion = "5.0.0"


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.11")

	implementation ("com.google.code.gson:gson:2.8.7")
	implementation ("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")

    implementation("org.springframework.boot:spring-boot-starter-security")

	// Querydsl 설정
	implementation("com.querydsl:querydsl-jpa:$queryDSLVersion") // 2
	kapt("com.querydsl:querydsl-apt:$queryDSLVersion:jpa") // 1
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("javax.persistence:javax.persistence-api:2.2")
	annotationProcessor(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa")
	implementation("com.querydsl:querydsl-apt:$queryDSLVersion")

    // validation
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // sms api
    implementation("net.nurigo:sdk:4.2.9")
    // totp
    implementation("dev.turingcomplete:kotlin-onetimepassword:2.4.0")

    // health-check
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // firebase
    implementation("com.squareup.okhttp3:okhttp:4.2.2")
    implementation("com.google.firebase:firebase-admin:9.2.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
