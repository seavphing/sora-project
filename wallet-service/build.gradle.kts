plugins {
	java
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.liquibase.gradle") version "2.2.0"
}

group = "com.soramitsu.assignment"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2024.0.1"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.liquibase:liquibase-core")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.amqp:spring-rabbit-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

configure<org.liquibase.gradle.LiquibaseExtension> {
	activities.register("main") {
		this.arguments = mapOf(
			"changeLogFile" to "src/main/resources/db/changelog/db.changelog-master.xml",
			"url" to (project.findProperty("dbUrl") as? String ?: "jdbc:postgresql://postgres:5432/wallet_system"),
			"username" to (project.findProperty("dbUsername") as? String ?: "postgres"),
			"password" to (project.findProperty("dbPassword") as? String ?: "root"),
			"defaultSchemaName" to (project.findProperty("dbSchema") as? String ?: "wallet_schema")
		)
	}
	runList = project.findProperty("runList") as? String ?: "main"
}

tasks.withType<Test> {
	useJUnitPlatform()
}
