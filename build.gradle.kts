plugins {
	java
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.1.5"
}

group = "com.nsteuerberg"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven(url = "https://maven.lavalink.dev/releases")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	// jda
	implementation("net.dv8tion:JDA:5.0.0")
	// lava player
	implementation("dev.arbjerg:lavaplayer:2.2.1")
	implementation("dev.lavalink.youtube:common:1.5.2")
	// spotify api
	implementation("se.michaelthelin.spotify:spotify-web-api-java:8.4.0")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
