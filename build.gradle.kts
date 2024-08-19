plugins {
	id("java-library")
	id("org.jetbrains.kotlin.jvm")
	id("maven-publish")
}

group = "dev.frozenmilk.util"

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
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
			name = "Dairy"
			url = uri("https://repo.dairy.foundation/releases")
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
			version = "1.0.0"

			afterEvaluate {
				from(components["kotlin"])
			}
		}
	}
}
