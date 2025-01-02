plugins {
	id("dev.frozenmilk.jvm-library") version "10.1.1-0.1.3"
	id("dev.frozenmilk.publish") version "0.0.4"
	id("dev.frozenmilk.doc") version "0.0.4"
}

group = "dev.frozenmilk.util"

publishing {
	publications {
		register<MavenPublication>("release") {
			groupId = "dev.frozenmilk.dairy"
			artifactId = "Util"

			artifact(dairyDoc.dokkaJavadocJar)
			artifact(dairyDoc.dokkaHtmlJar)

			afterEvaluate {
				from(components["java"])
			}
		}
	}
}
