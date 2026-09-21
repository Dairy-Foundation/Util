plugins {
    id("dev.frozenmilk.jvm-library") version "12.0.0-1.2.1"
    id("dev.frozenmilk.publish") version "0.1.0"
    id("dev.frozenmilk.doc") version "0.1.0"
}

group = "dev.frozenmilk.util"

ftc {
    kotlin()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

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
