plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])

            pom {
                groupId = "io.dbkover"
                artifactId = "junit5"
            }
        }
    }
}

val junit_version: String by project

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    api(project(":core"))

    implementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
    implementation("org.junit.jupiter:junit-jupiter-params:$junit_version")
    implementation("org.junit.jupiter:junit-jupiter-engine:$junit_version")

    testImplementation("org.testcontainers:postgresql:1.17.2")
}
