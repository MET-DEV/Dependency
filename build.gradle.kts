import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	jacoco
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	kotlin("kapt") version "1.8.22"
	id("org.jlleitschuh.gradle.ktlint") version "11.5.1"
	id("org.sonarqube") version "4.4.1.3373"
}

group = "com.met"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	//<--------------DATA--------------------->
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	//<--------------BASE--------------------->
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	//<--------------DOCS--------------------->
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

	//<--------------MAPPER--------------------->
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	//<--------------TEST--------------------->
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.10.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}


kapt {
	arguments {
		arg("mapstruct.defaultComponentModel", "spring")
	}
}



sonar {
	properties {
		property("sonar.projectKey", "danismanim")
		property("sonar.projectName", "danismanim")
		property("sonar.token", "sqp_4833670fcf62b1f4d4d5c6c1286c30ceee446f4f")
		property("sonar.host.url", "http://localhost:9000")
		property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
		property("sonar.sourceEncoding", "UTF-8")
	}
}

tasks.jacocoTestReport {

	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
	}
}

jacoco {
	toolVersion = "0.8.9"
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
	extensions.configure(JacocoTaskExtension::class) {
		classDumpDir = layout.buildDirectory.dir("jacoco/classpathdumps").get().asFile
	}
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				counter = "LINE"
				minimum = BigDecimal.valueOf(0.008)
			}
		}
	}
}

tasks.check {
	dependsOn(tasks.jacocoTestCoverageVerification)
}


