plugins {
	id("org.jetbrains.kotlin.jvm") version "2.0.21"
	id("java-library")
	id("maven-publish")
}

repositories {
	mavenCentral()
	google()
}

group = "dev.frozenmilk.util"

kotlin {
	jvmToolchain(8)
	compilerOptions {
		freeCompilerArgs.add("-Xjvm-default=all")
	}
}

dependencies {
	testImplementation("junit:junit:4.13.2")
}

publishing {
	repositories {
		maven {
			name = "Release"
			url = uri("https://repo.dairy.foundation/releases")
			credentials(PasswordCredentials::class)
			authentication {
				create<BasicAuthentication>("basic")
			}
		}
	}
	repositories {
		maven {
			name = "Snapshot"
			url = uri("https://repo.dairy.foundation/snapshots")
			credentials(PasswordCredentials::class)
			authentication {
				create<BasicAuthentication>("basic")
			}
		}
	}
	publications {
		register<MavenPublication>("release") {
			groupId = "dev.frozenmilk.dairy"
			artifactId = "Util"
			version = "1.1.0"

			afterEvaluate {
				from(components["kotlin"])
			}
		}
	}
}
