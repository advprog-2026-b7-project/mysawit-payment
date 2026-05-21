import org.gradle.kotlin.dsl.implementation

val flywayVersion = "10.10.0"

plugins {
    java
    jacoco
    checkstyle
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "4.4.1.3373"

}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"
description = "mysawit-payment"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("org.flywaydb:flyway-database-postgresql:$flywayVersion")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    implementation("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")
    
    // JWT Token library
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    filter {
        excludeTestsMatching("*FunctionalTest")
    }
    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

checkstyle {
    toolVersion = "10.12.5"
    isIgnoreFailures = false
}
