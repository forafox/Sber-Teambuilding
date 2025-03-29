plugins {
	id("java")
	id("java-library")
	id("io.spring.dependency-management") version "1.1.6"
	jacoco
}

group = "com.jellyone"
version = "1.0"


repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.0")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.springframework.boot:spring-boot-starter-mail:3.4.2")
	implementation("org.apache.poi:poi:5.2.3")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.4.3")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation(platform("org.junit:junit-bom:5.10.0"))
	testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
	useJUnitPlatform()
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}
